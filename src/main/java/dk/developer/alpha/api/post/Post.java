package dk.developer.alpha.api.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.developer.alpha.api.user.User;
import dk.developer.database.Collection;
import dk.developer.database.DatabaseObject;
import dk.developer.validation.single.NotEmpty;
import dk.developer.validation.single.NotNaN;
import dk.developer.validation.single.Stored;
import org.bson.types.ObjectId;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Collection("Posts")
public class Post extends DatabaseObject{
    @JsonProperty("_id")
    private final String id;
    @JsonProperty("creationDate")
    private final long creationDate;

    @Stored(message = "Brugeren eksisterer ikke")
    private User owner;

    @Stored(message = "Opslagstavle eksisterer ikke")
    private Board board;

    @Size(min = 10, max = 50, message = "Titlen skal være mellem 10 og 50 tegn")
    @NotEmpty(message = "Titlen er ikke valid")
    private final String title;

    @Size(min = 20, max = 500, message = "Beskrivelsen skal være mellem 20 og 500 tegn")
    @NotEmpty(message = "Beskrivelse er ikke valid")
    private final String description;
    @NotNull(message = "Opslag er hverken fundet eller mistet")
    private final Kind kind;
    @NotNaN(message = "Dato er ikke valid")
    private final double date;

    @NotEmpty(message = "Billede er ikke valid")
    private final String image;

    @Valid
    private final Location location;

    public Post(String id, long creationDate, User owner, Board board, String title, String description, Kind kind, double date, String image, Location location) {
        this.id = id;
        this.creationDate = creationDate;
        this.owner = owner;
        this.board = board;
        this.date = date;
        this.image = image;
        this.title = title;
        this.description = description;
        this.kind = kind;
        this.location = location;
    }

    @JsonCreator
    private Post(@JsonProperty("owner") User owner,
                 @JsonProperty("board") Board board,
                 @JsonProperty("title") String title,
                 @JsonProperty("description") String description,
                 @JsonProperty("kind") Kind kind,
                 @JsonProperty("date") double date,
                 @JsonProperty("image") String image,
                 @JsonProperty("location") Location location) {
        ObjectId objectId = ObjectId.get();
        this.id = objectId.toString();
        this.creationDate = objectId.getDate().getTime();
        this.owner = owner;
        this.board = board;
        this.title = title;
        this.description = description;
        this.kind = kind;
        this.date = date;
        this.image = image;
        this.location = location;
    }

    @Override
    public String getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    @JsonProperty("board")
    public Board getParent() {
        return board;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Kind getKind() {
        return kind;
    }

    public double getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", creationDate=" + creationDate +
                ", owner=" + owner +
                ", board=" + board +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", kind=" + kind +
                ", date=" + date +
                ", image='" + image + '\'' +
                ", location=" + location +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Post post = (Post) o;
        return Objects.equals(creationDate, post.creationDate) &&
                Objects.equals(date, post.date) &&
                Objects.equals(id, post.id) &&
                Objects.equals(owner, post.owner) &&
                Objects.equals(board, post.board) &&
                Objects.equals(title, post.title) &&
                Objects.equals(description, post.description) &&
                Objects.equals(kind, post.kind) &&
                Objects.equals(image, post.image) &&
                Objects.equals(location, post.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, owner, board, title, description, kind, date, image, location);
    }

    public enum Kind {
        LOST, FOUND
    }
}
