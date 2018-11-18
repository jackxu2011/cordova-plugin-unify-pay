package com.linkcld.cordova;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.chinaums.pppay.unify.UnifyPayListener;
import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.UnifyPayRequest;


public class Unifypay extends CordovaPlugin implements UnifyPayListener {
    public static String TAG = "cordova-plugin-unify-pay";
    
    public static final String ERROR_WECHAT_NOT_INSTALLED = "未安装微信";
    public static final String ERROR_INVALID_PARAMETERS = "参数格式错误";
    public static final String ERROR_SEND_REQUEST_FAILED = "发送请求失败";
    public static final String ERROR_WECHAT_RESPONSE_COMMON = "普通错误";
    public static final String ERROR_WECHAT_RESPONSE_USER_CANCEL = "用户点击取消并返回";
    public static final String ERROR_WECHAT_RESPONSE_SENT_FAILED = "发送失败";
    public static final String ERROR_WECHAT_RESPONSE_AUTH_DENIED = "授权失败";
    public static final String ERROR_WECHAT_RESPONSE_UNSUPPORT = "微信不支持";
    public static final String ERROR_WECHAT_RESPONSE_UNKNOWN = "未知错误";

    public static final String ERROR_PERMISSION_DENIED_ERROR = "权限请求被拒绝";

    /**
     * 安卓6以上动态权限相关
     */
    private static final int REQUEST_CODE = 1100001;
    
    public static final String[] permissions = { Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE };


    protected static CallbackContext currentCallbackContext;

    private CountDownLatch countDownLatch;

    @Override
    protected void pluginInitialize() {

        super.pluginInitialize();

        UnifyPayPlugin.getInstance(cordova.getActivity()).setListener(this);

        Log.d(TAG, "plugin initialized.");
    }

    public static boolean isAlipayInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo("com.eg.android.AlipayGphone", 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean hasPermissions() {
        for(String p : permissions) {
            if(!cordova.hasPermission(p)) {
                return false;
            }
        }
        return true;
    }

    private void requestPermission() {
        ArrayList<String> permissionsToRequire = new ArrayList<String>();

        for(String p : permissions) {
            if(!cordova.hasPermission(p)) {
                permissionsToRequire.add(p);
            }
        }

        String[] _permissionsToRequire = new String[permissionsToRequire.size()];
        _permissionsToRequire = permissionsToRequire.toArray(_permissionsToRequire);
        cordova.requestPermissions(this, REQUEST_CODE, _permissionsToRequire);
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != REQUEST_CODE) {
            return;
        }
        countDownLatch.countDown();
    }

    @Override
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) {
        Log.i(TAG, "Execute:" + action + " with :" + args.toString());
        
        if(action.equals("pay")) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    if(!hasPermissions()) {
                        countDownLatch = new CountDownLatch(1);
                        requestPermission();
                        try {
                            countDownLatch.await();
                            if(!hasPermissions()) {
                                Log.e(TAG, ERROR_PERMISSION_DENIED_ERROR);
                                getCurrentCallbackContext().error(ERROR_PERMISSION_DENIED_ERROR);
                                return;
                            }
                        } catch (InterruptedException e) {
                            Log.e(TAG, e.getMessage());
                            getCurrentCallbackContext().error(e.getMessage());
                        }
                    }
                    try {
                        String channel = args.getString(0);
                        String payData = args.getString(1);
//                        if(!isAlipayInstalled(cordova.getActivity())) {
//                            callbackContext.error("未安装支付宝");
//                            return;
//                        }
                        sendPaymentRequest(channel, payData, callbackContext);
                    } catch (JSONException e) {
                        Log.i(TAG, e.getMessage());
                        callbackContext.error("参数错误");
                    }
                }
            });
            return true;
        }
        return false;
    }

    protected void sendPaymentRequest(String channel, String payData, CallbackContext callbackContext) {
        currentCallbackContext = callbackContext;
        channel = getChannel(channel);
        if(channel != null)  {
            UnifyPayRequest msg = new UnifyPayRequest();
            msg.payChannel = UnifyPayRequest.CHANNEL_ALIPAY;
            msg.payData = payData;
            UnifyPayPlugin.getInstance(cordova.getActivity()).sendPayRequest(msg);
            sendNoResultPluginResult(callbackContext);
        } else {
            callbackContext.error("参数错误");
        }
    }

    private String getChannel(String channel) {
        switch (channel) {
            case UnifyPayRequest.CHANNEL_ALIPAY: return UnifyPayRequest.CHANNEL_ALIPAY;
            case UnifyPayRequest.CHANNEL_WEIXIN: return UnifyPayRequest.CHANNEL_WEIXIN;
            case UnifyPayRequest.CHANNEL_UMSPAY: return UnifyPayRequest.CHANNEL_UMSPAY;
            default: return null;
        }
    }

    @Override
    public void onResult(String resultCode, String resultInfo) {
        Log.i(TAG, resultInfo);
        if(UnifyPayListener.ERR_OK.equals(resultCode)) {
            getCurrentCallbackContext().success();
        } else {
            getCurrentCallbackContext().error(resultInfo);
        }
    }

    public static CallbackContext getCurrentCallbackContext() {
        return currentCallbackContext;
    }

    private void sendNoResultPluginResult(CallbackContext callbackContext) {
        // send no result and keep callback
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}