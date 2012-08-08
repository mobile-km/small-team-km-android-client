package com.teamkn.widget.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.teamkn.Logic.CompressPhoto;
import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.adapter.TeamknBaseAdapter;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.cache.image.ImageCacheSoftRefSingleton;
import com.teamkn.model.Note;
import com.teamkn.model.database.NoteDBHelper;

public class NoteListAdapter extends TeamknBaseAdapter<Note> {

    public NoteListAdapter(TeamknBaseActivity activity) {
        super(activity);
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
        return view_holder;

    }

    @Override
    public void fill_with_data(BaseViewHolder holder,
                               Note item,
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
    }

    private class ViewHolder implements BaseViewHolder {
        TextView  note_info_tv;
        TextView  note_content_tv;
        ImageView note_image_iv;
    }

}
