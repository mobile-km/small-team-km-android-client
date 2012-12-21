package com.teamkn.base.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

public class FileDirs {
//	http://blog.csdn.net/ljl961890233bear/article/details/7846146
	
	// 拷贝文件
    public static void copyfile(File fromFile, File toFile,Boolean rewrite ){
    	if (!fromFile.exists()) {
            return;
    	}
    	if (!fromFile.isFile()) {
    		return ;
    	}
    	if (!fromFile.canRead()) {
    		return ;
    	}
    	if (!toFile.getParentFile().exists()) {
    		toFile.getParentFile().mkdirs();
    	}
    	if (toFile.exists() && rewrite) {
    		toFile.delete();
    	}
//  　　当文件不存时，canWrite一直返回的都是false
     // if (!toFile.canWrite()) {
      // MessageDialog.openError(new Shell(),"错误信息","不能够写将要复制的目标文件" + toFile.getPath());
     // Toast.makeText(this,"不能够写将要复制的目标文件", Toast.LENGTH_SHORT);
    	// return ;
      // }
		try {
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
			java.io.FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); //将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();
		} catch (Exception ex) {
			Log.e("readfile", ex.getMessage());
		}
	}
    // 尝试获得一个文件夹引用，如果文件夹不存在就创建该文件夹
    public static File get_or_create_dir(String path) {
        File dir = new File(
                Environment.getExternalStorageDirectory().getPath() + path
        );
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    /*
     * 创建文本文件.  
     *  
     */  
    public static File creatTxtFile(String file_name){ 
    	File filename = new File(TEAMKN_SEARCH_FILE_DIR + file_name);
    	System.out.println(filename.getPath());
        if (!filename.exists()) {  
            try {
				filename.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}  
            System.err.println(filename + "  已创建！");  
        }
        return filename;
    }
    /*
     * 读取文本文件转化为List.
     *  
     */  
    public static List<String> readTxtFileList(File filename){  
    	List<String> readList = new ArrayList<String>();
    	String readStr =  "";  
        try {  
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filename),"UTF-8");
            BufferedReader bufread = new BufferedReader(isr); 
            String read;
            while ((read = bufread.readLine()) != null) {  
                readStr = readStr + read+ "\r\n";
                readList.add(read);
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
        System.out.println("文件内容是:"+ "\r\n" + readStr);  
        return readList;  
    } 
    /*
     * 读取文本文件.
     *  
     */  
    public static String readTxtFile(File filename){  
    	List<String> readList = new ArrayList<String>();
    	String readStr =  "";  
        try {  
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filename),"UTF-8");
            BufferedReader bufread = new BufferedReader(isr); 
            String read;
            while ((read = bufread.readLine()) != null) {  
                readStr = readStr + read+ "\r\n";
                readList.add(read);
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
        System.out.println("文件内容是:"+ "\r\n" + readStr);  
        return readStr;  
    } 
    /*
     * 写文件. 
     *  
     */  
    public static void writeTxtFile(File filename , String newStr){  
    	replaceTxtByStr(filename,newStr);
    	String filein = newStr + "\r\n" + readTxtFile(filename);
        try {  
        	OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(filename),"UTF-8");
        	BufferedWriter writer=new BufferedWriter(write); 
        	writer.write(filein);
        	writer.close();
        } catch (IOException e1) {  
            e1.printStackTrace();  
        } 
    } 
    /** *//** 
     * 将文件中指定内容的第一行替换为其它内容. 
     *  
     * @param oldStr 
     *            查找内容 
     * @param replaceStr 
     *            替换内容 
     */  
    public static void replaceTxtByStr(File filename,String replaceStr) {  
        String temp = "";  
        try {  
            FileInputStream fis = new FileInputStream(filename);  
            InputStreamReader isr = new InputStreamReader(fis);  
            BufferedReader br = new BufferedReader(isr);  
            StringBuffer buf = new StringBuffer();  
  
            // 保存该行前面的内容  
//            for (int j = 1; (temp = br.readLine()) != null  
//                    && !temp.equals(oldStr); j++) {  
//            	 buf = buf.append(temp);  
//                 buf = buf.append(System.getProperty("line.separator"));  
//             } 
	    	while ((temp = br.readLine()) != null  
	                && !temp.equals(replaceStr)) {
	    		 buf = buf.append(temp);  
	             buf = buf.append(System.getProperty("line.separator"));  
			}
  
            // 将内容插入  
//            buf = buf.append(replaceStr);  
  
            // 保存该行后面的内容  
            while ((temp = br.readLine()) != null) {   
                buf = buf.append(temp);  
                buf = buf.append(System.getProperty("line.separator"));  
            }  
  
            br.close();  
            FileOutputStream fos = new FileOutputStream(filename);  
            PrintWriter pw = new PrintWriter(fos);  
            pw.write(buf.toString().toCharArray());  
            pw.flush();  
            pw.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    //从byte[]转file
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
        file = new File(outputFile);
             FileOutputStream fstream = new FileOutputStream(file);
             stream = new BufferedOutputStream(fstream);
             stream.write(b);
         } catch (Exception e) {
             e.printStackTrace();
        } finally {
            if (stream != null) {
                 try {
                    stream.close();
                 } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
         return file;
     }
   //从file转为byte[]
    public static byte[] getBytesFromFile(File f){
       if (f == null){
           return null;
      }
       try {
           FileInputStream stream = new FileInputStream(f);
           ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
           byte[] b = new byte[1000];
           int n;
           while ((n = stream.read(b)) != -1)
               out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e){
       }
        return null;
     }

    public final static File TEAMKN_DIR = get_or_create_dir("/teamkn");
    public final static File TEAMKN_DATA_ITEM_DIR = get_or_create_dir("/teamkn/dataitems");
    
    public final static File TEAMKN_TEMP_DIR = get_or_create_dir("/teamkn/temp");
    public final static File TEAMKN_CAPTURE_DIR = get_or_create_dir("/teamkn/capture");
    public final static File TEAMKN_CAPTURE_TEMP_DIR = get_or_create_dir("/teamkn/capture_temp");
    public final static File TEAMKN_IMAGE_CACHE_DIR = get_or_create_dir("/teamkn/image_cache");
    
    public final static File TEAMKN_SEARCH_LIST_HISTORY = get_or_create_dir("/teamkn/search_history");
    public final static String TEAMKN_SEARCH_FILE_DIR = Environment.getExternalStorageDirectory().getPath() 
			+ "/teamkn/search_history";
    
    public final static String TEAMKN_SEARCH_LIST_STR = "/search_list.txt";
    public final static String TEAMKN_SEARCH_USER_STR = "/search_user.txt";
    public final static File TEAMKN_SEARCH_LIST = creatTxtFile(TEAMKN_SEARCH_LIST_STR);
    public final static File TEAMKN_SEARCH_USER = creatTxtFile(TEAMKN_SEARCH_USER_STR);
   
}
