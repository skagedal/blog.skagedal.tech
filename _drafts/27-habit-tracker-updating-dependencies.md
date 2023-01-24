---
layout: post
title:  "Writing a habit tracker, part 27: Updating dependencies"
---

I got a first external contribution to `hahabit`! A nice little [pull request](https://github.com/skagedal/hahabit/pull/1) from a friendly Github account called Dependabot, updating the version of Spring Boot from 3.0.1 to 3.0.2. I love updating dependencies – who knows, maybe one of the little bugs and annoyances mentioned before have been fixed?

There are currently two workaround things in `build.gradle`. One has been there [since](https://github.com/skagedal/hahabit/commit/c968ce4d2c5e839444a9b77a8435a963e01eceab) the following that [Spring Security tutorial](https://spring.io/guides/gs/securing-web/):

```
    //  Temporary explicit version to fix Thymeleaf bug
    implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity6:3.1.1.RELEASE'
```

I think I noted before that I got some issues when this was not included. But I don't remember what it was, and don't seem to get any issues now when removing. So. Maybe fixed? I'll remove it. I filed a [small issue](https://github.com/spring-guides/gs-securing-web/issues/72) against the documentation to include a reference.  

 Another thing was mentioned in [part twenty-one](/2023/01/21/habit-tracker-building-a-jar.html), I had to explicitly add a dependency on some findbugs annotations:

```
    // Because of https://github.com/spring-projects/spring-framework/issues/25095
    compileOnly 'com.google.code.findbugs:jsr305:3.0.2'
``` 

Can I get rid of that now? No, I can't. I then get a warning, which I treat as an error.   


And then I though, hey, while we're having fun in the `build.gradle` file, let's add the [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin):

```groovy
plugins {
    // ...
    id "com.github.ben-manes.versions" version '0.44.0' 
}
```
