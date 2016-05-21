package cn.edu.pku.kingarcher.wefund.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.database.AbstractCursor;

import cn.edu.pku.kingarcher.wefund.util.SelectionBuilder;

public class FundContentProvider extends ContentProvider {

    FundDatabase mDatabaseHelper;

    private static final String AUTHORITY = FundContract.AUTHORITY;

    private static final int ALL_ENTRIES = 1;
    private static final int ALL_ENTRY = 2;
    private static final int BASE_ENTRIES = 3;
    private static final int BASE_ENTRY = 4;
    private static final int A_ENTRIES = 5;
    private static final int A_ENTRY = 6;
    private static final int B_ENTRIES = 7;
    private static final int B_ENTRY = 8;
    private static final int SEARCH = 9;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "all", ALL_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "all/*", ALL_ENTRY);
        sUriMatcher.addURI(AUTHORITY, "base_fund", BASE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "base_fund/*", BASE_ENTRY);
        sUriMatcher.addURI(AUTHORITY, "a_fund", A_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "a_fund/*", A_ENTRY);
        sUriMatcher.addURI(AUTHORITY, "b_fund", B_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "b_fund/*", B_ENTRY);
        sUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new FundDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_ENTRIES:
                return FundContract.TABLE_TYPE;
            case ALL_ENTRY:
                return FundContract.ITEM_TYPE;
            case BASE_ENTRIES:
                return FundContract.BaseFund.TABLE_TYPE;
            case BASE_ENTRY:
                return FundContract.BaseFund.ITEM_TYPE;
            case A_ENTRIES:
                return FundContract.AFund.TABLE_TYPE;
            case A_ENTRY:
                return FundContract.AFund.ITEM_TYPE;
            case B_ENTRIES:
                return FundContract.BFund.TABLE_TYPE;
            case B_ENTRY:
                return FundContract.BFund.ITEM_TYPE;
            case SEARCH:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        String id = uri.getLastPathSegment();
        switch(match) {
            case ALL_ENTRIES:
            case ALL_ENTRY:
            case SEARCH:
                throw new UnsupportedOperationException("Delete not supported on Uri: " + uri);
            case BASE_ENTRIES:
                count = builder.table(FundContract.BaseFund.TABLE_NAME)
                               .where(selection, selectionArgs)
                               .delete(db);
                break;
            case BASE_ENTRY:
                count = builder.table(FundContract.BaseFund.TABLE_NAME)
                               .where(FundContract.BaseFund.COLUMN_NAME_ID + "=?", id)
                               .where(selection, selectionArgs)
                               .delete(db);
                break;
            case A_ENTRIES:
                count = builder.table(FundContract.AFund.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case A_ENTRY:
                count = builder.table(FundContract.AFund.TABLE_NAME)
                        .where(FundContract.AFund.COLUMN_NAME_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case B_ENTRIES:
                count = builder.table(FundContract.BFund.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case B_ENTRY:
                count = builder.table(FundContract.BFund.TABLE_NAME)
                        .where(FundContract.BFund.COLUMN_NAME_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }
        //Context context = getContext();
        //context.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String id;
        int match = sUriMatcher.match(uri);
        switch(match) {
            case ALL_ENTRIES:
            case ALL_ENTRY:
            case BASE_ENTRY:
            case A_ENTRY:
            case B_ENTRY:
            case SEARCH:
                throw new UnsupportedOperationException("Insert not supported on Uri: " + uri);
            case BASE_ENTRIES:
                db.insert(FundContract.BaseFund.TABLE_NAME, null, values);
                id = values.getAsString(FundContract.BaseFund.COLUMN_NAME_ID);
                break;
            case A_ENTRIES:
                db.insert(FundContract.AFund.TABLE_NAME, null, values);
                id = values.getAsString(FundContract.AFund.COLUMN_NAME_ID);
                break;
            case B_ENTRIES:
                db.insert(FundContract.BFund.TABLE_NAME, null, values);
                id = values.getAsString(FundContract.BFund.COLUMN_NAME_ID);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        //Context context = getContext();
        //context.getContentResolver().notifyChange(uri, null, false);
        return Uri.parse(uri + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SelectionBuilder builder = new SelectionBuilder();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        String id = uri.getLastPathSegment();
        Cursor cursor;
        switch (match) {
            case ALL_ENTRY:
                builder.where(FundContract.BaseFund.TABLE_NAME + "." + FundContract.BaseFund.COLUMN_NAME_ID
                + "=?", id);
            case ALL_ENTRIES:
                cursor = builder.table(FundContract.JOIN_TABLE)
                                .where(selection, selectionArgs)
                                .query(db, projection, sortOrder);
                break;
            case BASE_ENTRY:
                builder.where(FundContract.BaseFund.COLUMN_NAME_ID + "=?", id);
            case BASE_ENTRIES:
                cursor = builder.table(FundContract.BaseFund.TABLE_NAME)
                                .where(selection, selectionArgs)
                                .query(db, projection, sortOrder);
                break;
            case A_ENTRY:
                builder.where(FundContract.AFund.COLUMN_NAME_ID + "=?", id);
            case A_ENTRIES:
                cursor = builder.table(FundContract.AFund.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .query(db, projection, sortOrder);
                break;
            case B_ENTRY:
                builder.where(FundContract.BFund.COLUMN_NAME_ID + "=?", id);
            case B_ENTRIES:
                cursor = builder.table(FundContract.BFund.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .query(db, projection, sortOrder);
                break;
            case SEARCH:
                String[] columns = new String[]{
                        FundContract.BaseFund._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_TEXT_2,
                        SearchManager.SUGGEST_COLUMN_QUERY
                };
                String search_selection = SearchManager.SUGGEST_COLUMN_TEXT_1 + " MATCH ?";
                String query = selectionArgs[0];
                String[] search_selectionArgs = new String[]{query+"*"};
                cursor = builder.table(FundContract.BaseFund.TABLE_NAME)
                                .where(search_selection, search_selectionArgs)
                                .map(SearchManager.SUGGEST_COLUMN_TEXT_1, FundContract.BaseFund.COLUMN_NAME_ID)
                                .map(SearchManager.SUGGEST_COLUMN_TEXT_2, FundContract.BaseFund.COLUMN_NAME_NAME)
                                .map(SearchManager.SUGGEST_COLUMN_QUERY, FundContract.BaseFund.COLUMN_NAME_ID)
                                .query(db, columns, sortOrder);
                Log.v("FundContentProvider", "cursor count is " + cursor.getCount());
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int count;
        String id = uri.getLastPathSegment();
        switch(match) {
            case ALL_ENTRIES:
            case ALL_ENTRY:
            case SEARCH:
                throw new UnsupportedOperationException("Update not supported on Uri: " + uri);
            case BASE_ENTRY:
                builder.where(FundContract.BaseFund.COLUMN_NAME_ID + "=?", id);
            case BASE_ENTRIES:
                count = builder.table(FundContract.BaseFund.TABLE_NAME)
                               .where(selection, selectionArgs)
                               .update(db, values);
                break;
            case A_ENTRY:
                builder.where(FundContract.AFund.COLUMN_NAME_ID + "=?", id);
            case A_ENTRIES:
                count = builder.table(FundContract.AFund.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case B_ENTRY:
                builder.where(FundContract.BFund.COLUMN_NAME_ID + "=?", id);
            case B_ENTRIES:
                count = builder.table(FundContract.BFund.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        //Context context = getContext();
        //context.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    static class FundDatabase extends SQLiteOpenHelper {
        /** Schema version. */
        public static final int DATABASE_VERSION = 1;
        /** Filename for SQLite file. */
        public static final String DATABASE_NAME = "fund.db";

        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String TYPE_REAL = " REAL";
        private static final String COMMA_SEP = ",";

        /** SQL statement to create "base" table. */
        private static final String SQL_CREATE_BASE =
                "CREATE VIRTUAL TABLE " + FundContract.BaseFund.TABLE_NAME + " USING FTS3" + " (" +
                        FundContract.BaseFund._ID + " INTEGER PRIMARY KEY," +
                        FundContract.BaseFund.COLUMN_NAME_ID + TYPE_TEXT + COMMA_SEP +
                        FundContract.BaseFund.COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP +
                        FundContract.BaseFund.COLUMN_NAME_PRICE + TYPE_TEXT + COMMA_SEP +
                        FundContract.BaseFund.COLUMN_NAME_COMPANY + TYPE_TEXT + COMMA_SEP +
                        FundContract.BaseFund.COLUMN_NAME_OVERFLOW + TYPE_REAL + COMMA_SEP +
                        FundContract.BaseFund.COLUMN_NAME_MARKET + TYPE_TEXT + COMMA_SEP +
                        FundContract.BaseFund.COLUMN_NAME_MANAGER + TYPE_TEXT + ")";

        /** SQL statement to create "a" table. */
        private static final String SQL_CREATE_A =
                "CREATE TABLE " + FundContract.AFund.TABLE_NAME + " (" +
                        FundContract.AFund._ID + " INTEGER PRIMARY KEY," +
                        FundContract.AFund.COLUMN_NAME_ID + TYPE_TEXT + COMMA_SEP +
                        FundContract.AFund.COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP +
                        FundContract.AFund.COLUMN_NAME_PRICE + TYPE_TEXT + COMMA_SEP +
                        FundContract.AFund.COLUMN_NAME_VALUE + TYPE_TEXT + COMMA_SEP +
                        FundContract.AFund.COLUMN_NAME_PROFIT_RATE + TYPE_REAL + COMMA_SEP +
                        FundContract.AFund.COLUMN_NAME_INCREASE_RATE + TYPE_TEXT + COMMA_SEP +
                        FundContract.AFund.COLUMN_NAME_BASE_ID + TYPE_TEXT + ")";

        /** SQL statement to create "b" table. */
        private static final String SQL_CREATE_B =
                "CREATE TABLE " + FundContract.BFund.TABLE_NAME + " (" +
                        FundContract.BFund._ID + " INTEGER PRIMARY KEY," +
                        FundContract.BFund.COLUMN_NAME_ID + TYPE_TEXT + COMMA_SEP +
                        FundContract.BFund.COLUMN_NAME_NAME + TYPE_TEXT + COMMA_SEP +
                        FundContract.BFund.COLUMN_NAME_PRICE + TYPE_TEXT + COMMA_SEP +
                        FundContract.BFund.COLUMN_NAME_INDEX_NAME + TYPE_TEXT + COMMA_SEP +
                        FundContract.BFund.COLUMN_NAME_INCREASE_RATE + TYPE_REAL + COMMA_SEP +
                        FundContract.BFund.COLUMN_NAME_BASE_ID + TYPE_TEXT + ")";

        /** SQL statement to drop "base" table. */
        private static final String SQL_DELETE_BASE =
                "DROP TABLE IF EXISTS " + FundContract.BaseFund.TABLE_NAME;

        /** SQL statement to drop "base" table. */
        private static final String SQL_DELETE_A =
                "DROP TABLE IF EXISTS " + FundContract.AFund.TABLE_NAME;

        /** SQL statement to drop "base" table. */
        private static final String SQL_DELETE_B =
                "DROP TABLE IF EXISTS " + FundContract.BFund.TABLE_NAME;

        public FundDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_BASE);
            db.execSQL(SQL_CREATE_A);
            db.execSQL(SQL_CREATE_B);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_BASE);
            db.execSQL(SQL_DELETE_A);
            db.execSQL(SQL_DELETE_B);
            onCreate(db);
        }
    }

}
