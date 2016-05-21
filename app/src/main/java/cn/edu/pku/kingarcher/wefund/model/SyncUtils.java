package cn.edu.pku.kingarcher.wefund.model;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import cn.edu.pku.kingarcher.wefund.provider.FundContract;
import cn.edu.pku.kingarcher.wefund.util.GenericAccountService;

/**
 * Created by xtrao on 2016/3/30.
 */
public class SyncUtils {

    private static final String CONTENT_AUTHORITY = FundContract.AUTHORITY;
    public static final String ACCOUNT_TYPE = "cn.edu.pku.kingarcher.wefund.account";


    public static void CreateSyncAccount(Context context) {

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = GenericAccountService.GetAccount(ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
            if (spf.getBoolean("pref_auto_sync", false)) {
                ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            }
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            if (spf.getBoolean("pref_period_sync", false)) {
                int interval = spf.getInt("pref_period_sync_interval", 60);
                ContentResolver.addPeriodicSync(
                        account, CONTENT_AUTHORITY, new Bundle(), interval);
            }
        }
        TriggerRefresh();
    }

    public static void setAutoSync(SharedPreferences sharedPreferences) {
        Account account = GenericAccountService.GetAccount(ACCOUNT_TYPE);
        if (sharedPreferences.getBoolean("pref_auto_sync", false)) {
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
        } else {
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, false);
        }
    }

    public static void setPeriodSync(SharedPreferences sharedPreferences) {
        Account account = GenericAccountService.GetAccount(ACCOUNT_TYPE);
        if (sharedPreferences.getBoolean("pref_period_sync", false)) {
            int interval = sharedPreferences.getInt("pref_period_sync_interval", 60);
            ContentResolver.addPeriodicSync(
                    account, CONTENT_AUTHORITY, new Bundle(), interval);
        } else {
            ContentResolver.removePeriodicSync(account, CONTENT_AUTHORITY, new Bundle());
        }
    }

    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                GenericAccountService.GetAccount(ACCOUNT_TYPE), // Sync account
                FundContract.AUTHORITY,                         // Content authority
                b);                                             // Extras
    }
}
