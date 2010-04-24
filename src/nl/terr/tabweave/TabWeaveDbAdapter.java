/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package nl.terr.tabweave;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple tabs database access helper class. Defines the basic CRUD operations
 * for the tabpad example, and gives the ability to list all tabs as well as
 * retrieve or modify a specific tab.
 *
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class TabWeaveDbAdapter {

    public static final String KEY_TITLE    = "title";
    public static final String KEY_URL      = "url";
    public static final String KEY_ROWID    = "_id";

    private static final String TAG = "weaveTabsAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table tabs (_id integer primary key autoincrement, "
                    + "title text not null, body text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "tabs";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS tabs");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public TabWeaveDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the tabs database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TabWeaveDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new tab using the title and body provided. If the tab is
     * successfully created return the new rowId for that tab, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the tab
     * @param body the body of the tab
     * @return rowId or -1 if failed
     */
    public long createTab(String title, String url) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_URL, url);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the tab with the given rowId
     *
     * @param rowId id of tab to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTab(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all tabs in the database
     *
     * @return Cursor over all tabs
     */
    public Cursor fetchAllTabs() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_URL}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the tab that matches the given rowId
     *
     * @param rowId id of tab to retrieve
     * @return Cursor positioned to matching tab, if found
     * @throws SQLException if tab could not be found/retrieved
     */
    public Cursor fetchTab(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_URL}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the tab using the details provided. The tab to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of tab to update
     * @param title value to set tab title to
     * @param body value to set tab body to
     * @return true if the tab was successfully updated, false otherwise
     */
    public boolean updateTab(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_URL, body);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}