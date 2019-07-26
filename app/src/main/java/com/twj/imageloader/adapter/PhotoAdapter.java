package com.twj.imageloader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hust_twj.imageloderlibrary.ImageLoader;
import com.hust_twj.imageloderlibrary.listener.ImageLoadListener;
import com.twj.imageloader.R;
import com.twj.imageloader.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hust_twj
 * @date 2019/6/11
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mDataList;

    public PhotoAdapter(Context context) {
        mContext = context;

        initData();
    }

    private void initData() {
        ArrayList<String> mDataList = new ArrayList<>();
        mDataList.add("");

        setDataList(mDataList);
    }

    @NotNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_photo_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull final PhotoAdapter.ViewHolder holder, final int position) {
        final String url = mDataList.get(position);

        holder.mTvPosition.setText(String.valueOf(position));
        holder.mTvPhoto.post(new Runnable() {
            @Override
            public void run() {
                ImageLoader.with()
                        .load(url, holder.mTvPhoto)
               /* .onLoadListener(new ImageLoadListener() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, String uri) {

                    }

                    @Override
                    public void onFailure(Exception e) {
                        LogUtils.e("error_twj123" , " 错误位置：  " + position + "   " + e.toString());

                    }
                })*/;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public void setDataList(List<String> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public void addData(List<String> dataList) {
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvPosition;
        ImageView mTvPhoto;

        public ViewHolder(final View itemView) {
            super(itemView);
            mTvPosition = itemView.findViewById(R.id.tv_position);
            mTvPhoto = itemView.findViewById(R.id.iv_photo);
        }
    }

}
