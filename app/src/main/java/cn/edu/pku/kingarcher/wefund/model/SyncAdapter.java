package cn.edu.pku.kingarcher.wefund.model;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import cn.edu.pku.kingarcher.wefund.provider.FundContract;

/**
 * Created by xtrao on 2016/3/26.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String TAG = "SyncAdapter";

    private static final String BASE_URL = "https://www.jisilu.cn/data/sfnew/fundm_list/";

    private static final String A_URL = "https://www.jisilu.cn/data/sfnew/funda_list/";

    private static final String B_URL = "https://www.jisilu.cn/data/sfnew/fundb_list/";

    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    private final ContentResolver mContentResolver;

    private static final String[] PROJECTION_BASE = new String[] {FundContract.BaseFund.COLUMN_NAME_ID};

    private static final String[] PROJECTION_A = new String[] {FundContract.AFund.COLUMN_NAME_ID};

    private static final String[] PROJECTION_B = new String[] {FundContract.BFund.COLUMN_NAME_ID};

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.v(TAG, " begin perform sync");
        try {
            final URL base_url = new URL(BASE_URL);
            final URL a_url    = new URL(A_URL);
            final URL b_url    = new URL(B_URL);
            InputStream stream = null;
            try {
                stream = downloadUrl(base_url);
                updateBaseFund(stream, syncResult);
            } finally {
                if(stream != null) {
                    stream.close();
                }
            }
            try {
                stream = downloadUrl(a_url);
                updateAFund(stream, syncResult);
            } finally {
                if(stream != null) {
                    stream.close();
                }
            }
            try {
                stream = downloadUrl(b_url);
                updateBFund(stream, syncResult);
            } finally {
                if(stream != null) {
                    stream.close();
                }
            }
        } catch (MalformedURLException e) {
            syncResult.stats.numParseExceptions++;
        } catch (IOException e) {
            syncResult.stats.numIoExceptions++;
        } catch (JSONException e) {
            syncResult.stats.numParseExceptions++;
        } catch (RemoteException e) {
            syncResult.databaseError = true;
        } catch (OperationApplicationException e) {
            syncResult.databaseError = true;
        }
        Log.v(TAG, " finish perform sync");
    }

    private InputStream downloadUrl(URL url) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    private void updateBaseFund (InputStream stream, SyncResult syncResult)
            throws IOException, JSONException, RemoteException, OperationApplicationException {
        HashMap<String, ContentValues> hashMap;

        //step 1: get json string object
        StringBuffer buffer = convertInputStreamToString(stream);

        //step 2: parse json string
        hashMap = parseBaseJsonString(buffer.toString());

        //step 3: get data from Database.
        Cursor cursor = mContentResolver.query(FundContract.BaseFund.BASE_FUND_URI, PROJECTION_BASE, null, null, null);
        final int id_index = cursor.getColumnIndex(FundContract.BaseFund.COLUMN_NAME_ID);

        //step 4: sync database
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(id_index);
            Uri existingUri = FundContract.BaseFund.BASE_FUND_URI.buildUpon().appendPath(id).build();
            if(hashMap.containsKey(id)) {
                batch.add(ContentProviderOperation.newUpdate(existingUri)
                                                  .withValues(hashMap.get(id))
                                                  .build()
                );
                hashMap.remove(id);
                syncResult.stats.numUpdates++;
            } else {
                batch.add(ContentProviderOperation.newDelete(existingUri)
                                                  .build()
                );
                syncResult.stats.numDeletes++;
            }
        }
        cursor.close();
        for(String string : hashMap.keySet()) {
            batch.add(ContentProviderOperation.newInsert(FundContract.BaseFund.BASE_FUND_URI)
                                              .withValues(hashMap.get(string))
                                              .build()
            );
            syncResult.stats.numInserts++;
        }
        mContentResolver.applyBatch(FundContract.AUTHORITY, batch);
        mContentResolver.notifyChange(FundContract.BaseFund.BASE_FUND_URI, null, false);
    }

    private void updateAFund(InputStream stream, SyncResult syncResult)
            throws IOException, JSONException, RemoteException, OperationApplicationException {
        HashMap<String, ContentValues> hashMap;

        //step 1: get json string object
        StringBuffer buffer = convertInputStreamToString(stream);

        //step 2: parse json string
        hashMap = parseAJsonString(buffer.toString());

        //step 3: get data from Database.
        Cursor cursor = mContentResolver.query(FundContract.AFund.A_FUND_URI, PROJECTION_A, null, null, null);
        final int id_index = cursor.getColumnIndex(FundContract.AFund.COLUMN_NAME_ID);

        //step 4: sync database
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(id_index);
            Uri existingUri = FundContract.AFund.A_FUND_URI.buildUpon().appendPath(id).build();
            if(hashMap.containsKey(id)) {
                batch.add(ContentProviderOperation.newUpdate(existingUri)
                                .withValues(hashMap.get(id))
                                .build()
                );
                hashMap.remove(id);
                syncResult.stats.numUpdates++;
            } else {
                batch.add(ContentProviderOperation.newDelete(existingUri)
                                .build()
                );
                syncResult.stats.numDeletes++;
            }
        }
        cursor.close();
        for(String string : hashMap.keySet()) {
            batch.add(ContentProviderOperation.newInsert(FundContract.AFund.A_FUND_URI)
                            .withValues(hashMap.get(string))
                            .build()
            );
            syncResult.stats.numInserts++;
        }
        mContentResolver.applyBatch(FundContract.AUTHORITY, batch);
        mContentResolver.notifyChange(FundContract.AFund.A_FUND_URI, null, false);
    }

    private void updateBFund(InputStream stream, SyncResult syncResult)
            throws IOException, JSONException, RemoteException, OperationApplicationException {
        HashMap<String, ContentValues> hashMap;

        //step 1: get json string object
        StringBuffer buffer = convertInputStreamToString(stream);

        //step 2: parse json string
        hashMap = parseBJsonString(buffer.toString());

        //step 3: get data from Database.
        Cursor cursor = mContentResolver.query(FundContract.BFund.B_FUND_URI, PROJECTION_B, null, null, null);
        final int id_index = cursor.getColumnIndex(FundContract.BFund.COLUMN_NAME_ID);

        //step 4: sync database
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(id_index);
            Uri existingUri = FundContract.BFund.B_FUND_URI.buildUpon().appendPath(id).build();
            if(hashMap.containsKey(id)) {
                batch.add(ContentProviderOperation.newUpdate(existingUri)
                                .withValues(hashMap.get(id))
                                .build()
                );
                hashMap.remove(id);
                syncResult.stats.numUpdates++;
            } else {
                batch.add(ContentProviderOperation.newDelete(existingUri)
                                .build()
                );
                syncResult.stats.numDeletes++;
            }
        }
        cursor.close();
        for (String string : hashMap.keySet()) {
            batch.add(ContentProviderOperation.newInsert(FundContract.BFund.B_FUND_URI)
                            .withValues(hashMap.get(string))
                            .build()
            );
            syncResult.stats.numInserts++;
        }
        mContentResolver.applyBatch(FundContract.AUTHORITY, batch);
        mContentResolver.notifyChange(FundContract.BFund.B_FUND_URI, null, false);
    }

    private StringBuffer convertInputStreamToString(InputStream stream) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer;
    }

    private HashMap<String, ContentValues> parseBaseJsonString(String string) throws JSONException{
        JSONObject object = new JSONObject(string);
        JSONArray baseFundList = object.getJSONArray("rows");
        HashMap<String, ContentValues> hashMap = new HashMap<>();
        for (int i = 0; i < baseFundList.length(); ++i) {
            JSONObject fund = baseFundList.getJSONObject(i);
            //id
            String fund_id = fund.getString("id");
            //detail
            JSONObject fund_detail = fund.getJSONObject("cell");
            //insert to hash map
            BaseFundBean bean = new Gson().fromJson(fund_detail.toString(), BaseFundBean.class);
            ContentValues values = new ContentValues();
            values.put(FundContract.BaseFund.COLUMN_NAME_ID, bean.getBaseFundId());
            values.put(FundContract.BaseFund.COLUMN_NAME_NAME, bean.getBaseFundNm());
            values.put(FundContract.BaseFund.COLUMN_NAME_PRICE, bean.getPrice());
            values.put(FundContract.BaseFund.COLUMN_NAME_COMPANY, bean.getFund_company_nm());
            String overflow = bean.getBaseEstDisRt();
            Log.v("AAA",overflow);
            if (overflow.indexOf('%') != -1) {
                values.put(FundContract.BaseFund.COLUMN_NAME_OVERFLOW, Float.parseFloat(overflow.substring(0, overflow.indexOf('%'))));
            } else {
                values.put(FundContract.BaseFund.COLUMN_NAME_OVERFLOW, 0);
            }
            values.put(FundContract.BaseFund.COLUMN_NAME_MANAGER, bean.getFund_manager());
            values.put(FundContract.BaseFund.COLUMN_NAME_MARKET, bean.getMarket());
            hashMap.put(fund_id, values);
        }
        return hashMap;
    }

    private HashMap<String, ContentValues> parseAJsonString(String string) throws JSONException {
        JSONObject object = new JSONObject(string);
        JSONArray aFundList = object.getJSONArray("rows");
        HashMap<String, ContentValues> hashMap = new HashMap<>();
        for (int i = 0; i < aFundList.length(); ++i) {
            JSONObject fund = aFundList.getJSONObject(i);
            //id
            String fund_id = fund.getString("id");
            Log.v(TAG, "FundA " + fund_id + " has push to hashmap!");
            //detail
            JSONObject fund_detail = fund.getJSONObject("cell");
            //insert to hash map
            AFundBean bean = new Gson().fromJson(fund_detail.toString(), AFundBean.class);
            ContentValues values = new ContentValues();
            values.put(FundContract.AFund.COLUMN_NAME_ID, bean.getFunda_id());
            values.put(FundContract.AFund.COLUMN_NAME_NAME, bean.getFunda_name());
            values.put(FundContract.AFund.COLUMN_NAME_PRICE, bean.getFunda_current_price());
            values.put(FundContract.AFund.COLUMN_NAME_VALUE, bean.getFunda_value());
            String rate = bean.getFunda_profit_rt();
            values.put(FundContract.AFund.COLUMN_NAME_PROFIT_RATE, Float.parseFloat(rate.substring(0, rate.indexOf('%'))));
            values.put(FundContract.AFund.COLUMN_NAME_INCREASE_RATE, bean.getFunda_increase_rt());
            values.put(FundContract.AFund.COLUMN_NAME_BASE_ID, bean.getFunda_base_fund_id());
            hashMap.put(fund_id, values);
        }
        return hashMap;
    }

    private HashMap<String, ContentValues> parseBJsonString(String string) throws JSONException {
        JSONObject object = new JSONObject(string);
        JSONArray bFundList = object.getJSONArray("rows");
        HashMap<String, ContentValues> hashMap = new HashMap<>();
        for (int i = 0; i < bFundList.length(); ++i) {
            JSONObject fund = bFundList.getJSONObject(i);
            //detail
            JSONObject fund_detail = fund.getJSONObject("cell");
            //id
            String fund_id = fund_detail.getString("fundb_id");
            Log.v(TAG, "FundB " + fund_id + " has push to hashmap!");
            //insert to hash map
            BFundBean bean = new Gson().fromJson(fund_detail.toString(), BFundBean.class);
            ContentValues values = new ContentValues();
            values.put(FundContract.BFund.COLUMN_NAME_ID, bean.getFundb_id());
            values.put(FundContract.BFund.COLUMN_NAME_NAME, bean.getFundb_name());
            values.put(FundContract.BFund.COLUMN_NAME_PRICE, bean.getFundb_current_price());
            String rate = bean.getFundb_increase_rt();
            values.put(FundContract.BFund.COLUMN_NAME_INCREASE_RATE, Float.parseFloat(rate.substring(0, rate.indexOf('%'))));
            values.put(FundContract.BFund.COLUMN_NAME_INDEX_NAME, bean.getFundb_index_name());
            values.put(FundContract.BFund.COLUMN_NAME_BASE_ID, bean.getFundb_base_fund_id());
            hashMap.put(fund_id, values);
        }
        return hashMap;
    }

}
