package com.teamkn.widget.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.activity.note.EditNoteActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;

public class NoteListAdapter extends TeamknBaseAdapter<Note> {
    Activity activity ;
    public NoteListAdapter(TeamknBaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public View inflate_view() {
        return inflate(R.layout.list_note_item, null);
    }

    @Override
    public BaseViewHolder build_view_holder(View view) {

        ViewHolder view_holder      = new ViewHolder();
        view_holder.note_info_tv    = (TextView)  view.findViewById(R.id.note_info_tv);
        view_holder.note_content_tv = (TextView)  view.findViewById(R.id.note_content_tv);
        view_holder.note_image_iv   = (ImageView) view.findViewById(R.id.note_image_show_iv);
        view_holder.note_image_iv_edit  = (ImageView) view.findViewById(R.id.note_image_iv_edit);
        view_holder.note_image_iv_delete = (ImageView) view.findViewById(R.id.note_image_iv_delete);
        view_holder.note_time_tv = (TextView) view.findViewById(R.id.note_time_tv);
        return view_holder;

    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final Note item,
                               int position) {
	
        ViewHolder view_holder = (ViewHolder) holder;
        view_holder.note_info_tv.setTag(R.id.tag_note_uuid, item.uuid);
        view_holder.note_info_tv.setTag(R.id.tag_note_kind, item.kind);
        

        if (item.kind.equals(NoteDBHelper.Kind.IMAGE)) {
        	ImageCache.load_cached_image(Note.note_thumb_image_file(item.uuid), view_holder.note_image_iv);
            view_holder.note_image_iv.setVisibility(View.VISIBLE);
        } else {
            view_holder.note_image_iv.setVisibility(View.GONE);
        }
        view_holder.note_content_tv.setText(item.content);
        view_holder.note_time_tv.setText(BaseUtils.date_curront_time_String(item.client_created_time));
        final int p = position;
        view_holder.note_image_iv_delete.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {				
				AlertDialog.Builder builder = new AlertDialog.Builder(activity); //这里只能用this，不能用appliction_context
				builder
					.setTitle("删除信息")
					.setMessage("确定要删除吗")
					.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								NoteDBHelper.destroy(item.uuid);
								NoteListAdapter.this.remove_item(p);
							}
						})
					.setNegativeButton(R.string.dialog_cancel, null)
					.show();
			}
		});
        view_holder.note_image_iv_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent   intent  = new Intent(activity, EditNoteActivity.class);
                intent.putExtra(EditNoteActivity.Extra.NOTE_UUID, item.uuid);
                intent.putExtra(EditNoteActivity.Extra.NOTE_KIND, item.kind);
                
                if (item.kind == NoteDBHelper.Kind.IMAGE) {
                    String image_path = Note.note_image_file(item.uuid).getPath();
                    intent.putExtra(EditNoteActivity.Extra.NOTE_IMAGE_PATH,image_path);
                } 
                activity.startActivityForResult(intent,MainActivity.RequestCode.EDIT_TEXT);
			}
		});
        
    }

    private class ViewHolder implements BaseViewHolder {
        TextView  note_info_tv;
        TextView  note_content_tv;
        ImageView note_image_iv;
        ImageView note_image_iv_edit;
        ImageView note_image_iv_delete;
        
        TextView note_time_tv;
        
    }

}
