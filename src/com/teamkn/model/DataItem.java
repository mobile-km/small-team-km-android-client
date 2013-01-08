package com.teamkn.model;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.teamkn.Logic.CompressPhoto;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.base.BaseModel;
public class DataItem extends BaseModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	public static class Kind {
	    public static final String TEXT = "TEXT";
	    public static final String IMAGE = "IMAGE";
	    public static final String URL = "URL";
	    public static final String PRODUCT = "PRODUCT"; 
	}
	 @Expose
	  public int id = -1;	
	  @Expose
	  public String title;
	  @Expose
	  public String content;
	  @Expose
	  public String url;
	  @Expose
	  public String kind;
	  @Expose
	  public int server_data_list_id;
	  @Expose
	  public String position;	
	  @Expose
	  public int server_data_item_id;
	  @Expose
	  public String seed; // 字段标示 字符 
	  
//	  @Expose
	  public Product product;
	  
	  //添加的外链帮助字段
	  int next_commits_count;  // 剩余还有几项
	  String operation;   // # CREATE UPDATE REMOVE ORDER
	  boolean conflict;  //  是否有冲突
	  
	  public static DataItem NIL_DATA_ITEM = new DataItem();
	  
	 
	  
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public int getData_list_id() {
		return server_data_list_id;
	}
	public void setData_list_id(int server_data_list_id) {
		this.server_data_list_id = server_data_list_id;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public int getServer_data_item_id() {
		return server_data_item_id;
	}
	public void setServer_data_item_id(int server_data_item_id) {
		this.server_data_item_id = server_data_item_id;
	}
	public static DataItem getNIL_DATA_ITEM() {
		return NIL_DATA_ITEM;
	}
	public static void setNIL_DATA_ITEM(DataItem nIL_DATA_ITEM) {
		NIL_DATA_ITEM = nIL_DATA_ITEM;
	}
	
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		this.seed = seed;
	}
	
	
	public int getNext_commits_count() {
		return next_commits_count;
	}
	public void setNext_commits_count(int next_commits_count) {
		this.next_commits_count = next_commits_count;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public boolean isConflict() {
		return conflict;
	}
	public void setConflict(boolean conflict) {
		this.conflict = conflict;
	}
	public DataItem() {
		super();
	}
	public DataItem(int id, String title, String content, String url,
			String kind, int server_data_list_id, String position,
			int server_data_item_id, String seed) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.url = url;
		this.kind = kind;
		this.server_data_list_id = server_data_list_id;
		this.position = position;
		this.server_data_item_id = server_data_item_id;
		this.seed = seed;
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
	+ " : " +  server_data_list_id + " : " +  position 
	+ " : " +  server_data_item_id + " : " + seed;
		
		return to;
	}
}