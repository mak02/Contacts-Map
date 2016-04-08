package com.mayank.contactsmap;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DbConstants;
import database.DbMethods;

/**
 * Created by mayank on 6/4/16.
 */
public class AllContactsFragment extends Fragment implements DbConstants {

    private RecyclerView recyclerView;
    private AllContactsRecyclerAdapter allContactsRecyclerAdapter;
    private RecyclerView.Adapter mAdapter;
    RequestQueue queue;
    DbMethods dbMethods;

    String name, email, phone, officePhone;
    double latitude, longitude;

    public AllContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        queue = Volley.newRequestQueue(getActivity());
        dbMethods = new DbMethods(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String TAG = "CONTACTS MAP NETWORK";

        String url = "http://private-b08d8d-nikitest.apiary-mock.com/contacts";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray res = new JSONArray(response);
                    dbMethods.deleteAllContacts();
                    for (int i=0; i<res.length(); i++) {
                        JSONObject obj = res.getJSONObject(i);
                        for (int j=0; j<obj.length(); j++) {
                            JSONArray contacts = obj.getJSONArray("contacts");
                            for (int k=0; k<contacts.length(); k++) {
                                JSONObject finalObj = contacts.getJSONObject(k);
                                try { name = finalObj.getString("name");} catch (JSONException e) {name = null;}
                                try { email = finalObj.getString("email"); } catch (JSONException e) {email = null;}
                                try { phone = finalObj.getString("phone"); } catch (JSONException e) {phone = null;}
                                try { officePhone = finalObj.getString("officePhone"); } catch (JSONException e) {officePhone = null;}
                                try { latitude = Double.parseDouble(finalObj.getString("latitude")); } catch (JSONException e) {latitude = 0.0;}
                                try { longitude = Double.parseDouble(finalObj.getString("longitude")); } catch (JSONException e) {longitude = 0.0;}
                                dbMethods.insertContacts(name, email, phone, officePhone, latitude, longitude);
                            }
                            AsyncClass asyncClass = new AsyncClass();
                            asyncClass.execute("");
                        }
                    }

                    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_all_contacts);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);

                    allContactsRecyclerAdapter = new AllContactsRecyclerAdapter(getActivity(), dbMethods.queryContacts(null, null, null, null, null, null));
                    mAdapter = new RecyclerViewMaterialAdapter(allContactsRecyclerAdapter);
                    recyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                pDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        queue.add(strReq);



    }

    class AsyncClass extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String[] params) {

            DbMethods dbMethods = new DbMethods(getActivity());
            Cursor dbCursor = dbMethods.queryContacts(null, null, null, null, null, null);

            while (dbCursor.moveToNext()) {
                boolean insertResp1 = insertContact(getActivity().getContentResolver(),
                        dbCursor.getString(dbCursor.getColumnIndex(COL_CONTACTS_NAME)),
                        dbCursor.getString(dbCursor.getColumnIndex(COL_CONTACTS_PHONE)));
                boolean insertResp2 = insertContact(getActivity().getContentResolver(),
                        dbCursor.getString(dbCursor.getColumnIndex(COL_CONTACTS_NAME)) + " Office",
                        dbCursor.getString(dbCursor.getColumnIndex(COL_CONTACTS_OFFICE_PHONE)));
            }

            return null;
        }
    }

    public static Cursor getContactCursor(ContentResolver contactHelper,
                                          String startsWith) {

        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER };
        Cursor cur = null;

        try {
            if (startsWith != null && !startsWith.equals("")) {
                cur = contactHelper.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        projection,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                + " like \"" + startsWith + "%\"", null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                + " ASC");
            } else {
                cur = contactHelper.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        projection, null, null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                + " ASC");
            }
            cur.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cur;
    }

    public static boolean insertContact(ContentResolver contactAdder,
                                        String firstName, String mobileNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        firstName).build());
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                        mobileNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());

        try {
            contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static void deleteContact(ContentResolver contactHelper,
                                     String number) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String[] args = new String[] { String.valueOf(getContactID(
                contactHelper, number)) };

        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private static long getContactID(ContentResolver contactHelper,
                                     String number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));

        String[] projection = { ContactsContract.PhoneLookup._ID };
        Cursor cursor = null;

        try {
            cursor = contactHelper.query(contactUri, projection, null, null,
                    null);

            if (cursor.moveToFirst()) {
                int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                return cursor.getLong(personID);
            }

            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return -1;
    }


}