package order.smzs.com.companyorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import order.smzs.com.companyorder.download.MainActivity3;
import order.smzs.com.companyorder.image.SmartImageView;
import order.smzs.com.companyorder.model.AppUtils;
import order.smzs.com.companyorder.model.Singleton;
import order.smzs.com.companyorder.util.AnimDownloadProgressButton;
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
    private AnimDownloadProgressButton mAnimDownloadProgressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nav_view = navigationView.inflateHeaderView(R.layout.nav_nologin_header_main);
        navigationView.inflateHeaderView(R.layout.nav_header_main);


        start();


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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        if(Constants.ISLOGIN){
            linearLayout2.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.GONE);
            mImageView.setImageUrl(Singleton.getInstance().user_img);
            header_tv1.setText(Singleton.getInstance().user_nickname);
            header_tv2.setText(Singleton.getInstance().h_Name);
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
                    JSONObject jo = new JSONObject(result);
                    if ("200".equals(jo.getString("retcode"))) {//当前已经是最新版本
                        Toast.makeText(MainActivity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if ("300".equals(jo.getString("retcode"))) {//参数传递错误
                        Toast.makeText(MainActivity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if ("100".equals(jo.getString("retcode"))) {//发现新版本
                        Toast.makeText(MainActivity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();

                        MainActivity3.startAct(MainActivity.this);

                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void start() {
        mAnimDownloadProgressButton = (AnimDownloadProgressButton)findViewById(R.id.anim_btn);
        mAnimDownloadProgressButton.setCurrentText("安装");
        mAnimDownloadProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTheButton();
            }
        });

    }

    private void showTheButton() {
        mAnimDownloadProgressButton.setState(AnimDownloadProgressButton.DOWNLOADING);
        mAnimDownloadProgressButton.setProgressText("下载中", mAnimDownloadProgressButton.getProgress() + 8);

        if (mAnimDownloadProgressButton.getProgress() + 10 > 100) {
            mAnimDownloadProgressButton.setState(AnimDownloadProgressButton.INSTALLING);
            mAnimDownloadProgressButton.setCurrentText("安装中");
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mAnimDownloadProgressButton.setState(AnimDownloadProgressButton.NORMAL);
                    mAnimDownloadProgressButton.setCurrentText("打开");
                }
            }, 2000);   //2秒
        }
    }
}
