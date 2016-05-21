package cn.edu.pku.kingarcher.wefund.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.kingarcher.wefund.R;
import cn.edu.pku.kingarcher.wefund.fragment.FundListFragment.Type;
import cn.edu.pku.kingarcher.wefund.provider.FundContract.BaseFund;
import cn.edu.pku.kingarcher.wefund.provider.FundContract.AFund;
import cn.edu.pku.kingarcher.wefund.provider.FundContract.BFund;
import cn.edu.pku.kingarcher.wefund.util.SingletonRequestQueue;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static cn.edu.pku.kingarcher.wefund.FundDetailActivity.ARG_TYPE;
import static cn.edu.pku.kingarcher.wefund.FundDetailActivity.ARG_CODE;

public class FundDetailFragment extends Fragment {

    private static final String BASE_IMAGE = "http://image.sinajs.cn/newchart/v5/fund/nav/b/";
    private static final String TIME_IMAGE = "http://image.sinajs.cn/newchart/min/n/";
    private static final String DAILY_IMAGE = "http://image.sinajs.cn/newchart/daily/n/";
    private static final String WEEKLY_IMAGE = "http://image.sinajs.cn/newchart/weekly/n/";
    private static final String MONTHLY_IMAGE = "http://image.sinajs.cn/newchart/monthly/n/";

    //private static final String ARG_TYPE = "type";

    private Type mType;

    private String mCode;

    private String mMarket;

    private OnButtonClickListener mListener;

    //View
    TextView name;
    TextView code;
    TextView price;
    TextView rate;
    TextView special;
    TextView overflow;
    TextView company;
    TextView manager;

    TextView left_name;
    TextView left_code;
    TextView left_price;
    TextView left_rate;

    TextView right_name;
    TextView right_code;
    TextView right_price;
    TextView right_rate;

    TabLayout tablayout;
    ViewPager viewPager;

    NetworkImageView mTime;
    NetworkImageView mDaily;
    NetworkImageView mWeekly;
    NetworkImageView mMonthly;

    public FundDetailFragment() {
        // Required empty public constructor
    }

    public static FundDetailFragment newInstance(Type type, String code) {
        FundDetailFragment fragment = new FundDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type.toString());
        args.putString(ARG_CODE, code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String string = getArguments().getString(ARG_TYPE);
            if (string == null) {
                throw new IllegalArgumentException("ARG_TYPE shouldn't be null!");
            }
            switch(string) {
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
                    throw new IllegalArgumentException("Unsupported value of key ARG_TYPE!");
            }
            mCode = getArguments().getString(ARG_CODE);
            if (mCode.charAt(0) == '1') {
                mMarket = "sz";
            } else {
                mMarket = "sh";
            }
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fund_detail, container, false);
        name = (TextView)view.findViewById(R.id.name);
        code = (TextView)view.findViewById(R.id.code);
        price = (TextView)view.findViewById(R.id.price);
        rate = (TextView)view.findViewById(R.id.rate);
        special = (TextView)view.findViewById(R.id.special);
        overflow = (TextView)view.findViewById(R.id.overflow);
        company = (TextView)view.findViewById(R.id.company);
        manager = (TextView)view.findViewById(R.id.manager);

        left_name = (TextView)view.findViewById(R.id.left_name);
        left_code = (TextView)view.findViewById(R.id.left_code);
        left_price = (TextView)view.findViewById(R.id.left_price);
        left_rate = (TextView)view.findViewById(R.id.left_rate);

        right_name = (TextView)view.findViewById(R.id.right_name);
        right_code = (TextView)view.findViewById(R.id.right_code);
        right_price = (TextView)view.findViewById(R.id.right_price);
        right_rate = (TextView)view.findViewById(R.id.right_rate);

        tablayout = (TabLayout)view.findViewById(R.id.tablayout);
        viewPager = (ViewPager)view.findViewById(R.id.viewpager);

        switch(mType) {
            case BASE:
                initialBasePicture(tablayout, viewPager);
                return bindBaseToView(view);
            case A:
                initialABPicture(tablayout, viewPager);
                return bindAToView(view);
            case B:
                initialABPicture(tablayout, viewPager);
                return bindBToView(view);
            default:
                throw new IllegalArgumentException("Unsupported type value");
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnButtonClickListener) {
            mListener = (OnButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initialBasePicture(TabLayout tabLayout, ViewPager viewPager) {
        //ImageLoader imageLoader = SingletonRequestQueue.getInstance().getImageLoader();
        mTime = new NetworkImageView(getActivity());
        mTime.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        //time.setImageUrl(BASE_IMAGE + mCode + ".gif", imageLoader);
        final List<ImageView> views = new ArrayList<>();
        views.add(mTime);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position), 0);
                return views.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }

