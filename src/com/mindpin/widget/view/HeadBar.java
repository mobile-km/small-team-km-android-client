package com.mindpin.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mindpin.R;

public class HeadBar extends RelativeLayout {
	private TextView title_textview;
	
	public HeadBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//载入自定义xml，必须使用传入的 context，不能使用application_context否则事件报错
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.xml.widget_head_bar, this, true);
		
		this.title_textview = (TextView) view.findViewById(R.id.widget_head_bar_title);
		
		//读自定义参数，对组件赋值
		String title = attrs.getAttributeValue(null, "title");
		set_title(title);
		
	}
	
	public void set_title(String title){
		title_textview.setText(title);
	}
	
	public void set_title(int resid){
		title_textview.setText(resid);
	}

}
