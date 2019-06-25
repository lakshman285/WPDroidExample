package com.ikvaesolutions.android.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.ikvaesolutions.android.constants.AppConstants;
import com.ikvaesolutions.android.models.sqlite.BookmarksModel;
import com.ikvaesolutions.android.models.sqlite.UserDetailsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amarilindra on 05/05/17.
 */

public class DatabaseHandlerUtils extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = AppConstants.DATABASE_VERSION;
    private static final String DATABASE_NAME = CommonUtils.getPackageName();

    private static final String TABLE_USERINFO = "user_info";
    private static final String TABLE_BOOKMARKS = "bookmarks";

    private static final String NAME = "id";
    private static final String EMAIL = "name";
    private static final String PHONE = "phone_number";
    private static final String PROFILEPICTURE = "profile_picture";

    private static final String POST_ID = "id";
    private static final String TITLE = "title";
    private static final String IMAGE = "image";
    private static final String TIME = "time";
    private static final String POST_TIME = "post_time";
    private static final String AUTHOR_NAME = "author_name";



    public DatabaseHandlerUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERINFO + "("
                + NAME + " TEXT,"
                + EMAIL + " TEXT PRIMARY KEY,"
                + PHONE + " TEXT,"
                + PROFILEPICTURE + " TEXT" + ")";

        String CREATE_BOOKMARKS_TABLE = "CREATE TABLE " + TABLE_BOOKMARKS + "("
                + POST_ID + " TEXT PRIMARY KEY,"
                + TITLE + " TEXT,"
                + IMAGE + " TEXT,"
                + TIME + " DATETIME,"
                + AUTHOR_NAME + " TEXT,"
                + POST_TIME + " TEXT"
                + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_BOOKMARKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERINFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
        onCreate(db);
    }

    public void closeDatabase(DatabaseHandlerUtils databaseHandlerUtils) {
        databaseHandlerUtils.close();
    }

    public void addUser(UserDetailsModel userDetailsModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, userDetailsModel.getName());
        values.put(EMAIL, userDetailsModel.getEmail());
        values.put(PHONE, userDetailsModel.getPhone());
        values.put(PROFILEPICTURE, userDetailsModel.getProfilePicture());

        db.replace(TABLE_USERINFO, null, values);
    }

    public List<UserDetailsModel> getUserInfo() {
        List<UserDetailsModel> userDetailsModelList = new ArrayList<UserDetailsModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_USERINFO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                UserDetailsModel userDetailsModel = new UserDetailsModel();
                userDetailsModel.setName(cursor.getString(0));
                userDetailsModel.setEmail(cursor.getString(1));
                userDetailsModel.setPhone(cursor.getString(2));
                userDetailsModel.setProfilePicture(cursor.getString(3));

                userDetailsModelList.add(userDetailsModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userDetailsModelList;
    }

    //BOOKMARKS TABLE

    public void addBookmark(BookmarksModel bookmarksModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(POST_ID, bookmarksModel.getId());
        values.put(TITLE, bookmarksModel.getTitle());
        values.put(IMAGE, bookmarksModel.getImage());
        values.put(TIME, bookmarksModel.getTime());
        values.put(AUTHOR_NAME, bookmarksModel.getAuthorName());
        values.put(POST_TIME, bookmarksModel.getPostTime());

        db.replace(TABLE_BOOKMARKS, null, values);
    }

    public List<BookmarksModel> getBookmarks() {
        List<BookmarksModel> bookmarksModelList = new ArrayList<BookmarksModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_BOOKMARKS + " ORDER BY " + TIME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BookmarksModel bookmarksModel = new BookmarksModel();
                bookmarksModel.setId(cursor.getString(0));
                bookmarksModel.setTitle(cursor.getString(1));
                bookmarksModel.setImage(cursor.getString(2));
                bookmarksModel.setTime(cursor.getString(3));
                bookmarksModel.setAuthorName(cursor.getString(4));
                bookmarksModel.setPostTime(cursor.getString(5));

                bookmarksModelList.add(bookmarksModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookmarksModelList;
    }

    public void removeBookmark(String postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKMARKS, POST_ID + " = ?",
                new String[] { postId});
        db.close();
    }

    public boolean isBookmarked(String postId) {

        String isAlreadySaved = "SELECT " + TIME + " FROM " + TABLE_BOOKMARKS + " WHERE " + POST_ID + "=\"" + postId + "\"";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(isAlreadySaved, null);

        cursor.moveToFirst();

        try {
            if (cursor.getCount() > 0) {
                return true;
            }
        }
        catch (SQLiteException e) {
            CommonUtils.writeLog(AppConstants.ERROR_LOG, "isAlreadySaved: ", " Error while Searching for Bookmarked TIME " + e.getMessage());
        }
        finally {
            cursor.close();
            db.close();
        }
        return false;
    }


}
