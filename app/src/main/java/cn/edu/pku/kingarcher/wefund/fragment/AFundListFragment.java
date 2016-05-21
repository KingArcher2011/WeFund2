package cn.edu.pku.kingarcher.wefund.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.edu.pku.kingarcher.wefund.R;
import cn.edu.pku.kingarcher.wefund.provider.FundContract;

/**
 * Created by xtrao on 2016/3/30.
 */
public class AFundListFragment extends FundListFragment {

    {
        uri = FundContract.AFund.A_FUND_URI;
    }

    final private Type mType = Type.A;

    private AAdapter mAAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAAdapter = new AAdapter();
    }

    @Override
    final public AAdapter getAdapter() {
        return mAAdapter;
    }

    @Override
    protected void bindHeader(TextView view1, TextView view2, TextView view3) {
        view1.setText("净值");
        view2.setText("价格");
        view3.setText("实际利率");
    }

    @Override
    protected void setOrder(CursorLoader cursorLoader) {
        cursorLoader.setSortOrder(FundContract.AFund.COLUMN_NAME_PROFIT_RATE + " DESC");
    }

    class AAdapter extends RecyclerView.Adapter<AAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView image;
            private final TextView name;
            private final TextView code;
            private final TextView value;
            private final TextView price;
            private final TextView rate;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
                name = (TextView) itemView.findViewById(R.id.name);
                code = (TextView) itemView.findViewById(R.id.code);
                value = (TextView) itemView.findViewById(R.id.net_price);
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
            public TextView getValue() {
                return value;
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_a_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Cursor cursor = getCursor();
            if (cursor == null) {
                return;
            }
            cursor.moveToPosition(position);
            final String id = cursor.getString(cursor.getColumnIndex(FundContract.AFund.COLUMN_NAME_ID));

            viewHolder.getName().setText(cursor.getString(cursor.getColumnIndex(FundContract.AFund.COLUMN_NAME_NAME)));
            viewHolder.getName().setTextColor(0xFF000000);
            viewHolder.getName().setTextSize(15);
            viewHolder.getCode().setText(id);
            viewHolder.getValue().setText(cursor.getString(cursor.getColumnIndex(FundContract.AFund.COLUMN_NAME_VALUE)));
            viewHolder.getPrice().setText(cursor.getString(cursor.getColumnIndex(FundContract.AFund.COLUMN_NAME_PRICE)));
            String string = cursor.getFloat(cursor.getColumnIndex(FundContract.AFund.COLUMN_NAME_PROFIT_RATE)) + "%";
            viewHolder.getRate().setText(string);
            viewHolder.getRate().setTextColor(0xFFCF0000);

            final String baseId = cursor.getString(cursor.getColumnIndex(FundContract.AFund.COLUMN_NAME_BASE_ID));
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
            if(cursor == null) {
                return 0;
            }
            return cursor.getCount();
        }
    }

}
