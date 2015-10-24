package nanowrimo.onishinji.event;

import nanowrimo.onishinji.model.User;

/**
 * Created by Silwek on 17/10/15.
 */
public class UserEvent {

    protected User mUser;

    public UserEvent(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

}
