package dk.developer.alpha.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.developer.validation.single.Email;

import javax.validation.constraints.Size;
import java.util.Objects;

public class AlphaApiCredential implements Credential {
    @Email(message = "Email er ikke valid")
    private final String email;

    @Size(min = 6, max = 32, message = "Kodeord skal være mellem 6 og 32 tegn")
    private final String password;

    @JsonCreator
    public AlphaApiCredential(@JsonProperty("email") String email,
                              @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "AlphaApiCredential{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlphaApiCredential that = (AlphaApiCredential) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}
