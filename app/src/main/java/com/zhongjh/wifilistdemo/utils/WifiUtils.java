package com.zhongjh.wifilistdemo.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import com.zhongjh.wifilistdemo.App;
import com.zhongjh.wifilistdemo.contants.WifiCipherType;

import java.util.ArrayList;
import java.util.List;

public class WifiUtils {

    /**
     * 获取当前连接wifi实体
     */
    public static WifiInfo getConnectedWifiInfo() {
        WifiManager wifimanager = (WifiManager) App.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifimanager.getConnectionInfo();
    }

    /**
     * 扫描之前需要刷新附近的Wifi列表，这需要使用startScan方法
     * 谷歌现在已经记录了startScan()函数在Android P中的限制:
     * “我们将进一步限制扫描应用程序的数量，以提高网络性能和电池寿命。
     * startscan()的使用限制为:-每个前台应用程序被限制为每2分钟扫描4次。所有后台应用程序加起来只能每30分钟扫描一次。”
     */
    public static void scanStart() {
        WifiManager wifimanager = (WifiManager) App.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifimanager.startScan();
    }

    /**
     * @return 返回wifi列表
     */
    public static List<ScanResult> getWifiScanResult() {
        WifiManager wifimanager = (WifiManager) App.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifimanager != null) {
            return wifimanager.getScanResults();
        }
        return new ArrayList<>();
    }

    /**
     * 创建一个新的WI-FI网络信息
     *
     * @param SSID     ssid
     * @param password 密码
     * @param type     wifi的几种加密方式
     * @return WI-FI网络信息
     */
    public static WifiConfiguration createWifiConfig(String SSID, String password, @WifiCipherType String type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        // 对输入的配置设置EAP加密方式

        if (type.equals(WifiCipherType.WIFICIPHER_NOPASS)) {
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.clear();
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }

        if (type.equals(WifiCipherType.WIFICIPHER_WEP)) {
//            config.preSharedKey = "\"" + password + "\"";
//            config.hiddenSSID = true;
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;

            // WEP Security
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

            if (getHexKey(password)) config.wepKeys[0] = password;
            else config.wepKeys[0] = "\"".concat(password).concat("\"");
            config.wepTxKeyIndex = 0;
        }

        if (type.equals(WifiCipherType.WIFICIPHER_WPA)) {
//            config.preSharedKey = "\"" + password + "\"";
//            config.hiddenSSID = true;
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//            config.status = WifiConfiguration.Status.ENABLED;

            // wpa
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.preSharedKey = "\"".concat(password).concat("\"");
        }

        return config;
    }

    /**
     * 接入某个wifi热点
     */
    public static boolean addNetWork(WifiConfiguration config) {
        WifiManager wifimanager = (WifiManager) App.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wifimanager.getConnectionInfo();
        if (null != wifiinfo) {
            wifimanager.disableNetwork(wifiinfo.getNetworkId());
        }
        boolean result;
        if (config.networkId > 0) {
            result = wifimanager.enableNetwork(config.networkId, true);
            wifimanager.updateNetwork(config);
        } else {
            int i = wifimanager.addNetwork(config);
            result = false;
            if (i > 0) {
                wifimanager.saveConfiguration();
                return wifimanager.enableNetwork(i, true);
            }
        }
        return result;
    }

    /**
     * 打开WIFI
     */
    public static void openWifi() {
        WifiManager wifimanager = (WifiManager) App.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifimanager.isWifiEnabled()) {
            wifimanager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WIFI
     */
    public static void closeWifi() {
        WifiManager wifimanager = (WifiManager) App.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifimanager.isWifiEnabled()) {
            wifimanager.setWifiEnabled(false);
        }
    }

    /**
     * @return 是否打开wifi
     */
    public static boolean isOpenWifi() {
        WifiManager wifimanager = (WifiManager) App.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean b = false;
        if (wifimanager != null) {
            b = wifimanager.isWifiEnabled();
        }
        return b;
    }

    /**
     * 去除同名WIFI
     *
     * @param scanResults 需要去除同名的列表
     * @return 返回不包含同命的列表
     */
    public static List<ScanResult> noSameName(List<ScanResult> scanResults) {
        List<ScanResult> newScanResults = new ArrayList<>();
        for (ScanResult result : scanResults) {
            if (!TextUtils.isEmpty(result.SSID) && !containName(newScanResults, result.SSID))
                newScanResults.add(result);
        }
        return newScanResults;
    }

    /**
     * 判断一个扫描结果中，是否包含了某个名称的WIFI
     *
     * @param sr   扫描结果
     * @param name 要查询的名称
     * @return 返回true表示包含了该名称的WIFI，返回false表示不包含
     */
    public static boolean containName(List<ScanResult> sr, String name) {
        for (ScanResult result : sr) {
            if (!TextUtils.isEmpty(result.SSID) && result.SSID.equals(name))
                return true;
        }
        return false;
    }

    /**
     * 判断wifi热点支持的加密方式
     */
    public static String getWifiCipher(@WifiCipherType String s) {
        if (s.isEmpty()) {
            return WifiCipherType.WIFICIPHER_INVALID;
        } else if (s.contains("WEP")) {
            return WifiCipherType.WIFICIPHER_WEP;
        } else if (s.contains("WPA") || s.contains("WPA2") || s.contains("WPS")) {
            return WifiCipherType.WIFICIPHER_WPA;
        } else {
            return WifiCipherType.WIFICIPHER_NOPASS;
        }
    }

    /**
     * 查看以前是否也配置过这个网络
     *
     * @param SSID SSID
     */
    public static WifiConfiguration isExsits(String SSID) {
        WifiManager wifimanager = (WifiManager) App.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifimanager == null)
            return null;
        List<WifiConfiguration> existingConfigs = wifimanager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 返回level 等级
     */
    public static int getLevel(int level) {
        if (Math.abs(level) < 50) {
            return 1;
        } else if (Math.abs(level) < 75) {
            return 2;
        } else if (Math.abs(level) < 90) {
            return 3;
        } else {
            return 4;
        }
    }

    private static boolean getHexKey(String s) {
        if (s == null) {
            return false;
        }

        int len = s.length();
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                continue;
            }
            return false;
        }
        return true;
    }


}
