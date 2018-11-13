package __PACKAGE_NAME__;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.modelbiz.ChooseCardFromWXCardPackage;

import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.WXPayResultListener;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.linkcld.cordova.plugin.Unifypay;

public class EntryActivity extends Activity implements IWXAPIEventHandler {

    private static WXPayResultListener mListener;

    private void setListener(WXPayResultListener listener){
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IWXAPI api = WXAPIFactory.createWXAPI(this, UnifyPayPlugin.getInstance(this).getAppId());

        if (api == null) {
            startMainActivity();
        } else {
            api.handleIntent(getIntent(), this);
        }
        setListener(UnifyPayPlugin.getInstance(this).getWXListener());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        IWXAPI api = WXAPIFactory.createWXAPI(this, UnifyPayPlugin.getInstance(this).getAppId());
        if (api == null) {
            startMainActivity();
        } else {
            api.handleIntent(intent, this);
        }

        setListener(UnifyPayPlugin.getInstance(this).getWXListener());
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(Unifypay.TAG, resp.toString());

        CallbackContext ctx = Unifypay.getCurrentCallbackContext();

        if (ctx == null) {
            startMainActivity();
            return ;
        }

        if(ConstantsAPI.COMMAND_PAY_BY_WX == resp.getType()) {
            if(mListener != null){
                mListener.onResponse(this, resp);
            }
            return;
        }

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        auth(resp);
                        break;
                    case ConstantsAPI.COMMAND_CHOOSE_CARD_FROM_EX_CARD_PACKAGE:
                        plunckInvoiceData(resp);
                        break;
                    case ConstantsAPI.COMMAND_PAY_BY_WX:
                        if(mListener != null){
                            mListener.onResponse(this, resp);
                        }
                    default:
                        ctx.success();
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ctx.error(Unifypay.ERROR_WECHAT_RESPONSE_USER_CANCEL);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ctx.error(Unifypay.ERROR_WECHAT_RESPONSE_AUTH_DENIED);
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                ctx.error(Unifypay.ERROR_WECHAT_RESPONSE_SENT_FAILED);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                ctx.error(Unifypay.ERROR_WECHAT_RESPONSE_UNSUPPORT);
                break;
            case BaseResp.ErrCode.ERR_COMM:
                ctx.error(Unifypay.ERROR_WECHAT_RESPONSE_COMMON);
                break;
            default:
                ctx.error(Unifypay.ERROR_WECHAT_RESPONSE_UNKNOWN);
                break;
        }

        finish();
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    protected void startMainActivity() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(getApplicationContext().getPackageName());
        getApplicationContext().startActivity(intent);
    }

    protected void auth(BaseResp resp) {
        SendAuth.Resp res = ((SendAuth.Resp) resp);

        Log.d(Unifypay.TAG, res.toString());

        // get current callback context
        CallbackContext ctx = Unifypay.getCurrentCallbackContext();

        if (ctx == null) {
            return ;
        }

        JSONObject response = new JSONObject();
        try {
            response.put("code", res.code);
            response.put("state", res.state);
            response.put("country", res.country);
            response.put("lang", res.lang);
        } catch (JSONException e) {
            Log.e(Unifypay.TAG, e.getMessage());
        }

        ctx.success(response);
    }

    protected void plunckInvoiceData(BaseResp resp) {

        CallbackContext ctx = Unifypay.getCurrentCallbackContext();
        ChooseCardFromWXCardPackage.Resp resp1 = (ChooseCardFromWXCardPackage.Resp) resp;
        JSONObject response = new JSONObject();

        try {
            JSONArray resp2 = new JSONArray(resp1.cardItemList);
            response.put("data", resp2);
        } catch (JSONException e) {
            Log.e(Unifypay.TAG, e.getMessage());
        }

        ctx.success(response);
    }
}
