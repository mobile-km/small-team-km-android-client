package com.teamkn.model;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;

import com.teamkn.Logic.CompressPhoto;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.base.BaseModel;
public class DataItem extends BaseModel {
	  public int id;	 
	  public String title;
	  public String content;
	  public String url;
	  public String kind;
	  public int data_list_id;
	  public int position;	 
	  public int server_data_item_id;
	  public static DataItem NIL_DATA_ITEM = new DataItem();
	public DataItem() {
		super();
	}
	
	public DataItem(int id, String title, String content, String url,
			String kind, int data_list_id, int position, int server_data_item_id) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.url = url;
		this.kind = kind;
		this.data_list_id = data_list_id;
		this.position = position;
		this.server_data_item_id = server_data_item_id;
	}


	public static File data_item_image_file(String uuid) {
        File dir = new File(FileDirs.TEAMKN_DATA_ITEM_DIR, uuid);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, "image");
    }
	public static File data_item_thumb_image_file(String uuid) {
	      File data_item_image_file = data_item_image_file(uuid);
	      if(!data_item_image_file.exists()){ return null; }
	      
	      String thumb_image_file_path = BaseUtils.file_path_join(FileDirs.TEAMKN_DATA_ITEM_DIR.getPath(),uuid,"dataitems_image");
	      File thumb_image = new File(thumb_image_file_path);
	      
	      if(!thumb_image.exists()){
	        Bitmap bitmap = CompressPhoto.get_thumb_bitmap_form_file(data_item_image_file.getPath());
	        try {
	          FileOutputStream out = new FileOutputStream(thumb_image.getPath());
	          bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	        } catch (FileNotFoundException e) {
	          e.printStackTrace();
	        }
	      }
	      
	      return thumb_image;
   }
	@Override
	public String toString() {
		String to = id + " : " +  title + " : " 
	+  content + " : " +  url + " : " +  kind 
	+ " : " +  data_list_id + " : " +  position 
	+ " : " +  server_data_item_id;
		
		return to;
	}
}