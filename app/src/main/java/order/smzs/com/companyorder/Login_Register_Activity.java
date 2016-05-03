package order.smzs.com.companyorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
                new Thread(networkTask).start();
            }
        });
    }

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
        }
    };

    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
           String res = RecSmsToPost();
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", res);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };


    private String RecSmsToPost(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pass_Word", "345");
            jsonObject.put("user_id", "1000");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String strUrlPath = "http://192.168.19.47/UserLogin.php";
        String strResult = HttpUtils.sendPostMessage(strUrlPath, jsonObject,"utf-8");
        Log.i("res",strResult);
        return strResult;
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

}
