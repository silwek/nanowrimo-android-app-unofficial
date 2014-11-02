package nanowrimo.onishinji.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by guillaume on 02/11/14.
 */
public class HttpClient {

    private RequestQueue queue;

    /** Constructeur privé */
    private HttpClient()
    {

    }

    public void add(Request request) {
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
