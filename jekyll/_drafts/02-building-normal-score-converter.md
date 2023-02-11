---
layout: post
title:  "Updating normal score converter"
---
So, as I mentioned, there's a Python script, [build.py](https://github.com/skagedal/normalscore/blob/5b512f2302083479ed25c80bd8ff6b93cef7b839/build.py) to put all the Javascript together. Let's try running it.

```
$ python3 build.py
Traceback (most recent call last):
  File "/Users/simon/code/normalscore/build.py", line 7, in <module>
    from fabricate import *
  File "/Users/simon/code/normalscore/fabricate.py", line 183
    except OSError, e:
           ^^^^^^^^^^
SyntaxError: multiple exception types must be parenthesized
```

Hmm – oh wow, is this Python 2? I don't want to install that. Let's see if I can get it to run with Python 3.

So, i used this build tool called [fabricate.py](https://github.com/brushtechnology/fabricate). It's pretty cool, you just tell it "run this thing to build this other thing" and it figures out the dependencies so that the next time you run it, it ony builds the things that have changed – without having to mention the dependencies explicitly, and without any other knowledge of what exactly it is your building. 

It was shipped backed then as a single Python file you can include in your project, and seems it still is. So I'm gonna try just upgrading it. 

```
$ curl https://raw.githubusercontent.com/brushtechnology/fabricate/master/fabricate.py -o fabricate.py
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 67534  100 67534    0     0  46461      0  0:00:01  0:00:01 --:--:-- 46736

```

And then, let's try the build again.

```
$ python3 build.py
Traceback (most recent call last):
  File "/Users/simon/code/normalscore/build.py", line 29, in <module>
    file.setdefault("concat", not file.has_key("CDN"))
AttributeError: 'dict' object has no attribute 'has_key'```
```

Ah, seems that one was [deprecated](https://portingguide.readthedocs.io/en/latest/dicts.html#:~:text=has_key()%20method%2C%20long%20deprecated,longer%20available%20in%20Python%203.) and you should now use the `in` operator instead. Apparently, there's also a `python-modernize` tool that can do this for you, but I'll just do this one change myself. Now, next thing:

```
$ python3 build.py
yui-compressor js/jquery.flot.js -o js/jquery.flot.mintmp.js
Traceback (most recent call last):
<cut boring stack trace>
  File "/opt/homebrew/Cellar/python@3.10/3.10.10/Frameworks/Python.framework/Versions/3.10/lib/python3.10/subprocess.py", line 1847, in _execute_child
    raise child_exception_type(errno_num, err_msg, err_filename)
FileNotFoundError: [Errno 2] No such file or directory: 'yui-compressor' 
```

Hm, ok, so this is some Javascript minifier tool I used... [here's](https://yui.github.io/yuicompressor/) its homepage. How do I install that, does it have a Homebrew package? [Yes it does!](https://formulae.brew.sh/formula/yuicompressor#default)

I'm installing that with `brew install yuicompressor`. Then it seems the executable is called `yuicompressor`, not `yui-compressor`. So I'll just change that in the build script. And then, let's try again.

```
$ python3 build.py
yuicompressor js/jquery.flot.js -o js/jquery.flot.mintmp.js
yuicompressor js/jquery.flot.canvas.js -o js/jquery.flot.canvas.mintmp.js
yuicompressor js/jquery.flot.crosshair.js -o js/jquery.flot.crosshair.mintmp.js
yuicompressor js/jquery.flot.axislabels.js -o js/jquery.flot.axislabels.mintmp.js
yuicompressor js/sprintf-0.7-beta1.js -o js/sprintf-0.7-beta1.mintmp.js
yuicompressor js/jquery.handsontable.full.js -o js/jquery.handsontable.full.mintmp.js
yuicompressor js/normalscore.js -o js/normalscore.mintmp.js
Concatenating...
Building prod.index.html...
$
```

Oh cool, are we done? 

```
$ open -a Safari prod.index.html
```

Yeah! This opens up my single-page app, if we want to call it that, in Safari – and it works. I can enter a score, and it gets converted.

[Here's](https://github.com/skagedal/normalscore/commit/0b1a9ce804eb5305c1f4f7fa522b1439da967f4b) what my first commit in ten years (minus eleven days!) looks like. 

Now I want to get it deployed on 