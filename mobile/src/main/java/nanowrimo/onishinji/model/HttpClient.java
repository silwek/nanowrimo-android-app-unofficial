package nanowrimo.onishinji.model;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by guillaume on 02/11/14.
 */
public class HttpClient {

    public RequestQueue getQueue() {
        return queue;
    }

    private RequestQueue queue;

    /** Constructeur privé */
    private HttpClient()
    {

    }

    public void add(Request request) {
        add(request, false);
    }
    public void add(Request request, Boolean useCache) {

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(useCache);

        if(queue == null) {
            throw new Error("QUEUE UNINITIALIZED");
        }

        queue.add(request);
    }

    /** Holder */
    private static class HttpClientHolder
    {
        /** Instance unique non préinitialisée */
        private final static HttpClient instance = new HttpClient();
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static HttpClient getInstance()
    {
        return HttpClientHolder.instance;
    }

    public void setContext(Context context) {
        queue = Volley.newRequestQueue(context);
    }

}
