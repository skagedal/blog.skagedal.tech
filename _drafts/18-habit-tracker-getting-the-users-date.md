---
layout: post
title:  "Writing a habit tracker, part 18: Getting the users date"
---
In the previous post, we hard coded the date as 2023-01-13. But in the real world, the date is not always 2023-01-13. Most days, it's not. We'd like to show the current date. 

So, `LocalDate.now()`, right? No. That gives us the current date of whatever the timezone the server is configured to. We want the date it is at wherever the user is. 

Looking once again in [The Big List](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-methods), we find that we can ask for either a `java.util.TimeZone` or a `java.time.ZoneId`. Rule of thumb: when given a choice between a `java.util.SomeTimeRelatedConcept` and `java.time.SomeTimeRelatedConcept`, choose the latter. The `java.time` API:s are much better designed. 

So now we can just do this:

```java
public class HomeController {
    // ...
    @GetMapping("/")
    ModelAndView getHome(Principal principal, ZoneId zoneId) {
        final var date = LocalDate.now(zoneId);
        return new ModelAndView(
            "home",
            Map.of(
                "date", date,
                "habits", getHabitsForDate(principal, date)
            )
        );
    }
    // ...
}
```

Beautiful. But how does this _work_? How does it know what time zone I'm in? I logged the value of that `zoneId`, and it did say `Europe/Stockholm` for me, which is my time zone ID. 

According to the docs, it's determined by the [LocaleContextResolver](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/LocaleContextResolver.html). What does that do? I fiddled around with the debugger and managed to step myself to the place where that `ZoneId` argument was resolved, and it was using [RequestContextUtils.getTimeZone(HttpServletRequest)](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/support/RequestContextUtils.html#getTimeZone(jakarta.servlet.http.HttpServletRequest)), which was the method that tried to find a LocaleContextResolver and... didn't find one. (It only found a `LocaleResolver` which was not a `LocaleContextResolver` – which is an extended interface with support for various things like time zones.) And instead selected the system default. Which would be `Europe/Stockholm` since I'm running the server locally. Hmmm. 

