package com.sdk.mgr;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;

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
		utility.setPlatformId(0);
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
	}
	
	public static void login() {
		LogManager.d("����:", "login======");
	}

	public static void logout() {
		LogManager.d("�ǳ�:", "logout======");
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
			int roleId = json.optInt("roleId");  					//��ɫID
			int level = json.optInt("level");  						//�ȼ�
			String time = json.optString("time");
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}