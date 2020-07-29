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
import com.zhongjh.wifilistdemo.contants.WifiCipherType;
import com.zhongjh.wifilistdemo.contants.WifiConnectType;
import com.zhongjh.wifilistdemo.utils.WifiUtils;

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

    public void setData(List<WifiBean> data) {
        resultList.clear();
        resultList.addAll(data);
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
        if (position == 0 && (WifiConnectType.WIFI_STATE_ON_CONNECTING.equals(bean.getState()) || WifiConnectType.WIFI_STATE_CONNECT.equals(bean.getState()))) {
            holder.tvSsid.setTextColor(mContext.getResources().getColor(R.color.homecolor1));
            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.homecolor1));
        } else {
            holder.tvSsid.setTextColor(mContext.getResources().getColor(R.color.gray_home));
            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.gray_home));
        }

        // 是否有密码
        if (WifiUtils.getWifiCipher(bean.getCapabilities()).equals(WifiCipherType.WIFICIPHER_NOPASS)) {
            holder.imgLock.setVisibility(View.GONE);
        } else {
            holder.imgLock.setVisibility(View.VISIBLE);
        }

        switch (bean.getLevel()) {
            case 1:
                holder.imgWifi.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_wifi_item_lv5));
                break;
            case 2:
                holder.imgWifi.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_wifi_item_lv4));
                break;
            case 3:
                holder.imgWifi.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_wifi_item_lv3));
                break;
            case 4:
                holder.imgWifi.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_wifi_item_lv2));
                break;
        }

        holder.rootView.setOnClickListener(view -> onItemClickListener.onItemClick(view, position, bean));
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
        void onItemClick(View view, int position, Object o);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
            this.tvSsid = rootView.findViewById(R.id.tv_ssid);
            this.tvState = rootView.findViewById(R.id.tv_state);
            this.imgWifi = rootView.findViewById(R.id.img_wifi);
            this.imgLock = rootView.findViewById(R.id.img_lock);
            this.vItem = rootView.findViewById(R.id.vItem);
            this.vBottom = rootView.findViewById(R.id.vBottom);
        }

    }
}
