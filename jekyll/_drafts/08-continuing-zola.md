One thing I like is `{{ __tera_context }}` which is a special variable that contains the context of the current template. 
Copilot now suggests that I write:

> It's a bit like `this` in Java, but it's not the same. It's more like `this` in JavaScript, where it's the object that the current function is called on.

Yeah, that's a nice way of putting it. Hadn't thought about that. (Wonder what text i am _actually_ quoting now.) Copilot continues: 
 
> In this case, it's the object that the current template is rendered with. So, if you have a template like this:
>
>    `{{ __tera_context }}`
>   
>
>Then it will print out the context of the current template.

And 