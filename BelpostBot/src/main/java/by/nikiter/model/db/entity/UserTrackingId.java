package by.nikiter.model.db.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserTrackingId implements Serializable {

    @Column(name = "user_username")
    private String username;

    @Column(name = "tracking_number")
    private String number;

    public UserTrackingId() {
    }

    public UserTrackingId(String username, String trackingNumber) {
        this.username = username;
        this.number = trackingNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String trackingNumber) {
        this.number = trackingNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTrackingId that = (UserTrackingId) o;
        return username.equals(that.username) &&
                number.equals(that.number);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (username == null ? 0 : username.hashCode());
        hash = 31 * hash + (number == null ? 0 : number.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return "UserTrackingId{" +
                "username='" + username + '\'' +
                ", trackingNumber='" + number + '\'' +
                '}';
    }
}