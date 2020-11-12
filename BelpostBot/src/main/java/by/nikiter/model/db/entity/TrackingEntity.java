package by.nikiter.model.db.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tracking", schema = "telegram_belpost_bot")
public class TrackingEntity {

    @Id
    @Column(name = "number")
    private String number;

    @Column(name = "name")
    private String name;

    @Column(name = "last_event")
    private String lastEvent;

    @Column(name = "is_updatable")
    private boolean isUpdatable;

    @Column(name = "updated_at", insertable = false)
    private Timestamp updatedAt;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "trackings")
    private Set<UserEntity> users = new HashSet<UserEntity>();

    public TrackingEntity() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public boolean isUpdatable() {
        return isUpdatable;
    }

    public void setUpdatable(boolean updatable) {
        isUpdatable = updatable;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Collection<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

    public void addUser(UserEntity user) {
        users.add(user);
        user.getTrackings().add(this);
    }

    public void removeUser(UserEntity user) {
        users.remove(user);
        user.getTrackings().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackingEntity that = (TrackingEntity) o;
        return isUpdatable == that.isUpdatable &&
                number.equals(that.number) &&
                Objects.equals(lastEvent, that.lastEvent);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (number == null ? 0 : number.hashCode());
        hash = 31 * hash + (lastEvent == null ? 0 : lastEvent.hashCode());
        hash = 31 * hash + (!isUpdatable ? 0 : 1);
        return hash;
    }

    @Override
    public String toString() {
        return "TrackingEntity{" +
                "number='" + number + '\'' +
                ", lastEvent='" + lastEvent + '\'' +
                ", isUpdatable=" + isUpdatable +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
