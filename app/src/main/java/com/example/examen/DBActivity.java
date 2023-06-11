package com.example.examen;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
public class DBActivity extends AppCompatActivity {
    private ArrayList<String> lst;
    private ListView lv;
    private DatabaseConnector databaseConnector;
    private int currentTable; // 1: Table_Book, 2: Table_Reader, 3: Table_Card
    private Button bookButton;
    private Button readerButton;
    private Button cardButton;
    private boolean flag=true; //True - for adding, False - for editing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_xml);

        lv = findViewById(R.id.lv);
        registerForContextMenu(lv);

        databaseConnector = new DatabaseConnector(DBActivity.this);
        currentTable = 1; // Set the initial table to Books

        bookButton=findViewById(R.id.books_btn);
        readerButton=findViewById(R.id.readers_btn);
        cardButton=findViewById(R.id.card_btn);

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTable = 1;
                refresh_screen();
            }
        });
        readerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTable = 2;
                refresh_screen();
            }
        });
        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTable = 3;
                refresh_screen();
            }
        });

        Button returnButton=findViewById(R.id.button3);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DBActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        refresh_screen();
    }

    void refresh_screen() {
        switch (currentTable) {
            case 1:
                new GetTableBookRowsTask().execute();
                break;
            case 2:
                new GetTableReaderRowsTask().execute();
                break;
            case 3:
                new GetTableCardRowsTask().execute();
                break;
        }
    }

    public void add_btn_clicked(View view) {
        flag=true;
        switch (currentTable) {
            case 1:
                // Table_Book is selected
                showAddBookDialog(flag, 0);
                break;
            case 2:
                // Table_Reader is selected
                showAddReaderDialog(flag, 0);
                break;
            case 3:
                // Table_Card is selected
                showAddCardDialog(flag, 0);
                break;
        }
        refresh_screen();
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
    }

    final int MENU_CONTEXT_DELETE_ID = 123;
    final int MENU_CONTEXT_EDIT_ID = 124;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lv) {
            ListView lv = (ListView) v;
            menu.add(Menu.NONE, MENU_CONTEXT_DELETE_ID, Menu.NONE, "Delete");
            menu.add(Menu.NONE, MENU_CONTEXT_EDIT_ID, Menu.NONE, "Edit");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String str = lst.get(info.position);

        switch (item.getItemId()) {
            case MENU_CONTEXT_DELETE_ID: {
                Log.d(TAG, "removing item pos=" + info.position);

                long rid = Long.parseLong(str.split(":")[1].trim().split(",")[0]);
                switch (currentTable) {
                    case 1:
                        databaseConnector.deleteBook(rid);
                        break;
                    case 2:
                        databaseConnector.deleteReader(rid);
                        break;
                    case 3:
                        databaseConnector.deleteCard(rid);
                        break;
                }

                refresh_screen();
                return true;
            }
            case MENU_CONTEXT_EDIT_ID: {
                flag=false;

                Log.d(TAG, "edit item pos=" + info.position);

                String[] parts = str.split(", ");
                int id=0;
                for (String part : parts) {
                    if (part.startsWith("ID: ")) {
                        String idValue = part.substring(4);
                        id = Integer.parseInt(idValue);
                    }
                }
                switch (currentTable){
                    case 1: showAddBookDialog(flag, id); break;
                    case 2: showAddReaderDialog(flag, id); break;
                    case 3: showAddCardDialog(flag, id); break;
                }
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    private class GetTableBookRowsTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {
            databaseConnector.open();
            return databaseConnector.getAllBooks();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            lst = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                lst.add("ID: " + cursor.getString(0) + ", Name: " + cursor.getString(1)
                        + ", Genre: " + cursor.getString(2) + ", Author: " + cursor.getString(3));
            }
            databaseConnector.close();
            ListAdapter listAdapter = new ArrayAdapter<>(DBActivity.this,
                    android.R.layout.simple_list_item_1, lst);

            lv.setAdapter(listAdapter);
        }
    }

    private class GetTableReaderRowsTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {
            databaseConnector.open();
            return databaseConnector.getAllReaders();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            lst = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                lst.add("ID: " + cursor.getString(0) + ", Name: " + cursor.getString(1));
            }
            databaseConnector.close();
            ListAdapter listAdapter = new ArrayAdapter<>(DBActivity.this,
                    android.R.layout.simple_list_item_1, lst);

            lv.setAdapter(listAdapter);
        }
    }

    private class GetTableCardRowsTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {
            databaseConnector.open();
            return databaseConnector.getAllCards();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            lst = new ArrayList<>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                lst.add("ID: " + cursor.getString(0) + ", Book ID: " + cursor.getString(1)
                        + ", Reader ID: " + cursor.getString(2) + ", Date: " + cursor.getString(3));
            }
            databaseConnector.close();
            ListAdapter listAdapter = new ArrayAdapter<>(DBActivity.this,
                    android.R.layout.simple_list_item_1, lst);

            lv.setAdapter(listAdapter);
        }
    }
    private void showAddBookDialog(boolean flag, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_book, null);
        builder.setView(dialogView);

        EditText genreEditText = dialogView.findViewById(R.id.editText_genre);
        EditText authorEditText = dialogView.findViewById(R.id.editText_author);
        EditText nameEditText = dialogView.findViewById(R.id.editText_title);

        builder.setPositiveButton("Додати", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String genre = genreEditText.getText().toString();
                String author = authorEditText.getText().toString();
                String name = nameEditText.getText().toString();

                if (genre.isEmpty() || author.isEmpty() || name.isEmpty()) {
                    Toast.makeText(DBActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseConnector databaseConnector = new DatabaseConnector(DBActivity.this);
                    databaseConnector.open();
                    if (flag==true)
                    databaseConnector.insertBook(name, genre, author);
                    else { databaseConnector.updateBook(id, name, genre, author);}

                    databaseConnector.close();

                    refresh_screen();

                    Toast.makeText(DBActivity.this, "Book added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Відмінити", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showAddReaderDialog(boolean flag, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reader, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.editText_name);

        builder.setPositiveButton("Додати", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(DBActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseConnector databaseConnector = new DatabaseConnector(DBActivity.this);
                    databaseConnector.open();
                    if (flag==true)
                    databaseConnector.insertReader(name);
                    else { databaseConnector.updateReader(id, name);}

                    databaseConnector.close();

                    refresh_screen();

                    Toast.makeText(DBActivity.this, "Reader added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Відмінити", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showAddCardDialog(boolean flag, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_card, null);
        builder.setView(dialogView);

        EditText id_bookEditText = dialogView.findViewById(R.id.editText_idbook);
        EditText id_readerEditText = dialogView.findViewById(R.id.editText_idreader);
        EditText dateEditText = dialogView.findViewById(R.id.editText_date);

        builder.setPositiveButton("Додати", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int book_id = Integer.parseInt(id_bookEditText.getText().toString());
                int reader_id = Integer.parseInt(id_readerEditText.getText().toString());
                String date = dateEditText.getText().toString();

                if (book_id==0 || reader_id==0 || date.isEmpty()) {
                    Toast.makeText(DBActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseConnector databaseConnector = new DatabaseConnector(DBActivity.this);
                    databaseConnector.open();
                    if (flag==true)
                    databaseConnector.insertCard(book_id, reader_id, date);
                    else { databaseConnector.updateCard(id, book_id, reader_id, date);}
                    databaseConnector.close();

                    refresh_screen();

                    Toast.makeText(DBActivity.this, "Card added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Відмінити", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

