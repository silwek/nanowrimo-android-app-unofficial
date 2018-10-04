package nanowrimo.onishinji.task;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.model.User;
import nanowrimo.onishinji.utils.URLUtils;
import nanowrimo.onishinji.utils.WritingSessionHelper;

/**
 * Created by Silwek on 12/10/15.
 */
public class GetFavoriesRankingTask extends AsyncTask<ArrayList<String>, Void, ArrayList<User>> {

    WeakReference<OnFavoritesSortedListener> mListener;

    public GetFavoriesRankingTask(OnFavoritesSortedListener listener) {
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
                User noDataUser = new User();
                noDataUser.setId(usernames.get(i));
                noDataUser.setName(noDataUser.getId());
                noDataUser.setWordcount(User.NO_DATA_WORDCOUNT);
                users.add(noDataUser);
            }
        }

        users = sortList(users);

        return users;
    }

    protected User getUser(String username) {
        if (!TextUtils.isEmpty(username)) {

            final String url = URLUtils.getProjectUrl(WritingSessionHelper.getInstance().getSessionType(), username);

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(url, null, future, future);
            HttpClient.getInstance().getQueue().add(request);

            try {
                JSONObject response = future.get(30, TimeUnit.SECONDS);
                return getUserFrom(response);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return getCacheUser(url);

        }
        return null;
    }

    protected User getCacheUser(String url) {
        // Search from cache
        Cache c = HttpClient.getInstance().getQueue().getCache();
        Cache.Entry entry = c.get(url);
        if (entry != null) {
            // fetch the data from cache
            try {
                c.invalidate(url, true);
                String data = new String(entry.data, "UTF-8");
                return getUserFrom(new JSONObject(data));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected User getUserFrom(JSONObject json) {
        return new User(json);
    }

    protected ArrayList<User> sortList(ArrayList<User> users) {
        if (users.size() > 1) {
            Collections.sort(users, new WordcountDecreaseComparator());
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

    public class WordcountDecreaseComparator implements Comparator<User> {
        @Override
        public int compare(User o1, User o2) {
            return o1.getWordcount() < o2.getWordcount() ? 1 : (o1.getWordcount() == o2.getWordcount() ? 0 : -1);
        }
    }

    public interface OnFavoritesSortedListener {
        void onFavoritesSorted(ArrayList<User> users);
    }
}
