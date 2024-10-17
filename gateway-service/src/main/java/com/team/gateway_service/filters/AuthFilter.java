package com.team.gateway_service.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.gateway_service.dtos.ValidateTokenResponseDto;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("Missing Authorization header");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).getFirst();

            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                throw new RuntimeException("Invalid Authorization header");
            }

            String url = exchange.getRequest().getURI().getPath();

            return webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8084/authentication-service/auth/validate-token?token=" + parts[1])
                    .retrieve()
                    .bodyToMono(ValidateTokenResponseDto.class)
                    .flatMap(res -> {
                        System.out.println(res);

                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            String jsonHeaders = objectMapper.writeValueAsString(res.getHeaders());
                            ServerHttpRequest mutatedRequest = exchange.getRequest()
                                    .mutate()
                                    .header("x-auth-user-email", jsonHeaders).build();

                            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                            return chain.filter(mutatedExchange);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
        };
    }

    public static class Config {
    }
}
