package com.zhongjh.wifilistdemo;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhongjh.wifilistdemo.adapter.WiFiSettingAdapter;
import com.zhongjh.wifilistdemo.bean.WifiBean;
import com.zhongjh.wifilistdemo.broadcastreceiver.WifiBroadcastReceiver;
import com.zhongjh.wifilistdemo.contants.WifiCipherType;
import com.zhongjh.wifilistdemo.contants.WifiConnectType;
import com.zhongjh.wifilistdemo.dialog.WifiLinkDialog;
import com.zhongjh.wifilistdemo.utils.CollectionUtils;
import com.zhongjh.wifilistdemo.utils.WifiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WiFiSettingActivity extends AppCompatActivity {

    // 权限请求码
    private static final int PERMISSION_REQUEST_CODE = 0;
    // 两个权限需要动态申请
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    ViewHolder mViewHolder;
    private boolean mHasPermission; // 是否通过权限
    public WiFiSettingAdapter mWiFiSettingAdapter;
    public List<WifiBean> mRealWifiList = new ArrayList<>();
    private WifiBroadcastReceiver mWifiReceiver;
    public int mConnectType = 0; // 1：连接成功 2：正在连接（如果wifi热点列表发生变需要该字段）
    private Timer timer; // 用于轮询获取wifi列表
    private TimerTask timerTask; // 用于轮询获取wifi列表

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_setting);
        mViewHolder = new ViewHolder(WiFiSettingActivity.this);
        mWiFiSettingAdapter = new WiFiSettingAdapter(this, mRealWifiList);
        init();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播
        mWifiReceiver = new WifiBroadcastReceiver(WiFiSettingActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); // 监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION); // 监听wifi连接状态广播,是否连接了一个有效路由
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION); // 监听wifi列表变化（开启一个热点或者关闭一个热点）
        this.registerReceiver(mWifiReceiver, filter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                break;
            }
        }
        if (hasAllGranted) {
            // 权限请求成功
            init();
        }
    }

    /**
     * 判断权限是否通过，如果通过就初始化
     */
    private void init() {
        mHasPermission = checkPermission();
        if (!mHasPermission && WifiUtils.isOpenWifi()) {
            // 未获取权限，申请权限
            requestPermission();
        } else if (mHasPermission && WifiUtils.isOpenWifi()) {
            // 已经获取权限
            initData();
            startTimeTask();
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
     * 加载
     */
    private void initData() {
        if (WifiUtils.isOpenWifi()) {
            mViewHolder.cbWifiSwitch.setChecked(true);
        } else {
            mViewHolder.cbWifiSwitch.setChecked(false);
        }

        mViewHolder.rvWifi.setLayoutManager(new LinearLayoutManager(this));
        mViewHolder.rvWifi.setAdapter(mWiFiSettingAdapter);
        if (WifiUtils.isOpenWifi() && mHasPermission) {
            sortScaResultView();
        } else {
            Toast.makeText(WiFiSettingActivity.this, "WIFI处于关闭状态或权限获取失败", Toast.LENGTH_SHORT).show();
        }



    }

    /**
     * 初始化事件
     */
    private void initListener() {
        mWiFiSettingAdapter.setOnItemClickListener((view, position, o) -> {
            WifiBean wifiBean = mRealWifiList.get(position);
            if (wifiBean.getState().equals(WifiConnectType.WIFI_STATE_UNCONNECT) || wifiBean.getState().equals(WifiConnectType.WIFI_STATE_CONNECT)) {
                String capabilities = mRealWifiList.get(position).getCapabilities();
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
                    WifiLinkDialog linkDialog = new WifiLinkDialog(WiFiSettingActivity.this, R.style.dialog_download, mRealWifiList.get(position).getWifiName(), mRealWifiList.get(position).getCapabilities());
                    if (!linkDialog.isShowing()) {
                        linkDialog.show();
                    }
                }
            }
        });
        mViewHolder.cbWifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // 打开wifi
                WifiUtils.openWifi();
                // 显示view
                mViewHolder.llWiFi.setVisibility(View.VISIBLE);
                WifiUtils.scanStart();
                wifiListChange();
            } else {
                // 关闭wifi
                WifiUtils.closeWifi();
                // 隐藏view
                mViewHolder.llWiFi.setVisibility(View.GONE);
                // 清除数据
                mRealWifiList.clear();
                mWiFiSettingAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取wifi列表然后将bean转成自己定义的WifiBean
     */
    public void sortScaResultView() {
        Observable.create((ObservableOnSubscribe<List<WifiBean>>) emitter -> {
            if (!emitter.isDisposed()) {
                emitter.onNext(sortScaResult());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<WifiBean>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<WifiBean> s) {
                        mWiFiSettingAdapter.setData(s);
                        mWiFiSettingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 获取wifi列表然后将bean转成自己定义的WifiBean
     */
    private List<WifiBean> sortScaResult() {
        List<ScanResult> scanResults = WifiUtils.noSameName(WifiUtils.getWifiScanResult());
        List<WifiBean> realWifiList = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(scanResults)) {
            for (int i = 0; i < scanResults.size(); i++) {
                WifiBean wifiBean = new WifiBean();
                wifiBean.setWifiName(scanResults.get(i).SSID); // wifi名字
                wifiBean.setState(WifiConnectType.WIFI_STATE_UNCONNECT); // 只要获取都假设设置成未连接，真正的状态都通过广播来确定
                wifiBean.setCapabilities(scanResults.get(i).capabilities); // 加密方式
                wifiBean.setLevel(WifiUtils.getLevel(scanResults.get(i).level));
                realWifiList.add(wifiBean);

                // 根据信号等级排序
                Collections.sort(realWifiList);
            }
        }
        return realWifiList;
    }

    /**
     * 轮询获取wifi列表
     */
    private void startTimeTask() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    WifiUtils.scanStart();
                    wifiListChange();
                }
            };
        }
        timer.schedule(timerTask, 0, 2 * 1000);
    }

    /**
     * 网络状态发生改变
     */
    public void wifiListChange() {
        Observable.create((ObservableOnSubscribe<List<WifiBean>>) emitter -> {
            if (!emitter.isDisposed()) {
                List<WifiBean> wifiBeans = sortScaResult();
                WifiInfo connectedWifiInfo = WifiUtils.getConnectedWifiInfo();
                if (connectedWifiInfo != null) {
                    wifiBeans = wifiListSet(wifiBeans, connectedWifiInfo.getSSID(), mConnectType);
                }
                emitter.onNext(wifiBeans);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<WifiBean>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<WifiBean> s) {
                        mWiFiSettingAdapter.setData(s);
                        mWiFiSettingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    public void wifiListSetView(String wifiName, int type) {
        wifiListSet(mRealWifiList, wifiName, type);
        mWiFiSettingAdapter.notifyDataSetChanged();
    }

    /**
     * 将"已连接"或者"正在连接"的wifi热点放置在第一个位置
     *
     * @param wifiName wifiName
     * @param type     连接状态
     */
    private List<WifiBean> wifiListSet(List<WifiBean> realWifiList, String wifiName, int type) {
        int index = -1;
        WifiBean wifiInfo = new WifiBean();
        if (CollectionUtils.isNullOrEmpty(realWifiList)) {
            return null;
        }
        for (int i = 0; i < realWifiList.size(); i++) {
            realWifiList.get(i).setState(WifiConnectType.WIFI_STATE_UNCONNECT);
        }
        // 根据信号强度排序
        Collections.sort(realWifiList);
        // 拿到相同的wifiName
        for (int i = 0; i < realWifiList.size(); i++) {
            WifiBean wifiBean = realWifiList.get(i);
            if (index == -1 && ("\"" + wifiBean.getWifiName() + "\"").equals(wifiName)) {
                index = i;
                wifiInfo.setLevel(wifiBean.getLevel());
                wifiInfo.setWifiName(wifiBean.getWifiName());
                wifiInfo.setCapabilities(wifiBean.getCapabilities());
                if (type == 1) {
                    wifiInfo.setState(WifiConnectType.WIFI_STATE_CONNECT);
                } else {
                    wifiInfo.setState(WifiConnectType.WIFI_STATE_ON_CONNECTING);
                }
            }
        }
        // 迁移到第一个位置
        if (index != -1) {
            realWifiList.remove(index);
            realWifiList.add(0, wifiInfo);
        }
        return realWifiList;
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

    /**
     * 显示等待view
     */
    public void showProgressBar() {
        mViewHolder.progressWifi.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏等待view
     */
    public void hidingProgressBar() {
        mViewHolder.progressWifi.setVisibility(View.GONE);
    }

    public static class ViewHolder {

        public CheckBox cbWifiSwitch;
        public ProgressBar progressWifi;
        public RecyclerView rvWifi;
        public LinearLayout llWiFi;

        public ViewHolder(WiFiSettingActivity rootView) {
            this.cbWifiSwitch = (CheckBox) rootView.findViewById(R.id.cb_wifi_switch);
            this.progressWifi = (ProgressBar) rootView.findViewById(R.id.progress_wifi);
            this.rvWifi = (RecyclerView) rootView.findViewById(R.id.rv_wifi);
            this.llWiFi = rootView.findViewById(R.id.llWiFi);
        }

    }

}
