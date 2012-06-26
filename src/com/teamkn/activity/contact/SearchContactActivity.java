package com.teamkn.activity.contact;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;

public class SearchContactActivity extends TeamknBaseActivity {
  private EditText search_contact_et;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.search_contact);
    search_contact_et = (EditText)findViewById(R.id.search_contact_et);
  }
  
  public void click_search_contact_bn(View view){
    String query = search_contact_et.getText().toString();
    System.out.println(query);
    BaseUtils.toast("正在施工");
  }
}
