package com.teamkn.widget.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.teamkn.R;
import com.teamkn.model.Contact;
import com.teamkn.pinyin4j.PinyinComparator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactListAdapter_update  extends BaseAdapter implements SectionIndexer {
//	private String[] unchecked = {"unchecked"};
    private String item = null;
	private Context mContext;
	private List<Contact> list;
	@SuppressWarnings(value="unchecked")
	public ContactListAdapter_update(Context mContext,List<Contact> list,String item){
		this.mContext = mContext;
		this.item = item;
		this.list = getSort_name_List(list);	   
	}
	public List<Contact> getSort_name_List(List<Contact> list){
		List<Contact> sort_list = new ArrayList<Contact>();
		String nameStrs = "";
		for(Contact contact : list){
			nameStrs+=contact.contact_user_name+",";
		}
		String[] names = nameStrs.split(",");
		//排序(实现了中英文混排)
		Arrays.sort(names, new PinyinComparator());

		System.out.println("  ----------------------   ");
		for(int i = 0 ; i< list.size() ; i++ ){
			String name = names[i];
			for(int j = 0 ; j < list.size() ;j ++){
				if(list.get(j).contact_user_name.equals(name) && list.get(j).status.equals(item) ){
					sort_list.add(list.get(j));
				}
			}
		}
		return sort_list;	
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Contact contact = list.get(position);
		final String nickName = contact.contact_user_name;
		
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvCatalog = (TextView)convertView.findViewById(R.id.contactitem_catalog);
			viewHolder.ivAvatar = (ImageView)convertView.findViewById(R.id.contactitem_avatar_iv);
			viewHolder.tvNick = (TextView)convertView.findViewById(R.id.contactitem_nick);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		String catalog = converterToFirstSpell(nickName).substring(0, 1);
		if(position == 0){
			viewHolder.tvCatalog.setVisibility(View.VISIBLE);
			viewHolder.tvCatalog.setText(catalog);
		}else{
			String lastCatalog = converterToFirstSpell(list.get(position-1).contact_user_name).substring(0, 1);
			if(catalog.equals(lastCatalog)){
				viewHolder.tvCatalog.setVisibility(View.GONE);
			}else{
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
			}
		}
		
		viewHolder.tvNick.setText(nickName);
		
		if(list.get(position).contact_user_avatar != null){
		      Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(list.get(position).contact_user_avatar));
		      Drawable drawable = new BitmapDrawable(bitmap);
		      viewHolder.ivAvatar.setBackgroundDrawable(drawable);
	    }else{
		    	viewHolder.ivAvatar.setBackgroundResource(R.drawable.user_default_avatar_normal);
	    }
		return convertView;
	}

	static class ViewHolder{
		TextView tvCatalog;//目录
		ImageView ivAvatar;//头像
		TextView tvNick;//昵称
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < list.size(); i++) {  
            String l = converterToFirstSpell(list.get(i).contact_user_name).substring(0, 1);  
            char firstChar = l.toUpperCase().charAt(0);  
            if (firstChar == section) {  
                return i;  
            }  
        } 
		return -1;
	}
	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}
	@Override
	public Object[] getSections() {
		return null;
	}
	
    /**  
     * 汉字转换位汉语拼音首字母，英文字符不变  
     * @param chines 汉字  
     * @return 拼音  
     */     
    public static String converterToFirstSpell(String chines){             
         String pinyinName = "";      
        char[] nameChar = chines.toCharArray();      
         HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();      
         defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);      
         defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);      
        for (int i = 0; i < nameChar.length; i++) {      
            if (nameChar[i] > 128) {      
                try {      
                     pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);      
                 } catch (BadHanyuPinyinOutputFormatCombination e) {      
                     e.printStackTrace();      
                 }      
             }else{      
                 pinyinName += nameChar[i];      
             }      
         }      
        return pinyinName;      
     }

}
