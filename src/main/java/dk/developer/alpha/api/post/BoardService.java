package dk.developer.alpha.api.post;

import dk.developer.database.DatabaseFront;
import dk.developer.database.DatabaseProvider;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;

@Path("board")
public class BoardService {
    private final DatabaseFront database;

    public BoardService() {
        database = DatabaseProvider.databaseLayer();
    }

    @GET
    @Path("/get")
    @Produces(APPLICATION_JSON)
    public Response get() {
        List<Board> boards = database.loadAll(Board.class);
        return Response.status(OK).entity(boards).build();
    }

    @POST
    @Path("/create")
    @Consumes(APPLICATION_JSON)
    public Response create(@Valid Board board) {
        database.save(board);
        return Response.status(CREATED).build();
    }
}
