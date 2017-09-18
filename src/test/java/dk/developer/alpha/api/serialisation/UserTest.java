package dk.developer.alpha.api.serialisation;

import dk.developer.alpha.api.user.AlphaApiCredential;
import dk.developer.alpha.api.user.FacebookCredential;
import dk.developer.alpha.api.user.User;
import dk.developer.testing.JsonTool;
import dk.developer.utility.Converter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.ASSERT;
import static dk.developer.utility.Converter.converter;

public class UserTest {
    private static final Converter converter = converter();
    private JsonTool tool;

    @BeforeMethod
    public void setUp() throws Exception {
        tool = new JsonTool(UserTest.class);
    }

    @Test
    public void toAlphaApiOutput() throws Exception {
        User user = new User("abc123", "email", new AlphaApiCredential("email", "password"));
        String json = converter.toJson(user);

        ASSERT.that(json).isEqualTo(tool.readFilteredJsonFile("AlphaApiUserOutput.json"));
    }

    @Test
    public void toFacebookOutput() throws Exception {
        User user = new User("abc123", "email", new FacebookCredential("abc123", "token"));
        String json = converter.toJson(user);

        ASSERT.that(json).isEqualTo(tool.readFilteredJsonFile("FacebookUserOutput.json"));
    }
}