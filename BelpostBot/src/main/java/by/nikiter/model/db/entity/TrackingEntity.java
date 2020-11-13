package by.nikiter.model.db.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "tracking", schema = "telegram_belpost_bot")
public class TrackingEntity {

    @Id
    @Column(name = "number")
    private String number;

    @Column(name = "last_event")
    private String lastEvent;

    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "tracking", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<UserTrackingEntity> users = new ArrayList<>();

    public TrackingEntity() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<UserTrackingEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserTrackingEntity> users) {
        this.users = users;
    }

    public void addUser(UserEntity user) {
        UserTrackingEntity userTracking = new UserTrackingEntity(user,this);
        users.add(userTracking);
        user.getTrackings().add(userTracking);
    }

    public void removeUser(UserEntity user) {
        Iterator<UserTrackingEntity> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserTrackingEntity currentTracking = iterator.next();

            if (currentTracking.getUser().getUsername().equals(user.getUsername())) {
                iterator.remove();
                currentTracking.getUser().getTrackings().remove(currentTracking);
                currentTracking.setUser(null);
                currentTracking.setTracking(null);
            }
        }
    }

    public void removeAllUsers() {
        Iterator<UserTrackingEntity> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserTrackingEntity currentTracking = iterator.next();

            iterator.remove();
            currentTracking.getUser().getTrackings().remove(currentTracking);
            currentTracking.setUser(null);
            currentTracking.setTracking(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackingEntity that = (TrackingEntity) o;
        return number.equals(that.number) &&
                Objects.equals(lastEvent, that.lastEvent);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (number == null ? 0 : number.hashCode());
        hash = 31 * hash + (lastEvent == null ? 0 : lastEvent.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return "TrackingEntity{" +
                "number='" + number + '\'' +
                ", lastEvent='" + lastEvent + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
