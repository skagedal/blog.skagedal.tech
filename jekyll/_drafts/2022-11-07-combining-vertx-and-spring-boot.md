---
layout: post
title:  "Combining Vert.x and Spring Boot"
---
This is a text about combining Vert.x and Spring Boot in the same application.

At my work, we have been using Vert.x to write our Java server applications since the company started in 2014. We have built a successful product with this, but have realized that it does not serve our needs perfectly. The advertised benefits of the reactive approach are not materializing, and we have come to the conslusion that we would be better served by Spring Boot – easier to hire for, easier to get people up to speed with, and provides more functionality out of the box for us to just focus on the business logic.

So far, we have decided to use Spring Boot for new services. We of course have a lot of existing code written with Vert.x (and some in-house layers on top of that). Some of those servies are quite big, some are medium and a few are so small that a one-off "rewrite" project might even at some point make sense. But generally, I'm not a fan of rewriting working code _just because_. 

I much rather like to iterate and improve little by little. I've had good experiences for example with turning an Objective-C iOS app into a Swift app this way – new classes are written in the new way, and sometimes old classes are rewritten. One day you wake up and find yourself with a Swift app. 

Could we use the same kind of approach here? Let's play around a little with a simple project! 

## A simple Spring Boot project

Let me first say that I am no Spring Boot expert, I'm just learning. So read on if you want to learn with me. 

Let's create a project with [Spring Initializr](https://start.spring.io/). I'm going to use Gradle and Java 17 and only add Spring Web as a dependency. Spring Boot 2.7.5 is the latest stable version at this time of writing, so let's use that.

We get a `build.gradle` that looks like this:

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.5'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
}

group = 'tech.skagedal'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

Some day I'd like to explore what these Spring Boot and Spring dependency management plugins really bring to the table. Why not just leverage Gradle's dependency management features? Why should I use `./gradlew bootRun` to run the application, instead of just `./gradlew run`? But oh well. I'll look at that another day. 

Then, we also get the skeleton source code for our new app, `SpringVertxApplication.java`. It looks like this:

```java
package tech.skagedal.springvertx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringVertxApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringVertxApplication.class, args);
    }
}
```

