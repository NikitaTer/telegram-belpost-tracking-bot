package by.nikiter.model.state;

import by.nikiter.model.UserChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;

public class UsersRep {

    private final Map<UserChat, UserState> userStateMap;

    private static volatile  UsersRep instance = null;

    public static UsersRep getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (UsersRep.class) {
            if (instance == null) {
                instance = new UsersRep();
            }
            return instance;
        }
    }

    private UsersRep() {
        userStateMap = new HashMap<UserChat, UserState>();
    }

    public void addUser(User user, Chat chat) {
        userStateMap.put(new UserChat(user,chat), UserState.USING_BOT);
    }

    public boolean hasUser(User user) {
        boolean isContained = false;
        for (UserChat userChat : userStateMap.keySet()) {
            if (userChat.getUser().equals(user)) {
                isContained = true;
                break;
            }
        }
        return isContained;
    }

    public UserState getUserState(User user) {
        UserState userState = null;
        for (Map.Entry<UserChat,UserState> entry : userStateMap.entrySet()) {
            if (entry.getKey().getUser().equals(user)) {
                userState = entry.getValue();
                break;
            }
        }
        return userState;
    }

    public void setUserState(User user, UserState state) {
        for (UserChat uc : userStateMap.keySet()) {
            if (uc.getUser().equals(user)) {
                userStateMap.replace(uc,state);
                break;
            }
        }
    }

    public Chat getChat(User user) {
        Chat chat = null;
        for (UserChat userChat : userStateMap.keySet()) {
            if (userChat.getUser().equals(user)) {
                chat = userChat.getChat();
            }
        }
        return chat;
    }

    public void setChat(User user, Chat chat) {
        for (UserChat userChat : userStateMap.keySet()) {
            if (userChat.getUser().equals(user)) {
                userChat.setChat(chat);
            }
        }
    }
}
