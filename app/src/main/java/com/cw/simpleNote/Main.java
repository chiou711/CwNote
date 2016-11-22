package com.cw.simpleNote;

import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Main extends ListActivity
{
    private DB mDbHelper;
    private Cursor mNotesCursor;
    private long rowId;
    private EditText editText1;
    private String editString1;
    private int mNoteNumber = 1;
    protected static final int MENU_INSERT = Menu.FIRST;
    protected static final int MENU_DELETE = Menu.FIRST + 1;
    protected static final int MENU_UPDATE = Menu.FIRST + 2;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);//CW
        setAdapter();
    }

    private void setAdapter()
    {
        mDbHelper = new DB(this);
        mDbHelper.open();
        fillData();
    }

    // file data
    private void fillData()
    {
        mNotesCursor = mDbHelper.getAll();
        mNotesCursor.moveToLast();
        startManagingCursor(mNotesCursor);
        String[] from = new String[] { DB.KEY_NOTE };

        int[] to = new int[] { android.R.id.text1 };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                mNotesCursor,
                from,
                to);

        SimpleCursorAdapter.ViewBinder binder = new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                int getIndex = cursor.getColumnIndex("note");
                String empname = cursor.getString(getIndex);
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE);
//                tv.setTextColor(Color.RED);
                tv.setText(empname);
                if(cursor.isLast())
                {
                    tv.setTextColor(Color.rgb(128, 225, 128));
                    return true;
                }
                return false;
            }
        };

        adapter.setViewBinder(binder);
        setListAdapter(adapter);
    }


    public void onListItemClick(ListView l, View v, int position, long id)
    {

        super.onListItemClick(l, v, position, id);

        rowId = l.getItemIdAtPosition(position);
        System.out.println("rowId = " + rowId);

        Cursor cursr= mDbHelper.get(rowId);
        String str = cursr.getString(1) ;
        System.out.println("str = " + str);

        editText1 = new EditText(this);
        editText1.setText(str);
        editText1.setSelection(str.length());
        Builder builder = new Builder(this);
        builder.setTitle("修改記事")
                .setMessage("輸入記事內容")
                .setView(editText1)
                .setPositiveButton("刪除", new OnClickListener()
                {   @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mDbHelper.delete(rowId);
                    fillData();
                }
                })
                .setNegativeButton("修改", new OnClickListener()
                {   @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    editString1 = editText1.getText().toString();
                    if(!editString1.equals("")){
                        mDbHelper.update(rowId, editText1.getText().toString());
                        fillData();
                    }
                }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // TODO Auto-generated method stub
        menu.add(0, MENU_INSERT, 0, "新增記事").setIcon(android.R.drawable.ic_menu_add);
//        menu.add(0, MENU_DELETE, 0, "刪除記事").setIcon(android.R.drawable.ic_menu_delete);
//        menu.add(0, MENU_UPDATE, 0, "修改記事").setIcon(android.R.drawable.ic_menu_edit);
        //

        //
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        //rowId = getListView().getSelectedItemId();
        switch (item.getItemId())
        {
            case MENU_INSERT:

                // 最後的已用Id
                boolean bMove = mNotesCursor.moveToLast();
                System.out.println("bMove = " + bMove);

                if(bMove)
                {
                    System.out.println("last id check:" + mNotesCursor.getString(0));

                    //mNoteNumber = getListView().getCount()+1;//注意: 當count 小於 row Id 會新資料蓋掉舊資料
                    mNoteNumber = Integer.parseInt( mNotesCursor.getString(0)) + 1; //新的Id
                }
                else
                {
                    mNoteNumber = getListView().getCount()+1;
                }

                String noteName = "("+ (mNoteNumber) + ")";
                mDbHelper.create(noteName);
                fillData();
                rowId = mNoteNumber;
//	            System.out.println("a) rowId = " + rowId);

                editText1 = new EditText(this);
                editText1.setText(noteName);
                editText1.setSelection(noteName.length()); // set cursor start
                Builder builder1 = new Builder(this);
                builder1.setTitle("新增記事")
                        .setMessage("輸入記事內容")
                        .setView(editText1)
                        .setPositiveButton("新增", new OnClickListener()
                        {   @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            editString1 = editText1.getText().toString();
                            System.out.println("a) editString1 = " + editString1);
                            if(!editString1.equals("")){
                                System.out.println("b) editString1 = " + editString1);
//		                            mDbHelper.update(rowId, editText1.getText().toString());
                                mDbHelper.update(rowId, editString1 );
                                fillData();
                            }
                        }
                        })
                        .show();
//
                break;
//	        case MENU_DELETE:
//	            mDbHelper.delete(rowId);
//	            fillData();
//	            break;
//	        case MENU_UPDATE:
//	            editText1 = new EditText(this);
//	            Builder builder = new Builder(this);
//	            builder.setTitle("修改項目名稱")
//	                    .setMessage("請輸入您想修改的項目名稱")
//	                    .setView(editText1)
//	                    .setPositiveButton("確認", new OnClickListener()
//	                    {   
//	                    	@Override
//	                        public void onClick(DialogInterface dialog, int which)
//	                        {
//	                        	editString1 = editText1.getText().toString();
//	                            if(!editString1.equals("")){ 
//	                            mDbHelper.update(rowId, editText1.getText().toString());
//	                            fillData();
//	                            }
//	                        }
//	                    })
//	                    .show();   
//	            break;
        }
        return super.onOptionsItemSelected(item);
    }

}