package com.zhongjh.wifilistdemo.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.zhongjh.wifilistdemo.WiFiSettingActivity;
import com.zhongjh.wifilistdemo.contants.WifiConnectType;
import com.zhongjh.wifilistdemo.utils.WifiUtils;

//监听wifi状态
public class WifiBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiBroadcastReceiver";
    private WiFiSettingActivity mWiFiSettingActivity;

    public WifiBroadcastReceiver(WiFiSettingActivity wiFiSettingActivity) {
        mWiFiSettingActivity = wiFiSettingActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (state) {
                /**
                 * WIFI_STATE_DISABLED    WLAN已经关闭
                 * WIFI_STATE_DISABLING   WLAN正在关闭
                 * WIFI_STATE_ENABLED     WLAN已经打开
                 * WIFI_STATE_ENABLING    WLAN正在打开
                 * WIFI_STATE_UNKNOWN     未知
                 */
                case WifiManager.WIFI_STATE_DISABLED: {
                    Log.d(TAG, "已经关闭");
                    Toast.makeText(context, "WIFI处于关闭状态", Toast.LENGTH_SHORT).show();
                    break;
                }
                case WifiManager.WIFI_STATE_DISABLING: {
                    Log.d(TAG, "正在关闭");
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLED: {
                    Log.d(TAG, "已经打开");
                    mWiFiSettingActivity.queryWifiList();
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLING: {
                    Log.d(TAG, "正在打开");
                    break;
                }
                case WifiManager.WIFI_STATE_UNKNOWN: {
                    Log.d(TAG, "未知状态");
                    break;
                }
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            Log.d(TAG, "--NetworkInfo--" + info.toString());
            if (NetworkInfo.State.DISCONNECTED == info.getState()) {
                // wifi没连接上
                Log.d(TAG, "wifi没连接上");
                mWiFiSettingActivity.hidingProgressBar();
                for (int i = 0; i < mWiFiSettingActivity.mRealWifiList.size(); i++) {
                    // 没连接上,将所有的连接状态都置为“未连接”
                    if (mWiFiSettingActivity.mRealWifiList.get(i) != null)
                        mWiFiSettingActivity.mRealWifiList.get(i).setState(WifiConnectType.WIFI_STATE_UNCONNECT);
                }
                mWiFiSettingActivity.mWiFiSettingAdapter.notifyDataSetChanged();
            } else if (NetworkInfo.State.CONNECTED == info.getState()) {
                // wifi连接上了
                Log.d(TAG, "wifi连接上了");
                mWiFiSettingActivity.hidingProgressBar();
                WifiInfo connectedWifiInfo = WifiUtils.getConnectedWifiInfo();
                // 连接成功 跳转界面 传递ip地址
                Toast.makeText(context, "wifi连接上了", Toast.LENGTH_SHORT).show();
                mWiFiSettingActivity.mConnectType = 1;
                mWiFiSettingActivity.wifiListSetView(connectedWifiInfo.getSSID(), mWiFiSettingActivity.mConnectType);
            } else if (NetworkInfo.State.CONNECTING == info.getState()) {
                // 正在连接
                Log.d(TAG, "wifi正在连接");
                mWiFiSettingActivity.showProgressBar();
                WifiInfo connectedWifiInfo = WifiUtils.getConnectedWifiInfo();
                mWiFiSettingActivity.mConnectType = 2;
                mWiFiSettingActivity.wifiListSetView(connectedWifiInfo.getSSID(), mWiFiSettingActivity.mConnectType);
            }
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
            Log.d(TAG, "网络列表变化了");
            mWiFiSettingActivity.queryWifiList();
        }

    }

}
