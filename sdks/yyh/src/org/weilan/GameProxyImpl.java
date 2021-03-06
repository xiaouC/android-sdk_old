package org.weilan;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.appchina.usersdk.GlobalUtil;
import com.appchina.usersdk.Res;
import com.yyh.sdk.ApkInstall;
import com.yyh.sdk.CPInfo;
import com.yyh.sdk.LoginCallback;
import com.yyh.sdk.PayCallback;
import com.yyh.sdk.PayParam;
import com.yyh.sdk.SplashCallback;
import com.yyh.sdk.YYHAccount;
import com.yyh.sdk.YYHSDKAPI;

public class GameProxyImpl extends GameProxy{

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    @Override
    public void onDestroy(Activity activity) {
        YYHSDKAPI.onDestroy(activity);
        super.onDestroy(activity);
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
        YYHSDKAPI.onPause(activity);
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        YYHSDKAPI.onResume(activity);
    }

    public void applicationInit(Activity activity) {
        CPInfo cpinfo = new CPInfo();
        cpinfo.appid = "${APPID}";
        cpinfo.appkey = "${APPKEY}"; 
        cpinfo.payid = "${PAYID}"; 
        cpinfo.paykey = "${PAYKEY}";
        cpinfo.isLand = false;// 横屏显示 false 为竖屏 true 为横屏
        cpinfo.yyhdou = "test_yyh_dou";// 在应用汇中充值应用豆时的外部订单号，按照示例中的形式自定义即可。
        //cpinfo.notifyUrl = "http://sdk.nataku.winnergame.com/sdk/android/sdk/yyh/pay_callback";
        YYHSDKAPI.initSDKAPI(activity, cpinfo, new ApkInstall() {
            @Override
            public void InstallApkSuccess() {
                //sdk服务框架安装成功回调                
            }
        });
        YYHSDKAPI.openSplash(activity, new SplashCallback() {
            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                //goMain();
            }
        });
    }

    public void login(Activity activity, final Object customParams) {
        YYHSDKAPI.login(activity, new LoginCallback() {
            @Override
            public void onLoginSuccess(YYHAccount account) {				 
                //登录成功
                User u = new User();
                u.token = account.ticket;
                u.userID = account.userName;
                userListerner.onLoginSuccess(u, customParams);
            }
            @Override
            public void onLoginError() {				 
                //登录失败
                userListerner.onLoginFailed("", customParams);
            }
            @Override
            public void onSwitchAccount(YYHAccount pre, YYHAccount account) {
                //切换账号回调
                userListerner.onLogout(null);
                User u = new User();
                u.token = account.ticket;
                u.userID = account.userName;
                userListerner.onLoginSuccess(u, customParams);
            }
            @Override
            public void onLogout(YYHAccount account) {
                //退出账号回调
                userListerner.onLogout(null);
            }
            @Override
            public void onLoginCancel() {
                userListerner.onLoginFailed("", null);
            }
        });
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        PayParam payParam = new PayParam();
        payParam.setParams((int)(price * 100), Integer.parseInt(ID), orderID);
        payParam.cpprivateinfo = callBackInfo;
        YYHSDKAPI.pay(activity, new PayCallback(){
            @Override
            public void onPayResult(int resultCode, String signValue,
                String resultInfo) {
                if (resultCode == 2001) {
                    payCallBack.onSuccess("");
                }
                else {
                    payCallBack.onFail("");
                }
            }
        }, payParam);
    }
}
