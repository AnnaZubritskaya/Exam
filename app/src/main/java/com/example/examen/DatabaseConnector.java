package com.example.examen;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Button;

import java.util.Date;

public class DatabaseConnector {
    private static final String DATABASE_NAME="UserRows";
    private SQLiteDatabase database;
    private DBHelper databaseOpenHelper;
    public DatabaseConnector(Context context) {
        databaseOpenHelper=new DBHelper(context, DATABASE_NAME, null, 1);
    }
    public void open() throws SQLException {
        database=databaseOpenHelper.getWritableDatabase();
    }
    public void close() {
        if (database!=null) database.close();
    }
    public void deleteBook(long id) {
        open(); database.delete(DBHelper.TABLE_BOOK, "_id=" + id, null); close();
    }
    public void deleteReader(long id) {
        open(); database.delete(DBHelper.TABLE_READER, "_id=" + id, null); close();
    }
    public void deleteCard(long id) {
        open(); database.delete(DBHelper.TABLE_CARD, "_id=" + id, null); close();
    }
    public void insertBook(String name, String genre, String author) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.TABLE_BOOK_COLUMN_name, name);
        row.put(DBHelper.TABLE_BOOK_COLUMN_genre, genre);
        row.put(DBHelper.TABLE_BOOK_COLUMN_author, author);
        open();
        database.insert(DBHelper.TABLE_BOOK, null, row);
        close();
    }

    public void insertReader(String name) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.TABLE_READER_COLUMN_name, name);
        open();
        database.insert(DBHelper.TABLE_READER, null, row);
        close();
    }

    public void insertCard(long bookId, long readerId, String date) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.TABLE_CARD_COLUMN_book_id, bookId);
        row.put(DBHelper.TABLE_CARD_COLUMN_reader_id, readerId);
        row.put(DBHelper.TABLE_CARD_COLUMN_date, date);
        open();
        database.insert(DBHelper.TABLE_CARD, null, row);
        close();
    }

    public Cursor getAllBooks() {
        return database.query(DBHelper.TABLE_BOOK, new String[]{"_id", DBHelper.TABLE_BOOK_COLUMN_name,
                        DBHelper.TABLE_BOOK_COLUMN_genre, DBHelper.TABLE_BOOK_COLUMN_author},
                null, null, null, null, null);
    }

    public Cursor getAllReaders() {
        return database.query(DBHelper.TABLE_READER, new String[]{"_id", DBHelper.TABLE_READER_COLUMN_name},
                null, null, null, null, null);
    }

    public Cursor getAllCards() {
        return database.query(DBHelper.TABLE_CARD, new String[]{"_id", DBHelper.TABLE_CARD_COLUMN_book_id,
                        DBHelper.TABLE_CARD_COLUMN_reader_id, DBHelper.TABLE_CARD_COLUMN_date},
                null, null, null, null, null);
    }


    public class DBHelper extends SQLiteOpenHelper {

        public static final String TABLE_BOOK = "Tabel_Book";
        public static final String TABLE_BOOK_COLUMN_id = "_id";
        public static final String TABLE_BOOK_COLUMN_name = "name";
        public static final String TABLE_BOOK_COLUMN_genre = "genre";
        public static final String TABLE_BOOK_COLUMN_author = "author";

        public static final String TABLE_READER = "Table_Reader";
        public static final String TABLE_READER_COLUMN_id = "_id";
        public static final String TABLE_READER_COLUMN_name = "name";

        public static final String TABLE_CARD = "Table_Card";
        public static final String TABLE_CARD_COLUMN_id = "_id";
        public static final String TABLE_CARD_COLUMN_book_id = "book_id";
        public static final String TABLE_CARD_COLUMN_reader_id = "reader_id";
        public static final String TABLE_CARD_COLUMN_date = "date";

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {

            // Create the new tables
            db.execSQL("CREATE TABLE " + TABLE_BOOK + " ( "
                    + TABLE_BOOK_COLUMN_id + " integer primary key autoincrement, "
                    + TABLE_BOOK_COLUMN_name + " TEXT, "
                    + TABLE_BOOK_COLUMN_genre + " TEXT, "
                    + TABLE_BOOK_COLUMN_author + " TEXT " + " );");

            db.execSQL("CREATE TABLE " + TABLE_READER + " ( "
                    + TABLE_READER_COLUMN_id + " integer primary key autoincrement, "
                    + TABLE_READER_COLUMN_name + " TEXT " + " );");

            db.execSQL("CREATE TABLE " + TABLE_CARD + " ( "
                    + TABLE_CARD_COLUMN_id + " integer primary key autoincrement, "
                    + TABLE_CARD_COLUMN_book_id + " INTEGER, "
                    + TABLE_CARD_COLUMN_reader_id + " INTEGER, "
                    + TABLE_CARD_COLUMN_date + " TEXT, "
                    + "FOREIGN KEY(" + TABLE_CARD_COLUMN_book_id + ") REFERENCES " + TABLE_BOOK + "(" + TABLE_BOOK_COLUMN_id + "), "
                    + "FOREIGN KEY(" + TABLE_CARD_COLUMN_reader_id + ") REFERENCES " + TABLE_READER + "(" + TABLE_READER_COLUMN_id + ") " + " );");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }
    public void updateBook(long bookId, String name, String genre, String author) {
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("genre", genre);
        contentValues.put("author", author);

        String whereClause = "_id = ?";
        String[] whereArgs = { String.valueOf(bookId) };

        int rowsAffected = database.update("Tabel_Book", contentValues, whereClause, whereArgs);

        if (rowsAffected > 0) {
            Log.d(TAG, "Book updated successfully");
        } else {
            Log.d(TAG, "Failed to update book");
        }
        close();
    }
    public void updateReader(long readerId, String name) {
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        String whereClause = "_id = ?";
        String[] whereArgs = { String.valueOf(readerId) };

        int rowsAffected = database.update("Table_Reader", contentValues, whereClause, whereArgs);

        if (rowsAffected > 0) {
            Log.d(TAG, "Reader updated successfully");
        } else {
            Log.d(TAG, "Failed to update reader");
        }
        close();
    }
    public void updateCard(long cardId, int bookId, int readerId, String date) {
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("book_id", bookId);
        contentValues.put("reader_id", readerId);
        contentValues.put("date", date);

        String whereClause = "_id = ?";
        String[] whereArgs = { String.valueOf(cardId) };

        int rowsAffected = database.update("Table_Card", contentValues, whereClause, whereArgs);

        if (rowsAffected > 0) {
            Log.d(TAG, "Card updated successfully");
        } else {
            Log.d(TAG, "Failed to update card");
        }
        close();
    }
    public int getRowCount(String table) {
       open();
        int rowCount = 0;
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " +table, null);
        if (cursor.moveToFirst()) {
            rowCount = cursor.getInt(0);
        }
        cursor.close();
        close();
        return rowCount;
    }

}
