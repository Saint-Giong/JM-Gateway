package rmit.saintgiong.jmgateway.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

// Utility component for loading RSA public and private keys from PEM format.
@Component
public class RsaKeyLoader {

    @Value("${PUBLIC_KEY_B64:}")
    private String publicKeyB64Prop;

    @Value("${PRIVATE_KEY_B64:}")
    private String privateKeyB64Prop;

    // Load Public Key
    public RSAPublicKey loadPublicKey() throws Exception {
        byte[] encoded = Base64.getDecoder().decode(publicKeyB64Prop);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    // Load Private Key
    public RSAPrivateKey loadPrivateKey() throws Exception {
        byte[] encoded = Base64.getDecoder().decode(privateKeyB64Prop);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}