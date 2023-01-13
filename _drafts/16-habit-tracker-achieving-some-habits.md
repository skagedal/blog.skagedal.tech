---
layout: post
title:  "Writing a habit tracker, part 16: Achieving some habits"
---

There are more things we could do regarding the management of our list habits, like deleting and editing them, and handling errors, but I'm eager to get going with the part where we actually _achieve_ (as we called it, if you remember) our daily habits!  

This is going to be the main "home" screen of app, as it is what you will do most often. It'll say the current date, to just kind of clarify what day we are "achieving", and list your habits and, if they have not been achieved, present a way to achieve them, and if not, show some kind of green checkmark or something. 

So we need to figure out how to render HTML elements conditionally with Thymeleaf. Let's deal with that first, before we add any buttons or anything. [Baeldung tells us](https://www.baeldung.com/spring-thymeleaf-conditionals) that we can use the `th:if` and `th:unless` tags. So let's try this as the `home.html` template:

```html
<!DOCTYPE html>
<html xmlns:th="https://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>hahabit</title>
</head>
<body>
<h1>hahabit</h1>
<h2 th:text="${date}">1970-01-02</h2>
<ul>
    <li th:each="habit: ${habits}">
        <span th:if="${habit.achieved}">✅ <span th:text="${habit.description}">Take a walk</span></span>
        <span th:unless="${habit.achieved}">😐 <span th:text="${habit.description}">Take a walk</span></span>
    </li>
</ul>

<p><a th:href="@{/habits}">Manage my habits</a></p>
</body>
</html>
```

The achieved habits get a ✅, the unachieved get a 😐. 

The thing with this kind of system is that... now you gotta learn some new little programming language. Or several. There's the "language" of which th-tags you can use and how, then there's the language of those expressions within `${ ... }`, which for the [Spring-dialect of Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.html) are "SpEL" (Spring Expression Language, we'll meet this one again) expressions, while for non-Spring-integrated Thymeleaf are [OGML](https://commons.apache.org/proper/commons-ognl/language-guide.html) expressions.  

I always feel in those situations that, like, I already have a programming language, it's a pretty nice programming language, why can't I just use that? I know how `if` statements work and _lots_ of other cool things! We could of course totally put HTML together in code instead. That can get rather tedious as well, though, unless the programming language has a flexible syntax model. In Kotlin, for example, it's nice to create custom DSLs

Maybe discuss: https://j2html.com/
https://github.com/kotlin/kotlinx.html