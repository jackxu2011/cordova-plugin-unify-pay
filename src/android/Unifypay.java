package com.linkcld.cordova.plugin;

import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chinaums.pppay.unify.UnifyPayListener;
import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.UnifyPayRequest;
import com.chinaums.pppay.unify.WXPayResultListener;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


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

    protected static CallbackContext currentCallbackContext;
    protected static IWXAPI wxApi;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        UnifyPayPlugin.getInstance(cordova.getActivity()).setListener(this);
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, "Execute:" + action + " with :" + args.toString());
        
        if(action.equals("pay")) {
            String channel = args.getString(0);
            String payData = args.getString(1);
            return sendPaymentRequest(channel, payData, callbackContext);
        }
        return false;
    }

    protected boolean sendPaymentRequest(String channel, String payData, CallbackContext callbackContext) {
        try {
            currentCallbackContext = callbackContext;
            UnifyPayRequest msg = new UnifyPayRequest();
            msg.payChannel = channel;
            msg.payData = payData;
            UnifyPayPlugin.getInstance(cordova.getActivity()).sendPayRequest(msg);
            sendNoResultPluginResult(callbackContext);
            return true;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            callbackContext.error("参数错误");
            return true;
        }

        return true;
    }

    @Override
    public void onResult(String resultCode, String resultInfo) {
        Log.i(TAG, resultInfo);
        if(UnifyPayListener.ERR_OK.equals(resultCode)) {
            getCurrentCallbackContext().success()
        } else {
            getCurrentCallbackContext().error(resultInfo);
        }
    }

    public static IWXAPI getWxAPI(Context ctx) {
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(ctx, UnifyPayPlugin.getInstance(cordova.getActivity()).getAppId(), true);
        }
        return wxApi;
    }

    public static WXPayResultListener getWXListener() {
        return UnifyPayPlugin.getInstance(cordova.getActivity()).getWXListener();
    }

    public static CallbackContext getCurrentCallbackContext() {
        return currentCallbackContext;
    }

    private void sendNoResultPluginResult(CallbackContext callbackContext) {
        // save current callback context
        currentCallbackContext = callbackContext;

        // send no result and keep callback
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}