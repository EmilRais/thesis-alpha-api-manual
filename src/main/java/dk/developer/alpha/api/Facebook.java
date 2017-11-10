package dk.developer.alpha.api;

import dk.developer.alpha.api.user.InspectedFacebookToken;
import dk.developer.alpha.api.user.LongLivedToken;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static dk.developer.utility.Converter.converter;

public class Facebook {
    public static final long APP_ID = 1092068880930122L;
    public static final String APP_SECRET = "470f440e050eb59788e7178c86ca982f";
    private final Client client = ClientBuilder.newClient();

    public InspectedFacebookToken inspectAccessToken(String token) {
        Response response = client.target("https://graph.facebook.com/debug_token")
                .queryParam("input_token", token)
                .queryParam("access_token", APP_ID + "|" + APP_SECRET).request().get();

        String json = response.readEntity(String.class);
        response.close();

        InspectedFacebookToken userToken = converter().fromJson(json, InspectedFacebookToken.class);

        return userToken;
    }

    public LongLivedToken extendShortLivedToken(String token) {
        Response response = client.target("https://graph.facebook.com/oauth/access_token?")
                .queryParam("grant_type", "fb_exchange_token")
                .queryParam("client_id", APP_ID)
                .queryParam("client_secret", APP_SECRET)
                .queryParam("fb_exchange_token", token).request().get();

        String result = response.readEntity(String.class);
        response.close();

        return buildLongLivedToken(result);
    }

    private LongLivedToken buildLongLivedToken(String result) {
        int accessTokenBegin = result.indexOf("access_token=") + "access_token=".length();
        int accessTokenEnd = result.indexOf("&");
        String accessToken = result.substring(accessTokenBegin, accessTokenEnd);

        int expiresBegin = result.indexOf("expires=") + "expires=".length();
        int expiresEnd = result.length();
        String expiresString = result.substring(expiresBegin, expiresEnd);
        long expires = Long.parseLong(expiresString);

        return new LongLivedToken(accessToken, expires);
    }

    public boolean validToken(InspectedFacebookToken token, String userId) {
        return token.isValid() && token.getUserId().equals(userId);
    }
}
