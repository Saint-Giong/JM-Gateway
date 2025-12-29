package rmit.saintgiong.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rmit.saintgiong.gateway.filter.JweAuthenticationFilter;

@Configuration
public class MediaRoute {

    @Bean
    public RouteLocator setupMediaRouteConfig(RouteLocatorBuilder builder, JweAuthenticationFilter jweAuthenticationFilter) {
        return builder.routes()
                .route(
                        "media-service-route",
                        p -> p
                                .path("/v1/media/**")
                                .filters(
                                        spec -> spec
                                                .filter(jweAuthenticationFilter.apply(new JweAuthenticationFilter.Config()))
                                                .rewritePath(
                                                        "/v1/media/(?<segment>.*)",
                                                        "/${segment}"
                                                )
                                )
                                .uri("lb://MEDIA-SERVICE")
                )
                .build();
    }
}
