package nantes.crous.scrachx.soldecrousnantes;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ssl.SSLSocketFactory;

public class CrousRestClient {

    private static final String BASE_URL = "https://rechargement.crous-nantes.fr/CrousVAD";

    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.addHeader("Referer", "https://rechargement.crous-nantes.fr/CrousVAD/ihm/selectPayment.jsp");
        client.addHeader("Origin", "https://rechargement.crous-nantes.fr");
        client.setTimeout(5000);
        client.setResponseTimeout(10000);
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        client.addHeader("Referer", "https://rechargement.crous-nantes.fr/CrousVAD/ihm/authentication.jsp");
        client.addHeader("Origin", "https://rechargement.crous-nantes.fr");
        client.setTimeout(5000);
        client.setResponseTimeout(10000);
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static String getAbsoluteUrl(String relativeUrl){
        return BASE_URL + relativeUrl;
    }


}
