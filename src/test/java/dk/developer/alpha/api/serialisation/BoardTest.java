package dk.developer.alpha.api.serialisation;

import dk.developer.alpha.api.post.Board;
import dk.developer.testing.JsonTool;
import dk.developer.utility.Converter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.ASSERT;
import static dk.developer.utility.Converter.converter;

public class BoardTest {
    private static final Converter converter = converter();
    private JsonTool tool;

    @BeforeClass
    public void setUp() throws Exception {
        tool = new JsonTool(BoardTest.class);
    }

    @Test
    public void fromInput() throws Exception {
        String json = tool.readFilteredJsonFile("BoardInput.json");
        Board board = converter.fromJson(json, Board.class);

        ASSERT.that(board.getName()).isEqualTo("name");
        ASSERT.that(board.getImage()).isEqualTo("image");
    }

    @Test
    public void toOutput() throws Exception {
        Board board = new Board("id", "name", "image");

        String json = converter.toJson(board);
        ASSERT.that(json).isEqualTo(tool.readFilteredJsonFile("BoardOutput.json"));
    }
}
