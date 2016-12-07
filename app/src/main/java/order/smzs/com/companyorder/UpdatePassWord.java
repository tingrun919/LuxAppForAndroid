package order.smzs.com.companyorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import order.smzs.com.companyorder.Model.Singleton;
import order.smzs.com.companyorder.Util.Constants;
import order.smzs.com.companyorder.Util.EncrypMD5;
import order.smzs.com.companyorder.Util.HttpUtils_new;
import order.smzs.com.companyorder.Util.ThreadPoolUtils;

/**
 * Created by Tarn on 2016/5/5.
 */
public class UpdatePassWord extends AppCompatActivity{

    private EditText ud_mm,ud_mm2;
    private String mm,mm2;
    private Button btn_udpw;
    private JSONObject jsonObject = new JSONObject();

    public static void startAct(Activity context){
        Intent intent = new Intent(context,UpdatePassWord.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatepassword_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("修改密码");

        ud_mm = (EditText) findViewById(R.id.ud_mm);
        ud_mm2 = (EditText) findViewById(R.id.ud_mm2);

        btn_udpw = (Button) findViewById(R.id.btn_udpw);
        btn_udpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUdPw();
            }
        });
    }

    public void startUdPw(){
        mm = ud_mm.getText().toString();
        mm2 = ud_mm2.getText().toString();

        EncrypMD5 encrypMD5 = new EncrypMD5();
        String resMM = encrypMD5.encrypt(mm);
        String resMM2 = encrypMD5.encrypt(mm2);


        try {
            jsonObject.put("user_id", Constants.USERID);
            jsonObject.put("pass_Word",resMM);
            jsonObject.put("n_passWord",resMM2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils_new httpUtils_new = new HttpUtils_new().initWith(String.format("%s%s", Singleton.getInstance().httpServer, "/UpdatePassWord.php"),jsonObject,new BackListener(),UpdatePassWord.this);
        ThreadPoolUtils.execute(httpUtils_new);
    }


    class BackListener implements HttpUtils_new.CallbackListener {
        @Override
        public void callBack(String result) {
            if(!TextUtils.isEmpty(result)){
                try {
                    JSONObject jo = new JSONObject(result);
                    if("200".equals(jo.getString("retcode"))){//服务器返回错误
                        Toast.makeText(UpdatePassWord.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                    }
                    if("300".equals(jo.getString("retcode"))){//参数传递错误
                        Toast.makeText(UpdatePassWord.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                    }
                    if("100".equals(jo.getString("retcode"))){//密码修改成功
                        Toast.makeText(UpdatePassWord.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