            @Override
            public CharSequence getPageTitle(int position) {
                List<String> list = new ArrayList<>();
                list.add("实时净值");
                return list.get(position);
            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    private void triggerBasePicture(String id) {
        ImageLoader imageLoader = SingletonRequestQueue.getInstance().getImageLoader();
        mTime.setImageUrl(BASE_IMAGE + id + ".gif", imageLoader);
    }

    private void initialABPicture(TabLayout tabLayout, ViewPager viewPager) {
        mTime = new NetworkImageView(getActivity());
        mTime.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mDaily = new NetworkImageView((getActivity()));
        mDaily.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mWeekly = new NetworkImageView(getActivity());
        mWeekly.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mMonthly = new NetworkImageView(getActivity());
        mMonthly.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        //ImageLoader imageLoader = SingletonRequestQueue.getInstance().getImageLoader();
        //time.setImageUrl(TIME_IMAGE + mMarket + mCode + ".gif", imageLoader);
        //daily.setImageUrl(DAILY_IMAGE + mMarket + mCode + ".gif", imageLoader);
        //weekly.setImageUrl(WEEKLY_IMAGE + mMarket + mCode + ".gif", imageLoader);
        //monthly.setImageUrl(MONTHLY_IMAGE + mMarket + mCode + ".gif", imageLoader);
        final List<ImageView> views = new ArrayList<>();
        views.add(mTime);
        views.add(mDaily);
        views.add(mWeekly);
        views.add(mMonthly);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position), 0);
                return views.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }

            @Override
            public CharSequence getPageTitle(int position) {
                List<String> list = new ArrayList<>();
                list.add("分时");
                list.add("日K");
                list.add("周K");
                list.add("月K");
                return list.get(position);
            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    private void triggerABPicture(String id) {
        ImageLoader imageLoader = SingletonRequestQueue.getInstance().getImageLoader();
        mTime.setImageUrl(TIME_IMAGE + mMarket + id + ".gif", imageLoader);
        mDaily.setImageUrl(DAILY_IMAGE + mMarket + id + ".gif", imageLoader);
        mWeekly.setImageUrl(WEEKLY_IMAGE + mMarket + id + ".gif", imageLoader);
        mMonthly.setImageUrl(MONTHLY_IMAGE + mMarket + id + ".gif", imageLoader);
    }

    public void triggerRefresh(Cursor cursor) {
        switch(mType) {
            case BASE:
                baseContentInflated(cursor);
                //basePictureInflated();
                break;
            case A:
                aContentInflated(cursor);
                //aPictureInflated();
                break;
            case B:
                bContentInflated(cursor);
                //bContentInflated();
                break;
            default:
                throw new IllegalArgumentException("Unsupported type value!");
        }
    }

    private void baseContentInflated(Cursor cursor) {
        name.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_NAME)));

        String base_code = cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_ID));
        triggerBasePicture(base_code);
        code.setText(base_code);

        price.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_PRICE)));
        price.setTextColor(0xFFCF0000);
        if(mMarket.equals("sz")) {
            rate.setText("深市");
        } else {
            rate.setText("沪市");
        }
        rate.setTextColor(0xFFCF0000);

        special.setText("溢价率");
        String overflow_rate = cursor.getFloat(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_OVERFLOW)) + "%";
        overflow.setText(overflow_rate);
        company.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_COMPANY)));
        manager.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_MANAGER)));

        left_name.setText(cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_NAME)));
        left_code.setText(cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_ID)));
        left_price.setText(cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_PRICE)));
        String a_rate = cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_INCREASE_RATE));
        left_rate.setText(a_rate);
        if (a_rate.charAt(0) == '-') {
            left_price.setTextColor(0xFF00AF00);
            left_rate.setTextColor(0xFF00AF00);
        } else {
            left_price.setTextColor(0xFFCF0000);
            left_rate.setTextColor(0xFFCF0000);
        }
        right_name.setText(cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_NAME)));
        right_code.setText(cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_ID)));
        right_price.setText(cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_PRICE)));
        String b_rate = cursor.getFloat(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_INCREASE_RATE)) + "%";
        right_rate.setText(b_rate);
        if (b_rate.charAt(0) == '-') {
            right_price.setTextColor(0xFF00AF00);
            right_rate.setTextColor(0xFF00AF00);
        } else {
            right_price.setTextColor(0xFFCF0000);
            right_rate.setTextColor(0xFFCF0000);
        }
    }

    private void aContentInflated(Cursor cursor) {
        name.setText(cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_NAME)));

        String a_code = cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_ID));
        triggerABPicture(a_code);
        code.setText(a_code);

        String a_rate = cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_INCREASE_RATE));
        if (a_rate.charAt(0) == '-') {
            price.setTextColor(0xFF00AF00);
            rate.setTextColor(0xFF00AF00);
        } else {
            price.setTextColor(0xFFCF0000);
            rate.setTextColor(0xFFCF0000);
            a_rate = "+" + a_rate;
        }
        price.setText(cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_PRICE)));
        rate.setText(a_rate);


        special.setText("收益率");
        String profit_rate = cursor.getFloat(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_PROFIT_RATE)) + "%";
        overflow.setText(profit_rate);
        company.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_COMPANY)));
        manager.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_MANAGER)));

        left_name.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_NAME)));
        left_code.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_ID)));
        left_price.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_PRICE)));
        String overflow_rate = cursor.getFloat(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_OVERFLOW)) + "%";
        left_rate.setText(overflow_rate);
        left_price.setTextColor(0xFFCF0000);
        left_rate.setTextColor(0xFFCF0000);

        right_name.setText(cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_NAME)));
        right_code.setText(cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_ID)));
        right_price.setText(cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_PRICE)));
        String b_rate = cursor.getFloat(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_INCREASE_RATE)) + "%";
        right_rate.setText(b_rate);
        if (b_rate.charAt(0) == '-') {
            right_price.setTextColor(0xFF00AF00);
            right_rate.setTextColor(0xFF00AF00);
        } else {
            right_price.setTextColor(0xFFCF0000);
            right_rate.setTextColor(0xFFCF0000);
        }
    }

    private void bContentInflated(Cursor cursor) {
        name.setText(cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_NAME)));

        String b_code = cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_ID));
        triggerABPicture(b_code);
        code.setText(b_code);

        String b_rate = cursor.getFloat(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_INCREASE_RATE)) + "%";
        if (b_rate.charAt(0) == '-') {
            price.setTextColor(0xFF00AF00);
            rate.setTextColor(0xFF00AF00);
        } else {
            price.setTextColor(0xFFCF0000);
            rate.setTextColor(0xFFCF0000);
            b_rate = "+" + b_rate;
        }
        price.setText(cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_PRICE)));
        rate.setText(b_rate);

        special.setText("相关指数");
        overflow.setText(cursor.getString(cursor.getColumnIndex(BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_INDEX_NAME)));
        company.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_COMPANY)));
        manager.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_MANAGER)));

        left_name.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_NAME)));
        left_code.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_ID)));
        left_price.setText(cursor.getString(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_PRICE)));
        String overflow_rate = cursor.getFloat(cursor.getColumnIndex(BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_OVERFLOW)) + "%";
        left_rate.setText(overflow_rate);
        left_price.setTextColor(0xFFCF0000);
        left_rate.setTextColor(0xFFCF0000);

        right_name.setText(cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_NAME)));
        right_code.setText(cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_ID)));
        right_price.setText(cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_PRICE)));
        String a_rate = cursor.getString(cursor.getColumnIndex(AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_INCREASE_RATE));
        right_rate.setText(a_rate);
        if (a_rate.charAt(0) == '-') {
            right_price.setTextColor(0xFF00AF00);
            right_rate.setTextColor(0xFF00AF00);
        } else {
            right_price.setTextColor(0xFFCF0000);
            right_rate.setTextColor(0xFFCF0000);
        }

    }

    private View bindBaseToView(View view) {
        view.findViewById(R.id.left_fund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClick(Type.A);
            }
        });
        view.findViewById(R.id.right_fund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClick(Type.B);
            }
        });
        return view;
    }

    private View bindAToView(View view) {
        view.findViewById(R.id.left_fund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClick(Type.BASE);
            }
        });
        view.findViewById(R.id.right_fund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClick(Type.B);
            }
        });
        return view;
    }

    private View bindBToView(View view) {
        view.findViewById(R.id.left_fund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClick(Type.BASE);
            }
        });
        view.findViewById(R.id.right_fund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClick(Type.A);
            }
        });
        return view;
    }

    public interface OnButtonClickListener {
        void onButtonClick(Type type);
    }
}
