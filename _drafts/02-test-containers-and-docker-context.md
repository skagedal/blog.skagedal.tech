---
layout: post
title:  "docker-java and Docker Context"
---
In the previous post, I talked about why I want Testcontainers to know about Docker Contexts.  

Now, Testcontainers doesn't talk to Docker by itself. It uses a library called docker-java for this, which basically is a Java implementation of the Docker protocol and everything needed to talk to a Docker environment. 

So it is docker-java that needs to be made aware of Docker contexts. 

I filed a pull request to do exactly this earlier. But it needs a bit more work to get merged. 

I need to figure out how to get the test suite running for this thing. The CONTRIBUTING.md file has some pointers.  