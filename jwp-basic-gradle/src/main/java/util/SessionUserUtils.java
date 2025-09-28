package util;

import model.User;

import javax.servlet.http.HttpSession;
import java.util.Objects;

public class SessionUserUtils {
    public static final String USER_SESSION_KEY = "user";

    public static User getUserFromSession(HttpSession session){
        Object user = session.getAttribute(USER_SESSION_KEY);

        if(user == null){
            return null;
        }
        return (User)user;
    }

    public static boolean isLoggedIn(HttpSession session){
        return getUserFromSession(session) != null;
    }

    public static boolean isSameUser(HttpSession session, User user){
        if(!isLoggedIn(session) || user == null){
            return false;
        }

        return user.isSameUser(Objects.requireNonNull(getUserFromSession(session)));
    }
}
