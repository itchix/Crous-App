package nantes.crous.scrachx.soldecrousnantes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by scotscriven on 08/10/14.
 */
public class LoginActivity extends Activity {

    private Button mLogin;
    private EditText mId;
    private EditText mPass;
    private CheckBox mSouvenir;
    private ProgressBar mProgress;

    private String mPriceParsed;
    private String mIdText;
    private String mPassText;
    private Boolean mChecked;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mId = (EditText) findViewById(R.id.email);
        mPass = (EditText) findViewById(R.id.password);
        mSouvenir = (CheckBox) findViewById(R.id.checkBoxSouvenir);
        mLogin = (Button) findViewById(R.id.sign_in_button);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);

        ImageView img= (ImageView) findViewById(R.id.imageView);
        img.setImageResource(R.drawable.passimg);

        final SharedPreferences settings = getSharedPreferences("login", 0);


        mIdText = settings.getString("user", "error");
        mPassText = settings.getString("pass", "error");
        mChecked = settings.getBoolean("checked", false);
        mId.setText(mIdText);
        mPass.setText(mPassText);
        mSouvenir.setChecked(mChecked);

        mLogin.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View arg0){
                mIdText = mId.getText().toString();
                mPassText = mPass.getText().toString();

                if(mPassText.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Mot de passe manquant", Toast.LENGTH_LONG).show();
                }else if(mIdText.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Numéro manquant", Toast.LENGTH_LONG).show();
                }else if(mIdText.isEmpty() && mPassText.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Numéro & mot de passe manquant", Toast.LENGTH_LONG).show();
                }

                RequestParams params = new RequestParams();
                params.put("bControlNom", "false");
                params.put("numeroPorteur", mIdText);
                params.put("from", false);
                params.put("actualPassword", mPassText);
                params.put("ctl00$MainContent$btnValid", "Valider");
                params.put("nomPorteur", "");
                params.put("newPassword", "");
                params.put("newPasswordConf", "");

                if(!mIdText.isEmpty() && !mPassText.isEmpty()){
                    if(mSouvenir.isChecked()){
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("user", mIdText);
                        editor.putString("pass", mPassText);
                        editor.putBoolean("checked", true);
                        editor.commit();
                    }
                    mProgress.setVisibility(View.VISIBLE);
                    getLoggedIn(params);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getLoggedIn(RequestParams params){
        CrousRestClient.post("/getInformation", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                mLogin.setClickable(false);
                Toast.makeText(LoginActivity.this, "Récupération...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String htmlNoParsed = new String(responseBody);
                if(htmlNoParsed.contains("function redirect()")){
                    Toast.makeText(LoginActivity.this, "Erreur de login/mdp", Toast.LENGTH_LONG).show();
                }else{
                    String parsed = htmlParsing(htmlNoParsed).replace(" €", "€");
                    String format = "\\d+(.\\d+)?\\u20AC";
                    Pattern p = Pattern.compile(format, Pattern.CASE_INSENSITIVE);

                    try {
                        Matcher m = p.matcher(parsed);
                        while (m.find()) {
                            mPriceParsed = m.group();
                        }
                        Intent myIntent = new Intent(getBaseContext(), HomeActivity.class);
                        myIntent.putExtra("price", mPriceParsed);
                        startActivity(myIntent);
                        LoginActivity.this.finish();
                    } catch(Exception e) {
                        Log.v("err", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Impossible de se connecter", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mLogin.setClickable(true);
                mProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    public String htmlParsing(String htmlNoParsed){
        Document document = Jsoup.parse(htmlNoParsed);
        Element priceTag = document.getElementById("MainContent_lblSolde");
        return priceTag.text();
    }

}
