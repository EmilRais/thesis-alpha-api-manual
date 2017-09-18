package dk.developer.alpha.api.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.developer.alpha.api.FacebookTest;
import dk.developer.alpha.api.user.*;
import dk.developer.database.DatabaseFront;
import dk.developer.alpha.api.Facebook;
import dk.developer.testing.Result;
import dk.developer.utility.Converter;
import org.bson.types.ObjectId;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.truth.Truth.ASSERT;
import static dk.developer.testing.RestServiceTestHelper.from;
import static dk.developer.testing.RestServiceTestHelper.to;
import static dk.developer.testing.TestDatabaseProvider.persistedTestDatabase;
import static dk.developer.utility.Converter.converter;
import static javax.ws.rs.core.Response.Status.*;

public class UserServiceAcceptanceTest {
    private static final Converter converter = converter();

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldFailWhenTryingToCreateExistingUser() throws Exception {
        persistedTestDatabase();

        AlphaApiCredential credential = new AlphaApiCredential("info@alpha-api.dk", "password");
        User user = new User(ObjectId.get().toString(), "info@alpha-api.dk", credential);
        String json = converter.toJson(user);

        to(UserService.class).with(json).post("/user/create/alpha-api");
        to(UserService.class).with(json).post("/user/create/alpha-api");
    }

    @Test
    public void shouldCreateAlphaApiUser() throws Exception {
        DatabaseFront database = persistedTestDatabase();
        AlphaApiCredential credential = new AlphaApiCredential("info@alpha-api.dk", "password");
        User user = new User("info@alpha-api.dk", credential);
        String json = converter.toJson(user);
        Result result = to(UserService.class).with(json).post("/user/create/alpha-api");

        ASSERT.that(result.status()).isSameAs(CREATED);

        String resultJson = result.content();
        User createdUser = converter.fromJson(resultJson, User.class);
        ASSERT.that(createdUser).isEqualTo(user);

        User receivedUser = database.load(User.class).matching("email").with("info@alpha-api.dk");
        ASSERT.that(receivedUser).isEqualTo(user);
    }

    @Test(enabled = false)
    public void shouldCreateFacebookUser() throws Exception {
        DatabaseFront database = persistedTestDatabase();

        Facebook facebook = new Facebook();
        String token = FacebookTest.getTestUserToken();
        InspectedFacebookToken inspectedToken = facebook.inspectAccessToken(token);

        FacebookCredential credential = new FacebookCredential(inspectedToken.getUserId(), token);
        User user = new User(inspectedToken.getUserId(), "info@alpha-api.dk", credential);

        String json = converter.toJson(user);
        Result result = to(UserService.class).with(json).post("/user/create/facebook");

        ASSERT.that(result.status()).isSameAs(CREATED);

        String resultJson = result.content();
        User createdUser = converter.fromJson(resultJson, User.class);
        ASSERT.that(createdUser).isEqualTo(user);

        User receivedUser = database.load(User.class).matching("email").with("info@alpha-api.dk");
        ASSERT.that(receivedUser).isEqualTo(user);
    }

    @Test(enabled = false)
    public void shouldNotCreateFacebookUserWithWrongToken() throws Exception {
        Facebook facebook = new Facebook();
        String token = FacebookTest.getTestUserToken();
        InspectedFacebookToken inspectedToken = facebook.inspectAccessToken(token);

        FacebookCredential credential = new FacebookCredential(inspectedToken.getUserId(), token + "wrong");
        User user = new User(inspectedToken.getUserId(), "info@alpha-api.dk", credential);

        String json = converter.toJson(user);
        Result result = to(UserService.class).with(json).post("/user/create/facebook");

        ASSERT.that(result.status()).isSameAs(BAD_REQUEST);
    }

    @Test
    public void shouldGetZeroUsers() throws Exception {
        List<User> users = getHelper();
        ASSERT.that(users).isEmpty();
    }

    @Test
    public void shouldGetOneUser() throws Exception {
        AlphaApiCredential credential = new AlphaApiCredential("email", "password");
        User user = new User("email", credential);
        List<User> users = getHelper(user);
        ASSERT.that(users).containsExactly(user);
    }

