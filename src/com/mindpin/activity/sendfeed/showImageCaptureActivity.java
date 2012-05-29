package com.mindpin.activity.sendfeed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.mindpin.R;
import com.mindpin.base.activity.MindpinBaseActivity;

public class showImageCaptureActivity extends MindpinBaseActivity {
	public static final String EXTRA_NAME_IMAGE_CAPTURE_PATH = "image_capture";
	
	private Button back_bn;
	private ImageView image_view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image_capture);
		back_bn = (Button) findViewById(R.id.back);
		image_view = (ImageView)findViewById(R.id.capture_image);
		
		String path = getIntent().getStringExtra(EXTRA_NAME_IMAGE_CAPTURE_PATH);
		Bitmap b = BitmapFactory.decodeFile(path);
		image_view.setImageBitmap(b);
		
		back_bn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				showImageCaptureActivity.this.setResult(Activity.RESULT_OK,intent);
				showImageCaptureActivity.this.finish();
			}
		});
	}
}
