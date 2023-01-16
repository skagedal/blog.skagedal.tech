---
layout: post
title:  "Writing a habit tracker, part 19: A button to achieve a habit"
---
Ok, we need to make that "achieve" button actually do something, not just log something in the console. That's easy, let's just inject the `AttachmentRepository` into the `HomeController` again and do this:

```java
public class HomeController {
    // ...
    @PostMapping("/achieve/{habitId}/{date}/achieve")
    ModelAndView achieve(Principal principal, AchieveForm achieveForm) {
        achievements.save(Achievement.create(
            achieveForm.date(),
            achieveForm.habitId()
        ));
        return new ModelAndView(new RedirectView("/"));
    }
    // ...
}
```

And yes, it works! We can now achieve habits. (Still feels like there should be a better word for that.)

But there's something very wrong here. Can you spot it on your own? Maybe not, because your eyes have already scanned the next paragraph, where the answer is given away, so you didn't really get a chance. Unless you saw it at first glance of the code snippet. Of course you did. Kudos to you. 

We're just happily achieving any habit here that we get the ID of, without any concern for what is called access control or authorization.

The [Spring Security Reference Documentation](https://docs.spring.io/spring-security/reference/servlet/authorization/index.html) has a lot to say on this topic. More specifically, what we're talking about now is the [Domain Object Security](https://docs.spring.io/spring-security/reference/servlet/authorization/acls.html) – does this specific user have access to this specific domain object? 

> Imagine you are designing an application for a pet clinic. There are two main groups of users of your Spring-based application: staff of the pet clinic and the pet clinic’s customers. The staff should have access to all of the data, while your customers should be able to see only their own customer records. To make it a little more interesting, your customers can let other users see their customer records, such as their “puppy preschool” mentor or the president of their local “Pony Club”.

Honestly, I think there should be global permissions to access any resource for any Puppy Preschool Mentor or any President of the Local Pony Club, because these sound like awesome people. 

> When you use Spring Security as the foundation, you have several possible approaches:
> 
> * Write your business methods to enforce the security. You could consult a collection within the Customer domain object instance to determine which users have access. By using `SecurityContextHolder.getContext().getAuthentication()`, you can access the Authentication object.
> 
> * Write an `AccessDecisionVoter` to enforce the security from the `GrantedAuthority[]` instances stored in the `Authentication` object. This means that your `AuthenticationManager` needs to populate the `Authentication` with custom `GrantedAuthority[]` objects to represent each of the `Customer` domain object instances to which the principal has access.
> 
> Write an `AccessDecisionVoter` to enforce the security and open the target `Customer` domain object directly. This would mean your voter needs access to a DAO that lets it retrieve the `Customer` object. It can then access the `Customer` object’s collection of approved users and make the appropriate decision.

I'm a bit bored now, can we talk about the puppy preschool mentor again? The first approach sounds like the right level of abstraction for our little thingy. But how do we do it, exactly?

[This Stack Overflow answer](https://stackoverflow.com/questions/45546/how-do-i-return-a-403-forbidden-in-spring-mvc) says we can throw a `org.springframework.security.access.AccessDeniedException`. That sounds perfect. Let's do that. Let's try that it works:

```java
public class HomeController {
    // ...
    @PostMapping("/habit/{habitId}/{date}/achieve")
    ModelAndView achieve(Principal principal, AchieveForm achieveForm) {
        if (true) {
            throw new AccessDeniedException("Unknown habit");
        }
        achievements.save(Achievement.create(
            achieveForm.date(),
            achieveForm.habitId()
        ));
        return new ModelAndView(new RedirectView("/"));
    }
    // ...
}
```

Perfect! We now get a page that looks like this:

> ## Whitelabel Error Page
> 
> This application has no explicit mapping for /error, so you are seeing this as a fallback.
> 
> Sun Jan 15 13:53:32 CET 2023
> There was an unexpected error (type=Forbidden, status=403).

That's great for now. It clearly says "forbidden". I guess another way to think of this is to return a 404 Not Found, as we did not find a habit for this user with this ID. 

Now, instead of just doing `if (true)`, we do an actual check. We write the `if` statement like this:

```java
        if (!userOwnsHabitWithId(principal.getName(), achieveForm.habitId())) {
```

And implement that helper method like this:

```java
public class HomeController {
    // ...
    private boolean userOwnsHabitWithId(String userName, Long habitId) {
        return habits.findById(habitId)
            .map(habit -> Objects.equals(habit.ownedBy(), userName))
            .orElse(false);
    }
    // ...
}
```

Which seems to work well, but honestly this stuff should reeeally have some automated tests around it. I promise, later. 
