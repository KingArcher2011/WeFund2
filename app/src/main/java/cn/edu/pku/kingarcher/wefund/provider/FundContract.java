package cn.edu.pku.kingarcher.wefund.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by xtrao on 2016/3/22.
 */
public class FundContract {

    private FundContract() {

    }

    public static final String AUTHORITY = "cn.edu.pku.kingarcher.wefund";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_BASE = "base_fund";

    public static final String PATH_A = "a_fund";

    public static final String PATH_B = "b_fund";

    public static final String TABLE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.kingarcher.wefund.entries";

    public static final String ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.kingarcher.wefund.entry";

    public static final String JOIN_TABLE = "(" + BaseFund.TABLE_NAME + " INNER JOIN " + AFund.TABLE_NAME + " ON "
                                            + BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_ID + " = "
                                            + AFund.TABLE_NAME + "." + AFund.COLUMN_NAME_BASE_ID + ")"
                                            + " INNER JOIN " + BFund.TABLE_NAME + " ON "
                                            + BaseFund.TABLE_NAME + "." + BaseFund.COLUMN_NAME_ID + " = "
                                            + BFund.TABLE_NAME + "." + BFund.COLUMN_NAME_BASE_ID;
    public static final Uri JOIN_FUND_URI = BASE_URI.buildUpon().appendPath("all").build();

    public static class BaseFund implements BaseColumns {

        public static final String TABLE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.kingarcher.wefund.base.entries";

        public static final String ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.kingarcher.wefund.base.entry";

        public static final Uri BASE_FUND_URI = BASE_URI.buildUpon().appendPath(PATH_BASE).build();

        public static final String TABLE_NAME = "base_fund";

        public static final String COLUMN_NAME_ID = "base_fund_id";

        public static final String COLUMN_NAME_NAME = "base_fund_nm";

        public static final String COLUMN_NAME_PRICE = "price";

        public static final String COLUMN_NAME_COMPANY = "fund_company_nm";

        public static final String COLUMN_NAME_OVERFLOW = "base_est_dis_rt";

        public static final String COLUMN_NAME_MARKET = "market";

        public static final String COLUMN_NAME_MANAGER = "fund_manager";

    }

    public static class AFund implements BaseColumns {

        public static final String TABLE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.kingarcher.wefund.a.entries";

        public static final String ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.kingarcher.wefund.a.entry";

        public static final Uri A_FUND_URI = BASE_URI.buildUpon().appendPath(PATH_A).build();

        public static final String TABLE_NAME = "a_fund";

        public static final String COLUMN_NAME_ID = "funda_id";

        public static final String COLUMN_NAME_NAME = "funda_name";

        public static final String COLUMN_NAME_PRICE = "funda_current_price";

        public static final String COLUMN_NAME_VALUE = "funda_value";

        public static final String COLUMN_NAME_PROFIT_RATE = "funda_profit_rt";

        public static final String COLUMN_NAME_INCREASE_RATE = "funda_increase_rt";

        public static final String COLUMN_NAME_BASE_ID = "funda_base_fund_id";

    }

    public static class BFund implements BaseColumns {

        public static final String TABLE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.kingarcher.wefund.b.entries";

        public static final String ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.kingarcher.wefund.b.entry";

        public static final Uri B_FUND_URI = BASE_URI.buildUpon().appendPath(PATH_B).build();

        public static final String TABLE_NAME = "b_fund";

        public static final String COLUMN_NAME_ID = "fundb_id";

        public static final String COLUMN_NAME_NAME = "fundb_name";

        public static final String COLUMN_NAME_PRICE = "fundb_current_price";

        public static final String COLUMN_NAME_INDEX_NAME = "fundb_index_name";

        public static final String COLUMN_NAME_INCREASE_RATE = "fundb_increase_rt";

        public static final String COLUMN_NAME_BASE_ID = "fundb_base_fund_id";

    }

}
