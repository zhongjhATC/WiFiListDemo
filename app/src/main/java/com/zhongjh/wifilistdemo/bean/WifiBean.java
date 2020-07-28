package com.zhongjh.wifilistdemo.bean;

public class WifiBean implements Comparable<WifiBean> {

    private String wifiName; // wifi名称
    private String level; // wifi信号
    private String state; // 已连接  正在连接  未连接 三种状态
    private String capabilities; // 加密方式

    @Override
    public String toString() {
        return "WifiBean{" +
                "wifiName='" + wifiName + '\'' +
                ", level='" + level + '\'' +
                ", state='" + state + '\'' +
                ", capabilities='" + capabilities + '\'' +
                '}';
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int compareTo(WifiBean o) {
        int level1 = Integer.parseInt(this.getLevel());
        int level2 = Integer.parseInt(o.getLevel());
        return level1 - level2;
    }
}
