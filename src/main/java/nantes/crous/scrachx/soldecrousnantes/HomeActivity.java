package nantes.crous.scrachx.soldecrousnantes;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by scotscriven on 09/10/14.
 */
public class HomeActivity extends Activity {

    private Button mDisconnect;
    private TextView mPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle b = getIntent().getExtras();
        String price = b.getString("price");
        String parsed = price.replace("€", "");
        float fParsed = Float.parseFloat(parsed);

        mDisconnect = (Button) findViewById(R.id.buttonDeco);
        mPrice = (TextView) findViewById(R.id.result_text);

        if(fParsed < 5.0f){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                mPrice.setBackground(getResources().getDrawable(R.drawable.backred));
            }else{
                mPrice.setBackgroundDrawable(getResources().getDrawable(R.drawable.backred));
            }
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                mPrice.setBackground(getResources().getDrawable(R.drawable.backorange));
            }else{
                mPrice.setBackgroundDrawable(getResources().getDrawable(R.drawable.backorange));
            }
        }
        mPrice.setText(price);

        mDisconnect.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View arg0){
                getDisconnect();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    public void getDisconnect(){
        RequestParams params = null;
        CrousRestClient.get("/disconnect", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(myIntent);
                HomeActivity.this.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(myIntent);
                HomeActivity.this.finish();
            }
        });
    }
}
