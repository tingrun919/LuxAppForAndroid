package order.smzs.com.companyorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import order.smzs.com.companyorder.download.MainActivity3;
import order.smzs.com.companyorder.image.SmartImageView;
import order.smzs.com.companyorder.model.AppUtils;
import order.smzs.com.companyorder.model.Singleton;
import order.smzs.com.companyorder.util.Constants;
import order.smzs.com.companyorder.util.HttpUtils_new;
import order.smzs.com.companyorder.util.ThreadPoolUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextView,header_tv1,header_tv2,header_tv3,header_tv4;
    private LinearLayout linearLayout;
    private RelativeLayout linearLayout1,linearLayout2;
    private SmartImageView mImageView;
    private JSONObject jsonObject = new JSONObject();
    private ArrayList<String> sss = new ArrayList<String>();
    private boolean isExit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setTitle("天天吃啥");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nav_view = navigationView.inflateHeaderView(R.layout.nav_nologin_header_main);
        navigationView.inflateHeaderView(R.layout.nav_header_main);

        View ss = navigationView.getHeaderView(0);
        linearLayout2 = (RelativeLayout) ss.findViewById(R.id.login_sc);
        mImageView = (SmartImageView) ss.findViewById(R.id.main_tx);
//        mImageView.setImageUrl(Singleton.getInstance().user_img);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Myinformation_Activity.startAct(MainActivity.this);
            }
        });
        header_tv1 = (TextView) ss.findViewById(R.id.header_tv1);
        header_tv2 = (TextView) ss.findViewById(R.id.header_tv2);
        header_tv3 = (TextView) ss.findViewById(R.id.header_tv3);

        linearLayout1 = (RelativeLayout) nav_view.findViewById(R.id.login_ng);
        mTextView = (TextView) nav_view.findViewById(R.id.tv_login);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login_Register_Activity.startAct(MainActivity.this);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        // 判断是否第一次启动
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstTime = preferences.getBoolean("first_time", true);
        Log.i("firstTime", firstTime + "");
        if (firstTime) {
            // 写入SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("first_time", false);
            editor.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        SharedPreferences shrae = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = shrae.edit();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            edit.putInt("ISLOGIN",0);
            edit.commit();
            Constants.ISLOGIN = false;
            // 清空单例数据
            Singleton.cleanUserMessage();
            onResume();
            Toast.makeText(MainActivity.this,"退出登录",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //我要订餐
            OrderActivity.startAct(MainActivity.this);
        } else if (id == R.id.nav_gallery) {
            //订餐信息查询
        } else if (id == R.id.nav_slideshow) {
            //订餐信息修改
        } else if (id == R.id.nav_manage) {
            //餐厅列表
            if (Singleton.getInstance().isLogin){
                MainActivity.this.
                        startActivity(
                                new Intent(MainActivity.this, HotolList_Activity.class));
            }else {
                Login_Register_Activity.startAct(MainActivity.this);
            }
        } else if (id == R.id.nav_send) {
            //修改密码
            UpdatePassWord.startAct(MainActivity.this);
        } else if (id == R.id.nav_update) {
            //检查更新
            try {
                jsonObject.put("a_Version", AppUtils.getVersionName(MainActivity.this));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpUtils_new httpUtils_new = new HttpUtils_new().initWith(String.format("%s%s", Singleton.getInstance().httpServer, "/CheckVersion.php"),
                    jsonObject,new BackListener(),
                    MainActivity.this
            );
            ThreadPoolUtils.execute(httpUtils_new);
        } else if(id == R.id.nav_sign){
            //我要签到
            if(Singleton.getInstance().isLogin){
                startSign();
            }else{
                Login_Register_Activity.startAct(MainActivity.this);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startSign() {
        try {
            jsonObject.put("user_id", Singleton.getInstance().user_id);
            jsonObject.put("h_indentify",Singleton.getInstance().h_indentify);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils_new httpUtils_new = new HttpUtils_new().initWith(String.format("%s%s", Singleton.getInstance().httpServer, "/QuerySign.php"),jsonObject,new BackListener2(),MainActivity.this);
        ThreadPoolUtils.execute(httpUtils_new);
    }

    @Override
    protected void onResume() {
        if(Constants.ISLOGIN){
            linearLayout2.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.GONE);
            mImageView.setImageUrl(Singleton.getInstance().user_img);
            header_tv1.setText(Singleton.getInstance().user_nickname);
            header_tv2.setText(Singleton.getInstance().h_Name);
            header_tv3.setText(Singleton.getInstance().e_con_day);
        }
        if(!Constants.ISLOGIN){
            linearLayout2.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }


    class BackListener implements HttpUtils_new.CallbackListener {
        @Override
        public void callBack(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    final JSONObject jo = new JSONObject(result);
                    if ("200".equals(jo.getString("retcode"))) {//当前已经是最新版本
                        Toast.makeText(MainActivity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if ("300".equals(jo.getString("retcode"))) {//参数传递错误
                        Toast.makeText(MainActivity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if ("100".equals(jo.getString("retcode"))) {//发现新版本
                        Toast.makeText(MainActivity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();

                        String message = jo.getString("result");
                        final Intent intent = new Intent();
                        intent.setClass(MainActivity.this, MainActivity3.class);
                        intent.putExtra("result", message);

                        new MaterialDialog.Builder(MainActivity.this)
                                .iconRes(R.drawable.ic_menu_camera)
                                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                                .title("温馨提示")
                                .content(jo.getString("code"))
                                .positiveText("确定")
                                .negativeText("取消")
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        if (which.name() == "POSITIVE"){
                                            startActivity(intent);
                                        }
                                    }
                                })
                                .canceledOnTouchOutside(false)
                                .show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
            }
        }
    }
    class BackListener2 implements HttpUtils_new.CallbackListener {

        @Override
        public void callBack(String result) {
            if(!TextUtils.isEmpty(result)){
                try {
                    JSONObject jo = new JSONObject(result);
                    if("200".equals(jo.getString("retcode"))){//服务器返回错误
                        Toast.makeText(MainActivity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if("300".equals(jo.getString("retcode"))){//参数传递错误
                        Toast.makeText(MainActivity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if("100".equals(jo.getString("retcode"))){//返回签到天数成功
                        Toast.makeText(MainActivity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                        String isQd = jo.getJSONObject("result").getString("is_Sign");

                        jsonObject = jo.getJSONObject("result");

                        JSONArray js = jsonObject.getJSONArray("s_query");

                        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) getlistForJson(js.toString());
                        sss.clear();
                        for(int i = 0; i<list.size();i++){
                            sss.add(list.get(i).get("s_date").toString());
                    }


                        MainActivityV3.startAct(MainActivity.this,sss,isQd);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static Map<String, Object> getMapForJson(String jsonStr){
        JSONObject jsonObject ;
        try {
            jsonObject = new JSONObject(jsonStr);

            Iterator<String> keyIter= jsonObject.keys();
            String key;
            Object value ;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext()) {
                key = keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> getlistForJson(String jsonStr){
        List<Map<String, Object>> list = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObj ;
            list = new ArrayList<Map<String, Object>>();
            for(int i = 0 ; i < jsonArray.length() ; i ++){
                jsonObj = (JSONObject)jsonArray.get(i);
                list.add(getMapForJson(jsonObj.toString()));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    /* 退出程序 开始 */
    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            //如果不是自动登陆的状态，则把标签改为Null;
            Constants.ISLOGIN = false;
            finish();
            System.exit(0);
        }
    }
}
