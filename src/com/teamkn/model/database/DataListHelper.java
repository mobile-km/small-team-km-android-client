package com.teamkn.model.database;

import java.util.ArrayList;
import java.util.List;

import com.teamkn.activity.base.MainActivity;
import com.teamkn.model.DataList;

public class DataListHelper {
	public static List<DataList> by_type(List<DataList> data_list , String type){
		List<DataList> list = new ArrayList<DataList>();
		if(type.equals(MainActivity.RequestCode.ALL)){
			list = data_list ;
		}else{
			for(DataList item : data_list){
				if(item.kind .equals(type)){
					list.add(item);
				}
			}
		}
		return list;
	}
}
