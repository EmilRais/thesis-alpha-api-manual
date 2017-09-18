package dk.developer.alpha.api.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.developer.alpha.api.post.Board;
import dk.developer.alpha.api.post.Location;
import dk.developer.alpha.api.post.Post;
import dk.developer.alpha.api.post.PostService;
import dk.developer.alpha.api.user.AlphaApiCredential;
import dk.developer.alpha.api.user.User;
import dk.developer.database.DatabaseFront;
import dk.developer.testing.Result;
import dk.developer.utility.Converter;
import org.bson.types.ObjectId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.google.common.truth.Truth.ASSERT;
import static dk.developer.testing.RestServiceTestHelper.from;
import static dk.developer.testing.RestServiceTestHelper.to;
import static dk.developer.testing.TestDatabaseProvider.persistedTestDatabase;
import static dk.developer.utility.Converter.converter;
import static javax.ws.rs.core.Response.Status.*;

public class PostServiceAcceptanceTest {
    private static final Converter converter = converter();
    private DatabaseFront database;

    @BeforeMethod
    public void setUp() throws Exception {
        database = persistedTestDatabase();
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldFailWhenTryingToCreateExistingPost() throws Exception {
        String description = "Fandt noget på banegården";
        Location location = new Location(23.232, 245.12, "Aros", "Aarhus C", "8000");
        Post post = new Post(ObjectId.get().toString(), 1234, user(), savedBoard(), "iPad", description, Post.Kind.FOUND, date(), "image", location);
        database.save(post);
        database.save(post);
    }

    @Test
    public void shouldCreatePost() throws Exception {
        String description = "Mistedei Turingp AU æøå";
        Location location = new Location(123.456, 78.90, "Bruuns Galleri", "Aarhus C", "8000");
        Post post = new Post(ObjectId.get().toString(), 1234, user(), savedBoard(), "iPad - Tabt", description, Post.Kind.LOST, date(), "image", location);
        String json = converter.toJson(post);
        Result result = to(PostService.class).with(json).post("/post/create");

        ASSERT.that(result.status()).isSameAs(CREATED);

        Post receivedPost = database.load(Post.class).matching("title").with("iPad - Tabt");
        ASSERT.that(receivedPost).isEqualTo(post);
    }

    private User user() {
        AlphaApiCredential credential = new AlphaApiCredential("email@email.com", "password123");
        User user = new User("email@email.com", credential);
        database.save(user);
        return user;
    }

    @Test
    public void shouldFailWhenUpdatingPostThatDoesNotExist() throws Exception {
        String id = ObjectId.get().toString();
        Location location = new Location(23.232, 245.12, "Tivoli", "København K", "1422");
        Post post = new Post(id, 1234, user(), savedBoard(), "Other title", "Description", Post.Kind.FOUND, date(), "image", location);
        String json = converter.toJson(post);

        Result result = to(PostService.class).with(json).post("/post/update");
        ASSERT.that(result.status()).isSameAs(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldUpdatePost() throws Exception {
        String id = ObjectId.get().toString();
        Location location = new Location(23.232, 245.12, "Aros", "Aarhus C", "8000");
        Post post = new Post(id, 1234, user(), savedBoard(), "Title", "Description", Post.Kind.FOUND, date(), "image", location);
        database.save(post);

        Location updatedLocation = new Location(23.232, 245.12, "Aros", "Aarhus N", "8000");
        Post updatedPost = new Post(id, 1234, user(), savedBoard(), "Other title", "Description", Post.Kind.FOUND, date(), "image", updatedLocation);
        String json = converter.toJson(updatedPost);
        Result result = to(PostService.class).with(json).post("/post/update");
        ASSERT.that(result.status()).isSameAs(OK);

        Post savedPost = database.load(Post.class).matching("_id").with(id);
        ASSERT.that(savedPost.getTitle()).isEqualTo(updatedPost.getTitle());
    }

    @Test
    public void shouldFailWhenDeletingPostThatDoesNotExist() throws Exception {
        String id = ObjectId.get().toString();
        Result result = to(PostService.class).with(id).post("/post/delete");

        ASSERT.that(result.status()).isSameAs(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldDeletePost() throws Exception {
        Location location = new Location(23.232, 245.12, "Aros", "Aarhus C", "8000");
        Post post = new Post(ObjectId.get().toString(), 1234, user(), savedBoard(), "Title", "Description", Post.Kind.FOUND, date(), "image", location);
        database.save(post);

        String id = post.getId();
        Result result = to(PostService.class).with(id).post("/post/delete");

        ASSERT.that(result.status()).isSameAs(OK);

        Post savedPost = database.load(Post.class).matching("_id").with(id);
        ASSERT.that(savedPost).isNull();
    }

    @Test
    public void shouldGetZeroPostsFromEmptyBoard() throws Exception {
        Board board = savedBoard();

        Result result = from(PostService.class).get("/post/get/board?id=" + board.getId());
        ASSERT.that(result.status()).isSameAs(OK);

        List<Post> posts = converter.fromJson(result.content(), new TypeReference<List<Post>>() {});
        ASSERT.that(posts).isEmpty();
    }

    @Test
    public void shouldGetOneBoardPost() throws Exception {
        Board board = savedBoard();
        Location location = new Location(23.232, 245.12, "Aros", "Aarhus C", "8000");
        Post post = new Post(ObjectId.get().toString(), 1234, user(), board, "Title", "Description", Post.Kind.FOUND, date(), "image", location);
        database.save(post);
        database.save(new Post(ObjectId.get().toString(), 1234, user(), savedBoard(), "Title", "Description", Post.Kind.FOUND, date(), "image", location));


        Result result = from(PostService.class).get("/post/get/board?id=" + board.getId());
        ASSERT.that(result.status()).isSameAs(OK);

        List<Post> posts = converter.fromJson(result.content(), new TypeReference<List<Post>>() {});
        ASSERT.that(posts).containsExactly(post);
    }

    private List<Post> getHelper(Post... posts) {
        for (Post post : posts) {
            database.save(post);
        }

        Result result = from(PostService.class).get("/post/get");
        ASSERT.that(result.status()).isSameAs(OK);

        return converter.fromJson(result.content(), new TypeReference<List<Post>>() {
        });
    }

    private double date() {
        Calendar calendar = new GregorianCalendar(2015, 05, 05, 14, 00);
        return calendar.getTime().getTime();
    }

    private Board savedBoard() {
        Board board = new Board(ObjectId.get().toString(), "name", "image");
        database.save(board);
        return board;
    }
}