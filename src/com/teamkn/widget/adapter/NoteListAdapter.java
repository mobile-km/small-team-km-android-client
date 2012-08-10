package com.teamkn.widget.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.teamkn.Logic.CompressPhoto;
import com.teamkn.R;
import com.teamkn.activity.base.MainActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
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
        view_holder.note_image_iv   = (ImageView) view.findViewById(R.id.note_image_iv);
        view_holder.note_image_iv_delete = (ImageView) view.findViewById(R.id.note_image_iv_delete);
        return view_holder;

    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               final Note item,
                               int position) {

        final ViewHolder view_holder = (ViewHolder) holder;
        view_holder.note_info_tv.setTag(R.id.tag_note_uuid, item.uuid);
        view_holder.note_info_tv.setTag(R.id.tag_note_kind, item.kind);

        if (item.kind.equals(NoteDBHelper.Kind.IMAGE)) {
            ImageCache.load_cached_image(Note.note_thumb_image_file(item.uuid), view_holder.note_image_iv);
            view_holder.note_image_iv.setVisibility(View.VISIBLE);
        } else {
            view_holder.note_image_iv.setVisibility(View.GONE);
        }
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
        view_holder.note_content_tv.setText(item.content);
    }

    private class ViewHolder implements BaseViewHolder {
        TextView  note_info_tv;
        TextView  note_content_tv;
        ImageView note_image_iv;
        ImageView note_image_iv_delete;
    }

}
