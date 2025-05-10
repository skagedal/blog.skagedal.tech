[started to write this in the 2023-01-07 post, but got bored - there is also a branch in hahabit called `relationships`]


I'm curious about this stuff that I quoted in the previous post, where it deletes and recreates entities that are referenced from the "aggregate root". When does this happen...?

So, I'm thinking that if I don't want any such behavior, I'll just continue the way I've started and make `achievement` another aggregate root with its own AchievementRepository.

But just for the fun of it, let's view the "achievements" as something that belongs to the "habit" aggregate. So, my Achievement record will look like this:

```java
@Table(name = "achievements")
public record Achievement(
    @Id Long id,
    LocalDate date
) {
    public static Achievement create(LocalDate date) {
        return new Achievement(null, date);
    }
}
```

And then each habit will have a list of achievements:

```java
@Table(name = "habits")
public record Habit(
    @Id Long id,
    Long ownedBy,
    String description,
    @ReadOnlyProperty Instant createdAt,
    List<Achievement> achievements
) {
    public static Habit create(Long ownedBy, String description) {
        return new Habit(null, ownedBy, description, null, List.of());
    }

    public Habit withAchievements(List<Achievement> newAchievements) {
        return new Habit(
            id, ownedBy, description, createdAt, newAchievements
        );
    }
}
```

What happens now when we do something like this?

```java
class HahabitApplicationTests {
    // ...
    @Test
    @Transactional
    void createHabitWithAchievements() {
        final var user = createExampleUser();
        final var habit = habitRepository.save(Habit.create(
            user.id(),
            "Be outside every day"
        ));
        final var achievedHabit = habitRepository.save(habit.withAchievements(
            List.of(Achievement.create(LocalDate.of(2023, 1, 6)))
        ));

        assertEquals(1, achievedHabit.achievements().size());
    }
}
```

Hmm, we need to also set up the mapping between the two, but I can't get that to work. Let's leave this here 