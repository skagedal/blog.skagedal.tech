I spent way too much time trying to figure out how that works, by haphazardly googling and trying to figure out which reference manual I should read, until I realized it was the [Spring Framework Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-controller). I get confused sometimes, and I do think the way this seems to be typically done is a bit confusing and/or ugly.

The thing is, I've seen a few Spring REST API controllers before, and they look a little something like this:

```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String getHello(@RequestParam String name) {
        return "Hello, " + name + "!";
    }
}
```

TODO: Is there a difference between @RestControlelr and @Controller here adn what does @ResponseBody do? https://www.baeldung.com/spring-request-param

Typically, you'd not return a string there, but some kind of model object which typically gets serialized as JSON. It's a pretty neat and simple model, I find – the inputs to the method represent the request  inputs to the

```java
@Controller
public class HelloController {
    @GetMapping("/hello")
    public String handle(Model model) {
        model.addAttribute("message", "Hello World!");
        return "index";
    }
}
```