---
layout: post
title:  "Writing a habit tracker, part 2: A skeleton Spring Boot app"
---

Now I'll just get right on writing... what exactly? Hmm. Now might be a good time to discuss what functionality we really want for this thing to support.

## Brief functionality spec

As a user, I should be able to:
* Create a daily habit. We only support daily habits. Each habit has a description, and each habit gets an identifier.
* Probably edit and delete those habits as well, the whole CRUD deal.
* Mark a habit as having been completed for a certain day.
* List some stats of when the habit was completed.

That's pretty much it for now.

## Making it run

Right now, as we try to run the app, we get an error:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.

Reason: Failed to determine a suitable driver class
```

The same thing happens when we run the test suite, which looks like this:

```java
@SpringBootTest
class HahabitApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

It tries to start up the app, and fails in the same way. We need to configure Testcontainers and the PostgreSQL URL setting. A quick google gives me [this Baeldung article](https://www.baeldung.com/spring-boot-testcontainers-integration-test), which seems a bit old school (uses Junit 4, etc) but could be useful. It looks like like a use a thing like this to set up my test application context:

```java
@ContextConfiguration(initializers = {HahabitApplicationTests.Initializer.class})
class HahabitApplicationTests {

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            System.out.println("Here we're configuring the context!");
        }
    }
    // ...
}
```

That seems to work. I mean, that it executes. Now we'll also need to start the Postgres container. The Spring Initializer was smart enough to add a dependency to the Postgres-specific Testcontainers dependency, in `build.gradle`:

```groovy
testImplementation 'org.testcontainers:postgresql'
```


