package com.lauraeyal.taskmanager.activities;

/**
 * Created by Eyal on 12/26/2015.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.bl.UsersController;
import com.lauraeyal.taskmanager.common.User;
import com.lauraeyal.taskmanager.contacts.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PhoneContactsActivity extends Activity {

    // ArrayList
    ArrayList<SelectContact> selectContacts;
    List<SelectContact> temp;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones, email;
    Button doneBtn;
    // Pop up
    ContentResolver resolver;
    SearchView search;
    SelectContactAdapter adapter;
    private UsersController controller;
    List<User> usersList;
    List<String> emailslist = new ArrayList<String>();
    private Spinner usersSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonecontacts);
        controller = new UsersController(this);
        selectContacts = new ArrayList<SelectContact>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contacts_list);

        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,null);

        LoadContact loadContact = new LoadContact();
        loadContact.execute();
        search = (SearchView) findViewById(R.id.searchView);
        doneBtn = (Button) findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                for (SelectContact usr:adapter._data) {
                    if(usr.getCheckedBox()==true) {
                        try {
                            User u = controller.AddUser(usr.getEmail(), usr.getPhone(), usr.getPhone(), 0, 0, controller.GetTeamName());
                            addItemsOnUsersSpinner(usr.getEmail());
                        }
                        catch (Exception e) {
                        }
                    }
                }
                //Intent f = new Intent(v.getContext(),UsersActivity.class);
                Intent resultIntent=new Intent();
                resultIntent.putExtra("usersList", (Serializable) usersList);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });




        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                adapter.filter(newText);
                return false;
            }
        });
    }

    // add items into spinner dynamically
    public void addItemsOnUsersSpinner(String email) {

        usersSpinner = (Spinner) findViewById(R.id.usersspinner);
        List<User> tempList = controller.GetUsersList();
        for(User b : tempList)
        {
            emailslist.add(b.getUserName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, emailslist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usersSpinner.setAdapter(dataAdapter);
    }
    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(PhoneContactsActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();

                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr=null;
                    Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCursor.moveToNext())
                    {
                        String phone = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        int type = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        String s = (String) ContactsContract.CommonDataKinds.Email.getTypeLabel(Resources.getSystem(), type, "");
                        EmailAddr = phone;
                    }

                    emailCursor.close();

                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "--------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SelectContact selectContact = new SelectContact();
                    selectContact.setThumb(bit_thumb);
                    selectContact.setName(name);
                    selectContact.setPhone(phoneNumber);
                    selectContact.setEmail(EmailAddr);
                    selectContact.setCheckedBox(false);
                    selectContacts.add(selectContact);
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SelectContactAdapter(selectContacts, PhoneContactsActivity.this);
            listView.setAdapter(adapter);

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Log.e("search", "here---------------- listener");
                    selectContacts.get(i).setCheckedBox(true);
                    SelectContact data = selectContacts.get(i);
                }
            });

            listView.setFastScrollEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }
}