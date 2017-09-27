package com.sdk.mgr;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

import com.kxyx.KxyxSDK;
import com.kxyx.bean.PayStateBean;
import com.kxyx.bean.UserInfoBean;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class SDKManager{
	public static Activity mActivity = null;
	public static SDKManager instance = null;
	public static KxyxSDK sdkInstance = null;
	public static AlertDialog mAlertDialog = null;
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
		utility.setPlatformId(149);
		
		sdkInstance = KxyxSDK.getInstance();
		sdkInstance.init(activity);
		
		//��¼�ص�
		sdkInstance.setOnLoginListener(new KxyxSDK.onLoginListener()
        {
            @Override
            public void onSuccess(UserInfoBean userInfoBean)
            {
            	String userId = userInfoBean.getUsername();
            	LogManager.d("��¼�ɹ���", userId);
            	
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_SUCCESS);
	            	clientJson.put("platId", 149);
	            	clientJson.put("uid", userId);
					utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), "");
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
            }

            @Override
            public void onFail(String msg)
            {
            	LogManager.d("��¼ʧ�ܣ�", msg);
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_FAIL);
	            	utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), "");
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
            }
        });
		
		//֧���ص�
		sdkInstance.setOnPayListener(new KxyxSDK.onPayListener()
        {
            @Override
            public void onResponse(PayStateBean payStateBean)
            {
            	String state = payStateBean.getState();
            	if (state != "1")
            	{
            		LogManager.d("֧��ʧ�ܣ�", state);
            		return;
            	}
            	
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_SUCCESS);
	            	utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_PAY), clientJson.toString(), "");
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
            }
        });
		
		//�ǳ��ص�
		sdkInstance.setOnLogoutListener(new KxyxSDK.onLogoutListener()
        {
            @Override
            public void onResponse()
            {
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
			break;
		case SDKDefine.EVENT_UPLEVEL:
			playerUpLevel(msg.obj.toString());
			break;
		case SDKDefine.EVENT_SCORE:
			break;
		case SDKDefine.EVENT_EXIT:
			exit();
			break;
		case SDKDefine.EVENT_CUSTOM:
			break;
		}
	}
	
	public static void init(){
		LogManager.d("��ʼ����", "init======");
		try {
			JSONObject clientJson = new JSONObject();
			clientJson.put("result", SDKDefine.RESULT_SUCCESS);
			clientJson.put("platId", 149);
			utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_INIT), clientJson.toString(), "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void login() {
		LogManager.d("���룺", "login======");
		sdkInstance.login(mActivity);
	}

	public static void logout() {
		LogManager.d("�ǳ���", "logout======");
	}
	
	public static void exit() {
		LogManager.d("�˳���", "exit======");
		mAlertDialog = null;
		if(mActivity.isFinishing()){
			mAlertDialog = null;
			return;
		}
			
		try {
			mAlertDialog = new AlertDialog.Builder(mActivity)
			.setTitle("�˳�")
			.setMessage("ȷ��Ҫ�˳���Ϸ��")
			.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					mAlertDialog = null;
				}
			})
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					mAlertDialog = null;
					mActivity.finish();			
					System.exit(0);
				}
			})
			.create();
			
			mAlertDialog.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void roleinfo(String params) {
		LogManager.d("������ɫ��", params);
		try {
			JSONObject json = new JSONObject(params);
			String serverNo = json.optString("serverNo");       	//����
			String serverName = json.optString("serverName");   	//����������
			String roleName = json.optString("roleName"); 			//��ɫ���� 
			String roleLv = json.optString("level");  				//�ȼ�
			String time = json.optString("time");
			sdkInstance.reportCreateRole(serverName, serverNo, time, roleName, roleLv);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void playerUpLevel(String params){
		LogManager.d("��ɫ������", params);
		try {
			JSONObject json = new JSONObject(params);
			String serverNo = json.optString("serverNo");       	//����
			String serverName = json.optString("serverName");   	//����������
			String roleName = json.optString("roleName"); 			//��ɫ���� 
			String roleLv = json.optString("level");  				//�ȼ�
			sdkInstance.reportUpgrade(serverName, serverNo, roleName, roleLv);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void pay(String params) {
		LogManager.d("֧��������", params);
		try {
			JSONObject json = new JSONObject(params);
			String myOrderId = json.optString("myOrderId");   				//��Ϸ������
			String productName = json.optString("productName");  			//��Ʒ����
			int realPrice = json.optInt("productRealPrice") / 100; 			//ʵ�ʼ۸�
			String description = json.optString("description");  			//��Ʒ����
			
			JSONObject jsonObject = json.getJSONObject("exs");
			String roleName = jsonObject.optString("UserRoleName"); 		//��ɫ����
			String serverName = jsonObject.optString("UserServerName"); 	//����������
			String noticeUrl = jsonObject.optString("NoticeUrl"); 			//֧���ص���ַ
			sdkInstance.pay(mActivity, String.valueOf(realPrice), productName, description, myOrderId, noticeUrl, serverName, roleName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}		
}
