package com.mindpin.widget;

import com.mindpin.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MindpinAlertDialog extends Dialog {
	Context context;
	String title;
	String message;
	View ContentView;

	String button1_lable;
	String button2_lable;
	String button3_lable;
	DialogInterface.OnClickListener button1_listener;
	DialogInterface.OnClickListener button2_listener;
	DialogInterface.OnClickListener button3_listener;

	public MindpinAlertDialog(Context context) {
		super(context,R.style.mindpin_alert_dialog);
		this.context = context;
	}

	public MindpinAlertDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mindpin_alert_dialog);

		process_title();
		process_message();
		process_content_view();
		process_button();
		android.view.WindowManager.LayoutParams attrs = this.getWindow().getAttributes();
		Display d=this.getWindow().getWindowManager().getDefaultDisplay();
		attrs = this.getWindow().getAttributes();
		attrs.width = d.getWidth(); 
	}

	private void process_button() {
		Button b1 = (Button) findViewById(R.id.mindpin_alert_dialog_button1);
		Button b2 = (Button) findViewById(R.id.mindpin_alert_dialog_button2);
		Button b3 = (Button) findViewById(R.id.mindpin_alert_dialog_button3);
		if (button1_lable == null) {
			b1.setVisibility(View.GONE);
		} else {
			b1.setText(button1_lable);
			b1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (button1_listener != null) {
						button1_listener.onClick(MindpinAlertDialog.this,
								DialogInterface.BUTTON_POSITIVE);
					}
					MindpinAlertDialog.this.dismiss();
				}
			});
		}

		if (button2_lable == null) {
			b2.setVisibility(View.GONE);
		} else {
			b2.setText(button2_lable);
			b2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (button2_listener != null) {
						button2_listener.onClick(MindpinAlertDialog.this,
								DialogInterface.BUTTON_NEGATIVE);
					}
					MindpinAlertDialog.this.dismiss();
				}
			});
		}

		if (button3_lable == null) {
			b3.setVisibility(View.GONE);
		} else {
			b3.setText(button3_lable);
			b3.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (button3_listener != null) {
						button3_listener.onClick(MindpinAlertDialog.this,
								DialogInterface.BUTTON_NEUTRAL);
					}
					MindpinAlertDialog.this.dismiss();
				}
			});
		}

		if (button1_lable == null && button2_lable == null
				&& button3_lable == null) {
			LinearLayout panel = (LinearLayout)findViewById(R.id.mindpin_alert_dialog_button_panel);
			panel.setVisibility(View.GONE);
		}
	}

	private void process_content_view() {
		if (ContentView != null) {
			ScrollView sv = (ScrollView) findViewById(R.id.mindpin_alert_dialog_content_panel_scrollView);
			sv.removeAllViews();
			sv.addView(ContentView);
		}
	}

	private void process_message() {
		TextView view = (TextView) findViewById(R.id.mindpin_alert_dialog_message);
		if (message == null || "".equals(message)) {
			view.setVisibility(View.GONE);
		} else {
			view.setText(message);
		}
	}

	private void process_title() {
		if (title == null || "".equals(title)) {
			LinearLayout top = (LinearLayout) findViewById(R.id.mindpin_alert_dialog_title_panel);
			top.setVisibility(View.GONE);
		} else {
			TextView view = (TextView) findViewById(R.id.mindpin_alert_dialog_title);
			view.setText(title);
		}
	}

	public void set_title(String title) {
		this.title = (String) title;
	}
	
	public void set_Title(int title_id) {
		this.title = context.getResources().getString(title_id);
	}

	public void set_message(String message) {
		this.message = message;
	}

	public void set_content(View view) {
		this.ContentView = view;
	}
	
	public void set_content(int layoutResID) {
		LayoutInflater factory = LayoutInflater
				.from(context);
		View view = factory.inflate(layoutResID, null);
		this.ContentView = view;
	}

	public void set_button1(String lable,
			DialogInterface.OnClickListener listener) {
		this.button1_lable = lable;
		this.button1_listener = listener;
	}

	public void set_button2(String lable,
			DialogInterface.OnClickListener listener) {
		this.button2_lable = lable;
		this.button2_listener = listener;
	}

	public void set_button3(String lable,
			DialogInterface.OnClickListener listener) {
		this.button3_lable = lable;
		this.button3_listener = listener;
	}

}