    @Test
    public void shouldGetSeveralUsers() throws Exception {
        AlphaApiCredential credential = new AlphaApiCredential("email@email.dk", "password");
        User oneUser = new User("email@email.dk", credential);
        User anotherUser = new User("email@email.com", credential);

        List<User> users = getHelper(oneUser, anotherUser);
        ASSERT.that(users).containsExactly(oneUser, anotherUser);
    }

    @Test
    public void shouldDenyWrongEmail() throws Exception {
        DatabaseFront database = persistedTestDatabase();
        User user = new User("email", new AlphaApiCredential("email", "password"));
        database.save(user);

        AlphaApiCredential credential = new AlphaApiCredential("wrongEmail", "password");
        String loginJson = converter.toJson(credential);
        Result result = to(UserService.class).with(loginJson).post("/user/login/alpha-api");

        ASSERT.that(result.status()).isSameAs(UNAUTHORIZED);

        String resultJson = result.content();
        ASSERT.that(resultJson).isEqualTo("Der findes ingen bruger med email: wrongEmail");
    }

    @Test
    public void shouldDenyWrongPassword() throws Exception {
        DatabaseFront database = persistedTestDatabase();
        User user = new User("email", new AlphaApiCredential("email", "password"));
        database.save(user);

        AlphaApiCredential credential = new AlphaApiCredential("email", "wrongPassword");
        String json = converter.toJson(credential);
        Result result = to(UserService.class).with(json).post("/user/login/alpha-api");

        ASSERT.that(result.status()).isSameAs(UNAUTHORIZED);

        String resultJson = result.content();
        ASSERT.that(resultJson).isEqualTo("Forkert kodeord");
    }

    @Test
    public void shouldAcceptCorrectLogin() throws Exception {
        DatabaseFront database = persistedTestDatabase();
        User user = new User("email", new AlphaApiCredential("email", "password"));
        database.save(user);

        AlphaApiCredential credential = new AlphaApiCredential("email", "password");
        String loginJson = converter.toJson(credential);
        Result result = to(UserService.class).with(loginJson).post("/user/login/alpha-api");
        ASSERT.that(result.status()).isSameAs(OK);

        String resultJson = result.content();
        User loggedInUser = converter.fromJson(resultJson, User.class);
        ASSERT.that(loggedInUser).isEqualTo(user);
    }

    @Test(enabled = false)
    public void shouldDenyWrongAccessToken() throws Exception {
        String token = FacebookTest.getTestUserToken();
        String wrongToken = token + "wrong";

        FacebookCredential credential = new FacebookCredential("userId", wrongToken);
        String json = converter.toJson(credential);

        Result result = to(UserService.class).with(json).post("/user/login/facebook");

        ASSERT.that(result.status()).isSameAs(UNAUTHORIZED);
    }

    @Test(enabled = false)
    public void shouldAcceptNewFacebookUser() throws Exception {
        Facebook facebook = new Facebook();
        String token = FacebookTest.getTestUserToken();
        InspectedFacebookToken inspectedToken = facebook.inspectAccessToken(token);

        FacebookCredential credential = new FacebookCredential(inspectedToken.getUserId(), token);
        String json = converter.toJson(credential);

        Result result = to(UserService.class).with(json).post("/user/login/facebook");

        ASSERT.that(result.status()).isSameAs(NOT_ACCEPTABLE);
    }

    @Test(enabled = false)
    public void shouldLoginExistingFacebookUser() throws Exception {
        DatabaseFront database = persistedTestDatabase();
        Facebook facebook = new Facebook();
        String token = FacebookTest.getTestUserToken();
        InspectedFacebookToken inspectedToken = facebook.inspectAccessToken(token);

        String userId = inspectedToken.getUserId();
        FacebookCredential credential = new FacebookCredential(userId, token);
        User user = new User(userId, "email@email.dk", credential);

        database.save(user);

        String json = converter.toJson(credential);
        Result result = to(UserService.class).with(json).post("/user/login/facebook");

        ASSERT.that(result.status()).isSameAs(OK);

        User receivedUser = converter.fromJson(result.content(), User.class);

        ASSERT.that(receivedUser).isEqualTo(user);
    }

    private List<User> getHelper(User... users) throws java.io.IOException {
        DatabaseFront database = persistedTestDatabase();

        for (User user : users) {
            database.save(user);
        }

        Result result = from(UserService.class).get("/user/get");
        ASSERT.that(result.status()).isSameAs(OK);

        return converter.fromJson(result.content(), new TypeReference<List<User>>() {});
    }
}