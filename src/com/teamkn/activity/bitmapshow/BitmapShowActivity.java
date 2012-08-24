package com.teamkn.activity.bitmapshow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.teamkn.R;
import com.teamkn.Logic.CompressPhoto;
import com.teamkn.activity.note.EditNoteActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.base.utils.ImageTools;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper.Kind;

public class BitmapShowActivity extends TeamknBaseActivity{
	String note_uuid;
	ImageView bitmap_iv;
	static ImageView rotate_iv;
	static RelativeLayout bitmap_show_top;
	public static boolean isShow = true;
	Bitmap bmpAdd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bitmap_show);
		bitmap_iv = (ImageView)findViewById(R.id.bitmap_iv);
		rotate_iv = (ImageView)findViewById(R.id.rotate_iv);
		bitmap_show_top = (RelativeLayout)findViewById(R.id.bitmap_show_top);
		
		Intent intent = getIntent();
        note_uuid = intent.getStringExtra(EditNoteActivity.Extra.NOTE_UUID);
//        ImageCache.load_cached_image(Note.note_thumb_image_file(note_uuid),bitmap_iv);
        File file = Note.note_thumb_image_file(note_uuid);
        
        try {
			byte[] bt = ImageTools.getByte(file);
			Bitmap bmp = ImageTools.bytesToBimap(bt);
			 //DisplayMetrics 一个描述普通显示信息的结构，例如显示大小、密度、字体尺寸
	        DisplayMetrics displaysMetrics = new DisplayMetrics();
	        //获取手机窗口的Display 来初始化DisplayMetrics 对象
	        //getManager()获取显示定制窗口的管理器。
	        //获取默认显示Display对象
	        //通过Display 对象的数据来初始化一个DisplayMetrics 对象
	        getWindowManager().getDefaultDisplay().getMetrics( displaysMetrics );
	        //得到屏幕宽高
			bmpAdd = ImageTools.createBitmapBySize(bmp, displaysMetrics.widthPixels, displaysMetrics.heightPixels);
			
			bitmap_iv.setImageBitmap(bmpAdd);
//			ImageCache.load_cached_image(file,bitmap_iv);
		} catch (Exception e) {
			e.printStackTrace();
		}

        setTop();
		bitmap_iv.setOnTouchListener(new MulitPointTouchListener());
		rotate_iv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Bitmap btp = ImageTools.bitmapToRotate(bmpAdd);
				bitmap_iv.setImageBitmap(btp);
			}
		});
	}
	static void setTop(){
		bitmap_show_top.post(new Runnable() {
			@Override
			public void run() {
				
				if(isShow){
		        	bitmap_show_top.setVisibility(View.VISIBLE);
		        	rotate_iv.setVisibility(View.VISIBLE);
		        }else{
		        	bitmap_show_top.setVisibility(View.GONE);
		        	rotate_iv.setVisibility(View.GONE);
		        }
				isShow = !isShow;
			}
		});	
	}
}
