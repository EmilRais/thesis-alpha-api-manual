package dk.developer.alpha.api.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.developer.database.Collection;
import dk.developer.database.DatabaseObject;
import dk.developer.validation.single.NotEmpty;
import org.bson.types.ObjectId;

import java.util.Objects;

@Collection("Boards")
public class Board extends DatabaseObject{
    @JsonProperty("_id")
    private final String id;

    @NotEmpty(message = "Navn er ikke valid")
    private final String name;

    @NotEmpty(message = "Billede er ikke valid")
    private final String image;

    @JsonCreator
    private Board(@JsonProperty("name") String name,
                 @JsonProperty("image") String image) {
        id = ObjectId.get().toString();
        this.name = name;
        this.image = image;
    }

    public Board(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id) &&
                Objects.equals(name, board.name) &&
                Objects.equals(image, board.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image);
    }
}
