package com.zhongjh.wifilistdemo.contants;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@StringDef({
        WifiConnectType.WIFI_STATE_CONNECT,
        WifiConnectType.WIFI_STATE_ON_CONNECTING,
        WifiConnectType.WIFI_STATE_UNCONNECT})
@Retention(RetentionPolicy.SOURCE)
public @interface WifiConnectType {
    String WIFI_STATE_CONNECT = "已连接";
    String WIFI_STATE_ON_CONNECTING = "正在连接";
    String WIFI_STATE_UNCONNECT = "未连接";
}
