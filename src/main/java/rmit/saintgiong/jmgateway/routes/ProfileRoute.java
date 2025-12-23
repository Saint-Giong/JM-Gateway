package rmit.saintgiong.jmgateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rmit.saintgiong.jmgateway.filter.JweAuthenticationFilter;

@Configuration
public class ProfileRoute {

    @Bean
    public RouteLocator setupProfileRouteConfig(RouteLocatorBuilder builder, JweAuthenticationFilter jweAuthenticationFilter) {
        return builder.routes()
                .route(
                        "profile-service-route",
                        p -> p
                                .path("/v1/profile/**")

                                .filters(
                                        spec -> spec
                                                .filter(jweAuthenticationFilter.apply(new JweAuthenticationFilter.Config()))
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
