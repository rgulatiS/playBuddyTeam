Playbuddy — Sports Court Booking (v1)

Overview
- Backend: Java Spring Boot (configured for Java 21 / Spring Boot 3)
- Database: PostgreSQL
- Frontend: React Native (mobile app) — not included here
- Payment: Stripe (sandbox) — stubbed service included
- Authentication: JWT (OTP/email-password planned)
- Roles: USER, VENUE_OWNER, ADMIN

Project layout (important folders)
- src/main/java/pro/play — application sources
  - auth, user, venue, court, booking, payment, security, etc.
- src/main/resources — properties
- pom.xml — Maven build

What I added/updated
- Project pom.xml upgraded to Spring Boot 3.5.6 and Java 21 (release 21)
- Basic JWT utility, filter, and security config
- Module skeletons: auth, user, venue, court, booking, payment with DTOs, repos, services, controllers
- Stubbed PaymentService implementation
- Dev properties file (application-dev.properties) with placeholders

Quick dev setup (Windows / cmd.exe)
1) Install JDK 21
   - Adoptium Temurin: https://adoptium.net/temurin/releases/?version=21
   - After install, set JAVA_HOME and update PATH (open a new cmd shell after setx):

   ```cmd
   setx JAVA_HOME "C:\Program Files\Temurin\jdk-21" /M
   setx PATH "%JAVA_HOME%\bin;%PATH%" /M
   ```

   Verify:
   ```cmd
   java -version
   mvn -version
   ```

2) Postgres (dev)
   - Create a database and user, e.g.:
     - DB: playbuddy
     - USER: playbuddy
     - PWD: <your_password>
   - Or use Docker:
   ```cmd
   docker run --name playbuddy-db -e POSTGRES_USER=playbuddy -e POSTGRES_PASSWORD=change_me -e POSTGRES_DB=playbuddy -p 5432:5432 -d postgres:15
   ```

3) Configure dev properties
   - Update `src/main/resources/application-dev.properties` or set environment variables:
     - SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/playbuddy
     - SPRING_DATASOURCE_USERNAME=playbuddy
     - SPRING_DATASOURCE_PASSWORD=change_me
     - APP_JWT_SECRET=replace-with-a-long-secret
     - STRIPE_API_KEY=sk_test_xxx

4) Build and run
   - Build (skip tests for faster iteration):
     ```cmd
     mvnw.cmd -DskipTests package
     ```
   - Run app:
     ```cmd
     mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
     ```

Notes and troubleshooting
- The project is configured for Java 21 / Spring Boot 3. If your environment's Java is older, `mvnw package` will fail with "release version 21 not supported". Install JDK 21 and ensure `mvn -version` shows Java 21.
- JWT secret: use a strong random string. In production, store secrets in a secure vault or environment variables.
- Stripe: PaymentService is currently a stub. When ready, integrate Stripe Java SDK and exchange the sandbox payment id for real flows.

Next steps I can take for you
- Add unit/integration tests for core services.
- Implement availability and pricing logic.
- Integrate Stripe sandbox payment flow.
- Add OTP (SMS) flow for mobile authentication.

If you'd like, I can also revert to Java 11 / Spring Boot 2.7 temporarily so I can get a green build in this environment; otherwise install JDK 21 locally and run the build steps above.

