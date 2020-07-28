package com.zhongjh.wifilistdemo.contants;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@StringDef({
        WifiContants.WIFI_STATE_CONNECT,
        WifiContants.WIFI_STATE_ON_CONNECTING,
        WifiContants.WIFI_STATE_UNCONNECT})
@Retention(RetentionPolicy.SOURCE)
public @interface WifiContants {
    String WIFI_STATE_CONNECT = "已连接";
    String WIFI_STATE_ON_CONNECTING = "正在连接";
    String WIFI_STATE_UNCONNECT = "未连接";
}
