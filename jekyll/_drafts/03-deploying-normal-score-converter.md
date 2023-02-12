---
layout: post
title:  "Setting up a wildcard certificate for skagedal.tech"
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

Oh, I'm reading that guide again more slowly, and now I see the relevant paragraph:

> Before issuing certificates, Let’s Encrypt performs a challenge to verify that you control the hosts you’re requesting certificates for. In the case of a wildcard certificate, we need to prove that we control the entire domain. We do this by responding to a DNS-based challenge, where **Certbot answers the challenge by creating a special DNS record in the target domain**. Let’s Encrypt’s servers then verify this record before issuing the certificate.

(My emphasis.) Allright, that makes sense I guess. 

Well, I guess I'll just have to try it. Before I do, I should backup of my current certbot configuration, and take a look at what my DNS configuration currently looks like, in case things get messed up. It looks like this:

| Type | Hostname        | Value                          | TTL (seconds) |
|------|-----------------|--------------------------------|---------------|
| A    | *.skagedal.tech | directs to 142.93.136.170      | 3600          |
| A    | skagedal.tech   | directs to 142.93.136.170      | 3600          |
| NS   | skagedal.tech   | directs to n2.digitalocean.com | 1800          |
| NS   | skagedal.tech   | directs to n1.digitalocean.com | 1800          |
| NS   | skagedal.tech   | directs to n3.digitalocean.com | 1800          |

Ok. I generate a DigitalOcean API token, and put it in the `certbot-creds.ini` file according to the guide, and then I take a deep breath and run this:

```shell
$ sudo certbot certonly \
    --dns-digitalocean 
    --dns-digitalocean-credentials ~/certbot-creds.ini \
     -d '*.skagedal.tech'
```

It responds with:

```
Saving debug log to /var/log/letsencrypt/letsencrypt.log
Requesting a certificate for *.skagedal.tech
Waiting 10 seconds for DNS changes to propagate

Successfully received certificate.
Certificate is saved at: /etc/letsencrypt/live/skagedal.tech-0001/fullchain.pem
Key is saved at:         /etc/letsencrypt/live/skagedal.tech-0001/privkey.pem
This certificate expires on 2023-05-12.
These files will be updated when the certificate renews.
Certbot has set up a scheduled task to automatically renew this certificate in the background.

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
If you like Certbot, please consider supporting our work by:
 * Donating to ISRG / Let's Encrypt:   https://letsencrypt.org/donate
 * Donating to EFF:                    https://eff.org/donate-le
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
```

Cool, that went well I guess!

I love how well this thing works, my other certificates from Let's Encrypt have been all been successfully updated in the background without me ever having to worry.

But now I worry about something: when it renews this certificate this time, will it again need to do the DNS dance? If so, that DigitalOcean API token needs to be updated as well. And now I took one that expires after 90 days. 

Or is that just something it does the first time? I'll take a look at how this renewal thing is configured. Finding some very relevant stuff in the `/etc/letsencrypt/renewal` folder – here's the configuration for the certificate it just created, which I guess is called `skagedal.tech-0001` since there was already one called `skagedal.tech`:

```
# renew_before_expiry = 30 days
version = 1.21.0
archive_dir = /etc/letsencrypt/archive/skagedal.tech-0001
cert = /etc/letsencrypt/live/skagedal.tech-0001/cert.pem
privkey = /etc/letsencrypt/live/skagedal.tech-0001/privkey.pem
chain = /etc/letsencrypt/live/skagedal.tech-0001/chain.pem
fullchain = /etc/letsencrypt/live/skagedal.tech-0001/fullchain.pem

# Options used in the renewal process
[renewalparams]
account = 0a5acb3c10c1509717e114f5cd0297b6
authenticator = dns-digitalocean
dns_digitalocean_credentials = /home/simon/certbot-creds.ini
server = https://acme-v02.api.letsencrypt.org/directory
```

So yeah, it seems to have recorded the place where I stored those credentials. Pretty sure it'll use those again. Pretty sure I'll have to make sure that API token is updated. How am I going to remember that?!

Hopefully, DigitalOcean will send me an e-mail or something when the API token is about to expire. I'll just have to make sure to read my e-mails. I guess I'll try that. I can do it. I mean I could have also chosen an API token that never expires, but people say that's bad. 

Anyway, let's forget about tomorrow.

Now, can we set up my `normalscore.skagedal.tech` site to use this certificate?

Making my `normalscore.nginx` file to be this:

```
server {
    server_name normalscore.skagedal.tech;

    listen [::]:443 ssl;
    listen 443 ssl;
    ssl_certificate /etc/letsencrypt/live/skagedal.tech-0001/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/skagedal.tech-0001/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    root /home/simon/normalscore;
    index index.html;

    location / {
        try_files $uri $uri/ =404;
    }
}

server {
    server_name normalscore.skagedal.tech;

    listen 80;
    listen [::]:80;

    if ($host = normalscore.skagedal.tech) {
        return 301 https://$host$request_uri;
    }

    return 404;
}
```

Putting some nonsense in `~/normalscore/index.html`, and navigating to https://normalscore.skagedal.tech in my browser, and yeah! I can see the nonsense I put in `~/normalscore/index.html`. 

----
notes

Note, here: https://cloud.digitalocean.com/networking/domains/skagedal.tech?i=fc083a

DigitalOcean has a guide of what these mean: https://docs.digitalocean.com/products/networking/dns/how-to/manage-records/

Continue that wildcard guide: https://www.digitalocean.com/community/tutorials/how-to-create-let-s-encrypt-wildcard-certificates-with-certbot

And yes, let's encrypt does issue wildcard certs: https://letsencrypt.org/docs/faq/

And yes, subdomains need either their own cert or a wildcard cert:  - https://serverfault.com/questions/566426/does-each-subdomain-need-its-own-ssl-certificate