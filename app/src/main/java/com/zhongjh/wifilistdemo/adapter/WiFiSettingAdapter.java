package com.zhongjh.wifilistdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhongjh.wifilistdemo.R;
import com.zhongjh.wifilistdemo.bean.WifiBean;
import com.zhongjh.wifilistdemo.contants.WifiContants;

import java.util.List;


public class WiFiSettingAdapter extends RecyclerView.Adapter<WiFiSettingAdapter.ViewHolder> {

    private Context mContext;
    private List<WifiBean> resultList;
    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(WiFiSettingAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public WiFiSettingAdapter(Context mContext, List<WifiBean> resultList) {
        this.mContext = mContext;
        this.resultList = resultList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wifi_setting, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final WifiBean bean = resultList.get(position);
        holder.tvSsid.setText(bean.getWifiName() + "(" + bean.getState() + ")");

        // 可以传递给adapter的数据都是经过处理的，已连接或者正在连接状态的wifi都是处于集合中的首位，所以可以写出如下判断
        if (position == 0 && (WifiContants.WIFI_STATE_ON_CONNECTING.equals(bean.getState()) || WifiContants.WIFI_STATE_CONNECT.equals(bean.getState()))) {
            holder.tvSsid.setTextColor(mContext.getResources().getColor(R.color.homecolor1));
            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.homecolor1));
        } else {
            holder.tvSsid.setTextColor(mContext.getResources().getColor(R.color.gray_home));
            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.gray_home));
        }

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position, bean);
            }
        });
    }

    /**
     * 替换所有数据源
     */
    public void replaceAll(List<WifiBean> datas) {
        if (resultList.size() > 0) {
            resultList.clear();
        }
        resultList.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }


    public interface onItemClickListener {
        void onItemClick(View view, int postion, Object o);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View rootView;
        public TextView tvSsid;
        public TextView tvState;
        public ImageView imgWifi;
        public ImageView imgLock;
        public View vItem;
        public View vBottom;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.tvSsid = (TextView) rootView.findViewById(R.id.tv_ssid);
            this.tvState = (TextView) rootView.findViewById(R.id.tv_state);
            this.imgWifi = (ImageView) rootView.findViewById(R.id.img_wifi);
            this.imgLock = (ImageView) rootView.findViewById(R.id.img_lock);
            this.vItem = (View) rootView.findViewById(R.id.vItem);
            this.vBottom = (View) rootView.findViewById(R.id.vBottom);
        }

    }
}
