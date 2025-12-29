package rmit.saintgiong.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rmit.saintgiong.gateway.filter.JweAuthenticationFilter;

@Configuration
public class TagRoute {

    @Bean
    public RouteLocator setupTagRouteConfig(RouteLocatorBuilder builder, JweAuthenticationFilter jweAuthenticationFilter) {
        return builder.routes()
                .route(
                        "tag-service-route",
                        p -> p
                                .path("/v1/tag/**")
                                .filters(
                                        spec -> spec
                                                .filter(jweAuthenticationFilter.apply(new JweAuthenticationFilter.Config()))
                                                .rewritePath(
                                                        "/v1/tag/(?<segment>.*)",
                                                        "/${segment}"
                                                )
                                )
                                .uri("lb://TAG-SERVICE")
                )
                .build();
    }
}
