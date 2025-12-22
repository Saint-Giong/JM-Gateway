package rmit.saintgiong.jmgateway.common.utils;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rmit.saintgiong.jmgateway.common.dto.TokenClaimsDto;
import rmit.saintgiong.jmgateway.common.exception.InvalidTokenException;
import rmit.saintgiong.jmgateway.common.exception.TokenExpiredException;
import rmit.saintgiong.jmgateway.common.type.Issuer;
import rmit.saintgiong.jmgateway.common.type.Role;
import rmit.saintgiong.jmgateway.common.type.TokenType;

import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class JweUtils {

    private RsaKeyLoader keyLoader;
    private RSAPrivateKey privateKey;

    private List<Role> roles;
    private List<Issuer> issList;

    public JweUtils(RsaKeyLoader keyLoader) {
        this.keyLoader = keyLoader;
        this.roles = Arrays.asList(Role.values());
        this.issList = Arrays.asList(Issuer.values());
    }

    @PostConstruct
    public void init() throws Exception {
        this.privateKey = keyLoader.loadPrivateKey();
        log.info("Private Key is loaded successfully.");
    }

    public TokenClaimsDto buildTokenClaimsDto(String jweString) {
        try {
            JWEObject jweObject = validateAndGetDecryptedJweObject(jweString, null);
            Map<String, Object> tokenPayload = jweObject.getPayload().toJSONObject();

            Number iat = (Number) jweObject.getHeader().getCustomParam("iat");
            Number exp = (Number) jweObject.getHeader().getCustomParam("exp");
            String iss = jweObject.getHeader().getIssuer();

            return TokenClaimsDto.builder()
                    .sub(UUID.fromString((String) tokenPayload.get("sub")))
                    .email((String) tokenPayload.get("email"))
                    .role(Role.valueOf((String) tokenPayload.get("role")))
                    .type(TokenType.valueOf((String) tokenPayload.get("type")))
                    .jti((String) tokenPayload.get("jti"))
                    .iat(iat != null ? iat.longValue() : 0)
                    .exp(exp != null ? exp.longValue() : 0)
                    .iss(iss)
                    .build();

        } catch (TokenExpiredException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token validation failed", e);
            throw new InvalidTokenException("Invalid or malformed token");
        }
    }

    private JWEObject validateAndGetDecryptedJweObject(String jweString, TokenType type)
            throws JOSEException, ParseException {
        JWEObject jweObject = JWEObject.parse(jweString);
        jweObject.decrypt(new RSADecrypter(privateKey));
        Map<String, Object> tokenPayload = jweObject.getPayload().toJSONObject();

        Number exp = (Number) jweObject.getHeader().getCustomParam("exp");
        if (exp != null) {
            long now = Instant.now().getEpochSecond();
            if (now > exp.longValue()) {
                throw new TokenExpiredException("Token has expired");
            }
        }

        String tokenIss = jweObject.getHeader().getIssuer();
        if (tokenIss == null || issList.stream().noneMatch(r -> r.name().equals(tokenIss))) {
            throw new InvalidTokenException("Invalid token issuer");
        }

        String tokenRole = tokenPayload.get("role") != null ? tokenPayload.get("role").toString() : null;
        if (tokenRole == null || roles.stream().noneMatch(r -> r.name().equals(tokenRole))) {
            throw new InvalidTokenException(String.format("Invalid token role: %s, expected COMPANY", tokenRole));
        }

        if (type != null && type.equals(TokenType.TEMP)) {
            Object typeObj = tokenPayload.get("type");
            String typeStr = typeObj != null ? typeObj.toString() : null;
            if (typeStr == null || !typeStr.equalsIgnoreCase(TokenType.TEMP.name())) {
                throw new InvalidTokenException("Invalid token type: expected TEMP");
            }
        }

        return jweObject;
    }
}
