package order.smzs.com.companyorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import order.smzs.com.companyorder.model.Singleton;
import order.smzs.com.companyorder.mydatepicker.DPCManager;
import order.smzs.com.companyorder.mydatepicker.DPDecor;
import order.smzs.com.companyorder.mydatepicker.DPMode;
import order.smzs.com.companyorder.mydatepicker.DatePicker;
import order.smzs.com.companyorder.util.HttpUtils_new;
import order.smzs.com.companyorder.util.ThreadPoolUtils;

public class MainActivityV3 extends AppCompatActivity implements DatePicker.OnClickSignIn {

    @Bind(R.id.my_datepicker)
    DatePicker myDatepicker;
    private JSONObject jsonObject = new JSONObject();


    private Context mContext;

    private DPCManager dpcManager;
    ArrayList<String> sss = new ArrayList<String>();

    public static void startAct(Activity context,ArrayList<String> list){
        Intent intent = new Intent(context,MainActivityV3.class);
        intent.putStringArrayListExtra("sss",list);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainv3);
        ButterKnife.bind(this);
        mContext = this;
        //加载ActionBar的返回按钮
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        sss = getIntent().getStringArrayListExtra("sss");
        init(sss);
    }

    private void initData() {
        try {
            jsonObject.put("user_id", Singleton.getInstance().user_id);
            jsonObject.put("h_indentify",Singleton.getInstance().h_indentify);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils_new httpUtils_new = new HttpUtils_new().initWith(String.format("%s%s", Singleton.getInstance().httpServer, "/QuerySign.php"),jsonObject,new BackListener(),MainActivityV3.this);
        ThreadPoolUtils.execute(httpUtils_new);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //init();
    }

    private void init(ArrayList<String> sss) {
        dpcManager = DPCManager.getInstance();
        dpcManager.clearnDATE_CACHE(); //清除cache

        //自定义背景绘制示例
//        List<String> tmp = new ArrayList<>();
//        tmp.add("2016-05-3"); //yyyy-M-d
//        tmp.add("2016-05-1");
//        tmp.add("2016-05-9");
//        tmp.add("2016-05-10");
//        tmp.add("2016-05-11");
//        tmp.add("2016-05-12");
        dpcManager.setDecorBG(sss); //预先设置日期背景 一定要在在开始设置

        myDatepicker.setDate(2016, 5); //设置日期

        myDatepicker.setMode(DPMode.NONE); //设置选择模式

        myDatepicker.setFestivalDisplay(false); //是否显示节日
        myDatepicker.setTodayDisplay(true); //是否高亮显示今天
        myDatepicker.setHolidayDisplay(false); //是否显示假期
        myDatepicker.setDeferredDisplay(false); //是否显示补休
        myDatepicker.setIsScroll(false); //是否允许滑动 false表示左右上下都不能滑动  单项设置上下or左右 你需要自己扩展
        myDatepicker.setIsSelChangeColor(true, getResources().getColor(R.color.font_white_one)); //设置选择的日期字体颜色,不然有的背景颜色和默认的字体颜色不搭

        myDatepicker.setLeftTitle(2 + "月"); //左上方text
        myDatepicker.setRightTitle(false); //是否签到
        myDatepicker.setOnClickSignIn(this); //点击签到事件

        //设置预先选中日期的背景颜色
        myDatepicker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorBG(Canvas canvas, Rect rect, Paint paint) {
                paint.setColor(getResources().getColor(R.color.blue));
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 4F, paint);
            }
        });

    }
    /**
     * 退出finish掉当前Activity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void signIn() {
        //动态更新的时候必须  清除cache
        dpcManager.clearnDATE_CACHE(); //清除cache
        //重新设置日期
        List<String> tmp = new ArrayList<>();
        tmp.add("2016-2-20");
        tmp.add("2016-2-21");
        tmp.add("2016-2-22");
        tmp.add("2016-2-25");
        dpcManager.setDecorBG(tmp);

        myDatepicker.setDate(2016, 2);
        myDatepicker.setLeftTitle("2月");
        myDatepicker.setRightTitle(true);

        myDatepicker.setDPDecor(new DPDecor() {
            @Override
            public void drawDecorBG(Canvas canvas, Rect rect, Paint paint) {
                paint.setColor(getResources().getColor(R.color.blue));
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 4F, paint);
            }
        });
        myDatepicker.invalidate(); //刷新
    }

    class BackListener implements HttpUtils_new.CallbackListener {

        @Override
        public void callBack(String result) {
            if(!TextUtils.isEmpty(result)){
                try {
                    JSONObject jo = new JSONObject(result);
                    if("200".equals(jo.getString("retcode"))){//服务器返回错误
                        Toast.makeText(MainActivityV3.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if("300".equals(jo.getString("retcode"))){//参数传递错误
                        Toast.makeText(MainActivityV3.this, jo.getString("code"), Toast.LENGTH_SHORT).show();
                    }
                    if("100".equals(jo.getString("retcode"))){//返回签到天数成功
                        Toast.makeText(MainActivityV3.this, jo.getString("code"), Toast.LENGTH_SHORT).show();

                        jsonObject = jo.getJSONObject("result");

                        JSONArray js = jsonObject.getJSONArray("s_query");

                        ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) getlistForJson(js.toString());

                        for(int i = 0; i<list.size();i++){
                            sss.add(list.get(i).get("s_date").toString());
                        }
                        //init(sss);

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
}
