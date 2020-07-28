package com.zhongjh.wifilistdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhongjh.wifilistdemo.adapter.WiFiSettingAdapter;
import com.zhongjh.wifilistdemo.bean.WifiBean;
import com.zhongjh.wifilistdemo.contants.WifiCipherType;
import com.zhongjh.wifilistdemo.contants.WifiContants;
import com.zhongjh.wifilistdemo.utils.CollectionUtils;
import com.zhongjh.wifilistdemo.utils.WifiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WiFiSettingActivity extends AppCompatActivity {

    // 权限请求码
    private static final int PERMISSION_REQUEST_CODE = 0;
    // 两个权限需要动态申请
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    ViewHolder mViewHolder;
    private boolean mHasPermission; // 是否通过权限
    private WiFiSettingAdapter mWiFiSettingAdapter;
    List<WifiBean> realWifiList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_setting);
        mViewHolder = new ViewHolder(WiFiSettingActivity.this);
        mHasPermission = checkPermission();
        if (!mHasPermission && WifiUtils.isOpenWifi()) {  //未获取权限，申请权限
            requestPermission();
        } else if (mHasPermission && WifiUtils.isOpenWifi()) {  //已经获取权限
            initData();
            initListener();
        } else {
            Toast.makeText(WiFiSettingActivity.this, "WIFI处于关闭状态", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 申请权限
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    /**
     * 加载wifi数据
     */
    private void initData() {
        mWiFiSettingAdapter = new WiFiSettingAdapter(this, realWifiList);
        mViewHolder.rvWifi.setLayoutManager(new LinearLayoutManager(this));
        mViewHolder.rvWifi.setAdapter(mWiFiSettingAdapter);
        if (WifiUtils.isOpenWifi() && mHasPermission) {
            sortScaResult();
        } else {
            Toast.makeText(WiFiSettingActivity.this, "WIFI处于关闭状态或权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化事件
     */
    private void initListener() {
        mWiFiSettingAdapter.setOnItemClickListener(new WiFiSettingAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, Object o) {
                WifiBean wifiBean = realWifiList.get(postion);
                if (wifiBean.getState().equals(WifiContants.WIFI_STATE_UNCONNECT) || wifiBean.getState().equals(WifiContants.WIFI_STATE_CONNECT)) {
                    String capabilities = realWifiList.get(postion).getCapabilities();
                    if (WifiUtils.getWifiCipher(capabilities).equals(WifiCipherType.WIFICIPHER_NOPASS)) {
                        // 无需密码，直接连接
                        WifiConfiguration tempConfig = WifiUtils.isExsits(wifiBean.getWifiName());
                        if (tempConfig == null) {
                            WifiConfiguration exsits = WifiUtils.createWifiConfig(wifiBean.getWifiName(), null, WifiCipherType.WIFICIPHER_NOPASS);
                            WifiUtils.addNetWork(exsits);
                        } else {
                            WifiUtils.addNetWork(tempConfig);
                        }
                    } else {
                        // 需要密码，弹出输入密码dialog
                        noConfigurationWifi(postion);
                    }
                }
            }
        });
    }

    private void noConfigurationWifi(int position) {
        // 之前没配置过该网络， 弹出输入密码界面
        WifiLinkDialog linkDialog = new WifiLinkDialog(this, R.style.dialog_download, realWifiList.get(position).getWifiName(), realWifiList.get(position).getCapabilities());
        if (!linkDialog.isShowing()) {
            linkDialog.show();
        }
    }

    /**
     * 获取wifi列表然后将bean转成自己定义的WifiBean
     */
    public void sortScaResult() {
        List<ScanResult> scanResults = WifiUtils.noSameName(WifiUtils.getWifiScanResult());
        realWifiList.clear();
        if (!CollectionUtils.isNullOrEmpty(scanResults)) {
            for (int i = 0; i < scanResults.size(); i++) {
                WifiBean wifiBean = new WifiBean();
                wifiBean.setWifiName(scanResults.get(i).SSID); // wifi名字
                wifiBean.setState(WifiContants.WIFI_STATE_UNCONNECT); // 只要获取都假设设置成未连接，真正的状态都通过广播来确定
                wifiBean.setCapabilities(scanResults.get(i).capabilities); // 加密方式
                wifiBean.setLevel(WifiUtils.getLevel(scanResults.get(i).level) + "");
                realWifiList.add(wifiBean);

                // 根据信号等级排序
                Collections.sort(realWifiList);
                mWiFiSettingAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * @return 检查是否已经授予权限
     */
    private boolean checkPermission() {
        for (String permission : NEEDED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static class ViewHolder {
        public CheckBox cbWifiSwitch;
        public ProgressBar progressWifi;
        public RecyclerView rvWifi;

        public ViewHolder(WiFiSettingActivity rootView) {
            this.cbWifiSwitch = (CheckBox) rootView.findViewById(R.id.cb_wifi_switch);
            this.progressWifi = (ProgressBar) rootView.findViewById(R.id.progress_wifi);
            this.rvWifi = (RecyclerView) rootView.findViewById(R.id.rv_wifi);
        }

    }
}
