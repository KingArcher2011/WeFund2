package cn.edu.pku.kingarcher.wefund;

import android.accounts.Account;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.kingarcher.wefund.fragment.AFundListFragment;
import cn.edu.pku.kingarcher.wefund.fragment.BFundListFragment;
import cn.edu.pku.kingarcher.wefund.fragment.BaseFundListFragment;
import cn.edu.pku.kingarcher.wefund.model.SyncUtils;
import cn.edu.pku.kingarcher.wefund.fragment.FundListFragment.OnFundListItemClickListener;
import cn.edu.pku.kingarcher.wefund.fragment.FundListFragment.Type;
import cn.edu.pku.kingarcher.wefund.provider.FundContract;
import cn.edu.pku.kingarcher.wefund.util.GenericAccountService;

public class MainActivity extends AppCompatActivity implements OnFundListItemClickListener {

    public static final String TAG = "MainActivity";

    private static boolean mInitial = true;

    private Object mSyncObserverHandle;

    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mInitial) {
            findViewById(R.id.startup).setVisibility(View.VISIBLE);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initial setting
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        //start sync-adapter
        initialSyncAdapter();
        //inflate viewpager with fund list fragment
        initialPagerAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null) {
            return;
        }
        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    @Override
    public void onFundListItemClick(Type type, String baseId) {
        Intent intent = new Intent(this, FundDetailActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putString(FundDetailActivity.ARG_CODE, id);
        bundle.putString(FundDetailActivity.ARG_BASECODE, baseId);
        bundle.putString(FundDetailActivity.ARG_TYPE, type.toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mOptionsMenu = menu;

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView == null) {
            Log.v(TAG, "searcheView is null");
        }
        if (searchManager == null) {
            Log.v(TAG, "SearchManager is null");
        }
        SearchableInfo info = searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), FundDetailActivity.class));
        if (info == null) {
            Log.v(TAG, "info is null");
        } else {
            searchView.setSearchableInfo(info);
        }
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }*/
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_refresh:
                SyncUtils.TriggerRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initialPagerAdapter() {
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab);
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        FundListPagerAdapter pagerAdapter = new FundListPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initialSyncAdapter() {
        SyncUtils.CreateSyncAccount(getApplicationContext());
    }

    public void setInitial(boolean initial) {
        mInitial = initial;
    }

    public boolean getInitial() {
        return mInitial;
    }

    public static class  FundListPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mList = new ArrayList<>();
        private String[] titles = {"母基金", "分级A", "分级B"/*, "收藏"*/};

        {
            mList.add(new BaseFundListFragment());
            mList.add(new AFundListFragment());
            mList.add(new BFundListFragment());
            //mList.add(new FavoriteFragment());
        }

        public FundListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE);
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                        setRefreshActionButtonState(false);
                        return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, FundContract.AUTHORITY);
                    /*boolean syncPending = ContentResolver.isSyncPending(
                            account, FundContract.AUTHORITY);*/
                    setRefreshActionButtonState(syncActive);
                }
            });
        }
    };

}
