package com.mindpin.activity.contacts;

import java.util.List;

import android.os.Bundle;
import android.widget.GridView;

import com.mindpin.R;
import com.mindpin.Logic.HttpApi;
import com.mindpin.base.activity.MindpinBaseActivity;
import com.mindpin.base.task.MindpinAsyncTask;
import com.mindpin.model.ContactUser;
import com.mindpin.widget.adapter.FollowingGridAdapter;

public class FollowingGridActivity extends MindpinBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_following_grid);
		
		load_list_data();
	}

	private void load_list_data() {
		new MindpinAsyncTask<Void, Void, List<ContactUser>>(this,R.string.now_loading) {
			@Override
			public List<ContactUser> do_in_background(Void... params)
					throws Exception {
				return HttpApi.get_current_user_followings();
			}

			@Override
			public void on_success(List<ContactUser> followings) {
				GridView following_grid = (GridView) findViewById(R.id.following_grid);
				FollowingGridAdapter adapter = new FollowingGridAdapter(followings);
				following_grid.setAdapter(adapter);
			}
		}.execute();
	}
}
