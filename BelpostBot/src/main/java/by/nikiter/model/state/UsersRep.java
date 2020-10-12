package by.nikiter.model.state;

import by.nikiter.model.ParserJSON;
import by.nikiter.model.belpost.PostTracker;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;

public class UsersRep {

    private final Map<User,UserState> userStateMap;

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
        userStateMap = new HashMap<User, UserState>();
    }

    public UserState addUser(User user) {
        return userStateMap.put(user, UserState.USING_BOT);
    }

    public UserState getUserState(User user) {
        return userStateMap.get(user);
    }

    public UserState updateUserState(User user, UserState state) {
        return userStateMap.replace(user,state);
    }
}
