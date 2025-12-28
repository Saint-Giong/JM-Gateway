package rmit.saintgiong.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rmit.saintgiong.gateway.filter.JweAuthenticationFilter;

@Configuration
public class JobPostRoute {

    @Bean
    public RouteLocator setJobPostRouteConfig(RouteLocatorBuilder builder, JweAuthenticationFilter jweAuthenticationFilter) {
        return builder.routes()
                .route(
                        "jobpost-service-route",
                        p -> p
                                .path("/v1/jobpost/**")
                                .filters(
                                        spec -> spec
                                                .filter(jweAuthenticationFilter.apply(new JweAuthenticationFilter.Config()))
                                                .rewritePath(
                                                        "/v1/jobpost/(?<segment>.*)",
                                                        "/${segment}"
                                                )
                                )
                                .uri("lb://JOBPOST-SERVICE")
                )
                .build();
    }
}
