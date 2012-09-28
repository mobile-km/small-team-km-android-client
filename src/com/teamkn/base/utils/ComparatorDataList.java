package com.teamkn.base.utils;

import java.util.Comparator;

import com.teamkn.model.DataList;

public class ComparatorDataList  implements Comparator{
	@Override
	 public int compare(Object arg0, Object arg1) {
		  DataList user0=(DataList)arg0;
		  DataList user1=(DataList)arg1;
		return 0;

		   //首先比较年龄，如果年龄相同，则比较名字

//		  int flag=user0.getAge().compareTo(user1.getAge());
//		  if(flag==0){
//		   return user0.getName().compareTo(user1.getName());
//		  }else{
//		   return flag;
//		  }  
	}

}
