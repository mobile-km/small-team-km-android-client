package com.teamkn.pinyin4j;

import java.util.Arrays;

import com.teamkn.R;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class TestContactActivity extends Activity {
//    /** Called when the activity is first created. */
//	
//	private ListView lvContact;
//	private SideBar indexBar;
//	private WindowManager mWindowManager;
//	private TextView mDialogText;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
//        findView();
//    }
//    
//    private void findView(){
//    	lvContact = (ListView)this.findViewById(R.id.lvContact);
//    	lvContact.setAdapter(new ContactAdapter(this));
//    	indexBar = (SideBar) findViewById(R.id.sideBar);  
//        indexBar.setListView(lvContact); 
//        mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
//        mDialogText.setVisibility(View.INVISIBLE);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//        mWindowManager.addView(mDialogText, lp);
//        indexBar.setTextView(mDialogText);
//    }
//    
//    static class ContactAdapter extends BaseAdapter implements SectionIndexer {  
//    	private Context mContext;
//    	private String[] mNicks;
//    	@SuppressWarnings("unchecked")
//		public ContactAdapter(Context mContext){
//    		this.mContext = mContext;
//    		this.mNicks = nicks;
//    		//排序(实现了中英文混排)
//    		Arrays.sort(mNicks, new PinyinComparator());
//    	}
//		@Override
//		public int getCount() {
//			return mNicks.length;
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return mNicks[position];
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			
//			final String nickName = mNicks[position];
//			
//			ViewHolder viewHolder = null;
//			if(convertView == null){
//				convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item, null);
//				viewHolder = new ViewHolder();
//				viewHolder.tvCatalog = (TextView)convertView.findViewById(R.id.contactitem_catalog);
//				viewHolder.ivAvatar = (ImageView)convertView.findViewById(R.id.contactitem_avatar_iv);
//				viewHolder.tvNick = (TextView)convertView.findViewById(R.id.contactitem_nick);
//				convertView.setTag(viewHolder);
//			}else{
//				viewHolder = (ViewHolder)convertView.getTag();
//			}
//			
//			String catalog = converterToFirstSpell(nickName).substring(0, 1);
//			if(position == 0){
//				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
//				viewHolder.tvCatalog.setText(catalog);
//			}else{
//				String lastCatalog = converterToFirstSpell(mNicks[position-1]).substring(0, 1);
//				if(catalog.equals(lastCatalog)){
//					viewHolder.tvCatalog.setVisibility(View.GONE);
//				}else{
//					viewHolder.tvCatalog.setVisibility(View.VISIBLE);
//					viewHolder.tvCatalog.setText(catalog);
//				}
//			}
//			
//			viewHolder.ivAvatar.setImageResource(R.drawable.default_avatar);
//			viewHolder.tvNick.setText(nickName);
//			return convertView;
//		}
//    	
//		static class ViewHolder{
//			TextView tvCatalog;//目录
//			ImageView ivAvatar;//头像
//			TextView tvNick;//昵称
//		}
// 
//		@Override
//		public int getPositionForSection(int section) {
//			for (int i = 0; i < mNicks.length; i++) {  
//	            String l = converterToFirstSpell(mNicks[i]).substring(0, 1);  
//	            char firstChar = l.toUpperCase().charAt(0);  
//	            if (firstChar == section) {  
//	                return i;  
//	            }  
//	        } 
//			return -1;
//		}
//		@Override
//		public int getSectionForPosition(int position) {
//			return 0;
//		}
//		@Override
//		public Object[] getSections() {
//			return null;
//		}
//    }
//    
//    /**
//     * 昵称
//     */
//    private static String[] nicks = {"门旭","刘帅","刘盼","付久红","徐贤鱼","menxu","liushuai","liupan","fujiuhong","xuxianyu","阿雅","mak","tom","zhangsan","北风","张山","李四","欧阳锋","郭靖","黄蓉","杨过","凤姐","芙蓉姐姐","移联网","樱木花道","风清扬","张三丰","梅超风"};
//    /**  
//     * 汉字转换位汉语拼音首字母，英文字符不变  
//     * @param chines 汉字  
//     * @return 拼音  
//     */     
//    public static String converterToFirstSpell(String chines){             
//         String pinyinName = "";      
//        char[] nameChar = chines.toCharArray();      
//         HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();      
//         defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);      
//         defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);      
//        for (int i = 0; i < nameChar.length; i++) {      
//            if (nameChar[i] > 128) {      
//                try {      
//                     pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);      
//                 } catch (BadHanyuPinyinOutputFormatCombination e) {      
//                     e.printStackTrace();      
//                 }      
//             }else{      
//                 pinyinName += nameChar[i];      
//             }      
//         }      
//        return pinyinName;      
//     }      
}