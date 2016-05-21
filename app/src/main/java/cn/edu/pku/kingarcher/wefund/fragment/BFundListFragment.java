package cn.edu.pku.kingarcher.wefund.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.pku.kingarcher.wefund.R;
import cn.edu.pku.kingarcher.wefund.model.BFundBean;
import cn.edu.pku.kingarcher.wefund.provider.FundContract;

/**
 * Created by xtrao on 2016/3/30.
 */
public class BFundListFragment extends FundListFragment {

    {
        uri = FundContract.BFund.B_FUND_URI;
    }

    final private Type mType = Type.B;

    BAdapter mBAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBAdapter = new BAdapter();
    }

    @Override
    final public BAdapter getAdapter() {
        return mBAdapter;
    }

    @Override
    protected void bindHeader(TextView view1, TextView view2, TextView view3) {
        view1.setText("价格");
        view2.setText("相关指数");
        view3.setText("涨幅");
    }

    @Override
    protected void setOrder(CursorLoader cursorLoader) {
        cursorLoader.setSortOrder(FundContract.BFund.COLUMN_NAME_INCREASE_RATE + " DESC");
    }

    class BAdapter extends RecyclerView.Adapter<BAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView image;
            private final TextView name;
            private final TextView code;
            private final TextView price;
            private final TextView index;
            private final TextView rate;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
                name = (TextView) itemView.findViewById(R.id.name);
                code = (TextView) itemView.findViewById(R.id.code);
                index = (TextView) itemView.findViewById(R.id.index);
                price = (TextView) itemView.findViewById(R.id.price);
                rate = (TextView) itemView.findViewById(R.id.rate);
            }

            public ImageView getImage() {
                return image;
            }
            public TextView getName() {
                return name;
            }
            public TextView getCode() {
                return code;
            }
            public TextView getIndex() {
                return index;
            }
            public TextView getPrice() {
                return price;
            }
            public TextView getRate() {
                return rate;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_b_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Cursor cursor = getCursor();
            if (cursor == null) {
                return;
            }
            cursor.moveToPosition(position);
            final String id = cursor.getString(cursor.getColumnIndex(FundContract.BFund.COLUMN_NAME_ID));

            viewHolder.getName().setText(cursor.getString(cursor.getColumnIndex(FundContract.BFund.COLUMN_NAME_NAME)));
            viewHolder.getName().setTextColor(0xFF000000);
            viewHolder.getName().setTextSize(15);
            viewHolder.getCode().setText(id);
            viewHolder.getIndex().setText(cursor.getString(cursor.getColumnIndex(FundContract.BFund.COLUMN_NAME_INDEX_NAME)));
            viewHolder.getPrice().setText(cursor.getString(cursor.getColumnIndex(FundContract.BFund.COLUMN_NAME_PRICE)));
            String string = cursor.getFloat(cursor.getColumnIndex(FundContract.BFund.COLUMN_NAME_INCREASE_RATE)) + "%";
            if(string.charAt(0) == '-') {
                viewHolder.getRate().setText(string);
                viewHolder.getRate().setTextColor(0xFF00AF00);
            } else {
                string = '+' + string;
                viewHolder.getRate().setText(string);
                viewHolder.getRate().setTextColor(0xFFCF0000);
            }

            final String baseId = cursor.getString(cursor.getColumnIndex(FundContract.BFund.COLUMN_NAME_BASE_ID));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getListener().onFundListItemClick(mType, baseId);
                }
            });

        }

        @Override
        public int getItemCount() {
            Cursor cursor = getCursor();
            if (cursor == null) {
                return 0;
            }
            return cursor.getCount();
        }
    }

}
