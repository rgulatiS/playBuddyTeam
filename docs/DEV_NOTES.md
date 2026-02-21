Dev notes

- Project targets Java 21 and Spring Boot 3.5.6.
- Ensure JDK 21 is installed and active before building.
- Use the provided `application-dev.properties` or set environment variables per `.env.example`.

Common commands (Windows cmd.exe):

Check Java/Maven:

```
java -version
mvn -version
```

Build:

```
mvnw.cmd -DskipTests package
```

Run:

```
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

# Development properties for Playbuddy
spring.datasource.url=jdbc:postgresql://localhost:5432/playbuddy
spring.datasource.username=playbuddy
spring.datasource.password=change_me
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
app.jwt.secret=change-me-replace-with-long-random-string
app.jwt.expiration-ms=86400000

# Stripe (sandbox)
stripe.api.key=sk_test_replace_me
stripe.webhook.secret=whsec_replace_me

# Profile
spring.profiles.active=dev

