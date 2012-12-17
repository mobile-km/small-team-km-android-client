package com.teamkn.model;
import java.io.Serializable;

import com.teamkn.model.base.BaseModel;
import com.teamkn.model.database.UserDBHelper;

public class DataList extends BaseModel implements Serializable{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	  public int id = -1;
	  public int user_id;
	  public String title;
	  public String kind;
	  public String public_boolean;
	  public String has_commits;  // 是否有被  迁出的人
	  public int server_data_list_id=-1;	
	  public long server_created_time;
	  public long server_updated_time;
	  public int forked_from_id = -1;  // 迁出的这个列表来自哪
	  public String forked_from_is_removed = "false"; // 协作列表 已经 被原作者删除了
	  public String is_removed = "false";  // 是否被移除了
	  public boolean watched;  // 是否被收藏
	  
//	  forked : true|false,
	  public boolean forked;//是否被迁出
	  
    public boolean isForked() {
		return forked;
	}
	public void setForked(boolean forked) {
		this.forked = forked;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getPublic_boolean() {
		return public_boolean;
	}
	public void setPublic_boolean(String public_boolean) {
		this.public_boolean = public_boolean;
	}
	
	public String isHas_commits() {
		return has_commits;
	}
	public void setHas_commits(String has_commits) {
		this.has_commits = has_commits;
	}
	public int getServer_data_list_id() {
		return server_data_list_id;
	}
	public void setServer_data_list_id(int server_data_list_id) {
		this.server_data_list_id = server_data_list_id;
	}
	public static DataList getNIL_DATA_LIST() {
		return NIL_DATA_LIST;
	}
	public static void setNIL_DATA_LIST(DataList nIL_DATA_LIST) {
		NIL_DATA_LIST = nIL_DATA_LIST;
	}
	
	public long getServer_created_time() {
		return server_created_time;
	}
	public void setServer_created_time(long server_created_time) {
		this.server_created_time = server_created_time;
	}
	public long getServer_updated_time() {
		return server_updated_time;
	}
	public void setServer_updated_time(long server_updated_time) {
		this.server_updated_time = server_updated_time;
	}
	
	
	public int getForked_from_id() {
		return forked_from_id;
	}
	public void setForked_from_id(int forked_from_id) {
		this.forked_from_id = forked_from_id;
	}
	public String getHas_commits() {
		return has_commits;
	}
	
	public String isForked_from_is_removed() {
		return forked_from_is_removed;
	}
	public void setForked_from_is_removed(String forked_from_is_removed) {
		this.forked_from_is_removed = forked_from_is_removed;
	}
	public String isIs_removed() {
		return is_removed;
	}
	public void setIs_removed(String is_removed) {
		this.is_removed = is_removed;
	}

	

	public boolean isWatched() {
		return watched;
	}
	public void setWatched(boolean watched) {
		this.watched = watched;
	}
	public User get_user(){
		return UserDBHelper.find(this.user_id);
	}
	


	public static DataList NIL_DATA_LIST = new DataList();
	public DataList() {
		super();
	}
	
	
	
	
	public DataList(int user_id, String title, String kind,
			String public_boolean, String has_commits, int server_data_list_id,
			long server_created_time, long server_updated_time,
			int forked_from_id, String forked_from_is_removed,
			String is_removed) {
		super();
		this.user_id = user_id;
		this.title = title;
		this.kind = kind;
		this.public_boolean = public_boolean;
		this.has_commits = has_commits;
		this.server_data_list_id = server_data_list_id;
		this.server_created_time = server_created_time;
		this.server_updated_time = server_updated_time;
		this.forked_from_id = forked_from_id;
		this.forked_from_is_removed = forked_from_is_removed;
		this.is_removed = is_removed;
	}
	
	
	public DataList(int id, int user_id, String title, String kind,
			String public_boolean, String has_commits, int server_data_list_id,
			long server_created_time, long server_updated_time,
			int forked_from_id, String forked_from_is_removed, String is_removed) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.title = title;
		this.kind = kind;
		this.public_boolean = public_boolean;
		this.has_commits = has_commits;
		this.server_data_list_id = server_data_list_id;
		this.server_created_time = server_created_time;
		this.server_updated_time = server_updated_time;
		this.forked_from_id = forked_from_id;
		this.forked_from_is_removed = forked_from_is_removed;
		this.is_removed = is_removed;
	}
	@Override
	public String toString() {
		return id+" : "+user_id+" : "+title+" : "
				+kind+" : "+ public_boolean+" : "+has_commits+" : "+server_data_list_id + " : "
		+server_created_time + " : " + server_updated_time +" :  " +  forked_from_id+ " :  "
		+ forked_from_is_removed + " : " +  is_removed;
	}
}