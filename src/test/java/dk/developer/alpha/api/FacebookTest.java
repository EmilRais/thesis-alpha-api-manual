package dk.developer.alpha.api;

import dk.developer.alpha.api.user.InspectedFacebookToken;
import dk.developer.alpha.api.user.LongLivedToken;
import dk.developer.utility.Converter;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dk.developer.alpha.api.Facebook.APP_ID;
import static dk.developer.alpha.api.Facebook.APP_SECRET;
import static dk.developer.utility.Converter.converter;
import static org.truth0.Truth.ASSERT;

public class FacebookTest {
    private static final Converter converter = converter();
    private static final Facebook facebook = new Facebook();

    @Test(enabled = false)
    public void shouldDenyWrongAccessToken() throws Exception {
        String token = getTestUserToken();
        String wrongToken = token + "wrong";

        InspectedFacebookToken userToken = facebook.inspectAccessToken(wrongToken);

        ASSERT.that(userToken.isValid()).isFalse();
    }

    @Test(enabled = false)
    public void shouldAcceptCorrectAccessToken() throws Exception {
        String token = getTestUserToken();

        InspectedFacebookToken userToken = facebook.inspectAccessToken(token);
        ASSERT.that(userToken.isValid()).isTrue();
    }

    @Test(enabled = false)
    public void shouldExchangeToken() throws Exception {
        String token = getTestUserToken();
        LongLivedToken longLivedToken = facebook.extendShortLivedToken(token);
        ASSERT.that(longLivedToken).isNotNull();
        ASSERT.that(longLivedToken.getToken()).isNotNull();
        ASSERT.that(longLivedToken.getExpires()).isNotNull();
    }

    public static String getTestUserToken() {
        Client client = ClientBuilder.newClient();
        Response response = client.target("https://graph.facebook.com/" + APP_ID + "/accounts/test-users")
                .queryParam("access_token", APP_ID + "|" + APP_SECRET).request().get();

        String json = response.readEntity(String.class);
        return unPackToken(json);
    }

    private static String unPackToken(String json) {
        Map<String, Map> map = converter.fromJson(json, HashMap.class);
        List<Map<String, String>> list = (List) map.get("data");
        Map<String, String> dataMap = list.get(0);
        return dataMap.get("access_token");
    }
}