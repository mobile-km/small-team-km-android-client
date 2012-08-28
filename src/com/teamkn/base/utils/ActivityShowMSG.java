package com.teamkn.base.utils;

import com.teamkn.activity.base.MainActivity;
import com.teamkn.application.TeamknApplication;

public class ActivityShowMSG {
     public static void showMsg(int be_invited_size,int contactstatus_be_invited){
    	 if( TeamknApplication.current_show_activity != null && TeamknApplication.current_show_activity
         		  .equals("com.teamkn.activity.base.MainActivity")){
    		 if(be_invited_size>0){
           		contactstatus_be_invited = be_invited_size;
           		MainActivity.set_teamkn_show_msg_tv(" 你收到了   "+contactstatus_be_invited+" 条朋友的邀请");
           	}else{
           		MainActivity.set_teamkn_show_msg_tv("");
           	}
         }
    	 
//    	 if( TeamknApplication.current_show_activity
//        		  .equals("com.teamkn.activity.base.MainActivity")){
//          	if(be_invited_size>0){
//          		if(contactstatus_be_invited<be_invited_size){
//          			contactstatus_be_invited = be_invited_size;
//                  	MainActivity.set_teamkn_show_msg_tv(" 你收到了   "+contactstatus_be_invited+" 条朋友的邀请");
//          		}
//          	}else{
//          		MainActivity.set_teamkn_show_msg_tv("");
//          	}
//        }
    	 
     }
}
