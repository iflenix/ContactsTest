package com.storm.contactstest;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onSearchButtonClick(View v) {
        ContentResolver cr = getContentResolver();
        String searchName = "Asdf";
        Uri searchUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, searchName);
        String[] projection = new String[]{ContactsContract.Contacts._ID};

        Cursor cursor = cr.query(searchUri, projection, null, null, null);

        String id = null;

        if (cursor.moveToFirst()) {
            int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
            id = cursor.getString(idIdx);
        }
        cursor.close();

        String data = null;
        if (id!= null) {
            projection = new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
            String where = ContactsContract.Data.CONTACT_ID + " = " + id + "AND" + ContactsContract.Data.MIMETYPE
                    + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";

            Cursor datacursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, where, null, null);

            int idName, idNumber;
            data = null;
            if (cursor.moveToFirst()) {
                idName = datacursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME);
                idNumber = datacursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                data = datacursor.getString(idName) + " (" + cursor.getString(idNumber) + ")";
            }


            datacursor.close();
        }
        else
            Toast.makeText(this, "not found", Toast.LENGTH_SHORT).show();

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(data);




    }

    public void onGetContactsClick(View v) {

        String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        Cursor contacts = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);

        int nameIdx = contacts.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
        int idIdx = contacts.getColumnIndexOrThrow(ContactsContract.Contacts._ID);


        String[] result = new String[contacts.getCount()];
        /*while (contacts.moveToNext()) {
            result[contacts.getPosition()] = contacts.getString(nameIdx) + " (" + contacts.getString(idIdx) + ")";
        }*/

        ListView contactsView = (ListView) findViewById(R.id.contacts_view);


        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, contacts, new String[]{ContactsContract.Contacts.DISPLAY_NAME}, new int[]{R.id.contacts_view}, 0);

        contactsView.setAdapter(adapter);
        contactsView.invalidate();

        //contacts.close();
    }


}
