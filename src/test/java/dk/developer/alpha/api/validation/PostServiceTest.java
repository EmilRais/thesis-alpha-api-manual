package dk.developer.alpha.api.validation;

import dk.developer.alpha.api.post.Board;
import dk.developer.alpha.api.post.Location;
import dk.developer.alpha.api.post.Post;
import dk.developer.alpha.api.post.PostService;
import dk.developer.alpha.api.user.AlphaApiCredential;
import dk.developer.alpha.api.user.User;
import dk.developer.database.DatabaseFront;
import dk.developer.testing.Result;
import dk.developer.testing.TestDatabaseProvider;
import dk.developer.utility.Converter;
import org.bson.types.ObjectId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.google.common.truth.Truth.ASSERT;
import static dk.developer.testing.RestServiceTestHelper.from;
import static dk.developer.testing.RestServiceTestHelper.to;
import static java.lang.Double.NaN;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class PostServiceTest {
    private static final Converter converter = Converter.converter();
    private DatabaseFront database;

    @BeforeMethod
    public void setUp() throws Exception {
        database = TestDatabaseProvider.memoryTestDatabase();
    }

    @Test
    public void shouldFailCreatingWithNonExistingOwner() throws Exception {
        AlphaApiCredential credential = new AlphaApiCredential("email@email.dk", "password123");
        User user = new User("email@email.dk", credential);
        Result result = create(post(user, board(), title(), description(), kind(), date(), image(), location()));
        assertErrorMessage(result, "Brugeren eksisterer ikke");
    }

    @Test
    public void shouldFailCreatingWithNonExistingBoard() throws Exception {
        Board board = new Board(ObjectId.get().toString(), "name", "image");
        Result result = create(post(user(), board, title(), description(), kind(), date(), image(), location()));
        assertErrorMessage(result, "Opslagstavle eksisterer ikke");
    }

    @Test
    public void shouldFailCreatingWithTooShortTitle() throws Exception {
        Result result = create(post(user(), board(), "Title", description(), kind(), date(), image(), location()));
        assertErrorMessage(result, "Titlen skal være mellem 10 og 50 tegn");
    }

    @Test
    public void shouldFailCreatingWithTooLongTitle() throws Exception {
        Result result = create(post(user(), board(), longString(51), description(), kind(), date(), image(), location()));
        assertErrorMessage(result, "Titlen skal være mellem 10 og 50 tegn");
    }

    @Test
    public void shouldFailCreatingWithTooShortDescription() throws Exception {
        Result result = create(post(user(), board(), title(), "Description", kind(), date(), image(), location()));
        assertErrorMessage(result, "Beskrivelsen skal være mellem 20 og 500 tegn");
    }

    @Test
    public void shouldFailCreatingWithTooLongDescription() throws Exception {
        Result result = create(post(user(), board(), title(), longString(501), kind(), date(), image(), location()));
        assertErrorMessage(result, "Beskrivelsen skal være mellem 20 og 500 tegn");
    }

    @Test
    public void shouldFailCreatingWithEmptyKind() throws Exception {
        Result result = create(post(user(), board(), title(), description(), null, date(), image(), location()));
        assertErrorMessage(result, "Opslag er hverken fundet eller mistet");
    }

    @Test
    public void shouldFailCreatingWithEmptyDate() throws Exception {
        Result result = create(post(user(), board(), title(), description(), kind(), NaN, image(), location()));
        assertErrorMessage(result, "Dato er ikke valid");
    }

    @Test
    public void shouldFailCratingWithEmptyImage() throws Exception {
        Result result = create(post(user(), board(), title(), description(), kind(), date(), "", location()));
        assertErrorMessage(result, "Billede er ikke valid");
    }

    @Test
    public void shouldFailCreatingWithEmptyLatitude() throws Exception {
        Location location = new Location(NaN, longitude(), name(), city(), postalCode());
        Result result = create(post(user(), board(), title(), description(), kind(), date(), image(), location));
        assertErrorMessage(result, "Breddegrad er ikke valid");
    }

    @Test
    public void shouldFailCreatingWithEmptyLongitude() throws Exception {
        Location location = new Location(latitude(), NaN, name(), city(), postalCode());
        Result result = create(post(user(), board(), title(), description(), kind(), date(), image(), location));
        assertErrorMessage(result, "Længdegrad er ikke valid");
    }

    @Test
    public void shouldFailCreatingWithEmptyName() throws Exception {
        Location location = new Location(latitude(), longitude(), "", city(), postalCode());
        Result result = create(post(user(), board(), title(), description(), kind(), date(), image(), location));
        assertErrorMessage(result, "Lokationsnavn er ikke valid");
    }

    @Test
    public void shouldFailCreatingWithEmptyCity() throws Exception {
        Location location = new Location(latitude(), longitude(), name(), "", postalCode());
        Result result = create(post(user(), board(), title(), description(), kind(), date(), image(), location));
        assertErrorMessage(result, "By er ikke valid");
    }

    @Test
    public void shouldFailCreatingWithEmptyPostalCode() throws Exception {
        Location location = new Location(latitude(), longitude(), name(), city(), "");
        Result result = create(post(user(), board(), title(), description(), kind(), date(), image(), location));
        assertErrorMessage(result, "Postnummer er ikke valid");
    }

    @Test
    public void shouldFailGettingPostsWithEmptyBoardId() throws Exception {
        Result result = from(PostService.class).get("/post/get/board?id=");
        assertErrorMessage(result, "BoardId er ikke valid");
    }

    @Test
    public void shouldFailGettingPostsFromNonExistingBoard() throws Exception {
        Result result = from(PostService.class).get("/post/get/board?id=boardid");
        assertErrorMessage(result, "Opslagstavlen eksisterer ikke");
    }

    private void assertErrorMessage(Result result, String message) {
        ASSERT.that(result.status()).isSameAs(BAD_REQUEST);
        ASSERT.that(result.content()).isEqualTo(message);
    }

    private Result create(Post post) {
        String json = converter.toJson(post);
        return to(PostService.class).with(json).post("/post/create");
    }

    private Post post(User owner, Board board, String title, String description, Post.Kind kind, double date, String image, Location location) {
        return new Post("id", 123, owner, board, title, description, kind, date, image, location);
    }

    private User user() {
        AlphaApiCredential credential = new AlphaApiCredential("email@email.com", "password123");
        User user = new User("id", "email@email.com", credential);

        database.save(user);

        return user;
    }

    private String title() {
        return "TitleTitle";
    }

    private String description() {
        return "DescriptionDescription";
    }

    private String longString(int size) {
        char[] charArray = new char[size];
        Arrays.fill(charArray, ' ');
        return new String(charArray);
    }

    private Post.Kind kind() {
        return Post.Kind.FOUND;
    }

    private double date() {
        return 123.123;
    }

    private String image() {
        return "image";
    }

    private double latitude() {
        return 123;
    }

    private double longitude() {
        return 456;
    }

    private String name() {
        return "Name";
    }

    private String city() {
        return "City";
    }

    private String postalCode() {
        return "postalCode";
    }

    private Location location() {
        return new Location(latitude(), longitude(), name(), city(), postalCode());
    }

    private Board board() {
        Board board = new Board(ObjectId.get().toString(), "name", "image");
        database.save(board);
        return board;
    }
}
