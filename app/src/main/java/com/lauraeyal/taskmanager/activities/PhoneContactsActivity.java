package com.lauraeyal.taskmanager.activities;

/**
 * Created by Eyal on 12/26/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
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
import com.parse.ParseException;
import com.parse.SignUpCallback;

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
    static int selectedUsersCounter = 0;
    ProgressDialog progressDialog;
    private String userMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonecontacts);
        selectContacts = new ArrayList<SelectContact>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contacts_list);
        progressDialog = new ProgressDialog(PhoneContactsActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding Team Member...");

        controller = new UsersController(this);
        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,null);

        LoadContact loadContact = new LoadContact();
        loadContact.execute();
        search = (SearchView) findViewById(R.id.searchView);

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

    void MoveToUsersActivity()
    {
        progressDialog.dismiss();
        Intent nextScreen = new Intent(getApplicationContext(), UsersActivity.class);
        startActivity(nextScreen);
        finish();
    }

    public void OnDoneBtnClick(final View v)
    {
        progressDialog.show();
        for (SelectContact usr : adapter._data) {
            if (usr.getCheckedBox() == true) {
                selectedUsersCounter++;
                final User newUser = new User();
                newUser.setUserName(usr.getEmail());
                newUser.setPassword(usr.getPhone());
                newUser.setPhoneNumber(usr.getPhone());
                newUser.setTeamName(controller.GetTeamName());
                newUser.setMailSent(0);
                newUser.setPermission(0);

                controller.AddUser(newUser, new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null) {
                            selectedUsersCounter--;
                            if (selectedUsersCounter == 0) {
                                MoveToUsersActivity();
                            }
                        }
                        else {
                            progressDialog.dismiss();
                            Snackbar.make(v, "Team member " + newUser.getUserName() + " Already exist", Snackbar.LENGTH_LONG).setAction("action", null);
                        }
                    }
                });
            }
        }
        if(selectedUsersCounter == 0)
            MoveToUsersActivity();
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
