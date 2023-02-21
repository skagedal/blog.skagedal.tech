---
layout: post
title: "Continuing adding API:s"
---
So now let's just implement that "create habit" API that we added a failing test for last post.

Introducing a little `HabitsApiController`:

```java
@RestController
public class HabitsApiController {
    private final HabitRepository habits;

    public HabitsApiController(HabitRepository habits) {
        this.habits = habits;
    }

    @PostMapping("/api/habits")
    void addHabit(@RequestBody HabitCreateRequest request, Principal principal) {
        habits.save(Habit.create(
            principal.getName(),
            request.description()
        ));
    }

    private record HabitCreateRequest(String description) {}
}
```

I considered making the API call return the created habit object. It could be useful perhaps to get the ID of the thing you just created. But I also think it's a good thing to keep things simple until you need them. Also, should it perhaps return a `201 Created`? 

----

Regarding HttpClient /  BodySubscriber etc https://stackoverflow.com/questions/57629401/deserializing-json-using-java-11-httpclient-and-custom-bodyhandler-with-jackson