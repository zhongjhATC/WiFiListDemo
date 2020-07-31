package com.zhongjh.wifilistdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.zhongjh.wifilistdemo.R;


public class WifiLinkDialog extends Dialog {

    private ViewHolder mViewHolder;
    private String wifiName;
    private String capabilities;
    private Context mContext;


    public WifiLinkDialog(@NonNull Context context, @StyleRes int themeResId, String wifiName, String capabilities) {
        super(context, themeResId);
        this.wifiName = wifiName;
        this.capabilities = capabilities;
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_wifi_link, null);
        setContentView(view);
        mViewHolder = new ViewHolder(view);
        mViewHolder.tvTitle.setText(wifiName);
        initListener();
    }

    private void initListener() {
        mViewHolder.etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 一些格式识别
                if ((capabilities.contains("WPA") || capabilities.contains("WPA2") || capabilities.contains("WPS"))) {
                    if (mViewHolder.etValue.getText() == null || mViewHolder.etValue.getText().toString().length() < 8) {
                        mViewHolder.tvOK.setOnClickListener(null);
                        mViewHolder.tvOK.setTextColor(getContext().getResources().getColor(R.color.gray_home));
                    } else {
                        mViewHolder.tvOK.setOnClickListener(new OnOKListener());
                        mViewHolder.tvOK.setTextColor(getContext().getResources().getColor(R.color.blue));
                    }
                } else if (capabilities.contains("WEP")) {
                    if (mViewHolder.etValue.getText() == null || mViewHolder.etValue.getText().toString().length() < 8) {
                        mViewHolder.tvOK.setOnClickListener(null);
                        mViewHolder.tvOK.setTextColor(getContext().getResources().getColor(R.color.gray_home));
                    } else {
                        mViewHolder.tvOK.setOnClickListener(new OnOKListener());
                        mViewHolder.tvOK.setTextColor(getContext().getResources().getColor(R.color.blue));
                    }
                }
            }
        });
        mViewHolder.tvClose.setOnClickListener(v -> dismiss());
    }

    /**
     * 点击事件
     */
    private class OnOKListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            WifiUtils.withContext(mContext)
                    .connectWith(wifiName, mViewHolder.etValue.getText().toString())
                    .setTimeout(40000)
                    .onConnectionResult(new ConnectionSuccessListener() {
                        @Override
                        public void success() {
//                            Toast.makeText(mContext, "SUCCESS!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failed(@NonNull ConnectionErrorCode errorCode) {
                            Toast.makeText(mContext, "EPIC FAIL!" + errorCode.toString(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .start();
//            WifiConfiguration tempConfig = WifiUtils.isExsits(wifiName);
//            if (tempConfig == null) {
            // 如果以前没连接过，重新连接
//                WifiConfiguration wifiConfiguration = WifiUtils.createWifiConfig(wifiName, mViewHolder.etValue.getText().toString(), WifiUtils.getWifiCipher(capabilities));
//                WifiUtils.addNetWork(wifiConfiguration);
//            } else {
//                // 直接连接
//                WifiUtils.addNetWork(tempConfig);
//            }
            dismiss();
        }

    }

    public static class ViewHolder {
        public View rootView;
        public TextView tvTitle;
        public TextView tvClose;
        public TextView tvOK;
        public EditText etValue;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.tvTitle = rootView.findViewById(R.id.tvTitle);
            this.tvClose = rootView.findViewById(R.id.tvClose);
            this.tvOK = rootView.findViewById(R.id.tvOK);
            this.etValue = rootView.findViewById(R.id.etValue);
        }

    }
}
