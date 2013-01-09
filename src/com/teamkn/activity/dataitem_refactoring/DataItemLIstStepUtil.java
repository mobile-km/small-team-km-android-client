package com.teamkn.activity.dataitem_refactoring;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamkn.R;

public class DataItemLIstStepUtil {
	
	public static void data_item_list_step(View view){
		RelativeLayout data_item_step_rl;
		TextView data_item_step_tv;
		TextView data_item_step_text_tv;
		TextView data_item_step_content_text_tv;
		Button data_item_list_approach_button;
		Button data_item_next_button;
		Button data_item_back_button;
		
		// data_item  step 列表
		data_item_step_rl = (RelativeLayout)view.findViewById(R.id.data_item_step_rl);
		data_item_step_tv = (TextView)view.findViewById(R.id.data_item_step_tv);//步骤显示的步骤text
		data_item_step_text_tv = (TextView)view.findViewById(R.id.data_item_step_text_tv);//步骤显示的步骤title
		data_item_step_content_text_tv = (TextView)view.findViewById(R.id.data_item_step_content_text_tv);//步骤显示的步骤内容
		//步骤显示的以清单模式查看
		data_item_list_approach_button = (Button)view.findViewById(R.id.data_item_list_approach_button);
		data_item_next_button = (Button)view.findViewById(R.id.data_item_next_button); //下一步按钮
		data_item_back_button = (Button)view.findViewById(R.id.data_item_back_button);//上一步按钮
		
		
		
		
		
		
		
		
		
		
		
		
	}
}
