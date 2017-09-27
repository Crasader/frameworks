package com.sdk.mgr;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import com.snowfish.cn.ganga.helper.SFOnlineExitListener;
import com.snowfish.cn.ganga.helper.SFOnlineHelper;
import com.snowfish.cn.ganga.helper.SFOnlineLoginListener;
import com.snowfish.cn.ganga.helper.SFOnlinePayResultListener;
import com.snowfish.cn.ganga.helper.SFOnlineUser;

public class SDKManager{
	public static Activity mActivity = null;
	public static SDKManager instance = null;
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
		utility.setPlatformId(181);
		
		SFOnlineHelper.onCreate(activity);
		
		SFOnlineHelper.setLoginListener(activity, new SFOnlineLoginListener() {
			@Override
			public void onLoginSuccess(SFOnlineUser user, Object customParams)
			{
				String uid = user.getChannelUserId();
				try {
					uid = URLEncoder.encode(uid, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				String token = user.getToken();
				try {
					token = URLEncoder.encode(token, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				String channelId = user.getChannelId();
				try {
					channelId = URLEncoder.encode(channelId, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				LogManager.d("��¼�ɹ���", channelId + "==" + uid + "==" + token);
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_SUCCESS);
	            	clientJson.put("platId", 181);
	            	clientJson.put("uid", uid);
					utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), token + "|" + channelId);
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
			}
			
			@Override
			public void onLoginFailed(String reason, Object customParams) {
				LogManager.d("��¼ʧ�ܣ�", reason);
            	try {
	            	JSONObject clientJson = new JSONObject();
	            	clientJson.put("result", SDKDefine.RESULT_FAIL);
	            	utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), "");
            	} catch (JSONException e) {
            		e.printStackTrace();
        		}
			}
			
			@Override
			public void onLogout(Object customParams) {
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
			enterGame(msg.obj.toString());
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
		LogManager.d("��ʼ��:", "init======");
		try {
			JSONObject clientJson = new JSONObject();
			clientJson.put("result", SDKDefine.RESULT_SUCCESS);
			clientJson.put("platId", 181);
			utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_INIT), clientJson.toString(), "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void login() {
		LogManager.d("����:", "login======");
		SFOnlineHelper.login(mActivity, "Login");
	}

	public static void logout() {
		LogManager.d("�ǳ�:", "logout======");
		SFOnlineHelper.logout(mActivity, "LoginOut");
	}
	
	public static void exit() {
		LogManager.d("�˳���", "exit======");
		SFOnlineHelper.exit(mActivity, new SFOnlineExitListener() {
			@Override
			public void onSDKExit(boolean bool) {
				if (bool){
					mAlertDialog = null;
					mActivity.finish();			
					System.exit(0);
				}
			}
			
			@Override
			public void onNoExiterProvide() {
				if(mActivity.isFinishing()){
					return;
				}
				
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
			}
		});
	}
	
	public static void roleinfo(String params) {
		LogManager.d("������ɫ��", params);
		try {
			JSONObject json = new JSONObject(params);
			String serverName = json.optString("serverName");   	//����������
			String roleName = json.optString("roleName"); 			//��ɫ����
			String roleId = json.optString("roleId");  				//��ɫID
			String level = json.optString("level");  				//�ȼ�
			String serverId = json.optString("serverNo");       	//����
			String time = json.optString("time");
			
			SFOnlineHelper.setRoleData(mActivity, roleId, roleName, level, serverId, serverName);
			
			JSONObject roleInfo = new JSONObject();
			roleInfo.put("roleId", roleId);
			roleInfo.put("roleName", roleName);
			roleInfo.put("roleLevel", level);
			roleInfo.put("zoneId", serverId);
			roleInfo.put("zoneName", serverName);
			roleInfo.put("balance", "0");
			roleInfo.put("vip", "1");
			roleInfo.put("partyName", "�ް���");
			roleInfo.put("roleCTime", time);
			roleInfo.put("roleLevelMTime", time);
			SFOnlineHelper.setData(mActivity, "createrole", roleInfo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void enterGame(String params){
		LogManager.d("������Ϸ��", params);
		try {
			JSONObject json = new JSONObject(params);
			String serverId = json.optString("serverNo");
			String serverName = json.optString("serverName");
			String roleName = json.optString("roleName");
			String roleId = json.optString("roleId");
			String level = json.optString("level");
			String time = json.optString("time");
			
			JSONObject roleInfo = new JSONObject();
			roleInfo.put("roleId", roleId);
			roleInfo.put("roleName", roleName);
			roleInfo.put("roleLevel", level);
			roleInfo.put("zoneId", serverId);
			roleInfo.put("zoneName", serverName);
			roleInfo.put("balance", "0");
			roleInfo.put("vip", "1");
			roleInfo.put("partyName", "�ް���");
			roleInfo.put("roleCTime", time);
			roleInfo.put("roleLevelMTime", time);
			SFOnlineHelper.setData(mActivity, "enterServer", roleInfo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void playerUpLevel(String params){
		LogManager.d("��ɫ������", params);
		try {
			JSONObject json = new JSONObject(params);
			String serverId = json.optString("serverNo");
			String serverName = json.optString("serverName");
			String roleName = json.optString("roleName");
			String roleId = json.optString("roleId");
			String level = json.optString("level");
			String time = json.optString("time");
			
			JSONObject roleInfo = new JSONObject();
			roleInfo.put("roleId", roleId);
			roleInfo.put("roleName", roleName);
			roleInfo.put("roleLevel", level);
			roleInfo.put("zoneId", serverId);
			roleInfo.put("zoneName", serverName);
			roleInfo.put("balance", "0");
			roleInfo.put("vip", "1");
			roleInfo.put("partyName", "�ް���");
			roleInfo.put("roleCTime", time);
			roleInfo.put("roleLevelMTime", time);
			SFOnlineHelper.setData(mActivity, "levelup", roleInfo.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void pay(String params) {
		LogManager.d("֧��������", params);
		try {
			JSONObject json = new JSONObject(params);
			String orderId = json.optString("myOrderId");   				//��Ϸ������
			String strName = json.optString("productName");  				//��Ʒ����
			int amount = json.optInt("productRealPrice"); 					//ʵ�ʼ۸�
			
			JSONObject jsonObject = json.getJSONObject("exs");
			String url = jsonObject.optString("NoticeUrl"); 				//֧���ص���ַ
			
			SFOnlineHelper.pay(mActivity, amount, strName, 1, orderId, url, new SFOnlinePayResultListener() {
						@Override
						public void onSuccess(String remain) {
						}
						@Override
						public void onFailed(String remain) {
						}
						@Override
						public void onOderNo(String orderNo) {
						}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}