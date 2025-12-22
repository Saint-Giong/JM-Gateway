package rmit.saintgiong.jmgateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static rmit.saintgiong.jmgateway.common.type.ServicePrefix.AUTH_PREFIX;
import static rmit.saintgiong.jmgateway.common.type.ServicePrefix.PROFILE_PREFIX;

@Component
public class RouteValidator {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public static final List<String> AUTH_PUBLIC_ENDPOINTS =
            Stream.of(
                            "/google/**",
                            "/register",
                            "/login",
                            "/dashboard"
                    )
                    .map(AUTH_PREFIX::concat)
                    .toList();

    public static final List<String> GLOBAL_PUBLIC_ENDPOINTS = List.of(
            "/v1/*/actuator/**",
            "/actuator/**",
            "/eureka/**"
    );

    private static final List<String> PUBLIC_ENDPOINTS =
            Stream.of(
                            GLOBAL_PUBLIC_ENDPOINTS,
                            AUTH_PUBLIC_ENDPOINTS
                    )
                    .flatMap(List::stream)
                    .toList();


    public Predicate<ServerHttpRequest> isProtected =
            serverHttpRequest -> {
                String path = serverHttpRequest.getURI().getPath();

                return PUBLIC_ENDPOINTS
                        .stream()
                        .noneMatch(pattern -> pathMatcher.match(pattern, path));
            };

}
