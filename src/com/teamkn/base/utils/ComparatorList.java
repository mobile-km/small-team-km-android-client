package com.teamkn.base.utils;

import java.util.Comparator;

public class ComparatorList  implements Comparator<Object>{
	  private String funcName;

	 public ComparatorList()
	{
	}

	  public ComparatorList(String func)
	{
	  this.funcName = func;
	}

	   public void setFuncName( String funcName)
	{
	  this.funcName = funcName;
	}

	 public String getFuncName()
	{
	  return this.funcName;
	}
	 public int compare(ComparatorList o1, ComparatorList o2)
	 {
//	            String fname1 = o1.getFuncName();
//	            String fname2 = o2.getFuncName();
				

	            //下面对fname和fnam2进行比较  
//				比较规则是取fname1和fname2的拼音首字母进行比较  
//				如果fname1的拼音首字母较大  
//				则返回1 否则返回-1 相等返回0
	            
	            return 0;
	 }

	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}
}
