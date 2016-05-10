package order.smzs.com.companyorder.pull;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by linhonghong on 2015/11/13.
 */
public class PtrrvBaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{
    protected LayoutInflater mInflater;
    protected int mCount = 0;
    protected Context mContext = null;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_HISVIDEO = 1;
    public static final int TYPE_MESSAGE = 2;

    public PtrrvBaseAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    public void setCount(int count){
        mCount = count;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public Object getItem(int position){
        return null;
    }

    public interface OnItemClickLitener{
        void onItemClick(View view, int position);
    }


    private OnItemClickLitener mOnItemClickLitener;


    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public void onBindViewHolder(final VH holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                mOnItemClickLitener.onItemClick(holder.itemView, pos);
            }
        });
    }
}

