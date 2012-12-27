package com.teamkn.activity.base.slidingmenu;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;

public abstract class RightSlidingContainer {
	protected ViewGroup right_container;
	protected TeamknBaseActivity context;
	protected ViewGroup right_root_view;

	public RightSlidingContainer(TeamknBaseActivity context, int layout_xml){
		this.context = context;
		
		this.right_container = (ViewGroup)context.findViewById(R.id.right_container);
		LayoutInflater inflater = LayoutInflater.from(context);
		this.right_root_view = (ViewGroup)inflater.inflate(layout_xml, null);
		right_container.addView(right_root_view);
	}
	
	public abstract void on_create();
}
