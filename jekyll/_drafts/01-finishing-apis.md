---
layout: post
title: "Add API:s to track habits"
---

Finally, let's add those last API:s that we added tests for in the previous post. 

This will be an API-ification of the "achievement" functionality that we already have in the web app, as described in parts [sixteen](/2023/01/16/habit-tracker-listing-your-achievements.html) through twenty. So, we have the logic for this already, but it lives in the `HomeController`. We need to refactor a bit. 

The recommended architecture for Spring Boot is to have a controller for each entity, and then have the controller delegate to a service. So, we'll do that.

When I refactor I like to do as much as possible using the safe refactoring tools of my IDEA, IntelliJ IDEA. It gives me a nice fuzzy feeling when you can refactor and know it's correct, plus if you learn it well, it can speed up common tasks.

Honestly, that's also a bit why I don't really believe in the kind of thinking where every line of code has to follow the Best Practices, the Right Way, from the outset. For example, the fact that I put this code in the `HomeController` first. Someone who read that maybe went: "Noooo! That should be in the _service_ layer!". 

But instead of stressing over the structure of the code, I think you should get good at _changing_ the code. Optimize for change, and learn tools that help you change things.  

So.