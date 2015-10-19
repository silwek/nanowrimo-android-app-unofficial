package nanowrimo.onishinji.event;

import java.util.ArrayList;

import nanowrimo.onishinji.model.Friend;

/**
 * Created by Amandine Ferrand for Freemo on 18/10/15.
 */
public class FriendsEvent {

    ArrayList<Friend> mList = new ArrayList<Friend>();

    public FriendsEvent(ArrayList<Friend> list) {
        mList = list;
    }


    public ArrayList<Friend> getFriends() {
        return mList;
    }
}
