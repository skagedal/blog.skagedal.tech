---
layout: post
title:  "Writing a habit tracker, part 23: Running it stably"
---

There's a couple of things I'd like fix before regarding how Spring runs. First, I want it to output logs to a file. Second, I want the actual running of the service to be controlled by the operating system. I shouldn't manually start it, and if it for some reason goes down – or the whole virtual machine goes down – it should restart.

The first thing is easy. We just set another property in the `run.sh` script:

```shell
export LOGGING_FILE_PATH=/home/hahabit 
```

That'll make it write files in `hahabit`'s home directory, and rotate as needed.

For the second thing, let's write a little [systemd](https://en.wikipedia.org/wiki/Systemd) service specification. As `root` on my server, I put this in `/etc/systemd/system/hahabit.service`:

```toml
[Unit]
Description=Hahabit habit tracker

[Service]
User=hahabit
WorkingDirectory=/home/hahabit
ExecStart=run.sh
Restart=always

[Install]
WantedBy=multi-user.target
```

Then I reload the system settings:

```shell
$ sudo systemctl daemon-reload
```

Then I keep a close eye on my service's logs by running `tail -f spring.log` in a separate shell.

And then I run this to start the service:

```shell
$ sudo systemctl start hahabit.service
```

And this to enable it on every reboot:

```shell
$ sudo systemctl enable hahabit.service
```

Nice! (Kudos to [this random blog](https://www.shubhamdipt.com/blog/how-to-create-a-systemd-service-in-linux/) which happened to be the one I found when searching for how to do this.) 

And we can restart the service with the `systemctl restart` command. Good, we will need to restart the service after we have uploaded a new version! We should add this to the `deploy.sh` script! But... hmmm. I need to be `sudo`'d to run this command. I don't want that. I'd like the `hahabit` user to have as few privileges as possible, and I don't want it to be a sudoer and be able to assume full control over my machine.

So how do I best do that? First I googled around for solutions on how to trigger systemctl, and found [this](https://superuser.com/questions/1171751/restart-systemd-service-automatically-whenever-a-directory-changes-any-file-ins) kind of solution, where it would listen for changes to some path and then restart the service, but it felt... too complex. Ugh. I felt there should be something simpler, but couldn't quite come up with it.  

Then, as I was sitting in the couch watching Sweden's Master Chef with the family, it dawned on me. I simply need to kill the running process, and systemd will restart the service for me! That I have right to do as the `hahabit` user! They're making fish tacos on Sweden's Master Chef, by the way!   

So, this is what I add to my `deploy.sh` script:

```shell
echo 👋 Sending SIGTERM to running service...
ssh -i ~/.ssh/hahabit-key hahabit@skagedal.tech pkill -TERM -f hahabit-0.0.1
```

I am telling `ssh` on my machine to connect to skagedal.tech, assuming the identity `hahabit` using the specified private key, and there it should execute the command `pkill -TERM -f hahabit-0.0.1`, which will search through the full argument list of running processes for some process that contains the text `hahabit-0.0.1`, and then send the `SIGTERM` signal to that process. This is caught by Spring Boot, which starts the shutdown procedure. Then after the process ends, systemd starts a new one. And I can follow all this by tailing the logs! Cool!

## A few last things

TODO:
* The Spring documentation: https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#deployment.installing.nix-services.system-d
* Should I wait for Postgres?

* Obviously, in a more real production scenario, we wouldn't want any downtime on each deploy, as we'll get with this scenario. But to make that work we'd need some kind of load balancer in front of the application, and I ain't got time for that.    
* One thing that I'm not so happy with right is how much I'm configuring by just editing stuff directly on the server. Such as this systemd stuff right here. That should be deployed using some kind of Infrastructure-as-Code tool. Maybe Ansible? That'll be for later.