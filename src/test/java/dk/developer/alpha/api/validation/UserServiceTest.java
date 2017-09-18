package dk.developer.alpha.api.validation;

import dk.developer.alpha.api.user.AlphaApiCredential;
import dk.developer.alpha.api.user.User;
import dk.developer.alpha.api.user.FacebookCredential;
import dk.developer.alpha.api.user.UserService;
import dk.developer.testing.Result;
import dk.developer.testing.TestDatabaseProvider;
import dk.developer.utility.Converter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.google.common.truth.Truth.ASSERT;
import static dk.developer.testing.RestServiceTestHelper.to;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class UserServiceTest {
    private static final Converter converter = Converter.converter();

    @BeforeMethod
    public void setUp() throws Exception {
        TestDatabaseProvider.memoryTestDatabase();
    }

    @Test
    public void shouldFailCreatingWithInvalidEmail() throws Exception {
        Result result = createAlphaApi(new User("email.dk", alphaApiCredential()));
        assertErrorMessage(result, "Email er ikke valid");
    }

    @Test
    public void shouldFailCreatingWithInvalidCredentialEmail() throws Exception {
        AlphaApiCredential credential = new AlphaApiCredential("email.dk", password());
        Result result = createAlphaApi(new User(email(), credential));
        assertErrorMessage(result, "Email er ikke valid");
    }

    @Test
    public void shouldFailCreatingWithTooShortPassword() throws Exception {
        AlphaApiCredential credential = new AlphaApiCredential(email(), longString(5));
        Result result = createAlphaApi(new User(email(), credential));
        assertErrorMessage(result, "Kodeord skal være mellem 6 og 32 tegn");
    }

    @Test
    public void shouldFailCreatingWithTooLongPassword() throws Exception {
        AlphaApiCredential credential = new AlphaApiCredential(email(), longString(33));
        Result result = createAlphaApi(new User(email(), credential));
        assertErrorMessage(result, "Kodeord skal være mellem 6 og 32 tegn");
    }

    @Test(enabled = false)
    public void shouldFailCreatingWithInvalidFacebookToken() throws Exception {
        FacebookCredential credential = new FacebookCredential("id", "token");
        Result result = createAlphaApi(new User(email(), credential));
        assertErrorMessage(result, "Facebook token er ikke valid");
    }

    private void assertErrorMessage(Result result, String message) {
        ASSERT.that(result.content()).isEqualTo(message);
        ASSERT.that(result.status()).isSameAs(BAD_REQUEST);
    }

    private Result createAlphaApi(User user) {
        String json = converter.toJson(user);
        return to(UserService.class).with(json).post("/user/create/alpha-api");
    }

    private Result createFacebook(User user) {
        String json = converter.toJson(user);
        return to(UserService.class).with(json).post("/user/create/facebook");
    }

    private String email() {
        return "alpha-api@alpha-api.dk";
    }

    private String password() {
        return "AlphaApi12345";
    }

    private AlphaApiCredential alphaApiCredential() {
        return new AlphaApiCredential(email(), password());
    }

    private String longString(int size) {
        char[] charArray = new char[size];
        Arrays.fill(charArray, ' ');
        return new String(charArray);
    }
}
