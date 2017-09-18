package dk.developer.alpha.api.post;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.developer.validation.single.NotEmpty;
import dk.developer.validation.single.NotNaN;

import java.util.Objects;

public class Location {
    @NotNaN(message = "Breddegrad er ikke valid")
    private final double latitude;

    @NotNaN(message = "Længdegrad er ikke valid")
    private final double longitude;

    @NotEmpty(message = "Lokationsnavn er ikke valid")
    private final String name;

    @NotEmpty(message = "By er ikke valid")
    private final String city;

    @NotEmpty(message = "Postnummer er ikke valid")
    private final String postalCode;

    @JsonCreator
    public Location(@JsonProperty("latitude") double latitude,
                    @JsonProperty("longitude")double longitude,
                    @JsonProperty("name") String name,
                    @JsonProperty("city") String city,
                    @JsonProperty("postalCode") String postalCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.city = city;
        this.postalCode = postalCode;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(latitude, location.latitude) &&
                Objects.equals(longitude, location.longitude) &&
                Objects.equals(name, location.name) &&
                Objects.equals(city, location.city) &&
                Objects.equals(postalCode, location.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, name, city, postalCode);
    }
}
