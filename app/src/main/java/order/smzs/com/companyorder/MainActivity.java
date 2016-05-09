package order.smzs.com.companyorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import order.smzs.com.companyorder.util.Constants;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTextView;
    private LinearLayout linearLayout;
    private RelativeLayout linearLayout1;
    private ImageView mImageView;

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


        View ss = navigationView.getHeaderView(0);
        linearLayout = (LinearLayout) ss.findViewById(R.id.login_sc);
        mImageView = (ImageView) ss.findViewById(R.id.imageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login_Register_Activity.startAct(MainActivity.this);
            }
        });

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
            // 写入sharedpreferences
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
            MainActivity.this.
                    startActivity(
                            new Intent(MainActivity.this, order.smzs.com.companyorder.pull.PtrrvListViewMode.class));
        } else if (id == R.id.nav_gallery) {
            //订餐信息查询
        } else if (id == R.id.nav_slideshow) {
            //订餐信息修改
        } else if (id == R.id.nav_manage) {
            //餐厅列表


            MainActivity.this.
                    startActivity(
                            new Intent(MainActivity.this, order.smzs.com.companyorder.pull.PtrrvListViewMode.class));
        } else if (id == R.id.nav_share) {
            //修改昵称
        } else if (id == R.id.nav_send) {
            //修改密码
            UpdatePassWord.startAct(MainActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        if(Constants.ISLOGIN){
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.GONE);
        }
        if(!Constants.ISLOGIN){
            linearLayout.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }
}
