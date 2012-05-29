package com.mindpin.activity.sendfeed;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.mindpin.R;
import com.mindpin.base.utils.location.AvatarMyLocationOverlay;

public class MyLocationActivity extends MapActivity {
	public static final int GEO_FIX_SUCESS = 110;
	private MapView map_view;
	private MyLocationOverlay my_location_overlay;
	private MapController map_controller;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GEO_FIX_SUCESS:
				map_controller.animateTo(my_location_overlay.getMyLocation());
				map_controller.setZoom(15);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.my_location);
		init_field();
		build_my_location_overlay();

		init_relocate_button();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onResume() {
		// 启动地位，尝试从 GPS或基站 获取设备位置
		my_location_overlay.enableMyLocation();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 关闭地位
		my_location_overlay.disableMyLocation();
		super.onPause();
	}
	
	private void init_relocate_button() {
		ViewGroup container = map_view.getZoomButtonsController().getContainer(); 
		Button button = new Button(this);
		button.setText("⊙");
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.RIGHT);
		
		params.setMargins(0, 0, 50, 0);
		container.addView(button,params);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				map_controller.animateTo(my_location_overlay.getMyLocation());
				map_controller.setZoom(15);
			}
		});
	}
	
	private void init_field() {
		map_view = (MapView) findViewById(R.id.map_view);
		map_view.setBuiltInZoomControls(true);
		map_controller = map_view.getController();
	}

	private void build_my_location_overlay() {
		my_location_overlay = new AvatarMyLocationOverlay(this, map_view);
		my_location_overlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				handler.sendEmptyMessage(GEO_FIX_SUCESS);
			}
		});
		map_view.getOverlays().add(my_location_overlay);
	}

}
