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

Can I get rid of that now? No, I can't. I then get a warning, which I treat as an error. I should try to find, or file, the more appropriate bug for this, as the one I'm linking above is closed. Oh well.  

**Dependabot is a nice tool.** If you're curious, by the way, I enabled it [like this](https://github.com/skagedal/hahabit/commit/9d4e6c470d202657fafb7212f7fe1b3d3d4f98d9), giving me weekly pull requests. Another nice tool for projects building with Gradle is the [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) from Ben Manes, which I also added, like this:

```groovy
plugins {
    // ...
    id "com.github.ben-manes.versions" version '0.44.0' 
}
```

While Dependabot gives automatic updates, this can be used for manually checking whether there are any potential updates. If I run it now, I get the following report:

```
------------------------------------------------------------
: Project Dependency Updates (report to plain text file)
------------------------------------------------------------

The following dependencies are using the latest milestone version:
 - com.github.ben-manes.versions:com.github.ben-manes.versions.gradle.plugin:0.44.0
 - io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.0
 - org.postgresql:postgresql:42.5.1
 - org.springframework.boot:org.springframework.boot.gradle.plugin:3.0.2
 - org.springframework.boot:spring-boot-starter-data-jdbc:3.0.2
 - org.springframework.boot:spring-boot-starter-security:3.0.2
 - org.springframework.boot:spring-boot-starter-test:3.0.2
 - org.springframework.boot:spring-boot-starter-thymeleaf:3.0.2
 - org.springframework.boot:spring-boot-starter-web:3.0.2
 - org.springframework.security:spring-security-test:6.0.1
 - org.springframework.session:spring-session-jdbc:3.0.0
 - org.testcontainers:junit-jupiter:1.17.6
 - org.testcontainers:postgresql:1.17.6

The following dependencies have later milestone versions:
 - org.flywaydb:flyway-core [9.5.1 -> 9.12.0]
     https://flywaydb.org

Gradle release-candidate updates:
 - Gradle: [7.6 -> 8.0-rc-2]
 ```

I'm not going to update Flyway, because that version is set by the Spring dependency management plugin. I think. I do not specify the version myself explicitly in my `build.gradle`. I'm happy with running the version that has been tested to work well together with the rest of the Spring suite. 

Yeah, what is that Spring dependency management plugin anyway? Why aren't we happy with just the dependency mananagement we get from Gradle? That is, after all, its job. 

The Spring dependency management plugin was added to the `plugins` section of our `build.gradle` file when we first 