package dk.developer.alpha.api.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@Type(value = AlphaApiCredential.class, name = "alpha-api"), @Type(value = FacebookCredential.class, name = "facebook")})
public interface Credential {
}
