---
layout: post
title:  "Writing a habit tracker, part 1: Setting up some tools"
---

It's a new year and, as such, the time to try to form new habits. The time to fail to maintain those habits comes later (mid-January at the latest). But let's not be cynical! Let's do it! And let's have fun and be kind to ourselves! 

So – me and my wife agreed, inspired by my friend Linda, to set the goal of spending at least 30 minutes outside every day. This sounds, to me, like a very good habit to form. Fresh air and sunlight is really nice itself, but it could also be a way to sneak in some much-needed exercise. Staying physically active is something I absolutely know for sure makes me happier as a person, yet I struggle to maintain the habit. So let's try with this instead. 

Then the question inevitably arises: which app should I use on my phone to track this habit? If you're not tracking your wellness efforts digitally, and share your progress with your social network, does it even happen?

I am really ambivalent when it comes to "quantified self" style self-tracking. There's a part in me that enjoys the idea of seeing measurable results. Then there's a part that really just wants to scale down on all reliance on digital tools. Throw away my smart phone and get a Nokia 3310. 

But then there's the side of me who really enjoys programming, and starting up small projects every now and then. And this kind of small "productivity" tools are perfect to get an excuse to toy around with some technology, and get the feeling that you're "doing something". 

So, here we go. I want to do this by writing a backend service in Java with Spring Boot using PostgreSQL, because that's some stuff I want to toy around with more. And I'm going to sort of start backwards – with the deployment. Because it ain't no fun if you can't start using your stuff from almost day one. 

I'm gonna set it up on my Digital Ocean machine that also serves this blog. Let's install PostgreSQL.

## Installing PostgreSQL

```shell
$ sudo apt update
$ sudo apt install postgresql postgresql-contrib
```

And making sure the service is started using:

```shell
$ sudo systemctl start postgresql.service
```

I am following [this guide](https://www.digitalocean.com/community/tutorials/how-to-install-postgresql-on-ubuntu-20-04-quickstart), and also use the method described there to become the default `postgres` user and start a shell:

```shell
$ sudo -i -u postgres
$ psql
psql (12.12 (Ubuntu 12.12-0ubuntu0.20.04.1))
Type "help" for help.

postgres=#
```

Apparently, Postgres' default user management system is coupled to the user authentication system on the system. This seems nice enough for our purposes – I plan to just run everything on this machine, not deal with any Docker stuff. 

So I want to set up a user that I will also run the Java app with, and a database with the name. I thus need to select a name for my project. Let's go with... habit... habitual... tracky... habit souds like hobbit... bit is a computer thing... *hahabit* is silly enough! It's `haha-bit`, but also `ha-habit`. Yeah let's do that. 

```shell
$ sudo -u postgres createuser --interactive
[sudo] password for simon:
Enter name of role to add: hahabit
Shall the new role be a superuser? (y/n) y
```
I'm... not really sure what Postgres means with a superuser. It sounds like something we probably don't want to run our app as, in the end. But let's use that to get started.

And then create our OS user:
```shell
$ sudo adduser hahabit
Adding user `hahabit' ...
Adding new group `hahabit' (1002) ...
Adding new user `hahabit' (1002) with group `hahabit' ...
Creating home directory `/home/hahabit' ...
Copying files from `/etc/skel' ...
New password:
Retype new password:
passwd: password updated successfully
Changing the user information for hahabit
Enter the new value, or press ENTER for the default
	Full Name []: Haha Bit
	Room Number []:
	Work Phone []:
	Home Phone []:
	Other []:
Is the information correct? [Y/n]
```

That's such a funny old unix thing, that you have to even get the question about "Work Phone" to set up a user. 

Oh, and we also needed to create a database for this user.

```shell
$  sudo -u postgres createdb hahabit
```

And now we can connect to our new database by just doing `sudo -u hahabit psql`. Very nice. And we could set up schemas and stuff. But I don't want to do it that way, I want to set up the schemas with Flyway migrations. Which I want to run from within the Java app. So let's set up Java.

## Installing Java

I'd like to use the cutting edge Java here, which at the time of writing is Java 19. There are a bunch of different distributions of OpenJDK, but one general recommendation (see [whichjdk](https://whichjdk.com/)), and that I've used before and found to work well, is Temurin. So let's follow [these instructions](https://adoptium.net/blog/2021/12/eclipse-temurin-linux-installers-available/):  

```shell
$ apt-get install -y wget apt-transport-https gnupg
<unintersting apt-get output snipped>
$ wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo apt-key add -
--2023-01-01 14:55:30--  https://packages.adoptium.net/artifactory/api/gpg/key/public
Resolving packages.adoptium.net (packages.adoptium.net)... 52.89.28.166, 54.148.186.122, 52.33.103.162
Connecting to packages.adoptium.net (packages.adoptium.net)|52.89.28.166|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: unspecified [text/plain]
Saving to: ‘STDOUT’

-                                          [ <=>                                                                         ]   1.75K  --.-KB/s    in 0s

2023-01-01 14:55:31 (39.5 MB/s) - written to stdout [1793]

OK
$ echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list
deb https://packages.adoptium.net/artifactory/deb focal main
$ sudo apt-get update && sudo apt-get install temurin-19-jdk
<unintersting apt-get output snipped>
```

Allright! Is it working?

```shell
$ java -version
openjdk version "19.0.1" 2022-10-18
OpenJDK Runtime Environment Temurin-19.0.1+10 (build 19.0.1+10)
OpenJDK 64-Bit Server VM Temurin-19.0.1+10 (build 19.0.1+10, mixed mode, sharing)
```

Yes, it is working! Now, it's time to create a little Java app using Spring Boot. But that's for tomorrow.

Oh, and yes, I did spend more than 30 minutes outside today, snowboarding the wonderful slopes of Hamra, Tänndalen. And I wrote a blog post! 
