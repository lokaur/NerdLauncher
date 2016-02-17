package andremartynov.nerdlauncher.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import andremartynov.nerdlauncher.R;

public class NerdLauncherFragment extends Fragment {

	private static final String TAG = "NerdLauncherFragment";

	private RecyclerView mRecyclerView;

	public static NerdLauncherFragment newInstance() {
		return new NerdLauncherFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
		mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_nerd_launcher_recycler_view);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		setupAdapter();
		return v;
	}

	private void setupAdapter() {
		Intent startupIntent = new Intent(Intent.ACTION_MAIN);
		startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		PackageManager pm = getActivity().getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
		Collections.sort(activities, new Comparator<ResolveInfo>() {
			@Override
			public int compare(ResolveInfo lhs, ResolveInfo rhs) {
				PackageManager pm = getActivity().getPackageManager();
				return String.CASE_INSENSITIVE_ORDER.compare(
						lhs.loadLabel(pm).toString(),
						rhs.loadLabel(pm).toString());
			}
		});

		Log.i(TAG, "Found " + activities.size() + " activities.");
		mRecyclerView.setAdapter(new ActivityAdapter(activities));
	}

	private class ActivityHolder extends RecyclerView.ViewHolder
	implements View.OnClickListener {
		private ResolveInfo mResolveInfo;
		private TextView mNameTextView;

		public ActivityHolder(View itemView) {
			super(itemView);
			mNameTextView = (TextView) itemView;
			mNameTextView.setOnClickListener(this);
		}

		public void bindActivity(ResolveInfo resolveInfo) {
			mResolveInfo = resolveInfo;
			PackageManager pm = getActivity().getPackageManager();
			mNameTextView.setText(mResolveInfo.loadLabel(pm).toString());
		}

		@Override
		public void onClick(View v) {
			ActivityInfo activityInfo = mResolveInfo.activityInfo;

			Intent intent = new Intent(Intent.ACTION_MAIN)
					.setClassName(activityInfo.applicationInfo.packageName,
							activityInfo.name)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(intent);
		}
	}

	private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
		private final List<ResolveInfo> mActivities;

		private ActivityAdapter(List<ResolveInfo> activities) {
			mActivities = activities;
		}

		@Override
		public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			return new ActivityHolder(view);
		}

		@Override
		public void onBindViewHolder(ActivityHolder holder, int position) {
			ResolveInfo resolveInfo = mActivities.get(position);
			holder.bindActivity(resolveInfo);
		}

		@Override
		public int getItemCount() {
			return mActivities.size();
		}
	}
}
