package pro.play.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "BearerAuth";

    @Bean
    public OpenAPI playBuddyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PlayBuddy API")
                        .version("1.0.0")
                        .description(
                                "REST API for PlayBuddy – a sports-venue booking platform. " +
                                        "Use the **Authorize** button to paste a JWT token obtained from " +
                                        "`POST /api/auth/login/email` or `POST /api/auth/verify-otp`.")
                        .contact(new Contact()
                                .name("PlayBuddy Team")
                                .email("team@playpro.in")))
                // Apply BearerAuth globally so every endpoint shows the lock icon
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter the JWT token (without the 'Bearer ' prefix)")));
    }
}
