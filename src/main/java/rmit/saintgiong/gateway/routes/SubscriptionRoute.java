package rmit.saintgiong.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rmit.saintgiong.gateway.filter.JweAuthenticationFilter;

@Configuration
public class SubscriptionRoute {

    @Bean
    public RouteLocator setSubscriptionRouteConfig(RouteLocatorBuilder builder, JweAuthenticationFilter jweAuthenticationFilter) {
        return builder.routes()
                .route(
                        "subscription-service-route",
                        p -> p
                                .path("/v1/subscription/**")
                                .filters(
                                        spec -> spec
                                                .filter(jweAuthenticationFilter.apply(new JweAuthenticationFilter.Config()))
                                                .rewritePath(
                                                        "/v1/subscription/(?<segment>.*)",
                                                        "/${segment}"
                                                )
                                )
                                .uri("lb://SUBSCRIPTION-SERVICE")
                )
                .build();
    }
}