package dk.developer.alpha.api.serialisation;

import dk.developer.alpha.api.user.FacebookCredential;
import dk.developer.testing.JsonTool;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static dk.developer.utility.Converter.converter;
import static org.truth0.Truth.ASSERT;

public class FacebookCredentialTest {
    private JsonTool tool;

    @BeforeClass
    public void setUp() throws Exception {
        tool = new JsonTool(LocationTest.class);
    }

    @Test
    public void fromJson() throws Exception {
        String json = tool.readFilteredJsonFile("FacebookCredentialInput.json");
        FacebookCredential credential = converter().fromJson(json, FacebookCredential.class);

        ASSERT.that(credential.getUserId()).isEqualTo("userId");
        ASSERT.that(credential.getToken()).isEqualTo("token");
    }
}