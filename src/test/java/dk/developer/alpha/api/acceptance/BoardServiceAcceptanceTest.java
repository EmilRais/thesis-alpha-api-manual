package dk.developer.alpha.api.acceptance;

import com.fasterxml.jackson.core.type.TypeReference;
import dk.developer.alpha.api.post.BoardService;
import dk.developer.database.DatabaseFront;
import dk.developer.alpha.api.post.Board;
import dk.developer.testing.Result;
import dk.developer.utility.Converter;
import org.bson.types.ObjectId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.truth.Truth.ASSERT;
import static dk.developer.testing.RestServiceTestHelper.from;
import static dk.developer.testing.RestServiceTestHelper.to;
import static dk.developer.testing.TestDatabaseProvider.persistedTestDatabase;
import static dk.developer.utility.Converter.converter;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

public class BoardServiceAcceptanceTest {
    private static final Converter converter = converter();
    private DatabaseFront database;

    @BeforeMethod
    public void setUp() throws Exception {
        database = persistedTestDatabase();
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldFailWhenTryingToCreateExistingBoard() throws Exception {
        Board board = board();
        String json = converter.toJson(board);
        to(BoardService.class).with(json).post("/board/create");
        to(BoardService.class).with(json).post("/board/create");
    }

    @Test
    public void shouldCreateBoard() throws Exception {
        Board board = board();
        String json = converter.toJson(board);
        Result result = to(BoardService.class).with(json).post("/board/create");
        ASSERT.that(result.status()).isSameAs(CREATED);

        Board databaseBoard = database.load(Board.class).matching("_id").with(board.getId());
        ASSERT.that(databaseBoard).isEqualTo(board);
    }

    @Test
    public void shouldGetZeroBoards() throws Exception {
        List<Board> boards = getHelper();
        ASSERT.that(boards).isEmpty();
    }

    @Test
    public void shouldGetOneBoard() throws Exception {
        Board board = board();
        List<Board> boards = getHelper(board);
        ASSERT.that(boards).containsExactly(board);
    }

    @Test
    public void shouldGetSeveralBoards() throws Exception {
        Board firstBoard = board();
        Board secondBoard = board();
        List<Board> boards = getHelper(firstBoard, secondBoard);
        ASSERT.that(boards).containsExactly(firstBoard, secondBoard);
    }

    private List<Board> getHelper(Board... boards) throws java.io.IOException {
        for (Board board : boards) {
            database.save(board);
        }

        Result result = from(BoardService.class).get("/board/get");
        ASSERT.that(result.status()).isSameAs(OK);

        return converter.fromJson(result.content(), new TypeReference<List<Board>>() {});
    }

    private Board board() {
        return new Board(ObjectId.get().toString(), "name", "image");
    }
}
