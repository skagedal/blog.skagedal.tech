Here's an example.

```java
    given()
        .cookie(clinicianCookie(correctClinician))
        .get("/entry/" + entryId)
        .then()
        .statusCode(200);

    given().cookie(clinicianCookie(wrongClinician)).get("/entry/" + entryId).then().statusCode(403);
```

I want the latter case to look like the above one. But Google Java Format does not.

