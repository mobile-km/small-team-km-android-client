package com.teamkn.service;

import java.util.List;

import com.teamkn.Logic.HttpApi;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.Attitudes;
import com.teamkn.model.ChatNode;
import com.teamkn.model.database.AttitudesDBHelper;
import com.teamkn.model.database.ChatNodeDBHelper;
import com.teamkn.model.database.UserDBHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class FaceCommentService extends Service implements Runnable{
    public static Context context;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		while (true) {
			try {
				if(BaseUtils.is_wifi_active(context)){
					List<Attitudes> list = AttitudesDBHelper.find("false");	
					for(Attitudes att : list){
						ChatNode node = ChatNodeDBHelper.find(att.chat_node_id);
						int service_user_id = UserDBHelper.find(att.client_user_id).user_id;
						HttpApi.Attitudes.create(att.chat_node_id,service_user_id,att.kind,node.server_chat_node_id);
					}
//					System.out.println(" facecommentservice... "+list.size());
					HttpApi.Attitudes.getcreat(context);
				}	
				Thread.sleep(15000);		
			}catch (InterruptedException e) {
				e.printStackTrace();
			}catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Thread thread = new Thread(this);
		thread.start();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.stopSelf();
	}

}
