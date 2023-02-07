---
layout: post
title:  "Testing Zola"
---

I feel like testing the [Zola](https://www.getzola.org/) static site generator.

I'm following the [Getting Started](https://www.getzola.org/documentation/getting-started/overview/) guide.

I'm running Zola 0.16.1, installed with Homebrew.

I moved all of my existing Jekyll site into a `jekyll` subdirectory, and ran:

```
$ zola init zola
Welcome to Zola!
Please answer a few questions to get started quickly.
Any choices made can be changed by modifying the `config.toml` file later.
> What is the URL of your site? (https://example.com): https://blog.skagedal.tech
> Do you want to enable Sass compilation? [Y/n]: y
> Do you want to enable syntax highlighting? [y/N]: y
> Do you want to build a search index of the content? [y/N]: n

Done! Your site was created in /Users/simon/code/blog.skagedal.tech/zola

Get started by moving into the directory and using the built-in server: `zola serve`
Visit https://www.getzola.org for the full documentation.
```

Sass compilation seems great, since I could then move over the CSS I have for my current site, which is written in Sass. (ALthough I don't love it and it's just the default Jekyll theme from 2017, but still.) I do want syntax highlighting and, while building a search index sounds fun, I'll save that for later.

`zola` really sets up an empty site for me. I'm just gonna follow the guide and set up first a `base.html` in the `templates` directory:

```html
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8">
  <title>MyBlog</title>
</head>

<body>
  <section class="section">
    <div class="container">
      {% block content %} {% endblock %}
    </div>
  </section>
</body>

</html>
```

And an `index.html`, also in the `templates` directory:

```html
{% extends "base.html" %}

{% block content %}
<h1 class="title">
  This is my blog made with Zola.
</h1>
{% endblock content %}
```

I keep a `zola serve` running in a shell, and when I make edits the locally served sites gets regenerated. So now I can see this text above. Very nice.

