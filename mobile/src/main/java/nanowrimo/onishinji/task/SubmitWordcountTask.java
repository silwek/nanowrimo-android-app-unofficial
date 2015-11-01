package nanowrimo.onishinji.task;

import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import nanowrimo.onishinji.model.HttpClient;
import nanowrimo.onishinji.utils.EncryptionUtils;
import nanowrimo.onishinji.utils.URLUtils;

/**
 * Created by Silwek on 17/10/15.
 */
public class SubmitWordcountTask extends AsyncTask<Integer, Void, Integer> {

    protected static final int OK = 0;
    protected static final int ERROR = -1;

    protected Callback mCallback;
    protected String mUserid;
    protected String mSecretKey;

    public SubmitWordcountTask(String userid, String secretkey, Callback callback) {
        if (callback == null || userid == null || secretkey == null)
            throw new IllegalArgumentException();
        mCallback = callback;
        mSecretKey = secretkey;
        mUserid = userid;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        final String url = URLUtils.WRITE_API;
        String hash;
        int wordcount = params[0];
        try {
            hash = getHash(mSecretKey, mUserid, String.valueOf(wordcount));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ERROR;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ERROR;
        }

        JSONObject requestparams = new JSONObject();
        try {
            requestparams.put("hash", hash);
            requestparams.put("name", mUserid);
            requestparams.put("wordcount", String.valueOf(wordcount));
        } catch (JSONException e) {
            return ERROR;
        }


        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestparams, future, future);
        HttpClient.getInstance().getQueue().add(request);

        try {
            JSONObject response = future.get(30, TimeUnit.SECONDS);
            return OK;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (integer < OK) {
            mCallback.onError();
        } else {
            mCallback.onSuccess();
        }
    }

    public interface Callback {
        void onSuccess();

        void onError();
    }

    public static boolean runTest() {
        //abc123annabelle23000 => 59d8fb8ba284cf3e4cd671b2df848e2bb9251074
        final String resultExpected = "59d8fb8ba284cf3e4cd671b2df848e2bb9251074";
        try {
            final String result = getHash("abc123", "annabelle", "23000");
            return result.equals(resultExpected);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;

    }

    protected static String getHash(String secretKey, String name, String count) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final String baseString = secretKey + name + count;
        return EncryptionUtils.SHA1(baseString);
    }
}
