package dk.developer.alpha.api.user;

import dk.developer.database.DatabaseFront;
import dk.developer.database.DatabaseProvider;
import dk.developer.alpha.api.Facebook;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;

@Path("user")
public class UserService {
    private final DatabaseFront database;
    private final Facebook facebook = new Facebook();

    public UserService() {
        database = DatabaseProvider.databaseLayer();
    }

    @GET
    @Path("/get")
    @Produces(APPLICATION_JSON)
    public Response get() {
        List<User> users = database.loadAll(User.class);
        return Response.status(OK).entity(users).build();
    }

    @POST
    @Path("/create/alpha-api")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response createAlphaApi(@Valid User user) {
        database.save(user);
        return Response.status(CREATED).entity(user).build();
    }

    @POST
    @Path("/create/facebook")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response createFacebook(@Valid User user) {
        Credential credential = user.getCredential();

        if (!(credential instanceof FacebookCredential)) {
            return Response.status(BAD_REQUEST).entity("Ugyldigt Facebook-login").build();
        }

        FacebookCredential facebookCredential = (FacebookCredential) credential;
        String userId = facebookCredential.getUserId();
        String token = facebookCredential.getToken();
        InspectedFacebookToken inspectedToken = facebook.inspectAccessToken(token);

        if (!inspectedToken.isValid() || !inspectedToken.getUserId().equals(userId)) {
            return Response.status(BAD_REQUEST).entity("Ugyldigt Facebook-login").build();
        }

        LongLivedToken longLivedToken = facebook.extendShortLivedToken(token);
        FacebookCredential newCredential = new FacebookCredential(userId, longLivedToken.getToken());
        User facebookUser = new User(userId, user.getEmail(), newCredential);
        database.save(facebookUser);

        return Response.status(CREATED).entity(facebookUser).build();
    }

    @POST
    @Path("/login/alpha-api")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response login(AlphaApiCredential credential) {
        User user = database.load(User.class).matching("email").with(credential.getEmail());
        if (user == null) {
            String errorMessage = "Der findes ingen bruger med email: " + credential.getEmail();
            return Response.status(UNAUTHORIZED).entity(errorMessage).build();
        }

        AlphaApiCredential userCredential = (AlphaApiCredential) user.getCredential();
        if (!userCredential.getPassword().equals(credential.getPassword())) {
            String errorMessage = "Forkert kodeord";
            return Response.status(UNAUTHORIZED).entity(errorMessage).build();
        }

        return Response.status(OK).entity(user).build();
    }

    @POST
    @Path("/login/facebook")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response login(FacebookCredential credential) {
        String userId = credential.getUserId();
        String token = credential.getToken();
        InspectedFacebookToken inspectedToken = facebook.inspectAccessToken(token);

        if (!inspectedToken.isValid() || !inspectedToken.getUserId().equals(userId))
            return Response.status(UNAUTHORIZED).entity("Ugyldigt Facebook-login").build();

        User user = database.load(User.class).matching("_id").with(userId);
        if (user == null) return Response.status(NOT_ACCEPTABLE).entity("Bruger ikke oprettet").build();

        if (!(user.getCredential() instanceof FacebookCredential))
            return Response.status(UNAUTHORIZED).entity("Ugyldigt Facebook-login").build();

        String userToken = ((FacebookCredential) user.getCredential()).getToken();
        if (!userToken.equals(token)) {
            LongLivedToken longLivedToken = facebook.extendShortLivedToken(token);
            FacebookCredential newCredential = new FacebookCredential(userId, longLivedToken.getToken());
            user.setCredential(newCredential);

            boolean updated = database.update(user);
            if (!updated) return Response.status(BAD_REQUEST).entity("Kunne ikke logge ind").build();
        }

        return Response.status(OK).entity(user).build();
    }
}
