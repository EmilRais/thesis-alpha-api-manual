package dk.developer.alpha.api.post;

import dk.developer.database.DatabaseFront;
import dk.developer.database.DatabaseProvider;
import dk.developer.validation.single.Id;
import dk.developer.validation.single.NotEmpty;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Path("post")
public class PostService {
    private final DatabaseFront database;

    public PostService() {
        database = DatabaseProvider.databaseLayer();
    }

    @POST
    @Path("/create")
    @Consumes(APPLICATION_JSON)
    public Response create(@Valid Post post) {
        database.save(post);
        return Response.status(CREATED).build();
    }

    @POST
    @Path("/update")
    @Consumes(APPLICATION_JSON)
    public Response update(Post post) {
        boolean didUpdate = database.update(post);
        return expectModification(didUpdate);
    }

    @POST
    @Path("/delete")
    @Consumes(APPLICATION_JSON)
    public Response delete(String id) {
        boolean didDelete = database.delete(Post.class).matching("_id").with(id);
        return expectModification(didDelete);
    }

    private Response expectModification(boolean didChange) {
        if ( didChange )
            return Response.status(OK).build();

        return Response.status(INTERNAL_SERVER_ERROR).build();
    }

    @GET
    @Path("/get/board")
    @Produces(APPLICATION_JSON)
    public Response getBoardPosts(
            @QueryParam("id")
            @NotEmpty(message = "BoardId er ikke valid")
            @Id(of = Board.class, message ="Opslagstavlen eksisterer ikke" ) String boardId) {
        List<Post> posts = database.loadAll(Post.class);
        List<Post> matchingPosts = posts.stream()
                .filter(isChildOfBoard(boardId))
                .collect(Collectors.toList());

        return Response.status(OK).entity(matchingPosts).build();
    }

    private Predicate<Post> isChildOfBoard(String boardId) {
        return post -> post.getParent().getId().equals(boardId);
    }
}
