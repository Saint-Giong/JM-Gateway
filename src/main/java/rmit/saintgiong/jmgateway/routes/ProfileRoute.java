package rmit.saintgiong.jmgateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileRoute {

    @Bean
    public RouteLocator setupProfileRouteConfig(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(
                        p -> p
                                .path("/v1/profile/**")
                                .filters(
                                        spec -> spec
                                                .rewritePath(
                                                        "/v1/profile/(?<segment>.*)",
                                                        "/${segment}"
                                                )
                                )
                                .uri("lb://PROFILE-SERVICE")
                )
                .build();
    }
}
