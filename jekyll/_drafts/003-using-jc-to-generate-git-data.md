---
layout: post
title: "Git log JSON data with jc"
summary: "I generate some data"
---
The other day, I was playing around with some React code. I wanted to build a component that would show a git commit history. I have some ideas of a simple deploy tool, and I basically just wanted to try out some UI ideas.

So I wasn't going to write any real git integration right at the moment, as that wasn't the purpose of my little exploration, but I still wanted to have some real data to use in my UI experiments, as that just makes everything more fun. 

My Typescript data model looked a little like this:

```typescript
type GitCommit = {
    sha: string;
    date: Date;
    author: string;
    message: string;
};
```

I began thinking about how I could do a `git log` in one of my repositories and get it into something JSON-y to use as my data. 

I started with the timestamps, as it seemed simple. JavaScript's `Date` constructor prefers to get them in a [specific simplified form](https://tc39.es/ecma262/multipage/numbers-and-dates.html#sec-date-time-string-format) of ISO-8601 that looks like `YYYY-MM-DDTHH:mm:ss.sssZ`. This is unfortunately not what you get with git's `git log --date=iso8601` command; it gives you something like `2023-11-13 21:53:28 +0200` which doesn't look like it's actually valid ISO-8601 [combined date and time respresentation](https://en.wikipedia.org/wiki/ISO_8601#Combined_date_and_time_representations) at all? But anyway, with just a little googling I got this:

```console
$ git log  --date=format:'%Y-%m-%dT%H:%M:%S.000Z'
commit fecbcf68e012948831910264a6a180923a8deda3
Author: Simon Kågedal Reimer <skagedal@gmail.com>
Date:   2024-06-08T07:21:02.000Z

    fix tests
    
commit 78210d9912db07f91132b827eeb8a8e09c8415cb
Author: Simon Kågedal Reimer <skagedal@gmail.com>
Date:   2024-06-08T07:20:57.000Z

    update dependencies
    
[etc]
$ 
```

So that's nice. But how do I get all that other stuff into JSON? I started to envision some kind of unholy combination of `awk`, `sed`, `cut`, `grep` and friends, or digging deep into git's own log formatting capabilities.

Then it suddenly struck me that I might actually have a perfect tool for the job in my toolkit already! There's this thing called `jc` which I read about some time ago. The author Kelly Brazil has a great blog post [here](https://blog.kellybrazil.com/2019/11/26/bringing-the-unix-philosophy-to-the-21st-century/) about how JSON really should be the interchange format CLI tools to bring the Unix philosophy into the 21st century. It's a great post and I agree with it. Putting together ad hoc parsing with the aforementioned tools is tiring and you will often end up with something that isn't very robust – suddenly your data contains a quotation mark or a tab character that you expected to be a delimiter and now everything breaks.

Some modern tools are 

```console
$ jc git log  --date=format:'%Y-%m-%dT%H:%M:%S.000Z' | jq '. | del(.[].epoch) | del(.[].epoch_utc)' | pbcopy
```

