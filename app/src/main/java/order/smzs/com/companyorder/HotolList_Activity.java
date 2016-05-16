package order.smzs.com.companyorder;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.lhh.ptrrv.library.PullToRefreshRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import order.smzs.com.companyorder.image.SmartImageView;
import order.smzs.com.companyorder.model.Singleton;
import order.smzs.com.companyorder.pull.DemoLoadMoreView;
import order.smzs.com.companyorder.pull.DividerItemDecoration;
import order.smzs.com.companyorder.pull.PtrrvBaseAdapter;
import order.smzs.com.companyorder.util.HttpUtils_new;
import order.smzs.com.companyorder.util.ThreadPoolUtils;

/**
 * Created by Tarn on 2016/5/5.
 */
public class HotolList_Activity extends AppCompatActivity{

    private PullToRefreshRecyclerView mPtrrv;
    private PtrrvAdapter mAdapter;
    private static final int DEFAULT_ITEM_SIZE = 20;
    private static final int ITEM_SIZE_OFFSET = 20;

    private static final int MSG_CODE_REFRESH = 0;
    private static final int MSG_CODE_LOADMORE = 1;

    private static final int TIME = 1000;
    private JSONObject jsonObject = new JSONObject();
    private List<HotelModel> dataSource;
    private int selcetposition;
    private int curposition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("餐厅列表");


