package com.storm.contactstest;

import android.content.ContentResolver;
import android.content.Intent;
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
        String searchName = "Алексей";
        Uri searchUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(searchName));
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
            String where = ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Data.MIMETYPE
                    + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";

            //String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id;


            Cursor datacursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, where, null, null);

            int idName, idNumber;
            if (datacursor.moveToFirst()) {
                idName = datacursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME);
                idNumber = datacursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                data = datacursor.getString(idName) + " (" + datacursor.getString(idNumber) + ")";
            }


            datacursor.close();
        }
        else
            Toast.makeText(this, "not found", Toast.LENGTH_SHORT).show();

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(data);

    }


    public void onSearchByPhoneClick(View v) {
        String number = "+380505217457";

        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,number);
        Cursor phoneCursor = getContentResolver().query(lookupUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        String name = "not found";
        if(phoneCursor.moveToFirst()){
            int nameIdx = phoneCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME);
            name = phoneCursor.getString(nameIdx);
        }
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        phoneCursor.close();


    }
    private static int PICK_CONTACT = 0;

    public void onPickContactButtonClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);

        startActivityForResult(intent, PICK_CONTACT);



    }    public void onAddContactButtonClick(View v) {
        Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,ContactsContract.Contacts.CONTENT_URI);
        intent.setData(Uri.parse("tel: +380503014594"));
        intent.putExtra(ContactsContract.Intents.Insert.NAME,"Love");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri contactUri = null;

        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            contactUri = data.getData();
        }
        Toast.makeText(this, contactUri.getEncodedPath(), Toast.LENGTH_SHORT).show();

        Cursor cursor = getContentResolver().query(contactUri,null,null,null,null);
        String contName = null;
        if(cursor.moveToFirst()){
            int idxId = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
            contName = cursor.getString(idxId);
        }
        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(contName);
        cursor.close();


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

        contacts.close();
    }


}
