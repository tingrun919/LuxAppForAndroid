package order.smzs.com.companyorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.lhh.ptrrv.library.PullToRefreshRecyclerView;

/**
 * Created by Tarn on 2016/5/5.
 */
public class HotolList_Activity extends AppCompatActivity{

    private PullToRefreshRecyclerView mPullToRefreshRecyclerView;


    public static void startAct(Activity context){
        Intent intent = new Intent(context,HotolList_Activity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotollist_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPullToRefreshRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.pv_test);
        mPullToRefreshRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setTitle("试试");
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

}
