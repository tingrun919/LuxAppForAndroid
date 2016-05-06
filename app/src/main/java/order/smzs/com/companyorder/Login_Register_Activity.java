package order.smzs.com.companyorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import order.smzs.com.companyorder.util.Constants;
import order.smzs.com.companyorder.util.EncrypMD5;
import order.smzs.com.companyorder.util.HttpUtils_new;
import order.smzs.com.companyorder.util.ThreadPoolUtils;

public class Login_Register_Activity extends AppCompatActivity {

    private Button mButton, btn_register;
    private EditText userName, passWord;
    private CheckBox isLogin;
    private String userNameValue, passWordValue, MD5passWord;
    private SharedPreferences sp;
    private String url = "http://192.168.19.47/UserLogin.php";
    private JSONObject jsonObject = new JSONObject();

    public static void startAct(Activity context) {
        Intent intent = new Intent(context, Login_Register_Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userName = (EditText) findViewById(R.id.et_username);
        passWord = (EditText) findViewById(R.id.et_password);
        isLogin = (CheckBox) findViewById(R.id.cb_mima);
        mButton = (Button) findViewById(R.id.btn_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Register_Activity.startAct(Login_Register_Activity.this);
                enterSecond();
            }
        });


        isLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isLogin.isChecked()) {
                    //记住密码
                    sp.edit().putBoolean("ISCHECK", true).commit();
                } else {
                    //没有记住密码
                    sp.edit().putBoolean("ISCHECK", false).commit();
                }
            }
        });
        if (sp.getBoolean("ISCHECK", false)) {
            isLogin.setChecked(true);
            userName.setText(sp.getString("USER_NAME", ""));
            passWord.setText(sp.getString("PASSWORD", ""));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * 开始登陆
     */
    public void startLogin() {
        userNameValue = userName.getText().toString();
        passWordValue = passWord.getText().toString();
        EncrypMD5 md5 = new EncrypMD5();
        MD5passWord = md5.encrypt(passWordValue);
        try {
            jsonObject.put("user_id", userNameValue);
            jsonObject.put("pass_Word", MD5passWord);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils_new httpUtils_new = new HttpUtils_new(url, jsonObject, new BackListener());
        ThreadPoolUtils.execute(httpUtils_new);

    }

    class BackListener implements HttpUtils_new.CallbackListener {

        @Override
        public void callBack(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject jo = new JSONObject(result);
                    if ("200".equals(jo.getString("retcode"))) {//服务器返回错误
                        Toast.makeText(Login_Register_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if ("300".equals(jo.getString("retcode"))) {//参数传递错误
                        Toast.makeText(Login_Register_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if ("100".equals(jo.getString("retcode"))) {//登陆成功
                        Toast.makeText(Login_Register_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                        Constants.ISLOGIN = true;
                        Constants.USERID = jo.getJSONObject("result").getString("user_id");
                        editSp();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(Login_Register_Activity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void editSp() {
        //0未登录，1登陆
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("USER_NAME", userNameValue);
        editor.putString("PASSWORD", passWordValue);
        editor.putInt("ISLOGIN", 1);
        editor.commit();
    }

    public void enterSecond() {
        Intent intent=new Intent(this,Register_Activity.class);
        startActivityForResult(intent, 100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100) {
            if(resultCode==100) {
                String username=data.getStringExtra("username");
                String password=data.getStringExtra("password");
                String toast=data.getStringExtra("toast");

                Toast.makeText(Login_Register_Activity.this, toast, Toast.LENGTH_SHORT).show();

                userName.setText(username);
                passWord.setText(password);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("USER_NAME", username);
                editor.putString("PASSWORD", password);
                editor.putInt("ISLOGIN", 1);
                editor.putBoolean("ISCHECK", true);
                editor.commit();
                isLogin.setChecked(true);
                startLogin();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
