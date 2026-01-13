package rmit.saintgiong.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rmit.saintgiong.gateway.filter.JweAuthenticationFilter;

@Configuration
public class PaymentRoute {

        @Bean
        public RouteLocator setPaymentRouteConfig(RouteLocatorBuilder builder,
                        JweAuthenticationFilter jweAuthenticationFilter) {
                return builder.routes()
                                // Stripe webhook route - NO authentication, preserves headers
                                .route(
                                                "stripe-webhook-route",
                                                p -> p
                                                                .path("/stripe/webhook")
                                                                .filters(
                                                                                spec -> spec
                                                                                                .preserveHostHeader())
                                                                .uri("lb://PAYMENT-SERVICE"))
                                // Regular payment routes - WITH authentication
                                .route(
                                                "payment-service-route",
                                                p -> p
                                                                .path("/v1/payment/**")
                                                                .filters(
                                                                                spec -> spec
                                                                                                .filter(jweAuthenticationFilter
                                                                                                                .apply(new JweAuthenticationFilter.Config()))
                                                                                                .rewritePath(
                                                                                                                "/v1/payment/(?<segment>.*)",
                                                                                                                "/${segment}"))
                                                                .uri("lb://PAYMENT-SERVICE"))
                                .build();
        }
}