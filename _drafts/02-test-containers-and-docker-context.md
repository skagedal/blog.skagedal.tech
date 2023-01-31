---
layout: post
title:  "docker-java and Docker Context"
---
In the previous post, I talked about why I want Testcontainers to know about Docker Contexts.  

Now, Testcontainers doesn't talk to Docker by itself. It uses a library called docker-java for this, which basically is a Java implementation of the Docker protocol and everything needed to talk to a Docker environment. 

So it is docker-java that needs to be made aware of Docker contexts. 

I set out in the start of december 2022 to try to add this support. 

The [first thing](https://github.com/docker-java/docker-java/pull/2036/commits/54c41f5327c3f78b17dd56e6f7aa958382f7c0ec) I did was to add support for reading the name of the current context from config.json. So, there is a file called `~/.docker/config.json` that you probably have on your system if you're using Docker. And `docker-java` already supported reading it, but did not support the property `currentContext`. I just modified the model object to support and deserialize this property, and added a small test to test the deserialization.

Then I felt a bit unsure about how to continue. I would have wanted to create an integration test where I could test the full feature as it was being built. But getting to know a new code base can be difficult, especially when you don't have a friend setting next to you to guide you, and also when you're new to various parts of the technology you're working with – for example, I have never really used Maven much, which docker-java uses. I also have never used the `docker-java` library directly, only indirectly via Testcontaikners.

So instead I 

I filed a pull request to do exactly this earlier. But it needs a bit more work to get merged. 

I need to figure out how to get the test suite running for this thing. The CONTRIBUTING.md file has some pointers.  