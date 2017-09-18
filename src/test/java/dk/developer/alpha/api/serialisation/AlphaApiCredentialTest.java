package dk.developer.alpha.api.serialisation;

import dk.developer.alpha.api.user.AlphaApiCredential;
import dk.developer.testing.JsonTool;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static dk.developer.utility.Converter.converter;
import static org.truth0.Truth.ASSERT;

public class AlphaApiCredentialTest {
    private JsonTool tool;

    @BeforeClass
    public void setUp() throws Exception {
        tool = new JsonTool(LocationTest.class);
    }

    @Test
    public void fromJson() throws Exception {
        String json = tool.readFilteredJsonFile("AlphaApiCredentialInput.json");
        AlphaApiCredential credential = converter().fromJson(json, AlphaApiCredential.class);

        ASSERT.that(credential.getEmail()).isEqualTo("email");
        ASSERT.that(credential.getPassword()).isEqualTo("password");
    }
}