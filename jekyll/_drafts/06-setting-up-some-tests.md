---
layout: post
title:  "Hahabit: Setting up some tests"
---
I want to add some new kinds of user experiences for Hahabit. I'm not all that happy with server-generated HTML. Eventually, I might consider either writing an iOS app, or a web app that uses React, or something entirely different. Either way, I'll want some API:s.

And I want those API:s to be covered by automated tests. I generally want to improve on the testing situation in Hahabit. Mostly, I want to blog some about testing. 

For fun, I'll add JaCoCo – the Java Code Coverage tool – to the project. Not that I care all that much about code coverage, but it's a fun metric to have. If it's really low, things are probably bad. 

My current coverage is... drumroll... **36%**. This is the number of instructions that are covered by tests, divided by the total number of instructions. 

The kind of tests 