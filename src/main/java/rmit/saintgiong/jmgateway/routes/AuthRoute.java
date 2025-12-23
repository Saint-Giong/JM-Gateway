package rmit.saintgiong.jmgateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rmit.saintgiong.jmgateway.filter.JweAuthenticationFilter;

@Configuration
public class AuthRoute {

    @Bean
    public RouteLocator setupAuthRouteConfig(RouteLocatorBuilder builder, JweAuthenticationFilter jweAuthenticationFilter) {
        return builder.routes()
                .route(
                        "auth-service-route",
                        p -> p
                                .path("/v1/auth/**")
                                .filters(
                                        spec -> spec
                                                .filter(jweAuthenticationFilter.apply(new JweAuthenticationFilter.Config()))
                                                .rewritePath(
                                                        "/v1/auth/(?<segment>.*)",
                                                        "/${segment}"
                                                )
                                )
                                .uri("lb://AUTH-SERVICE")
                )
                .build();
    }
}
