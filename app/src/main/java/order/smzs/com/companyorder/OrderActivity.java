package order.smzs.com.companyorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import order.smzs.com.companyorder.datasave.DemoData;
import order.smzs.com.companyorder.datasave.GoodsDataBaseInterface;
import order.smzs.com.companyorder.datasave.OperateGoodsDataBase;
import order.smzs.com.companyorder.recycler.DividerItemDecoration;
import order.smzs.com.companyorder.recycler.RecyclerViewContentAdapter;
import order.smzs.com.companyorder.recycler.RecyclerViewMenuAdapter;
import order.smzs.com.companyorder.util.GoodsAnimUtil;


/**
 * Created by Tarn on 2016/8/16.
 */
public class OrderActivity extends AppCompatActivity{

    /**
     * 侧边栏菜单RecyclerView
     */
//    @ViewInject(R.id.m_list_menu)
    RecyclerView mRecyclerMenu;
    /**
     * 内容RecyclerView
     */
//    @ViewInject(R.id.m_list_content)
    RecyclerView mRecyclerContent;
    /**
     * 商品总价
     */
//    @ViewInject(R.id.m_list_all_price)
    TextView mListAllPrice;
    /**
     * 物品总数量
     */
//    @ViewInject(R.id.m_list_num)
    TextView mListAllNum;
    /**
     * 侧边栏菜单数据填充器
     */
    private RecyclerViewMenuAdapter mRecyclerViewMenuCommonadapter = null;
    /**
     * 内容数据填充器
     */
    private RecyclerViewContentAdapter mRecyclerViewContentCommonadapter = null;
    private Context mContext;
    /**
     * 存储数据list
     */
    private List<String> stringMenuList = new ArrayList<String>();
    private List<String> stringContentList = new ArrayList<String>();
    public static int SELECTPOSITION = 0;
    /**
     * 数据操作接口
     */
    GoodsDataBaseInterface mGoodsDataBaseInterface = null;
    /**
     * 购物车布局
     */
//    @ViewInject(R.id.m_list_car_lay)
    RelativeLayout mCarLay;


