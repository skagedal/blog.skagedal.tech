---
layout: post
title:  "Writing a habit tracker, part 21: Uploading that JAR"
---
In the previous post, we build a JAR file! It was big! Now we want to upload it to the server. 

I'd like to upload this to the Unix account[^1] that we created for this purpose in part one, the one that force me to decide on a name for this project, and then I chose the name `hahabit`. Good times. 

I use `ssh` authentication exclusively to log in to this server, because that's what security people say is the thing to do. So I need to generate a key pair and upload the public key to my account, and keep the private key somewhere super safe. I do this with `ssh-keygen` and then upload it to my server with `scp`:

```shell
$ ssh-keygen -f hahabit-key
Generating public/private rsa key pair.
<other stuff from ssh-keygen>
$ scp hahabit-key.pub simon@skagedal.tech:
hahabit-key.pub                                                                  100%  586    16.3KB/s   00:00 
```

And then I log in to my server as `simon`, change user to `hahabit` with the `su` command and the password I gave it, and copy the `hahabit-key.pub` to `~/.ssh/authorized_keys`. 

Now I can log in to my server using `ssh hahabit@skagedal.tech -i hahabit-key`! This means I can upload files to it as well. So, let's put it all together so far with a little deploy script:

```shell
#!/usr/bin/env bash

# Exit on errors
set -e

echo 👋 Building JAR with Java 19...
export JAVA_HOME=$(/usr/libexec/java_home -v 19)
./gradlew clean bootJar

echo
echo 👋 Uploading JAR to skagedal.tech...
scp -i ~/.ssh/hahabit-key build/libs/hahabit-0.0.1-SNAPSHOT.jar hahabit@skagedal.tech:
```

[^1]: Yes, I know, my server is running Linux – or if you're so inclined, GNU/Linux – which is not "Unix", but I think that if I write "Unix account", most people will directly understand what I mean. Is that ok with you? 