We can run it, and it tells us with ugly ASCII graphics that we are using Spring, and then a few more interesting things as log statements. (For log output, I'm going to only keep the log level, (truncated) logger name and the log text.)

```
INFO : t.s.springvertx.SpringVertxApplication   : Starting SpringVertxApplication using Java 17.0.5 on Simons-MBP-2 with PID 52620 (/Users/simon/code/spring-vertx/out/production/classes started by simon in /Users/simon/code/spring-vertx)
INFO : t.s.springvertx.SpringVertxApplication   : No active profile set, falling back to 1 default profile: "default"
INFO : o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
INFO : o.apache.catalina.core.StandardService   : Starting service [Tomcat]
INFO : org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.68]
INFO : o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
INFO : w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 399 ms
INFO : o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
INFO : t.s.springvertx.SpringVertxApplication   : Started SpringVertxApplication in 0.773 seconds (JVM running for 1.529)
```

As we can see, Spring Boot here is by default using the embedded Apache Tomcat Servlet engine. This is an implementation of the [Jakarta Servlet](https://jakarta.ee/specifications/servlet/) platform, a nice old Java platform for writing server application. You know, you'd have your Java Applet on the frontend and your Java Servlet on the backend. But these days we don't have applets any more. Anyway. I digress.

Let's also add a small endpoint handler. Putting this in `HelloController.java`:

```java
package tech.skagedal.springvertx;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/spring/hello")
    String hello() {
        return "Hello from Spring Boot!";
    }
}
```
Now when we run our app, we can give it a small test in the shell:

```shell
❯ curl http://127.0.0.1:8080/spring/hello
Hello from Spring Boot!%
```

Hooray! It worked! (The `%` you see there is my shell's way of saying "this thing didn't end with a newline".)

## Adding Vert.x

So again, the Servlet Container is the core technology that handles serving HTTP requests for Spring Boot and several other Java frameworks. Vert.x, however, is not built on top of that. It is built on different kind of architecture, more similar to for example Node.js, where instead of spawning a thread for handling each request, there is one thread (or a few) with a main loop that handles I/O events, and passes them on various handlers. The core library that Vert.x uses for such asynchronous network handling is called Netty. 

There is actually also a version of Spring Boot that uses Netty, Spring Boot Reactive, but we're not using that here. We want the vanilla Spring Boot. 

So our Spring Boot code and Vert.x code is going to live it two rather different worlds. But there's nothing saying that the two can not coexist in the same JVM process. 

Let's add Vert.x to our class path by adding this two lines to the `dependencies` section of `build.gradle`:

```groovy
    implementation platform("io.vertx:vertx-stack-depchain:4.3.4")
    implementation "io.vertx:vertx-web"
```

`vertx-web` gives us the capabilities to write HTTP ("web") applications, just like we added Spring Web (and got the `-web` variant of the Spring Boot starter). 

We can now write a small _Verticle_, which is Vert.x's core thing where you put code. You can almost think of it as the "application", but not really; an application can consist of several verticles that talk to each other. This is used as a mechanism of handling concurrency according to the "actor model". 

But we're only gonna have one verticle, and to begin with it is, like our Spring RestController, just going to respond to GET requests on a single path. Here's `HelloVerticle.java`:

```java
package tech.skagedal.springvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public class HelloVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx
            .createHttpServer()
            .requestHandler(createRoutes())
            .listen(8888, httpServerResult -> {
                if (httpServerResult.succeeded()) {
                    startPromise.complete();
                } else {
                    startPromise.fail(httpServerResult.cause());
                }
            });
    }

    private Router createRoutes() {
        final var router = Router.router(vertx);

        router.get("/vertx/hello").handler(context ->
            context.response()
                .putHeader("Content-Type", "text/plain")
                .end("Hello from Vert.x!"));

        return router;
    }
}
```

Not gonna get into the details here, but hopefully anyone who's ever written an HTTP application should be able to basically follow what it's doing.

Now let's just deploy this in our `main` function, making it look like this:

```java
    public static void main(String[] args) {
        Launcher.executeCommand("run", HelloVerticle.class.getName());
        SpringApplication.run(SpringVertxApplication.class, args);
    }
```

Now, we can run the app again. We see a looottt of DEBUG log output from mostly Netty, then we see the Spring Boot logs. Can we now reach both things?

```shell
❯ curl http://127.0.0.1:8080/spring/hello
Hello from Spring Boot!%

❯ curl http://127.0.0.1:8888/vertx/hello
Hello from Vert.x!%
```

Woo-hoo! Yes we can! Yes we can! 

Now, we could pretty much deploy this, if we wanted. We could run it in a Docker container that exposes both the 8080 port for Spring Boot endpoints and the 8888 port for Vert.x endpoints, and then we could set up our load balancer to forward calls that start with `/spring` to port 8080 and calls that start with `/vertx` to port 8888.

But that's not really what we want. At least it's not what I want. I don't know about you. But as you remember from the start of the post, we'd want to gradually move things from Vert.x to Spring Boot. We should be able to do that without the outside world (including the load balancer) having to have any knowledge of these two subsystem; it's an application implementation detail. 

So let's look at an approach on how to do that. But first, let's not just use `curl` to test things, bud add some stuff to our test suite.

## Adding some tests

The kind of tests I would like for this are those that actually start the service and make some HTTP calls to it. Otherwise, we really don't know what we're testing here. Actually, it's the kind of test I find most useful in general – if you haven't already, go watch the talk [TDD: where did it all go wrong](https://www.youtube.com/watch?v=EZ05e7EMOLM) by Ian Cooper. That's better use of your time than continuing reading this blog post. But, oh well, I can tell you're still reading, so let's continue. 

The Spring Initializr (which you can tell is a really cool product since they've skipped the "e" in Initilizer) has kindly provided us with a test class called `SpringVertxApplicationTests`, looking like this:

```java
package tech.skagedal.springvertx;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringVertxApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

This starts up the application, but running with the `MOCK` web environment – it doesn't actually start listening to a port. We would like it to start up for real, and listen to a random port, which can be done by specifying the `webEnvironment` property. We can then get the port number by asking for the `ServletWebServerApplicationContext` to be injected:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringVertxApplicationTests {
    @Autowired
    private ServletWebServerApplicationContext servletContext;

    @Test
    void showPort() {
        System.out.println("Running on port " + servletContext.getWebServer().getPort());
    }
}
```

(I'm not sure what I feel about `@Autowired` yet, I'm much more a fan of constructor injection and `final` properties, which I could of course have used here. But for some reason it feels ok for test classes, and I can't argue with the boilerplate reduction.)

Now we can set up a HTTP client and make some calls. Spring Web comes with some clients of its own, some specifically for test suite use, but I'm going to use the [Java 11 HTTP Client](https://openjdk.org/jeps/321) because I think it's pretty neat, and I like the composable approach of using a regular HTTP client and regular tools for asserting the results. But also I suck at Spring Boot, and also I'm afraid it might do some unwanted magic behind my back. 

I'm going to introduce a few helpers though to make things a bit more convenient and forget about annoying checked exceptions. Here's my `SpringVertxApplicationTests` class now:

```java
package tech.skagedal.springvertx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringVertxApplicationTests {
    @Autowired
    private ServletWebServerApplicationContext servletContext;
    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    // Tests

    // HTTP helpers

    private URI uri(String path) {
        try {
            return new URI("http://127.0.0.1:" + servletContext.getWebServer().getPort() + path);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpRequest.Builder get(URI uri) {
        return HttpRequest.newBuilder(uri).GET();
    }

    private HttpResponse<String> sendReturningString(HttpRequest.Builder builder) {
        try {
            return httpClient.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpStatus httpStatus(HttpResponse<String> response) {
        return HttpStatus.valueOf(response.statusCode());
    }
}
```

Let's unpack this:

* `httpClient` is a HTTP client, configured for doing HTTP 1.1 calls. Yeah, we'll stick to those for now.
* `uri` gives us a full URI to the port on localhost where our Spring Boot service is running
* `get` is a small, self-explaining helper
* `sendReturningString` sends a HTTP request and giver the HTTP client a _body handler_ that returns the whole body as a string  
* `httpStatus` transforms the HTTP status integer into a type we happen to have access to, making our asserts read a bit nicer. 

So here's how we can test our Spring Boot endpoint now:

```java
    @Test
    void getHelloFromSpring() {
        final var response = sendReturningString(get(uri("/spring/hello")));

        assertEquals(HttpStatus.OK, httpStatus(response));
        assertEquals("Hello from Spring Boot!", response.body());
    }
```

Now we can run this test and rejoice in the fact that it is green! Then we can add another test:

```java
    @Test
    void getHelloFromVertx() {
        final var response = sendReturningString(get(uri("/vertx/hello")));

        assertEquals(HttpStatus.OK, httpStatus(response));
        assertEquals("Hello from Vert.x!", response.body());
    }
```

We similarly take joy and pride in the fact that this test is red! We get this output:

```
org.opentest4j.AssertionFailedError: 
Expected :200 OK
Actual   :404 NOT_FOUND
```

We haven't actually implemented this stuff, that's what we're about to do, so we're just asking Spring Boot for something it doesn't know anything about. We'll implement support for forwarding to our Vert.x service in the next section. 

## Forwarding to an external service 

We'd like to intercept the HTTP calls before they even reach our Spring MVC Servlet and instead forward them to our other services if needed. One neat place where we could do this is with a [ServletFilter](https://jakarta.ee/specifications/servlet/4.0/apidocs/javax/servlet/filter). We can use the abstract class [HttpFilter](https://jakarta.ee/specifications/servlet/4.0/apidocs/javax/servlet/http/httpfilter) as this gives us the correct types from the get go.

Here's our first skeleton `ForwardingFilter.java`:

```java
package tech.skagedal.springvertx;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class ForwardingFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        System.out.println("Passing request down the chain: " + request.getServletPath());
        chain.doFilter(request, response);
    }
}
```

We use the `@Component` annotation from Spring to automatically wire up this thing. (Fun fact / spoiler: this is the only Spring thing we're gonna use in this filter, the rest will just depend on the JDK and the Servlet API. There will also be no knowledge of Vert.x in this class, it could   

We run the test suite and can confirm that the filter is being hit for both our tests:

```
Passing request down the chain: /spring/hello
Passing request down the chain: /vertx/hello
```

We could implement our filter with one of the two existing mechanisms for forwarding that Baeldung writes about [here](https://www.baeldung.com/servlet-redirect-forward), Servlet redirects and Servlet forwarding. But neither is really what we want. 

Using the Redirect method would mean sending a HTTP redirect response pointing to our Vert.x service back to the caller, which would then call it. But we don't want the Vert.x service to be exposed at all to the outside world. And he forward mechanism (using the Servlet context's RequestDispatcher) will just forward it to a different servlet, which is not what we want.

We want to actually make a new HTTP call to that other service.

First of all, let's decide whether the call should be forwarded or not. This shouldn't be up to `ForwardingFilter` to decide, so let's have a small interface.

```java
package tech.skagedal.springvertx;

import java.net.URI;
import java.util.Optional;

public interface ForwardingTargetSelector {
    Optional<URI> forwardingTargetUri(String path);
}
```

(Every time I go for an `Optional` in Java, I have a debate with myself whether I really like it. I love it in languages that properly support it, and where an `Optional<T>` also can't be null. [Project Valhalla](https://openjdk.org/projects/valhalla/), save us on that last part? And then some syntax nicities from [Project Amber](https://openjdk.org/projects/amber/)? Dear Santa of 2032...)

And then, someone should implement that interface. I'm gonna let my `HelloVerticle` do that, because it knows where to forward the calls, and it could know which calls should be forwarded by looking at which routes are registered in our Vert.x router. However, for now I'm just gonna look at the path prefix.

```java
@Component
public class HelloVerticle extends AbstractVerticle implements ForwardingTargetSelector {

    // { ... } 

    @Override
    public Optional<URI> forwardingTargetUri(String path) {
        if (path.startsWith("/vertx")) {
            try {
                return Optional.of(new URI("http://127.0.0,1:8888" + path));
            } catch (URISyntaxException exception) {
                throw new RuntimeException(exception);
            }
        } else {
            return Optional.empty();
        }
    }
}
```

Also, it's lulz that the Vert.x Verticle is now a Spring `@Component`, so that the filter can get it dependency injected. This is what we're doing now:  

```java
@Component
public class ForwardingFilter extends HttpFilter {
    private final ForwardingTargetSelector targetSelector;

    public ForwardingFilter(ForwardingTargetSelector targetSelector) {
        this.targetSelector = targetSelector;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        final var targetUri = targetSelector.forwardingTargetUri(request.getServletPath());
        if (targetUri.isPresent()) {
            response.setStatus(501); // NOT IMPLEMENTED
        } else {
            System.out.println("Passing request down the chain: " + request.getServletPath());
            chain.doFilter(request, response);
        }
    }
}
```

Now, our `getHelloFromVertx` test successfully fails in a alightly more interesting way:

```
org.opentest4j.AssertionFailedError: 
Expected :200 OK
Actual   :501 NOT_IMPLEMENTED
```

So, let's start implementing that bad boy. For the second time in this long-winding blog post, we're going to have to select a HTTP client, and for the second time we're going to choose `java.net.http.HttpClient`. Again, I think it's neat, it fits the task very well and it's available on any modern JDK without further dependencies.

We create our HTTP Client like in the test suite, again let's stick to HTTP 1.1:

```java
    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
```

And then, for each incoming servlet request, we need to transform it in to a request for the HTTP client. Here's a naïve start:

```java
    private HttpRequest httpRequestFromServletRequest(HttpServletRequest request, URI targetUri) throws IOException {
        return HttpRequest.newBuilder(targetUri).GET().build();
    }
```

Yeah, let's assume everything is a GET. And let's have ourselves a little method to make the call:

```java
    private HttpResponse<byte[]> forwardCall(HttpServletRequest request, URI uri)
        throws IOException, ServletException {
        try {
            return httpClient.send(httpRequestFromServletRequest(request, uri), HttpResponse.BodyHandlers.ofByteArray());
        } catch (InterruptedException exception) {
            throw new ServletException(exception);
        }
    }
```

And that's just what we'll call from our `doFilter` method, like this:

```java
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        final var targetUri = targetSelector.forwardingTargetUri(request.getServletPath());
        if (targetUri.isPresent()) {
            final var clientResponse = forwardCall(request, targetUri.get());
            response.getOutputStream().write(clientResponse.body());
            response.flushBuffer();
        } else {
            System.out.println("Passing request down the chain: " + request.getServletPath());
            chain.doFilter(request, response);
        }
    }
```

As you see, for now we'll just fetch the whole response body as a big chunk o'data and write it out again. We'll fix this later to do it in a streaming way. And also, we don't yet care about anything other than the body. 

So let's try this again! Let's run the tests!

```
org.opentest4j.AssertionFailedError: 
Expected :200 OK
Actual   :500 INTERNAL_SERVER_ERROR
```

D'oh. What's wrong this time? Looking at the output from the Servlet, we see this stack trace:

```
java.net.ConnectException: null
	at java.net.http/jdk.internal.net.http.HttpClientImpl.send(HttpClientImpl.java:573) ~[java.net.http:na]
	at java.net.http/jdk.internal.net.http.HttpClientFacade.send(HttpClientFacade.java:123) ~[java.net.http:na]
	at tech.skagedal.springvertx.ForwardingFilter.forwardCall(ForwardingFilter.java:44) ~[classes/:na]
	at tech.skagedal.springvertx.ForwardingFilter.doFilter(ForwardingFilter.java:32) ~[classes/:na]
```

It's like it can't connect to the Vert.x server. But why! I've seen it running! I've connected to it with `curl`! But, oh... maybe it's just not running _yet_...? 

We took a pretty naïve approach to starting this whole thing up. If you remember, this is the `main` method:

```java
    public static void main(String[] args) {
        Launcher.executeCommand("run", HelloVerticle.class.getName());
        SpringApplication.run(SpringVertxApplication.class, args);
    }
```

We're just telling this `Launcher` thing to fire up my verticle without waiting for any particular event, such as the verticle having been successfully deployed. This would also be an issue in production, not just in my test; there could be requests that fail because the Spring Boot app happily reported itself as healthy and up and running, and then there are calls coming through from the load balancer, but they get forwarded to the Verticle which isn't ready at all.    

And also, this is just kind of ugly. It's like there's no one in charge, just two free souls randomly interacting at each one's leisure. Let's fix that. And just like we could have instead chosen to have Vert.x as the first receiver of HTTP calls, forwarding to Spring, we choose either one as the Owner of the Lifecycle. For consistency, we'll stick with Spring Boot as the main player.

We get rid of the `Launcher` call and instead inject the Verticle into the `SpringVertxApplication`, where we deploy it manually using `Vertx.deployVerticle`. We'll have to also remember to undeploy it so that our shutdown procedure works as expected (but I'm leaving it to you, reader, to confirm that this is working correctly).

Here's our new `SpringVertxApplication`:

```java
@SpringBootApplication
public class SpringVertxApplication {
    private final Vertx vertx = Vertx.vertx();
    private final HelloVerticle helloVerticle;
    private String verticleDeploymentId;

    public SpringVertxApplication(HelloVerticle helloVerticle) {
        this.helloVerticle = helloVerticle;
    }

    @PostConstruct
    void startService() {
        verticleDeploymentId = vertx
            .deployVerticle(helloVerticle)
            .toCompletionStage().toCompletableFuture().join();
    }

    @PreDestroy
    void stopService() {
        vertx.undeploy(verticleDeploymentId)
            .toCompletionStage().toCompletableFuture().join();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringVertxApplication.class, args);
    }
}
```

Not much to say about the Vert.x stuff here, we're "deploying" a verticle and getting an identifier that we can later use to "undeploy" it. Oh, maybe I should comment on the fact that in order to blockingly get a result back from a Vert.x [Future](https://vertx.io/docs/apidocs/io/vertx/core/Future.html), I have to first convert it to a Java [CompletionStage](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletionStage.html), and then that to a Java [CompletableFuture](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletableFuture.html), and then use "join" on that – but I don't want to.   

But anyway – now we run our tests again, and they are green! Very nice! 

## Drawing the rest of the damn owl

Now, we have only implemented support for a small part of HTTP: very simple GET requests. What are the things we need to support? Maybe you'd like to stop here and brush up on how HTTP works. You could do worse than reading (parts of) [this blog post](https://fasterthanli.me/articles/the-http-crash-course-nobody-asked-for), which I recently read and whose long-form didactic style inspired me a bit to write this one. But anyway, it's quite simple:

The client sends a request, containing:
* a request line, containing the method (`GET`, `POST` etc), the resource (everything after the domain in the URL) and the HTTP version;
* some headers;
* and a body.

The server sends a response, containing:
* a status line, including the status code; 
* some headers;
* and a body.

Let's make our Vert.x service a little bit more interesting to cover these features more fully. I'm going to do it with some really unrealistic, but simple, endpoints exercising one thing each. Adding this below my `/vertx/hello` handler in `HelloVerticle`:


```java
        // Return the first query parameter of the key "q" in the body
        router.get("/vertx/query").handler(context -> context.response()
            .end(context.queryParam("q").get(0)));

        // Return the value of header X-Header in the body
        router.get("/vertx/header").handler(context -> context.response()
            .end(context.request().getHeader("X-Header")));

        // Return some JSON
        router.get("/vertx/json").handler(context -> context.response()
            .putHeader("Content-Type", "application/json")
            .end("{}"));

        // Return an item (but none exist)
        router.get("/vertx/item/:id").handler(context -> context.response().setStatusCode(404).end("not found"));

        // Handle a DELETE and return "deleted" in the body
        router.delete("/vertx/delete").handler(context -> context.response().end("deleted"));

        // Handle a POST and just echo the input body back as the output body
        router.post("/vertx/post").handler(context -> context.request()
            .bodyHandler(buffer -> context.response().end(buffer)));
```

Now, let's add some tests and make them go green one by one. 

### Query parameters

The test:

```java
    @Test
    void getWithQueryParameter() {
        final var response = sendReturningString(get(uri("/vertx/query?q=Adams")));

        assertEquals(HttpStatus.OK, httpStatus(response));
        assertEquals("Adams", response.body());
    }
```

This one fails because our Vert.x endpoint is just not getting any query parameters (making the `.get(0)` throw an error and the endpoint return a 500, internal server error). Turns out that `getServletPath()` does not include query parameters – indeed, the docs say:

> Returns the part of this request's URL that calls the servlet. This path starts with a "/" character and includes either the servlet name or a path to the servlet, but does not include any extra path information or a query string. 

I do wonder what that "extra path information" might be? Anyway, we'll add this little helper:

```java
    // HttpServletRequest utilities

    private String getPathIncludingQueryParameters(HttpServletRequest request) {
        final var queryString = request.getQueryString();
        return request.getServletPath() + (queryString != null ? "?" + queryString : "");
    }
```

We use that from our `doFilter` method:

```java
    final var targetUri = targetSelector.forwardingTargetUri(getPathIncludingQueryParameters(request));
```

And now our test is green. ✅

### Sending request headers

```java
    @Test
    void getWithHeader() {
        final var response = sendReturningString(get(uri("/vertx/header"))
            .header("X-Header", "Baker"));

        assertEquals(HttpStatus.OK, httpStatus(response));
        assertEquals("Baker", response.body());
    }
```

This one fails because Vert.x isn't getting the headers. We need to set them when we create our `HttpClient` request in the Servlet. As we happen to see in the snippet above (because the test suite is also using `HttpClient`), this can be done with `.header`. But we don't really want to set them one at a time. (That's one of the main problems with such fluid / builder API:s – suddenly you need to do something conditionally or repeated and the whole fluid elegance disappears. Unless you have a language that allows for extensions.)

Thankfully, there's also a variant that takes several headers at once, as a variadic parameter `String...` of alternating header keys and values. Such methods can also be called with an array. We rewrite the `httpRequestFromServletRequest` method as:

```java
    private HttpRequest httpRequestFromServletRequest(HttpServletRequest request, URI targetUri) throws IOException {
        return HttpRequest.newBuilder(targetUri).GET()
            .headers(getHeadersFromRequeat(request))
            .build();
    }
```

And then we look at how to get the headers from the HttpServletRequest. Looking at [the docs](https://jakarta.ee/specifications/servlet/4.0/apidocs/javax/servlet/http/httpservletrequest), it appears as we can get all the header names with `getHeaderNames`, and then for each header name, get the headers with `getHeaders`. Both these methods have `Enumeration<String>` as the return type – one of many places where we can tell that this `Servlet` stuff is kind of old. `Enumeration` is a really old interface, and the documentation suggests that you should instead use `Iterable` for new things. 

