package com.teamkn.base.runnable;

import com.teamkn.Logic.AccountManager.AuthenticateException;
import com.teamkn.Logic.HttpApi.IntentException;

public abstract class MindpinRunnable implements Runnable {
	public static final int METHOD_NOT_DEFINE_EXCEPTION = 9001;
	public static final int INTENT_CONNECTION_EXCEPTION = 9002;
	public static final int AUTHENTICATE_EXCEPTION = 9003;
	public static final int UNKNOW_EXCEPTION = 9099;
	
	private MindpinHandler handler;
	public MindpinRunnable(MindpinHandler handler){
		this.handler = handler;
	}
	
	@Override
	public void run() {
		try {
			mindpin_run();
		} 
		
		// 四种典型的全局通用错误
		catch (MethodNotDefineException e) {
			// 方法没有定义，通常用于功能未实现时
			handler.sendEmptyMessage(METHOD_NOT_DEFINE_EXCEPTION);
			e.printStackTrace();
		} 
		
		catch (IntentException e){
			// 网络连接错误
			if(on_intent_connection_exception()){
				handler.sendEmptyMessage(INTENT_CONNECTION_EXCEPTION);
			}
			e.printStackTrace();
		} 
		
		catch (AuthenticateException e){
			// 用户身份验证错误
			handler.sendEmptyMessage(AUTHENTICATE_EXCEPTION);
			e.printStackTrace();
		} 
		
		catch (Exception e){
			// 程序执行错误
			if(on_exception()){;
				handler.sendEmptyMessage(UNKNOW_EXCEPTION);
			}
			e.printStackTrace();
		}
	}
	
	public abstract void mindpin_run() throws Exception;
	
	public boolean on_intent_connection_exception(){
		return true;
		// do nothing .. 可自行重载该方法
		// 重载时 如果 
		// return true   则仍然发送 message UNKNOW_EXCEPTION
		// return false  则不再发送 message UNKNOW_EXCEPTION
	}
	
	public boolean on_exception(){
		return true;
		// do nothing .. 可自行重载该方法
		// 重载时 如果 
		// return true   则仍然发送 message UNKNOW_EXCEPTION
		// return false  则不再发送 message UNKNOW_EXCEPTION
	}
	
	public class MethodNotDefineException extends Exception{
		private static final long serialVersionUID = -1400532382871315093L;
	}
}
