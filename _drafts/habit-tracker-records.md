---
layout: post
title:  "Writing a habit tracker, part 6: Yay, records!"
---

In the previous post, I wrote:

> I'd really like the `User` class to be a `record` – the new thing from Java 14, an immutable data type with accessors and default implementation of things like `hashCode` and `equals`. But I doubt Spring Data supports that. Let's try it later perhaps. 

You know what – I was wrong! It works great! My `User` is now a record:

```java
@Table(name = "users")
public record User(
    @Id
    Long id,
    String email,
    String password,
    LocalDateTime createdAt
) { }
```
I'm very happy! 

I'm pretty sure that JPA does not support this. [Spring Data JDBC](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/) explicitly does though,  see Object Creation under 9.6.1.

This looks like somethign to watch out for:
> Saving an aggregate can be performed with the CrudRepository.save(…) method. If the aggregate is new, this results in an insert for the aggregate root, followed by insert statements for all directly or indirectly referenced entities.
> 
> If the aggregate root is not new, all referenced entities get deleted, the aggregate root gets updated, and all referenced entities get inserted again. Note that whether an instance is new is part of the instance’s state.

Now I'm just going to finish of the repository layer by adding a few more tables:

