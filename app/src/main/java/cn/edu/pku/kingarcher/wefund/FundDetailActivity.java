package cn.edu.pku.kingarcher.wefund;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageRequest;

import cn.edu.pku.kingarcher.wefund.fragment.FundDetailFragment;
import cn.edu.pku.kingarcher.wefund.fragment.FundListFragment;
import cn.edu.pku.kingarcher.wefund.fragment.FundListFragment.Type;
import cn.edu.pku.kingarcher.wefund.provider.FundContract;
import cn.edu.pku.kingarcher.wefund.util.SelectionBuilder;
import cn.edu.pku.kingarcher.wefund.util.SingletonRequestQueue;

public class FundDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, FundDetailFragment.OnButtonClickListener {

    public static final String ARG_TYPE = "type";
    public static final String ARG_BASECODE = "base_code";
    public static final String ARG_CODE = "code";

    //private String mCode;
    private String mBaseCode;
    private Type mType;

    private Cursor mCursor;

    //private Bitmap mBaseBitmap;
    //private Bitmap[] mABitmap = new Bitmap[3];
    //private Bitmap[] mBBitmap = new Bitmap[3];

    FundDetailFragment mBaseFragment;
    FundDetailFragment mAFragment;
    FundDetailFragment mBFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //mCode = getIntent().getExtras().getString(ARG_CODE);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mBaseCode = intent.getStringExtra(SearchManager.QUERY);
            mType = Type.BASE;
            Uri uri = FundContract.BaseFund.BASE_FUND_URI.buildUpon().appendPath(mBaseCode).build();
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            if(cursor.getCount() == 0) {
                ((TextView)findViewById(R.id.blank_fragment)).setText(R.string.incorrect_code);
                return;
            }
            initialFragment(mType);
            getSupportLoaderManager().initLoader(0, null, this);
        } else {
            mBaseCode = intent.getExtras().getString(ARG_BASECODE);
            String string = intent.getExtras().getString(ARG_TYPE);
            switch (string) {
                case "BASE":
                    mType = Type.BASE;
                    break;
                case "A":
                    mType = Type.A;
                    break;
                case "B":
                    mType = Type.B;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported type value!");
            }
            initialFragment(mType);
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    private void initialFragment(Type type) {
        mBaseFragment = FundDetailFragment.newInstance(Type.BASE, mBaseCode);
        mAFragment = FundDetailFragment.newInstance(Type.A, mBaseCode);
        mBFragment = FundDetailFragment.newInstance(Type.B, mBaseCode);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, mBaseFragment)
                   .hide(mBaseFragment)
                   .add(R.id.container, mAFragment)
                   .hide(mAFragment)
                   .add(R.id.container, mBFragment)
                   .hide(mBFragment);
        switch(type) {
            case BASE:
                transaction.show(mBaseFragment).commit();
                break;
            case A:
                transaction.show(mAFragment).commit();
                break;
            case B:
                transaction.show(mBFragment).commit();
                break;
            default:
                throw new IllegalArgumentException("Unsupported type value");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = FundContract.JOIN_FUND_URI.buildUpon().appendPath(mBaseCode).build();
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            return;
        }
        data.setNotificationUri(getContentResolver(),FundContract.BFund.B_FUND_URI);
        if(!data.moveToFirst()) {
            throw new RuntimeException("Cursor must have valid values!");
        }
        mBaseFragment.triggerRefresh(data);
        mAFragment.triggerRefresh(data);
        mBFragment.triggerRefresh(data);
        if (data != null) {
            Log.v("FundDetailActivity", "data is not null, data count is " + data.getCount());
        } else {
            Log.v("FundDetailActivity", "data is null!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onButtonClick(Type type) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mBaseFragment)
                   .hide(mAFragment)
                   .hide(mBFragment);
        switch(type) {
            case BASE:
                transaction.show(mBaseFragment)
                           .commit();
                break;
            case A:
                transaction.show(mAFragment)
                           .commit();
                break;
            case B:
                transaction.show(mBFragment)
                           .commit();
                break;
            default:
                throw new IllegalArgumentException("Unsupported type value");

        }
    }
}
