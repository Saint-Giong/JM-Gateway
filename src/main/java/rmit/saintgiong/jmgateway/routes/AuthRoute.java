package rmit.saintgiong.jmgateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthRoute {

    @Bean
    public RouteLocator setupAuthRouteConfig(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(
                        p -> p
                                .path("/company/auth-service/**")
                                .filters(
                                        spec -> spec
                                                .rewritePath(
                                                        "/company/auth-service/(?<segment>.*)",
                                                        "/${segment}"
                                                )
                                )
                                .uri("lb://AUTH-SERVICE")
                )
                .build();
    }
}
