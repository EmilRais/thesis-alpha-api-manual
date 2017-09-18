package dk.developer.alpha.api.user;

import com.fasterxml.jackson.annotation.*;
import dk.developer.alpha.api.Facebook;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonTypeName(value = "data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InspectedFacebookToken {
    private final long appId;
    private final long expiresAt;
    private final boolean isValid;
    private final String userId;

    @JsonCreator
    public InspectedFacebookToken(@JsonProperty("app_id") long appId,
                                  @JsonProperty("expires_at") long expiresAt,
                                  @JsonProperty("is_valid") boolean isValid,
                                  @JsonProperty("user_id") String userId) {
        this.appId = appId;
        this.expiresAt = expiresAt;
        this.isValid = isValid;
        this.userId = userId;
    }

    public long getAppId() {
        return appId;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isValid() {
        return isValid &&
                appId == Facebook.APP_ID &&
                expiresAt >= currentTimeInSeconds();
    }

    private long currentTimeInSeconds() {
        return new Date().getTime()/1000;
    }
}
