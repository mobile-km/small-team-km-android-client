package com.teamkn.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.teamkn.Logic.CompressPhoto;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.base.utils.FileDirs;
import com.teamkn.model.base.BaseModel;
import com.teamkn.model.database.ChatDBHelper;

public class Chat extends BaseModel {
  public int id;
  public int server_chat_id;
  public long server_created_time;
  public long server_updated_time;
  public List<User> members;
  public List<Integer> server_user_id_list;
  public String uuid;
  
  final public static Chat NIL_CHAT = new Chat();

  private Chat() {
      set_nil();
  }

  public Chat(String uuid, int id, int server_chat_id, long server_created_time,
      long server_updated_time) {
    this.uuid = uuid;
    this.id = id;
    this.server_chat_id = server_chat_id;
    this.server_created_time = server_created_time;
    this.server_updated_time = server_updated_time;
    this.members = ChatDBHelper.get_member_list(this.id);
    this.server_user_id_list = get_member_server_user_id_list();
  }
  
  
  public boolean is_syned(){
    return this.server_chat_id != 0;
  }
  
  private List<Integer> get_member_server_user_id_list(){
    List<Integer> server_user_id_list = new ArrayList<Integer>();
    for(User user : this.members){
      server_user_id_list.add(user.user_id);
    }
    return server_user_id_list;
  }
  public static File note_image_file(String uuid) {
      File dir = new File(FileDirs.TEAMKN_CHATS_DIR, uuid);
      if (!dir.exists()) {
          dir.mkdir();
      }
      return new File(dir, "image");
  }
  public static File note_list_image_file(String uuid) {
	  File chat_image_file = note_list_image_file(uuid);
      if(!chat_image_file.exists()){ return null; }
      
      String thumb_image_file_path = BaseUtils.file_path_join(FileDirs.TEAMKN_CHATS_DIR.getPath(),uuid,"thumb_image");
      File thumb_image = new File(thumb_image_file_path);
      
      if(!thumb_image.exists()){
          Bitmap bitmap = CompressPhoto.get_thumb_bitmap_form_file(chat_image_file.getPath());
          try {
            FileOutputStream out = new FileOutputStream(thumb_image.getPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
      }
      
      return thumb_image;
  }
}
