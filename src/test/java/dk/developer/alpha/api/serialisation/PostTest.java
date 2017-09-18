package dk.developer.alpha.api.serialisation;

import dk.developer.alpha.api.user.AlphaApiCredential;
import dk.developer.alpha.api.user.User;
import dk.developer.alpha.api.post.Board;
import dk.developer.alpha.api.post.Location;
import dk.developer.alpha.api.post.Post;
import dk.developer.testing.JsonTool;
import dk.developer.utility.Converter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.ASSERT;
import static dk.developer.alpha.api.post.Post.Kind.LOST;
import static dk.developer.utility.Converter.converter;

public class PostTest {
    private static final Converter converter = converter();
    private JsonTool tool;

    @BeforeClass
    public void setUp() throws Exception {
        tool = new JsonTool(PostTest.class);
    }

    @Test
    public void fromInput() throws Exception {
        String json = tool.readFilteredJsonFile("PostInput.json");
        Post post = converter.fromJson(json, Post.class);

        ASSERT.that(post.getOwner()).isEqualTo(user());
        ASSERT.that(post.getParent()).isEqualTo(board());
        ASSERT.that(post.getTitle()).isEqualTo("title");
        ASSERT.that(post.getDescription()).isEqualTo("description");
        ASSERT.that(post.getKind()).isSameAs(LOST);
        ASSERT.that(post.getDate()).isEqualTo(123.456);
        ASSERT.that(post.getImage()).isEqualTo("image");
        ASSERT.that(post.getLocation()).isEqualTo(new Location(12.34, 56.78, "name", "city", "postalCode"));
    }

    @Test
    public void toOutput() throws Exception {
        Location location = new Location(12.34, 56.78, "name", "city", "postalCode");
        Post post = new Post("abc123", 1234, user(), board(), "title", "description", LOST, 123.456, "image", location);

        String json = converter.toJson(post);
        ASSERT.that(json).isEqualTo(tool.readFilteredJsonFile("PostOutput.json"));
    }

    private User user() {
        AlphaApiCredential credential = new AlphaApiCredential("email", "password");
        return new User("userId", "email", credential);
    }

    private Board board() {
        return new Board("boardId","name", "image");
    }
}