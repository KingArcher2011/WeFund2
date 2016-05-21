package cn.edu.pku.kingarcher.wefund.fragment;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.edu.pku.kingarcher.wefund.MainActivity;
import cn.edu.pku.kingarcher.wefund.R;
import cn.edu.pku.kingarcher.wefund.util.DividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FundListFragment.OnFundListItemClickListener} interface
 * to handle interaction events.
 */
public abstract class FundListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public final String TAG = "FundListFragment";

    //use to define sub class's type
    public enum Type {
        BASE,
        A,
        B
    }

    Uri uri;

    private Cursor mCursor;

    private OnFundListItemClickListener mListener;

    private RecyclerView mRecyclerView;

    public FundListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fund_list, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.setHasFixedSize(true);
        TextView header_1 = (TextView)view.findViewById(R.id.header_1);
        TextView header_2 = (TextView)view.findViewById(R.id.header_2);
        TextView header_3 = (TextView)view.findViewById(R.id.header_3);
        bindHeader(header_1, header_2, header_3);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFundListItemClickListener) {
            mListener = (OnFundListItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFundListItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, null, null, null, null);
        setOrder(cursorLoader);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mCursor.setNotificationUri(getActivity().getContentResolver(), uri);
        Log.v(TAG, "Get cursor data");
        if (getAdapter() != null) {
            getAdapter().notifyDataSetChanged();
            Log.v(TAG, "Notify dataset changed");
        } else {
            Log.v(TAG, "Adapter is not prepare");
        }
        if(((MainActivity)getActivity()).getInitial()) {
            View view = getActivity().findViewById(R.id.startup);
            view.setVisibility(View.GONE);
            ((MainActivity)getActivity()).setInitial(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        if (getAdapter() != null) {
            getAdapter().notifyDataSetChanged();
        }
    }

    Cursor getCursor() {
        return mCursor;
    }

    OnFundListItemClickListener getListener() {
        return mListener;
    }

    abstract protected RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapter();

    abstract protected void bindHeader(TextView view1, TextView view2, TextView view3);

    abstract protected void setOrder(CursorLoader cursorLoader);

    public interface OnFundListItemClickListener {
        void onFundListItemClick(Type type, String baseId);
    }
}
