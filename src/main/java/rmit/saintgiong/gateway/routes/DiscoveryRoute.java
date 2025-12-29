package rmit.saintgiong.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rmit.saintgiong.gateway.filter.JweAuthenticationFilter;

@Configuration
public class DiscoveryRoute {

    @Bean
    public RouteLocator setDiscoveryRouteConfig(RouteLocatorBuilder builder, JweAuthenticationFilter jweAuthenticationFilter) {
        return builder.routes()
                .route(
                        "discovery-service-route",
                        p -> p
                                .path("/v1/discovery/**")
                                .filters(
                                        spec -> spec
                                                .filter(jweAuthenticationFilter.apply(new JweAuthenticationFilter.Config()))
                                                .rewritePath(
                                                        "/v1/discovery/(?<segment>.*)",
                                                        "/${segment}"
                                                )
                                )
                                .uri("lb://DISCOVERY-SERVICE")
                )
                .build();
    }
}