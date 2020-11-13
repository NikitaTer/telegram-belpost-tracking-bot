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
    private long chatId;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private StateEntity state;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<UserTrackingEntity> trackings = new ArrayList<UserTrackingEntity>();

    public UserEntity() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }

    public List<UserTrackingEntity> getTrackings() {
        return trackings;
    }

    public void setTrackings(List<UserTrackingEntity> trackings) {
        this.trackings = trackings;
    }

    public void addTracking(TrackingEntity tracking, String name) {
        UserTrackingEntity userTracking = new UserTrackingEntity(this,tracking);
        userTracking.setTrackingName(name);
        trackings.add(userTracking);
        tracking.getUsers().add(userTracking);
    }

    public void removeTracking(TrackingEntity tracking) {
        Iterator<UserTrackingEntity> iterator = trackings.iterator();
        while (iterator.hasNext()) {
            UserTrackingEntity currentTracking = iterator.next();

            if (currentTracking.getTracking().getNumber().equals(tracking.getNumber())) {
                iterator.remove();
                currentTracking.getTracking().getUsers().remove(currentTracking);
                currentTracking.setUser(null);
                currentTracking.setTracking(null);
            }
        }
    }

    public void removeAllTrackings() {
        Iterator<UserTrackingEntity> iterator = trackings.iterator();
        while (iterator.hasNext()) {
            UserTrackingEntity currentTracking = iterator.next();

            iterator.remove();
            currentTracking.getTracking().getUsers().remove(currentTracking);
            currentTracking.setUser(null);
            currentTracking.setTracking(null);
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
        hash = (int) (31 * hash + chatId);
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