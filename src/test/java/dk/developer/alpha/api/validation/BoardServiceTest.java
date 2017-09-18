package dk.developer.alpha.api.validation;

import dk.developer.alpha.api.post.Board;
import dk.developer.alpha.api.post.BoardService;
import dk.developer.testing.Result;
import dk.developer.testing.TestDatabaseProvider;
import dk.developer.utility.Converter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.ASSERT;
import static dk.developer.testing.RestServiceTestHelper.to;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class BoardServiceTest {
    private static final Converter converter = Converter.converter();

    @BeforeMethod
    public void setUp() throws Exception {
        TestDatabaseProvider.memoryTestDatabase();
    }

    @Test
    public void shouldFailCreatingWithEmptyName() throws Exception {
        Result result = create(board("", image()));
        assertErrorMessage(result, "Navn er ikke valid");
    }

    @Test
    public void shouldFailCreatingWithEmptyImage() throws Exception {
        Result result = create(board(name(), ""));
        assertErrorMessage(result, "Billede er ikke valid");
    }

    private Board board(String name, String image) {
        return new Board("id", name, image);
    }

    private String name() {
        return "name";
    }

    private String image() {
        return "image";
    }

    private Result create(Board board) {
        String json = converter.toJson(board);
        return to(BoardService.class).with(json).post("/board/create");
    }

    private void assertErrorMessage(Result result, String message) {
        ASSERT.that(result.status()).isSameAs(BAD_REQUEST);
        ASSERT.that(result.content()).isEqualTo(message);
    }
}
