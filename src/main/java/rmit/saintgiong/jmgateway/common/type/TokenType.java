package rmit.saintgiong.jmgateway.common.type;

// Enum representing the type of JWT/JWE token.
// Used to differentiate between access and refresh tokens.
public enum TokenType {
    ACCESS,
    REFRESH,
    TEMP
}
