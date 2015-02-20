package it.polimi.frontend.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import it.polimi.frontend.fragment.AccountSettings;
import it.polimi.frontend.fragment.FeedbackDetail;
import it.polimi.frontend.fragment.MasterFragment;
import it.polimi.frontend.util.QueryManager;

public class SettingsActivity extends ActionBarActivity implements AccountSettings.OnFeedbackOptionClickedListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	public void OnFeedbackOptionClicked() {
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.accSetting);
		getSupportFragmentManager().beginTransaction()
		.hide(f)
		.addToBackStack("xyz")
		.add(R.id.accSettingContainer, 
				new FeedbackDetail(QueryManager.getInstance().getCurrentUser(), MasterFragment.ALL_REQUEST, null), 
				FeedbackDetail.ID)
				.commit();
	}

}
