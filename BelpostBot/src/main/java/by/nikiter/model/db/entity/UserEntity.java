package by.nikiter.model.db.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user", schema = "telegram_belpost_bot")
public class UserEntity {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "chat_id")
    private int chatId;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StateEntity state;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "user_tracking",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "tracking"))
    private Set<TrackingEntity> trackings = new HashSet<TrackingEntity>();

    public UserEntity() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }

    public Collection<TrackingEntity> getTrackings() {
        return trackings;
    }

    public void setTrackings(Set<TrackingEntity> trackings) {
        this.trackings = trackings;
    }

    public void addTracking(TrackingEntity tracking) {
        trackings.add(tracking);
        tracking.getUsers().add(this);
    }

    public void removeTracking(TrackingEntity trackingEntity) {
        trackings.remove(trackingEntity);
        trackingEntity.getUsers().remove(this);
    }

    public void removeAllTrackings() {
        Iterator<TrackingEntity> iterator = trackings.iterator();
        while (iterator.hasNext()) {
            TrackingEntity tracking = iterator.next();
            iterator.remove();
            tracking.getUsers().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return chatId == that.chatId &&
                username.equals(that.username) &&
                state.equals(that.state);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (username == null ? 0 : username.hashCode());
        hash = 31 * hash + chatId;
        hash = 31 * hash + (state == null ? 0 : state.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "username='" + username + '\'' +
                ", chatId=" + chatId +
                ", state=" + state +
                '}';
    }
}
