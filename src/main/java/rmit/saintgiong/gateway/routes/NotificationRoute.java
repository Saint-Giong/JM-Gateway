package rmit.saintgiong.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rmit.saintgiong.gateway.filter.JweAuthenticationFilter;

@Configuration
public class NotificationRoute {

    @Bean
    public RouteLocator setNotificationRouteConfig(RouteLocatorBuilder builder, JweAuthenticationFilter jweAuthenticationFilter) {
        return builder.routes()
                .route(
                        "notification-service-route",
                        p -> p
                                .path("/v1/noti/**")
                                .filters(
                                        spec -> spec
                                                .filter(jweAuthenticationFilter.apply(new JweAuthenticationFilter.Config()))
                                                .rewritePath(
                                                        "/v1/noti/(?<segment>.*)",
                                                        "/${segment}"
                                                )
                                )
                                .uri("lb://JM-NOTIFICATION-SERVICE")
                )
                .route(
                        "notification-socketio-ws-route",
                        p -> p
                                .path("/socket.io/**")
                                .and()
                                .header("Upgrade", "websocket")
                                .uri("ws://notification-service:9092")
                )
                .route(
                        "notification-socketio-http-route",
                        p -> p
                                .path("/socket.io/**")
                                .uri("http://notification-service:9092")
                )
                .build();
    }
}