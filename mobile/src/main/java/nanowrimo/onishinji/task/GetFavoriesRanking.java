package nanowrimo.onishinji.task;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import nanowrimo.onishinji.model.User;

/**
 * Created by Amandine Ferrand for Freemo on 12/10/15.
 */
public class GetFavoriesRanking extends AsyncTask<ArrayList<String>, Void, ArrayList<User>> {

    WeakReference<OnFavoritesSortedListener> mListener;

    public GetFavoriesRanking(OnFavoritesSortedListener listener) {
        mListener = new WeakReference<>(listener);
    }

    @Override
    protected ArrayList<User> doInBackground(ArrayList<String>... params) {
        final ArrayList<String> usernames = params[0];
        final int nbUsers = usernames.size();
        User u;
        ArrayList<User> users = new ArrayList<>(nbUsers);
        for (int i = 0; i < nbUsers; i++) {
            u = getUser(usernames.get(i));
            if (u != null) {
                users.add(u);
            } else {
                Log.w("GetFavoriesRanking", "user empty for this id : " + usernames.get(i));
            }
        }

        users = sortList(users);

        return users;
    }

    protected User getUser(String username) {
        //TODO
        return null;
    }

    protected ArrayList<User> sortList(ArrayList<User> users) {
        if (users.size() > 0) {
            //TODO
        }
        return users;
    }

    @Override
    protected void onPostExecute(ArrayList<User> users) {
        super.onPostExecute(users);
        if (mListener.get() != null) {
            mListener.get().onFavoritesSorted(users);
        }
    }

    public interface OnFavoritesSortedListener {
        void onFavoritesSorted(ArrayList<User> users);
    }
}
