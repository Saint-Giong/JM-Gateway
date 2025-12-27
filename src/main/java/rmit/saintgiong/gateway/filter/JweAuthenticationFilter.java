package rmit.saintgiong.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import rmit.saintgiong.shared.type.CookieType;


@Component
@Slf4j
public class JweAuthenticationFilter extends AbstractGatewayFilterFactory<JweAuthenticationFilter.Config> {

    public JweAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpRequest.Builder requestBuilder = request.mutate();

            HttpCookie tempCookie = request.getCookies().getFirst(CookieType.TEMP_TOKEN);
            HttpCookie accessCookie = request.getCookies().getFirst(CookieType.ACCESS_TOKEN);
            HttpCookie refreshCookie = request.getCookies().getFirst(CookieType.REFRESH_TOKEN);

            // Has TempToken
            if (tempCookie != null) {
                requestBuilder.header("X-Temp-Token", tempCookie.getValue());
            }

            // Has RefreshToken (without AccessToken)
            if (refreshCookie != null) {
                requestBuilder.header("X-Refresh-Token", refreshCookie.getValue());
            }

            // Has RefreshToken (with AccessToken)
            if (accessCookie != null) {
                requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessCookie.getValue());
            }

            ServerHttpRequest updatedRequest = requestBuilder.build();
            return chain.filter(exchange.mutate().request(updatedRequest).build());
        };
    }

    public static class Config {
    }
}
