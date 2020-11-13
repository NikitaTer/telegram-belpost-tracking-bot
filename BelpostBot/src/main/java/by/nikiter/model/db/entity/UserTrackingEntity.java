package by.nikiter.model.db.entity;

import javax.persistence.*;

@Entity
@Table(name = "user_tracking", schema = "telegram_belpost_bot")
public class UserTrackingEntity {

    @EmbeddedId
    private UserTrackingId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("username")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("number")
    private TrackingEntity tracking;

    @Column(name = "tracking_name")
    private String trackingName;

    public UserTrackingEntity() {
        trackingName = "nameless";
    }

    public UserTrackingEntity(UserEntity user, TrackingEntity tracking) {
        this.user = user;
        this.tracking = tracking;
        this.id = new UserTrackingId(user.getUsername(),tracking.getNumber());
        this.trackingName = "nameless";
    }

    public UserTrackingId getId() {
        return id;
    }

    public void setId(UserTrackingId id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public TrackingEntity getTracking() {
        return tracking;
    }

    public void setTracking(TrackingEntity tracking) {
        this.tracking = tracking;
    }

    public String getTrackingName() {
        return trackingName;
    }

    public void setTrackingName(String trackingName) {
        this.trackingName = trackingName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTrackingEntity that = (UserTrackingEntity) o;
        return id.equals(that.id) &&
                user.equals(that.user) &&
                tracking.equals(that.tracking) &&
                trackingName.equals(that.trackingName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (id == null ? 0 : id.hashCode());
        hash = 31 * hash + (user == null ? 0 : user.hashCode());
        hash = 31 * hash + (tracking == null ? 0 : tracking.hashCode());
        hash = 31 * hash + (trackingName == null ? 0 : trackingName.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return "UserTrackingEntity{" +
                "id=" + id +
                ", user=" + user +
                ", tracking=" + tracking +
                ", trackingName='" + trackingName + '\'' +
                '}';
    }
}