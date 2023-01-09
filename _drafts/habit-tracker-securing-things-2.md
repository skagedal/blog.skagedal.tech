---
layout: post
title:  "Writing a habit tracker, part 10: Continued Spring Security"
---
Ok, I [completed](https://github.com/skagedal/hahabit/commit/c968ce4d2c5e839444a9b77a8435a963e01eceab) that [Securing a Web Application]([this guide](https://spring.io/guides/gs/securing-web/)) tutorial. (After all, it's supposed to take 15 minutes. But it takes a bit more when you keep trying to blog about it, and pause to investigate stuff with `curl` and stuff.) 

Now there's a form-based login system in places, on every page except the `home` and `login` pages. This is a different thing than the Basic Authentication scheme we got by default from Spring Security. In this system, we give our credentials in a HTML form, and then our session – identified with the `JSESSIONID` cookie – gets tied to this authenticated identity.

We can read more about form-based login system [here](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html). And this also tells me how to use the default Spring Security login page rather than the ugly one I've now replaced it with, by using 

```java
.formLogin(Customizer.withDefaults())
```

I'm doing that instead. 

But what I need to figure out now is how to enable the user repository we set up earlier.

Post on default schema: https://stackoverflow.com/questions/24174884/spring-security-jdbc-authentication-default-schema-error-when-using-postgresql