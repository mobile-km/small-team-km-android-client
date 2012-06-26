package com.teamkn.activity.contact;

import android.os.Bundle;
import android.view.View;

import com.teamkn.R;
import com.teamkn.activity.note.SearchActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.utils.BaseUtils;

public class ContactsActivity extends TeamknBaseActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.contact_list);
  }
  
  public void click_to_search_contact_page(View view){
    open_activity(SearchContactActivity.class);
  }
}
