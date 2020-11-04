package by.nikiter.model;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

public class UserChat {

    private User user;
    private Chat chat;

    public UserChat(User user, Chat chat) {
        this.user = user;
        this.chat = chat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserChat userChat = (UserChat) o;
        return user.equals(userChat.user) &&
                chat.equals(userChat.chat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, chat);
    }

    @Override
    public String toString() {
        return "UserChat{" +
                "user=" + user +
                ", chat=" + chat +
                '}';
    }
}
