package com.teamkn.widget.adapter;

import android.view.View;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.model.Note;

public class NoteListAdapter extends TeamknBaseAdapter<Note> {

  public NoteListAdapter(TeamknBaseActivity activity) {
    super(activity);
  }

  @Override
  public View inflate_view() {
    return inflate(R.layout.list_note_item, null);
  }

  @Override
  public BaseViewHolder build_view_holder(
      View view) {
    
    ViewHolder view_holder = new ViewHolder();
    view_holder.note_uuid_tv = (TextView) view.findViewById(R.id.note_uuid_tv);
    view_holder.note_content_tv = (TextView) view.findViewById(R.id.note_content_tv);
    return view_holder;
    
  }

  @Override
  public void fill_with_data(
      BaseViewHolder holder,
      Note item, int position) {
    ViewHolder view_holder = (ViewHolder) holder;
    
    view_holder.note_uuid_tv.setText(item.uuid);
    view_holder.note_content_tv.setText(item.content);
  }
  
  private class ViewHolder implements BaseViewHolder{
    TextView note_uuid_tv;
    TextView note_content_tv;
  }

}
