package com.cw.simpleNote;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB   
{

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "notes";
    private static final String DATABASE_CREATE =
    "CREATE TABLE notes(_id INTEGER PRIMARY KEY,note TEXT,created INTEGER);";      
    private Context mCtx = null;
    private DatabaseHelper dbHelper ;
    private SQLiteDatabase db;

    /** Constructor */
    public DB(Context ctx) {
        this.mCtx = ctx;
    }

    public DB open () throws SQLException {
  
        dbHelper = new DatabaseHelper(mCtx);      
        db = dbHelper.getWritableDatabase();      
       
        return this;  
    }

    public void close() {
        dbHelper.close(); 
    }
    
    
    private static class DatabaseHelper extends SQLiteOpenHelper
    {  
        public DatabaseHelper(Context context) 
        {  
            super(context, DATABASE_NAME , null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {   
            db.execSQL(DATABASE_CREATE);         
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        { 
            db.execSQL("DROP TABLE IF EXISTS"+DATABASE_TABLE); 
     	    onCreate(db);
        }
    }
    
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NOTE = "note";
    public static final String KEY_CREATED = "created";
    
    String[] strCols = new String[] {
          KEY_ROWID,
          KEY_NOTE,
          KEY_CREATED
      };

    public Cursor getAll() {  
        return db.query(DATABASE_TABLE, 
             strCols,
             null, 
             null, 
             null, 
             null, 
             null  
             );    
    }      
    
    // add an entry
    public long create(String noteName) { 
        Date now = new Date();  
        ContentValues args = new ContentValues(); 
        args.put(KEY_NOTE, noteName);   
        args.put(KEY_CREATED, now.getTime());  

        return db.insert(DATABASE_TABLE, null, args);  
    }
    
    public boolean delete(long rowId) {  
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    //query single entry
    public Cursor get(long rowId) throws SQLException {  
        Cursor mCursor = db.query(true,
                DATABASE_TABLE,
                new String[] {KEY_ROWID, KEY_NOTE, KEY_CREATED},
                KEY_ROWID + "=" + rowId,
                null, null, null, null, null);

        if (mCursor != null) { 
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    //update
    public boolean update(long rowId, String note) { 
        ContentValues args = new ContentValues();
        args.put(KEY_NOTE, note);
        
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}