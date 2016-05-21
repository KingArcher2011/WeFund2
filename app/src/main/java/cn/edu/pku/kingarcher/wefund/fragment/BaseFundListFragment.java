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
 * Created by xtrao on 2016/3/29.
 */
public class BaseFundListFragment extends FundListFragment {

    {
        uri = FundContract.BaseFund.BASE_FUND_URI;
    }

    private BaseAdapter mBaseAdapter;

    final private Type mType = Type.BASE;

    @Override
    final public BaseAdapter getAdapter() {
        return mBaseAdapter;
    }

    @Override
    protected void bindHeader(TextView view1, TextView view2, TextView view3) {
        view1.setText("公司名称");
        view2.setText("价格");
        view3.setText("折溢比率");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseAdapter = new BaseAdapter();
    }

    @Override
    protected void setOrder(CursorLoader cursorLoader) {
        cursorLoader.setSortOrder(FundContract.BaseFund.COLUMN_NAME_OVERFLOW + " ASC");
    }

    class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView image;
            private final TextView name;
            private final TextView code;
            private final TextView company;
            private final TextView price;
            private final TextView overflow;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
                name = (TextView) itemView.findViewById(R.id.name);
                code = (TextView) itemView.findViewById(R.id.code);
                company = (TextView) itemView.findViewById(R.id.company);
                price = (TextView) itemView.findViewById(R.id.price);
                overflow = (TextView) itemView.findViewById(R.id.overflow);
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
            public TextView getCompany() {
                return company;
            }
            public TextView getPrice() {
                return price;
            }
            public TextView getOverflow() {
                return overflow;
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_base_list, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Cursor cursor = getCursor();
            if (cursor == null) {
                return;
            }
            cursor.moveToPosition(position);
            final String id = cursor.getString(cursor.getColumnIndex(FundContract.BaseFund.COLUMN_NAME_ID));

            // Todo set image sourse
            // viewHolder.getImage().setImageResource();

            viewHolder.getName().setText(cursor.getString(cursor.getColumnIndex(FundContract.BaseFund.COLUMN_NAME_NAME)));
            viewHolder.getName().setTextColor(0xFF000000);
            viewHolder.getName().setTextSize(15);
            viewHolder.getCode().setText(id);
            viewHolder.getCompany().setText(cursor.getString(cursor.getColumnIndex(FundContract.BaseFund.COLUMN_NAME_COMPANY)));
            viewHolder.getPrice().setText(cursor.getString(cursor.getColumnIndex(FundContract.BaseFund.COLUMN_NAME_PRICE)));
            String string = cursor.getFloat(cursor.getColumnIndex(FundContract.BaseFund.COLUMN_NAME_OVERFLOW)) + "%";
            if(string.charAt(0) == '-') {
                viewHolder.getOverflow().setText(string);
                viewHolder.getOverflow().setTextColor(0xFF00AF00);
            } else {
                string = '+' + string;
                viewHolder.getOverflow().setText(string);
                viewHolder.getOverflow().setTextColor(0xFFCF0000);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getListener().onFundListItemClick(mType, id);
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
