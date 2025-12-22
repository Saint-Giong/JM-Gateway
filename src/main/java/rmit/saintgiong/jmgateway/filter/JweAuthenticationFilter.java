package rmit.saintgiong.jmgateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import rmit.saintgiong.jmgateway.common.dto.TokenClaimsDto;
import rmit.saintgiong.jmgateway.common.utils.JweUtils;


@Component
@Slf4j
public class JweAuthenticationFilter extends AbstractGatewayFilterFactory<JweAuthenticationFilter.Config> {

    private final RouteValidator routeValidator;
    private final JweUtils jweUtils;

    public JweAuthenticationFilter(RouteValidator routeValidator, JweUtils jweUtils) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.jweUtils = jweUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!routeValidator.isProtected.test(request)) {
                return chain.filter(exchange);
            }

            HttpCookie authCookie = request.getCookies().getFirst("auth_token");

            if (authCookie == null) {
                return onError(exchange, "Missing Authorization Cookie", HttpStatus.UNAUTHORIZED);
            }

            String token = authCookie.getValue();
            TokenClaimsDto claimDto = jweUtils.buildTokenClaimsDto(token);

            try {
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", String.valueOf(claimDto.getSub()))
                        .header("X-User-Role", String.valueOf(claimDto.getRole()))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return onError(exchange, "Error parsing claims and passing downstream", HttpStatus.UNAUTHORIZED);
            }

        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus code) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(code);
        return response.setComplete();
    }

    public static class Config {
    }
}
