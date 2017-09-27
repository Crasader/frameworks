package com.sdk.mgr;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

import com.zqhy.sdk.platform.btgame.BTGameSDKApi;
import com.zqhy.sdk.callback.InitCallBack;
import com.zqhy.sdk.callback.ReLoginCallBack;
import com.zqhy.sdk.callback.LoginCallBack;
import com.zqhy.sdk.model.PayParams;
import com.zqhy.sdk.callback.PayCallBack;
import com.zqhy.sdk.callback.ExitCallBack;

public class SDKManager{
	public static Activity mActivity = null;
	public static SDKManager instance = null;
	public static String mUserName = null;
	public static String mToken = null;
	public static int initStatus = 0;
	
	/*
	 * ����
	 */
	public static SDKManager getInstance(){
		if(instance == null){
			instance = new SDKManager();
		}
		return instance;
	}
	
	/*
	 * ��ʼ��SDK
	 */
	public void initSdk(Activity activity, Bundle savedInstanceState) {
		mActivity = activity;
		utility.setPlatformId(150);
		init();
	}

	/*
	 * ����ͷ�����Ϣ
	 */
	public static void onHandleMessage(Message msg) {
		switch (msg.what) {
		case SDKDefine.EVENT_INIT:
			init();
			break;
		case SDKDefine.EVENT_LOGIN:
			login();
			break;
		case SDKDefine.EVENT_LOGOUT:
			logout();
			break;
		case SDKDefine.EVENT_PAY:
			pay(msg.obj.toString());
			break;
		case SDKDefine.EVENT_ROLEINFO:
			roleinfo(msg.obj.toString());
			break;
		case SDKDefine.EVENT_ENTER:
			enterGame(msg.obj.toString());
			break;
		case SDKDefine.EVENT_UPLEVEL:
			playerUpLevel(msg.obj.toString());
			break;
		case SDKDefine.EVENT_SCORE:
			setScore(msg.obj.toString());
			break;
		case SDKDefine.EVENT_EXIT:
			exit();
			break;
		case SDKDefine.EVENT_CUSTOM:
			break;
		}
	}
	
	public static void init(){
		LogManager.d("��ʼ��:", "init======");
		if (initStatus == 1)
		{
			try {
    			JSONObject clientJson = new JSONObject();
    			clientJson.put("result", SDKDefine.RESULT_SUCCESS);
    			clientJson.put("platId", 150);
    			utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_INIT), clientJson.toString(), "");
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
			return;
		}
		
		if (initStatus > 0)
		{
			return;
		}
		
		initStatus = 2;
		BTGameSDKApi.getInstance().init(mActivity, 11699, "f17354c8ae8b7a1cb5aab8f285c22c69", new InitCallBack() {
			@Override
            public void onInitSuccess() {
            	LogManager.d("��ʼ���ɹ�", "======");
            	initStatus = 1;
            	try {
        			JSONObject clientJson = new JSONObject();
        			clientJson.put("result", SDKDefine.RESULT_SUCCESS);
        			clientJson.put("platId", 150);
        			utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_INIT), clientJson.toString(), "");
        		} catch (JSONException e) {
        			e.printStackTrace();
        		}
            	
                BTGameSDKApi.getInstance().registerReLoginCallBack(new ReLoginCallBack() {
                    @Override
                    public void onReLogin() {
                    	LogManager.d("�ǳ��ص���", "�ɹ�");
                        try {
        	            	JSONObject clientJson = new JSONObject();
        	            	clientJson.put("result", SDKDefine.RESULT_SUCCESS);
        	            	utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGOUT), clientJson.toString(), "");
                    	} catch (JSONException e) {
                    		e.printStackTrace();
                		}
                    }
                });
            }
            @Override
            public void onInitFailure(String message) {
            	LogManager.d("��ʼ��ʧ�ܣ�", message);
            	initStatus = 0;
            }
        });
	}
	
	public static void login() {
		LogManager.d("����:", "login======");
		BTGameSDKApi.getInstance().login(mActivity, new LoginCallBack() {
            @Override
            public void onLoginSuccess(String uid, String username, String token) {
                LogManager.d("��¼�ɹ���", uid + "====" + token);
                mUserName = username;
                mToken = token;
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_SUCCESS);
	            	clientJson.put("platId", 150);
	            	clientJson.put("uid", username + "|" + uid);
					utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), token);
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
            }
            @Override
            public void onLoginFailure(String message) {
                LogManager.d("��¼ʧ�ܣ�", message);
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_FAIL);
	            	utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), "");
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
            }
            @Override
            public void onLoginCancel() {
                LogManager.d("��¼ȡ����", "==");
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_FAIL);
	            	utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), "");
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
            }
        });
	}

	public static void logout() {
		LogManager.d("�ǳ�:", "logout======");
	}
	
	public static void exit() {
		LogManager.d("�˳���", "exit======");
		BTGameSDKApi.getInstance().exit(mActivity, new ExitCallBack() {
            @Override
            public void onExit() {
            	mActivity.finish();			
				System.exit(0);
            }
            @Override
            public void onContinueGame() {
            }
            @Override
            public void onCancel() {
            }
        }, null);
	}
	
	public static void roleinfo(String params) {
		LogManager.d("������ɫ��", params);
	}
	
	public static void enterGame(String params){
		LogManager.d("������Ϸ��", params);
	}
	
	public static void playerUpLevel(String params){
		LogManager.d("��ɫ������", params);
	}
	
	public static void setScore(String params){
		LogManager.d("���÷�����", params);
	}

	public static void pay(String params) {
		LogManager.d("֧��������", params);
		try {
			JSONObject json = new JSONObject(params);
			JSONObject jsonObject = json.getJSONObject("exs");
			PayParams payParams = new PayParams();
	        payParams.extendsinfo = json.optString("myOrderId");   				//��Ϸ������
	        payParams.username = mUserName;
	        payParams.token = mToken;
	        payParams.serverid = jsonObject.optString("UserServerId");			//������id
	        payParams.amount = json.optInt("productRealPrice") / 100;			//ʵ�ʼ۸�
			BTGameSDKApi.getInstance().pay(mActivity, payParams, new PayCallBack() {
	            @Override
	            public void onPaySuccess(String message) {
	            	LogManager.d("֧���ɹ�", "====");
	            }
	            @Override
	            public void onPayFailure(String message) {
	            	LogManager.d("֧��ʧ��", "====");
	            }
	            @Override
	            public void onPayCancel() {
	            	LogManager.d("֧��ȡ��", "====");
	            }
	        });
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}