        findViews();
        startHttpRequest("/HotolList.php");
    }

    // 网络请求
    private void startHttpRequest(String method){
        String url = String.format("%s%s", Singleton.getInstance().httpServer, method);
        if(method == "/HotolList.php"){
            try {
                jsonObject.put("user_id", Singleton.getInstance().user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {

            HotelModel model = dataSource.get(curposition);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String date = formatter.format(curDate);
                jsonObject.put("user_id", Singleton.getInstance().user_id);
                jsonObject.put("h_indentify", model.h_indentify);
                jsonObject.put("date", date);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        HttpUtils_new httpUtils_new = new HttpUtils_new().initWith(url, jsonObject, new BackListener().initWith(method),HotolList_Activity.this);
        ThreadPoolUtils.execute(httpUtils_new);

    }



    private void findViews(){

        mPtrrv = (PullToRefreshRecyclerView) this.findViewById(R.id.ptrrv);
        mPtrrv.setSwipeEnable(true);//open swipe
        DemoLoadMoreView loadMoreView = new DemoLoadMoreView(this, mPtrrv.getRecyclerView());
        loadMoreView.setLoadmoreString(getString(R.string.demo_loadmore));
        loadMoreView.setLoadMorePadding(100);
        mPtrrv.setLayoutManager(new LinearLayoutManager(this));
        mPtrrv.setPagingableListener(new PullToRefreshRecyclerView.PagingableListener() {
            @Override
            public void onLoadMoreItems() {
                mHandler.sendEmptyMessageDelayed(MSG_CODE_LOADMORE, TIME);
            }
        });
        mPtrrv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(MSG_CODE_REFRESH, TIME);
            }
        });
        mPtrrv.getRecyclerView().addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mPtrrv.addHeaderView(View.inflate(this, R.layout.header, null));
        mPtrrv.setEmptyView(View.inflate(this,R.layout.empty_view,null));
//        mPtrrv.removeHeader();
        mPtrrv.setLoadMoreFooter(loadMoreView);
        mAdapter = new PtrrvAdapter(this);
//        mAdapter.setCount(0);
        mAdapter.setCount(DEFAULT_ITEM_SIZE);
        mAdapter.setOnItemClickLitener(new PtrrvBaseAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

                if (dataSource != null) {
                    for (HotelModel model :
                            dataSource) {
                        model.h_active = "0";
                    }
                    HotelModel tempmodel = dataSource.get(position);
                    tempmodel.h_active = "1";
//                mAdapter.setCount(dataSource.size());
                    mAdapter.notifyItemChanged(selcetposition);
                    mAdapter.notifyItemChanged(position);
                    curposition = position;
                    startHttpRequest("/ActiveHotel.php");
                }
            }
        });
        mPtrrv.setAdapter(mAdapter);
        mPtrrv.onFinishLoading(true, false);
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CODE_REFRESH) {
                startHttpRequest("/HotolList.php");
                mPtrrv.onFinishLoading(true, false);
                mPtrrv.setOnRefreshComplete();
                mPtrrv.onFinishLoading(true, false);
            } else if (msg.what == MSG_CODE_LOADMORE) {
                mPtrrv.onFinishLoading(true, false);
            }
        }
    };

    private class PtrrvAdapter extends PtrrvBaseAdapter<PtrrvAdapter.ViewHolder> {

        public PtrrvAdapter(Context context) {
            super(context);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.hotol_item, parent ,false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            CheckBox checkBox = (CheckBox) holder.itemView.findViewById(R.id.h_check);
            TextView textView = (TextView) holder.itemView.findViewById(R.id.h_Name);
            SmartImageView imageView = (SmartImageView) holder.itemView.findViewById(R.id.h_img);



            if (dataSource != null){
                HotelModel model = dataSource.get(position);
                imageView.setImageUrl(model.h_img);
                if (Integer.valueOf(model.h_active) == 1){
                    checkBox.setChecked(true);
                    selcetposition = position;
                }else {
                    checkBox.setChecked(false);
                }
                textView.setText(model.h_Name);
            }else {
                textView.setText("");
            }

        }

        class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


    private class HotelModel{

        String h_id;
        String h_Name;
        String h_Price;
        String h_indentify;
        String h_active;
        String h_img;

        public HotelModel initWithMessage(String h_id,
                                           String h_Name,
                                           String h_Price,
                                           String h_indentify,
                                           String h_active,
                                           String h_img)
        {
            this.h_active = h_active;
            this.h_id = h_id;
            this.h_img = h_img;
            this.h_indentify = h_indentify;
            this.h_Name = h_Name;
            this.h_Price = h_Price;

            return this;
        }

        public void ModelArrtoArray(JSONArray arr) throws JSONException {
            int i = 0;
            dataSource = new ArrayList<HotelModel>();

            while (i < arr.length()){
                try {
                    JSONObject objc = arr.getJSONObject(i);

                    HotelModel model = new HotelModel().initWithMessage(
                            objc.getString("h_id"),
                            objc.getString("h_Name"),
                            objc.getString("h_Price"),
                            objc.getString("h_indentify"),
                            objc.getString("h_active"),
                            objc.getString("h_img")
                    );
                    dataSource.add(i, model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
    }


    class BackListener implements HttpUtils_new.CallbackListener {

        public String method;

        public BackListener initWith(String method){
            this.method = method;
            return this;
        }

        @Override
        public void callBack(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject jo = new JSONObject(result);
                    if ("200".equals(jo.getString("retcode"))) {//服务器返回错误
                        Toast.makeText(HotolList_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    if ("300".equals(jo.getString("retcode"))) {//参数传递错误
                        Toast.makeText(HotolList_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if ("100".equals(jo.getString("retcode"))) {//登陆成功
                        Toast.makeText(HotolList_Activity.this, jo.getString("code"), Toast.LENGTH_SHORT).show();


                        if (this.method == "/HotolList.php"){
                            JSONArray JsonArr = jo.getJSONObject("result").getJSONArray("info");
                            new HotelModel().ModelArrtoArray(JsonArr);

                            mAdapter.setCount(dataSource.size());
                            mAdapter.notifyDataSetChanged();
                            mPtrrv.setOnRefreshComplete();
                            mPtrrv.onFinishLoading(true, false);
                        }else{

                            HotelModel hotel  = dataSource.get(curposition);
                            Singleton.getInstance().h_indentify = hotel.h_indentify;
                            Singleton.getInstance().h_Name = hotel.h_Name;

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(HotolList_Activity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
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
