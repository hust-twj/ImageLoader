package com.twj.imageloader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twj.imageloader.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hust_twj
 * @date 2019/8/4
 */
public class PicassoPhotoAdapter extends RecyclerView.Adapter<PicassoPhotoAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mDataList;

    public PicassoPhotoAdapter(Context context) {
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
    public PicassoPhotoAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_photo_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull PicassoPhotoAdapter.ViewHolder holder, final int position) {
        final String url = mDataList.get(position);

        holder.mTvPosition.setText(String.valueOf(position));

        Picasso.get()
                .load(url)
                .error(R.drawable.img_error)
                .placeholder(R.drawable.img_place_holder)
                .into(holder.mTvPhoto);
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
