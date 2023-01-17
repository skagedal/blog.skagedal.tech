---
layout: post
title:  "Writing a habit tracker, part 21: Uploading that JAR"
---
In the previous post, we build a JAR file! It was big! Now we want to upload it to the server. 

But I think we're ready to try to upload that guy to the server! 

```shell
$ scp build/libs/hahabit-0.0.1-SNAPSHOT.jar simon@skagedal.tech:hahabit/
```

That uploads the 27 MB JAR file (because of course a trivial little Java server app should be 27 MB) to my account, in a directory I created before. Great, can we run it?

```shell
$ ssh simon@skagedal.tech
<welcome to ubuntu etc>
$ cd hahabit
$ java -jar hahabit-0.0.1-SNAPSHOT.jar
2023-01-15T16:38:27.770Z  INFO 873558 --- [           main] t.skagedal.hahabit.HahabitApplication    : Starting HahabitApplication using Java 19.0.1 with PID 873558 (/home/simon/hahabit/hahabit-0.0.1-SNAPSHOT.jar started by simon in /home/simon/hahabit)
<lots of logs>
2023-01-15T16:38:35.518Z ERROR 873558 --- [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Exception during pool initialization.

org.postgresql.util.PSQLException: FATAL: password authentication failed for user "postgres"
```

Ah, right - it's configured to run things like on my local machine, trying to get into PostgreSQL with a simple password. Back in the [first blog post](/2023/01/01/writing-a-habit-tracker.html) of the series, where I set up PostgreSQL, I noted that:

> Apparently, Postgres’ default user management system is coupled to the user authentication system on the system. This seems nice enough for our purposes – I plan to just run everything on this machine, not deal with any Docker stuff.
> 
> 