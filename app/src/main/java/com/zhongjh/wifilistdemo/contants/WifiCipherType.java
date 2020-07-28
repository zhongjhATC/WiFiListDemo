package com.zhongjh.wifilistdemo.contants;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
        WifiCipherType.WIFICIPHER_WEP,
        WifiCipherType.WIFICIPHER_WPA,
        WifiCipherType.WIFICIPHER_NOPASS,
        WifiCipherType.WIFICIPHER_INVALID})
@Retention(RetentionPolicy.SOURCE)
public @interface WifiCipherType {
    String WIFICIPHER_WEP = "WIFICIPHER_WEP";
    String WIFICIPHER_WPA = "WIFICIPHER_WPA";
    String WIFICIPHER_NOPASS = "WIFICIPHER_NOPASS";
    String WIFICIPHER_INVALID = "WIFICIPHER_INVALID";
}
