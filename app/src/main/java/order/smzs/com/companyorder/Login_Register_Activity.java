package order.smzs.com.companyorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Login_Register_Activity extends AppCompatActivity {

    private Button mButton;
    private TextView  mTextView;

    public static void startAct(Activity context){
        Intent intent = new Intent(context,Login_Register_Activity.class);
        context.startActivity(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mTextView = (TextView) findViewById(R.id.tv_login);


        mButton = (Button) findViewById(R.id.btn_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("pass_Word", "345");
                    jsonObject.put("user_id", "1000");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String strUrlPath = "http://192.168.19.47/UserLogin.php";
                HttpUtils_new HttpUtils_new = new HttpUtils_new(strUrlPath,jsonObject,new BackListener());
                ThreadPoolUtils.execute(HttpUtils_new);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class BackListener implements HttpUtils_new.CallbackListener {

        @Override
        public void callBack(String result) {

            try {
                JSONObject js = new JSONObject(result);
                String res = js.getString("retcode");
                Log.i("TAG",res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
