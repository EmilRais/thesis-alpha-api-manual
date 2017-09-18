package dk.developer.alpha.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.developer.database.Collection;
import dk.developer.database.DatabaseObject;
import dk.developer.validation.single.Email;
import org.bson.types.ObjectId;

import javax.validation.Valid;
import java.util.Objects;

@Collection("Users")
public class User extends DatabaseObject {
    @JsonProperty("_id")
    private final String id;

    @Email(message = "Email er ikke valid")
    private final String email;

    @Valid
    private Credential credential;

    public User(String id, String email, Credential credential) {
        this.id = id;
        this.email = email;
        this.credential = credential;
    }

    public User(@JsonProperty("email") String email, @JsonProperty("credential") Credential credential) {
        this.id = ObjectId.get().toString();
        this.email = email;
        this.credential = credential;
    }

    @Override
    public String getId() {
        return id;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", credential=" + credential +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(credential, user.credential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, credential);
    }
}
