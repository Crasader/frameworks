package com.sdk.mgr;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.talkingsdk.MainApplication;
import com.talkingsdk.SdkBase;
import com.talkingsdk.ZQCode;
import com.talkingsdk.ZqgameSdkListener;
import com.talkingsdk.models.PayData;
import com.talkingsdk.models.PlayerData;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;


public class SDKManager implements ZqgameSdkListener{
	public static MainApplication mainInstance = null;
	public static SdkBase sdkInstance = null;
	public static Activity mActivity = null;
	public static SDKManager instance = null;
	
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
		mainInstance = MainApplication.getInstance();		
		sdkInstance = mainInstance.getSdkInstance();
		mainInstance.setZqgameSdkListener(this);
		sdkInstance.setCurrentActivity(activity);
		mainInstance.onCreate(savedInstanceState);
		
		String pid = mainInstance.getPropertiesByKey("PlatformId");
		utility.setPlatformId(Integer.parseInt(pid));
	}
	
	/*
	 * ��ʼ����ɻص�
	 */
	@Override
	public void onInitComplete(String arg0) {
		LogManager.d("onInitComplete:",arg0);
		try {
			JSONObject json = new JSONObject(arg0);
			int platformId = json.optInt("PlatformId");
			int code = json.optInt("code");
			String msg = json.optString("msg");
			LogManager.d("��ʼ�����:","platformId=" + platformId + "code=" + code + "msg:" + msg);
			
			if(ZQCode.CODE_INIT_SUCCESS == code){
				JSONObject clientJson = new JSONObject();
				clientJson.put("result", SDKDefine.RESULT_SUCCESS);
				clientJson.put("platId", platformId);
				utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_INIT), clientJson.toString(), "");
			}else{
				JSONObject clientJson = new JSONObject();
				clientJson.put("result", SDKDefine.RESULT_FAIL);
				utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_INIT), clientJson.toString(), "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * ����ɹ��ص�
	 */
	@Override
	public void onLoginResult(String arg0) {
		LogManager.d("onLoginResult:",arg0);
		try {
			JSONObject json = new JSONObject(arg0);
			String ext = json.optString("Ext");
			String status = new JSONObject(ext).getString("status");
			if(String.valueOf(ZQCode.CODE_LOGIN_SUCCESS).equals(status)){
				//��¼�ɹ�
				String UserId = json.optString("UserId");
				String UserName = json.optString("UserName");
				String NickName = json.optString("NickName");
				String Password = json.optString("Password");
				String SessionId = json.optString("SessionId");
				String pid = sdkInstance.getPlatformId();
				
				LogManager.d("����ɹ���", "UserId=" + UserId + "UserName=" + UserName + "NickName=" + NickName + 
						"Password=" + Password + "SessionId=" + SessionId + "pid=" + pid);
				
				JSONObject clientJson = new JSONObject();
				clientJson.put("result", SDKDefine.RESULT_SUCCESS);
				clientJson.put("platId", pid);
				clientJson.put("uid", UserId);
				utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), SessionId);
				
				//��ʾ����������¼����ã�
				sdkInstance.showToolBar();
			}else{
				//��¼ʧ��
				JSONObject clientJson = new JSONObject();
				clientJson.put("result", SDKDefine.RESULT_FAIL);
				utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGIN), clientJson.toString(), "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * ֧����ɻص�
	 */
	@Override
	public void onPayResult(String arg0) {
		LogManager.d("onPayResult:", arg0);
		try {
			JSONObject json = new JSONObject(arg0);
			String ext = json.optString("Ext");
			String status = new JSONObject(ext).getString("status");
			if(String.valueOf(ZQCode.CODE_PAY_SUCCESS).equals(status)){
			}else{
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResult(String arg0){
		LogManager.d("onResult:", arg0);
	}
	
	@Override
	public void onChangeAccountResult(String arg0){
		LogManager.d("onChangeAccountResult:", arg0);
		try {
			JSONObject json = new JSONObject(arg0);
			String ext = json.optString("Ext");
			String status = new JSONObject(ext).getString("status");
			if(String.valueOf(ZQCode.CODE_CHANGEACCOUNT_SUCCESS).equals(status)){
				String UserId = json.optString("UserId");
				String UserName = json.optString("UserName");
				String NickName = json.optString("NickName");
				String Password = json.optString("Password");
				String SessionId = json.optString("SessionId");
				String pid = sdkInstance.getPlatformId();
				
				LogManager.d("�л��˺ųɹ���", "UserId=" + UserId + "UserName=" + UserName + "NickName=" + NickName + 
						"Password=" + Password + "SessionId=" + SessionId + "pid=" + pid);
				
				JSONObject clientJson = new JSONObject();
				clientJson.put("result", SDKDefine.RESULT_SUCCESS);
				clientJson.put("platId", pid);
				clientJson.put("uid", UserId);
				utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_CHANGE), clientJson.toString(), SessionId);
			}else{
				JSONObject clientJson = new JSONObject();
				clientJson.put("result", SDKDefine.RESULT_FAIL);
				utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_CHANGE), clientJson.toString(), "");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onLogoutResult(String arg0){
		LogManager.d("onLogoutResult:", arg0);
		utility.runNativeCallback(String.valueOf(SDKDefine.EVENT_LOGOUT), "",  "");
	}
	
	@Override
	public void onExitAppResult(){
		mActivity.finish();
		System.exit(0);
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
		LogManager.d("��ʼ����", "init======");
		sdkInstance.initCommonSdkObject(mActivity);
	}
	
	public static void login() {
		LogManager.d("���룺", "login======");
		sdkInstance.login();
	}

	public static void logout() {
		LogManager.d("�ǳ���", "logout======");
		sdkInstance.destroyToolBar();   	//�������������ǳ�ǰ���ã�
		sdkInstance.logout();
	}
	
	public static void exit() {
		LogManager.d("�˳���", "exit======");
		sdkInstance.onKeyBack();
	}
	
	public static void roleinfo(String params) {
		LogManager.d("������ɫ��", params);
		try {
			JSONObject json = new JSONObject(params);
			String serverNo = json.optString("serverNo");       	//����
			String serverName = json.optString("serverName");   	//����������
			String roleName = json.optString("roleName"); 			//��ɫ����
			int roleId = json.optInt("roleId");  					//��ɫID
			int level = json.optInt("level");  						//�ȼ�
			String time = json.optString("time");
			
			Map<String,String> exs = new HashMap<String,String>();
			exs.put("roleCTime",time);
			
			PlayerData _playerData = new PlayerData();
			_playerData.setServerNo(serverNo);
			_playerData.setServerName(serverName);
			_playerData.setRoleName(roleName);
			_playerData.setRoleId(roleId);
			_playerData.setLevel(level);
			_playerData.setEx(exs);
			sdkInstance.createRole(_playerData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void enterGame(String params){
		LogManager.d("������Ϸ��", params);
		try {
			JSONObject json = new JSONObject(params);
			String serverNo = json.optString("serverNo");
			String serverName = json.optString("serverName");
			String roleName = json.optString("roleName");
			int roleId = json.optInt("roleId");
			int level = json.optInt("level");
			String time = json.optString("time");
			
			Map<String,String> exs = new HashMap<String,String>();
			exs.put("roleCTime",time);
			
			PlayerData _playerData = new PlayerData();
			_playerData.setServerNo(serverNo);
			_playerData.setServerName(serverName);
			_playerData.setRoleName(roleName);
			_playerData.setRoleId(roleId);
			_playerData.setLevel(level);
			_playerData.setEx(exs);
			sdkInstance.enterGame(_playerData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void playerUpLevel(String params){
		LogManager.d("��ɫ������", params);
		try {
			JSONObject json = new JSONObject(params);
			String serverNo = json.optString("serverNo");
			String serverName = json.optString("serverName");
			String roleName = json.optString("roleName");
			int roleId = json.optInt("roleId");
			int level = json.optInt("level");
			String time = json.optString("time");
			
			Map<String,String> exs = new HashMap<String,String>();
			exs.put("roleCTime",time);
			
			PlayerData _playerData = new PlayerData();
			_playerData.setServerNo(serverNo);
			_playerData.setServerName(serverName);
			_playerData.setRoleName(roleName);
			_playerData.setRoleId(roleId);
			_playerData.setLevel(level);
			_playerData.setEx(exs);
			sdkInstance.userUpLevel(_playerData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void setScore(String params){
		LogManager.d("���÷�����", params);
		try {
			JSONObject json = new JSONObject(params);
			String sorce = json.optString("sorce");
			String rank = json.optString("rank");
			sdkInstance.uploadScore(sorce,rank);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void pay(String params) {
		LogManager.d("֧��������", params);
		try {
			JSONObject json = new JSONObject(params);
			String myOrderId = json.optString("myOrderId");   				//��Ϸ������
			String productId = json.optString("productId"); 				//��Ʒid����ֵ��Ҫ���ɷ�������
			String productName = json.optString("productName");  			//��Ʒ����
			int productRealPrice = json.optInt("productRealPrice"); 		//ʵ�ʼ۸�
			int productIdealPrice = json.optInt("productIdealPrice"); 		//����۸�
			int productCount = json.optInt("productCount"); 				//��Ʒ����
			String description = json.optString("description");  			//��Ʒ����
			String submitTime = json.optString("submitTime"); 				//�����������ص�ʱ��
			
			JSONObject jsonObject = json.getJSONObject("exs");
			String UserBalance = jsonObject.optString("UserBalance");  		//�û����
			String UserGamerVip = jsonObject.optString("UserGamerVip"); 	//VIP�ȼ�
			String UserLevel = jsonObject.optString("UserLevel"); 			//��ɫ�ȼ�
			String UserPartyName = jsonObject.optString("UserPartyName"); 	//����
			String UserRoleName = jsonObject.optString("UserRoleName"); 	//��ɫ����
			String UserRoleId = jsonObject.optString("UserRoleId"); 		//��ɫid
			String UserServerName = jsonObject.optString("UserServerName"); //����������
			String UserServerId = jsonObject.optString("UserServerId"); 	//������id
			int GameMoneyAmount = jsonObject.optInt("GameMoneyAmount"); 	//��ֵ��Ϸ�ҽ����������ͽ�� �밴(֧�����*���������Ϸ�Ҷһ��ʣ����磬6Ԫ60��ʯ���˴���60��Ӧ�ñ�����ͨ����ֵȷ����Ʒ�۸���Ϸ������)
			String GameMoneyName = jsonObject.optString("GameMoneyName"); 	//�������ƣ���Ԫ������ʯ
			String UserId = jsonObject.optString("UserId"); 				//�û�id��accid��
			String LoginAccount = jsonObject.optString("LoginAccount"); 	//��½�ʻ�(account)
			String LoginDataEx = jsonObject.optString("LoginDataEx"); 		//��½��չ��Ϣ(ext)
			String LoginSession = jsonObject.optString("LoginSession"); 	//��½session(session)
			String AccessKey = jsonObject.optString("AccessKey"); 			//sdk����˷��ص�sign��ĳЩ������Ҫǩ����֤
			String OutOrderID = jsonObject.optString("OutOrderID"); 		//������������(ƽ̨������plat_order)
			String NoticeUrl = jsonObject.optString("NoticeUrl"); 			//֧���ص���ַ
			
			PayData payData = new PayData();
			payData.setSubmitTime(submitTime+"");
			payData.setMyOrderId(myOrderId);
			payData.setDescription(description);
			payData.setProductCount(productCount);
			payData.setProductId(productId+"");
			payData.setProductIdealPrice(productIdealPrice);
			payData.setProductRealPrice(productRealPrice);
			payData.setProductName(productName);
			Map<String,String> exs = new HashMap<String,String>();
			exs.put("UserBalance", UserBalance);
			exs.put("UserGamerVip", UserGamerVip);
			exs.put("UserLevel", UserLevel);
			exs.put("UserPartyName", UserPartyName);
			exs.put("UserRoleName", UserRoleName);
			exs.put("UserRoleId", UserRoleId);
			exs.put("UserServerName", UserServerName);
			exs.put("UserServerId", UserServerId);
			exs.put("GameMoneyAmount", GameMoneyAmount+"");
			exs.put("GameMoneyName", GameMoneyName);
			exs.put("UserId", UserId);
			exs.put("LoginAccount", LoginAccount);
			exs.put("LoginDataEx", LoginDataEx);
			exs.put("LoginSession", LoginSession);
			exs.put("AccessKey", AccessKey);
			exs.put("OutOrderID", OutOrderID);
			exs.put("NoticeUrl",NoticeUrl);
			payData.setEx(exs);

			sdkInstance.pay(payData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}