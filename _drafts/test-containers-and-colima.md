---
layout: post
title:  "TestContainers and Colima"
---

At work, we use Docker containers quite a bit. For deploying stuff on ECS and Kubernetes, of course, but also for various things on local machines. All developers have Macs, so this has meant we use Docker Desktop for Mac. But recently, the cost for this has increased quite a bit, so the developer experience team investigated alternatives. And found [Colima](https://github.com/abiosoft/colima). It is based on a project called Lima, which means "Linux on Mac", and then Colima adds the "Co", standing for Containers.

It works great for the most part, but there have been some annoyances about running Testcontainers. Individual experiences seem to vary a little, but at least for me, to get this working properly, I need the following to be true in order to successfully run Testcontainers tests (and I am speaking about Testcontainers for Java):

* `colima` needs to be started with the `--network-address` flag
* `DOCKER_HOST` needs to be set to `unix://${HOME}/.colima/docker.sock`
* `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE` needs to be set to /var/run/docker.sock
* `TESTCONTAINERS_HOST_OVERRIDE` needs to be set to the IP address which you get from `colima ls -j | jq -r '.address'`, given that you have started `colima` with `--network-address`.

That's a lot of annoying stuff. Many other docker commands Just Work. And with Docker Desktop for Mac, Testcontainers used to just work. I'd like to unpack what's going on with all of this, and see what we can do to fix it.

## DOCKER_HOST

The first thing, which is what this blog post is going to be about, is sort of the simplest and also the most clear what we should do about. This simply tells Docker clients where to find the Docker server. Not that strange. But still, other Docker clients seem to just work, even without this. How come?

Turns out there's this fairly recent feature of Docker called Docker Context. 