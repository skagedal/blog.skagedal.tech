---
layout: post
title:  "Deploying normal score converter"
---
Ok, this is starting to become a pattern here at skagedal's oboy – I write about some building some software project, but I also write about the mundane details of getting it deployed. Like with hahabit, the habit tracker, I wrote about [uploading it to the server](/2023/01/22/habit-tracker-deploying-the-jar.html) and [exposing the service](/2023/01/25/habit-tracker-exposing-it.html) to the Internet. 

Let's first discuss TLS. So far, I've set up certbot to get and renew certificates for each domain, `skagedal.tech`, `blog.skagedal.tech` and `hahabit.skagedal.tech`. Now I think I want to also set up `normalscore.skagedal.tech`. It gets a bit tedious, wouldn't it be sweet if I could just get one certificate for all of them? 

[This guide](https://www.digitalocean.com/community/tutorials/how-to-create-let-s-encrypt-wildcard-certificates-with-certbot) tells me how to get a so-called wildcard certificate. I guess it can't hurt to try?

As far as I understand, the first step, "Setting up a Wildcard DNS", is something I've already taken care of, as I also discussed in [this post](/2023/01/25/habit-tracker-exposing-it.html). 

but now apparently, I'm going ton need to install that "correct certbot DNS plugin". As you know, I'm using DigitalOcean, so I'll go with the `certbot-dns-digitalocean` plugin. 

```
$ sudo apt install python3-certbot-dns-digitalocean
```

And now let's check if the plugins are loaded:

```
$ certbot plugins

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
* dns-digitalocean
Description: Obtain certificates using a DNS TXT record (if you are using
DigitalOcean for DNS).
Interfaces: Authenticator, Plugin
Entry point: dns-digitalocean =
certbot_dns_digitalocean._internal.dns_digitalocean:Authenticator

* nginx
Description: Nginx Web Server plugin
Interfaces: Installer, Authenticator, Plugin
Entry point: nginx = certbot_nginx._internal.configurator:NginxConfigurator

* standalone
Description: Spin up a temporary webserver
Interfaces: Authenticator, Plugin
Entry point: standalone = certbot._internal.plugins.standalone:Authenticator

* webroot
Description: Place files in webroot directory
Interfaces: Authenticator, Plugin
Entry point: webroot = certbot._internal.plugins.webroot:Authenticator
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
```

Allright, yeah, that seems right.

I'm not quite sure why we are doing this, but now I'm reading that...

> Because Certbot needs to connect to your DNS provider and create DNS records on your behalf, you’ll need to give it permission to do so. 

Ugh. Why does it have to create new DNS records? DNS is fine? I just want a certificate? No?

I'm reading that guide again, closer, trying to understand. I'm taking a look at what the DNS setup looks like in my DigitalOcean console. It's like this:

| Type | Hostname        | Value                          | TTL (seconds) |
|------|-----------------|--------------------------------|---------------|
| A    | *.skagedal.tech | directs to 142.93.136.170      | 3600          |
| A    | skagedal.tech   | directs to 142.93.136.170      | 3600          |
| NS   | skagedal.tech   | directs to n2.digitalocean.com | 1800          |
| NS   | skagedal.tech   | directs to n1.digitalocean.com | 1800          |
| NS   | skagedal.tech   | directs to n3.digitalocean.com | 1800          |

Note, here: https://cloud.digitalocean.com/networking/domains/skagedal.tech?i=fc083a

DigitalOcean has a guide of what these mean: https://docs.digitalocean.com/products/networking/dns/how-to/manage-records/

Continue that wildcard guide: https://www.digitalocean.com/community/tutorials/how-to-create-let-s-encrypt-wildcard-certificates-with-certbot

And yes, let's encrypt does issue wildcard certs: https://letsencrypt.org/docs/faq/

And yes, subdomains need either their own cert or a wildcard cert:  - https://serverfault.com/questions/566426/does-each-subdomain-need-its-own-ssl-certificate