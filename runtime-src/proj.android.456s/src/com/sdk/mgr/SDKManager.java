package com.sdk.mgr;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;

import com.vqs456.sdk.VqsManager;
import com.vqs456.sdk.http.LogOutListener;
import com.vqs456.sdk.http.LoginListener;
import com.vqs456.sdk.http.PayListener;

public class SDKManager{
	public static Activity mActivity = null;
	public static SDKManager instance = null;
	public static AlertDialog mAlertDialog = null;
	
	public static VqsManager vqsManager;
	private LoginListener loginListener;
	private PayListener payListener;
	private LogOutListener logOutListener;
	
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
		utility.setPlatformId(151);
		
		//��ʼ���ص�
		loginListener = new LoginListener() {
			@Override
			public void LoginSuccess(String result, String userID, String username, String logintime, String sign) {
				LogManager.d("��¼�ɹ���", userID + "||" + sign);
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_SUCCESS);
	            	clientJson.put("platId", 151);
	            	clientJson.put("uid", userID);
					utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), sign + "|" + logintime);
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
			}

			@Override
			public void LoginFailure(String errorID) {
				LogManager.d("��¼ʧ�ܣ�", errorID);
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_FAIL);
	            	utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), "");
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
			}
		};
		
		payListener = new PayListener() {
			@Override
			public void PaySuccess(String result) {
				LogManager.d("֧���ɹ�", "====");
			}

			@Override
			public void PayFailure(String errorID) {
				LogManager.d("֧��ʧ�ܣ�", errorID);
			}

			@Override
			public void PayCancel(String errorID) {
				LogManager.d("֧��ȡ����", errorID);
			}
		};
		
		logOutListener = new LogOutListener() {
			@Override
			public void LogOut(String result) {
				LogManager.d("�ǳ��ص���", "�ɹ�");
                try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_SUCCESS);
	            	utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGOUT), clientJson.toString(), "");
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
			}
		};
		
		//��ʼ��sdk
		vqsManager = VqsManager.getInstance();
		vqsManager.init(mActivity, loginListener, payListener);
		//����ע���ص�
		vqsManager.setLogOutListerner(logOutListener);
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
		try {
			JSONObject clientJson = new JSONObject();
			clientJson.put("result", SDKDefine.RESULT_SUCCESS);
			clientJson.put("platId", 151);
			utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_INIT), clientJson.toString(), "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void login() {
		LogManager.d("����:", "login======");
		vqsManager.login();
	}

	public static void logout() {
		LogManager.d("�ǳ�:", "logout======");
		vqsManager.LogOut();
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
			String serverName = json.optString("serverName");   	//����������
			String roleName = json.optString("roleName"); 			//��ɫ����
			String roleId = json.optString("roleId");  				//��ɫID
			String level = json.optString("level");  				//�ȼ�
			vqsManager.setGameInfo(roleId, roleName, "սʿ", level, "", "0", "0", "��������", serverName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void enterGame(String params){
		LogManager.d("������Ϸ��", params);
		try {
			JSONObject json = new JSONObject(params);
			String serverName = json.optString("serverName");
			String roleName = json.optString("roleName");
			String roleId = json.optString("roleId");
			String level = json.optString("level");
			vqsManager.setGameInfo(roleId, roleName, "սʿ", level, "", "0", "0", "��������", serverName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
			String myOrderId = json.optString("myOrderId");   				//��Ϸ������
			String productName = json.optString("productName");  			//��Ʒ����
			int realPrice = json.optInt("productRealPrice"); 				//ʵ�ʼ۸�
			vqsManager.Pay(String.valueOf(realPrice), productName, myOrderId, "0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}