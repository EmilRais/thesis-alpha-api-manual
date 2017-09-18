package dk.developer.alpha.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class LongLivedToken {
    private final String token;
    private final long expires;

    @JsonCreator
    public LongLivedToken(@JsonProperty("access_token") String token,
                          @JsonProperty("expires") long expires) {
        this.token = token;
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public long getExpires() {
        return expires;
    }

    @Override
    public String toString() {
        return "LongLivedToken{" +
                "token='" + token + '\'' +
                ", expires=" + expires +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongLivedToken that = (LongLivedToken) o;
        return Objects.equals(expires, that.expires) &&
                Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, expires);
    }
}
