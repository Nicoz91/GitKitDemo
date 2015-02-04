package it.polimi.frontend.activity;

import it.polimi.frontend.fragment.MasterFragment;
import it.polimi.frontend.fragment.RequestMap;

import java.util.Locale;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class TabbedActivity extends ActionBarActivity implements
ActionBar.TabListener {

	private static final int REQUEST_TAB=0;
	private static final int MAP_TAB=1;
	private static final int OWNER_TAB=2;
	private static final int JOINED_TAB=3;
	private MasterFragment masterFragment,masterFragmentOwner,masterFragmentJoined;
	private RequestMap requestMap;


	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabbed);
		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		//Siccome impostata come singleTop, così gestisce intent che arrivano per il search
		handleIntent(getIntent());
	}

	@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        super.onNewIntent(intent);
    }
	
	private void handleIntent(Intent intent){
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //TODO use the query to search your data somehow
            //Probabile soluzione per noi: chiamare il metodo di search del QueryManager
            //che alla fine del filtraggio notifica ai listener un nuovo ArrayList<Request>
            //con i risultati filtrati
        }
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tabbed, menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager =
					(SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView =
					(SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.searchRequest));
			searchView.setSearchableInfo(
					searchManager.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Intent i;
		switch (id) {
		case R.id.action_settings:
			i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		case R.id.logout:
			i = new Intent(this, MainActivity.class);
			i.putExtra("Reason", "Logout");
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			this.finish();
			return true;
		case R.id.insertRequest:
			i = new Intent(this, RequestActivity.class);
			startActivity(i);
			return true;
		case R.id.searchRequest:
			onSearchRequested();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition(),false);
	}



	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		switch (tab.getPosition()) {
		case REQUEST_TAB:
			if (masterFragment!=null && masterFragment.getChildFragmentManager().getBackStackEntryCount() > 0)
				masterFragment.getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			break;
		case MAP_TAB:
			if (requestMap!=null && requestMap.getChildFragmentManager().getBackStackEntryCount() > 0)
				requestMap.getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			break;
		case OWNER_TAB:
			if (masterFragmentOwner!=null && masterFragmentOwner.getChildFragmentManager().getBackStackEntryCount() > 0)
				masterFragmentOwner.getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			break;
		case JOINED_TAB:
			if (masterFragmentJoined!=null && masterFragmentJoined.getChildFragmentManager().getBackStackEntryCount() > 0)
				masterFragmentJoined.getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			break;
		default:
			break;
		}
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onBackPressed() {

		// If the fragment exists and has some back-stack entry
		if (masterFragment != null && mViewPager.getCurrentItem()==REQUEST_TAB && masterFragment.getChildFragmentManager().getBackStackEntryCount() > 0){
			// Get the fragment fragment manager - and pop the backstack
			masterFragment.getChildFragmentManager().popBackStack();
		} else if(requestMap != null && mViewPager.getCurrentItem()==MAP_TAB && requestMap.getChildFragmentManager().getBackStackEntryCount() > 0){
			requestMap.getChildFragmentManager().popBackStack();
		} else if (masterFragmentOwner != null && mViewPager.getCurrentItem()==OWNER_TAB && masterFragmentOwner.getChildFragmentManager().getBackStackEntryCount() > 0){
			// Get the fragment fragment manager - and pop the backstack
			masterFragmentOwner.getChildFragmentManager().popBackStack();
		} else if (masterFragmentJoined != null && mViewPager.getCurrentItem()==JOINED_TAB && masterFragmentJoined.getChildFragmentManager().getBackStackEntryCount() > 0){
			// Get the fragment fragment manager - and pop the backstack
			masterFragmentJoined.getChildFragmentManager().popBackStack();
		}
		// Else, nothing in the direct fragment back stack
		else{
			// Let super handle the back press
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra("Reason", "Exit");
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			this.finish();
			//super.onBackPressed();
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			Bundle args = new Bundle();
			switch (position) {
			case REQUEST_TAB:
				masterFragment = new MasterFragment();
				args.putInt("mode", MasterFragment.ALL_REQUEST);
				masterFragment.setArguments(args);
				return masterFragment;
			case MAP_TAB:
				requestMap = new RequestMap();
				return requestMap;
			case OWNER_TAB:
				masterFragmentOwner = new MasterFragment();
				args.putInt("mode", MasterFragment.OWNER_REQUEST);
				masterFragmentOwner.setArguments(args);
				return masterFragmentOwner;
			case JOINED_TAB:
				masterFragmentJoined = new MasterFragment();
				args.putInt("mode", MasterFragment.JOINED_REQUEST);
				masterFragmentJoined.setArguments(args);
				return masterFragmentJoined;
				//case 4:
				//	return PlaceholderFragment.newInstance(position + 1);
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case REQUEST_TAB:
				return getString(R.string.request_tab_title).toUpperCase(l);
			case MAP_TAB:
				return getString(R.string.map_tab_title).toUpperCase(l);
			case OWNER_TAB:
				return getString(R.string.owner_tab_title).toUpperCase(l);
			case JOINED_TAB:
				return getString(R.string.joined_tab_title).toUpperCase(l);
			default:
				return "";
			}
		}

		/* Non so se sia realmente necessario, ma pare sia meglio fare override di questo 
		 * metodo in caso di viewpager che contengono liste*/
		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			if (observer != null) {
				super.unregisterDataSetObserver(observer);
			}
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tabbed,
					container, false);
			return rootView;
		}
	}
}
