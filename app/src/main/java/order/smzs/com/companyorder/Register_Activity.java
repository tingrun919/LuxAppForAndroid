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

import java.text.SimpleDateFormat;
import java.util.Date;

import order.smzs.com.companyorder.image.SmartImageView;
import order.smzs.com.companyorder.util.EncrypMD5;
import order.smzs.com.companyorder.util.HttpUtils_new;
import order.smzs.com.companyorder.util.ThreadPoolUtils;


/**
 * Created by Tarn on 2016/5/4.
 */
public class Register_Activity extends AppCompatActivity{

    private EditText et_zh,et_nc,et_mm,et_mm2,et_yzm;
    private Button register_btn;
    private String zh,nc,mm,mm2,yzm,url="http://192.168.19.47/RegisterUser.php",url2="http://192.168.19.47/IsExistNickName.php";
    private SmartImageView imageView;
    private String imageUrl = "http://192.168.19.47/PhoneMessage.php?v_uuid=";
    private JSONObject jsonObject = new JSONObject();
    private int resNum;

    public static void startAct(Activity context){
        Intent intent = new Intent(context,Register_Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_zh = (EditText) findViewById(R.id.et_zh);
        et_nc = (EditText) findViewById(R.id.et_nc);
        et_mm = (EditText) findViewById(R.id.et_mm);
        et_mm2 = (EditText) findViewById(R.id.et_mm2);
        et_yzm = (EditText) findViewById(R.id.et_yzm);

        resNum = getRandomNum();
        imageView = (SmartImageView) this.findViewById(R.id.my_image);
        imageView.setImageUrl(imageUrl+resNum);

        register_btn = (Button) findViewById(R.id.btn_register);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(et_mm.getText().toString())){
                    if(!TextUtils.isEmpty(et_mm2.getText().toString())){
                        if(et_mm.getText().toString().equals(et_mm2.getText().toString())){
                            checkNc();
                        }else{
                            Toast.makeText(Register_Activity.this, "两次密码输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
    }

    /***
     *开始注册
     */
    public void startRegister(){
        zh = et_zh.getText().toString();
        nc = et_nc.getText().toString();
        mm = et_mm.getText().toString();
        mm2 = et_mm2.getText().toString();
        yzm = et_yzm.getText().toString();

        EncrypMD5 md5 = new EncrypMD5();
        String resYzm = md5.encrypt(yzm);
        String resMm2 = md5.encrypt(mm2);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);

        try {
            jsonObject.put("user_Name",zh);
            jsonObject.put("pass_Word",resMm2);
            jsonObject.put("time_date",str);
            jsonObject.put("v_uuid",resNum);
            jsonObject.put("v_code",resYzm);
            jsonObject.put("user_nickname",nc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils_new httpUtils_new = new HttpUtils_new(url,jsonObject,new BackListener());
        ThreadPoolUtils.execute(httpUtils_new);
    }

    class BackListener implements HttpUtils_new.CallbackListener {
        @Override
        public void callBack(String result) {
            if(!TextUtils.isEmpty(result)){
                try {
                    JSONObject jo = new JSONObject(result);
                    if("200".equals(jo.getString("retcode"))){//服务器返回错误
                        Toast.makeText(Register_Activity.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                    }
                    if("300".equals(jo.getString("retcode"))){//参数传递错误
                        Toast.makeText(Register_Activity.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                    }
                    if("400".equals(jo.getString("retcode"))){//验证码输入错误
                        Toast.makeText(Register_Activity.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                    }
                    if("100".equals(jo.getString("retcode"))){//注册成功
                        Toast.makeText(Register_Activity.this, jo.getString("messageCode"), Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent();
                        intent.putExtra("username", nc);
                        intent.putExtra("password", mm2);
                        intent.putExtra("toast", jo.getString("messageCode"));
                        setResult(100, intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取随机数
     * @return 随机5位数
     */
    public int getRandomNum(){
        int resNum = (int) (Math.random()*90000+10000);
        return resNum;
    }

    /***
     * 检查名称是否可用
     */
    public void checkNc(){
        nc = et_nc.getText().toString();
        try {
            jsonObject.put("user_nickname",nc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils_new httpUtils_new = new HttpUtils_new(url2,jsonObject,new BackListener2());
        ThreadPoolUtils.execute(httpUtils_new);
    }

    class BackListener2 implements HttpUtils_new.CallbackListener {
        @Override
        public void callBack(String result) {

            if(!TextUtils.isEmpty(result)){
                try {
                    JSONObject jo = new JSONObject(result);
                    if("200".equals(jo.getString("retcode"))){//服务器返回错误
                        Toast.makeText(Register_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if("300".equals(jo.getString("retcode"))){//参数传递错误
                        Toast.makeText(Register_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if("100".equals(jo.getString("retcode"))){//校验成功
                        if("false".equals(jo.getString("result"))){
                            Toast.makeText(Register_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                        }else{
                            //昵称校验成功
                            startRegister();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(Register_Activity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
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