    public static void startAct(Activity context) {
        Intent intent = new Intent(context, OrderActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("我要订餐");

        mContext = this;
        initView();
        setRecyclerView();
        initHttp();
    }
    private void initView() {
        mGoodsDataBaseInterface = OperateGoodsDataBase.getInstance();
        //清空数据库缓存
        mGoodsDataBaseInterface.deleteAll(mContext);

        mRecyclerMenu = (RecyclerView) findViewById(R.id.m_list_menu);
        mRecyclerContent = (RecyclerView) findViewById(R.id.m_list_content);
        mListAllPrice = (TextView) findViewById(R.id.m_list_all_price);
        mListAllNum = (TextView) findViewById(R.id.m_list_num);
        mCarLay = (RelativeLayout) findViewById(R.id.m_list_car_lay);
    }

    /**
     * 设置RecyclerView的布局方式
     */
    private void setRecyclerView() {
        //垂直listview显示方式
        mRecyclerMenu.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerMenu.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        mRecyclerContent.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    /**
     * 模拟网络请求数据
     */
    private void initHttp() {
        for (int i = 0; i < 10; i++) {
            stringMenuList.add("1111");
        }
        for (int i = 0; i < 4; i++) {
            stringContentList.add("2222");
        }
        setMenuCommonadapter();
        setContentCommonadapter();
    }
    /**
     * 菜单列表    数据填充
     */
    private void setMenuCommonadapter() {
        mRecyclerViewMenuCommonadapter = new RecyclerViewMenuAdapter(mContext, stringMenuList);
        mRecyclerMenu.setAdapter(mRecyclerViewMenuCommonadapter);
        mRecyclerViewMenuCommonadapter.setOnItemClickListener(new RecyclerViewMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                SELECTPOSITION = position;
                Log.e("TAG", "SELECTPOSITION:" + SELECTPOSITION);
                mRecyclerViewMenuCommonadapter.notifyDataSetChanged();
                mRecyclerViewContentCommonadapter.notifyDataSetChanged();
                setAll();
            }
            @Override
            public void onItemLongClick(View v, int position) {}
        });
    }
    /**
     * 点击加号和减号的时候设置总数和总价格
     */
    private void setAll() {
        //设置所有购物数量
        if (mGoodsDataBaseInterface.getSecondGoodsNumberAll(mContext, SELECTPOSITION) == 0) {
            mListAllNum.setVisibility(View.GONE);
            mListAllPrice.setText("￥0");
            mListAllNum.setText("0");
        } else {
            mListAllPrice.setText("￥" + mGoodsDataBaseInterface.getSecondGoodsPriceAll(mContext, SELECTPOSITION) + "");
            mListAllNum.setText(mGoodsDataBaseInterface.getSecondGoodsNumberAll(mContext, SELECTPOSITION) + "");
            mListAllNum.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 商品种类列表    数据填充
     */
    private void setContentCommonadapter() {
        mRecyclerViewContentCommonadapter = new RecyclerViewContentAdapter(mContext, stringContentList);
        mRecyclerContent.setAdapter(mRecyclerViewContentCommonadapter);
        mRecyclerViewContentCommonadapter.setOnItemClickListener(new RecyclerViewContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewContentAdapter.ViewHolder holder) {}
            @Override
            public void onItemLongClick(RecyclerViewContentAdapter.ViewHolder holder) {}
            /** 添加 */
            @Override
            public void onItemJiaClick(RecyclerViewContentAdapter.ViewHolder holder) {
                String numText = holder.mNumber.getText().toString().trim();
                /** 点击加号之前还没有数据的时候 */
                if (numText.isEmpty() || numText.equals("0")) {
                    Log.e("TAG", "点击获取信息：SELECTPOSITION--" + SELECTPOSITION + "  DemoData.ListMenu_GOODSID[position]--" + DemoData.ListMenu_GOODSID[holder.getPosition()]);
                    holder.mImgJian.setVisibility(View.VISIBLE);
                    holder.mNumber.setText(mGoodsDataBaseInterface.saveGoodsNumber(mContext, SELECTPOSITION, DemoData.ListMenu_GOODSID[holder.getPosition()], "1", DemoData.ListMenu_PPRICE[holder.getPosition()]) + "");
                    holder.mNumber.setVisibility(View.VISIBLE);
                }/** 点击加号之前有数据的时候 */
                else {
                    holder.mNumber.setText(mGoodsDataBaseInterface.saveGoodsNumber(mContext, SELECTPOSITION, DemoData.ListMenu_GOODSID[holder.getPosition()], String.valueOf(Integer.parseInt(numText) + 1), DemoData.ListMenu_PPRICE[holder.getPosition()]) + "");
                }
                /** 动画 */
                GoodsAnimUtil.setAnim(OrderActivity.this, holder.mImgJia, mCarLay);
                GoodsAnimUtil.setOnEndAnimListener(new onEndAnim());
                /** 统计购物总数和购物总价 */
            }
            /** 减少 */
            @Override
            public void onItemJianClick(RecyclerViewContentAdapter.ViewHolder holder) {
                String numText = holder.mNumber.getText().toString().trim();
                holder.mNumber.setText(mGoodsDataBaseInterface.saveGoodsNumber(mContext, SELECTPOSITION, DemoData.ListMenu_GOODSID[holder.getPosition()], String.valueOf(Integer.parseInt(numText) - 1), DemoData.ListMenu_PPRICE[holder.getPosition()]) + "");
                numText = holder.mNumber.getText().toString().trim();
                /** 减完之后  数据为0 */
                if (numText.equals("0")) {
                    holder.mNumber.setVisibility(View.GONE);
                    holder.mImgJian.setVisibility(View.GONE);
                }
                setAll();
            }
        });
    }
    /**
     * 动画结束后，更新所有数量和所有价格
     */
    class onEndAnim implements GoodsAnimUtil.OnEndAnimListener {
        @Override
        public void onEndAnim() {
            setAll();
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
