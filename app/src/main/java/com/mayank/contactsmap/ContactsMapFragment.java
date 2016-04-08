package com.mayank.contactsmap;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import database.DbConstants;
import database.DbMethods;

/**
 * Created by mayank on 6/4/16.
 */
public class ContactsMapFragment extends Fragment implements DbConstants {

    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.fragment_contacts_map, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        List<MarkerOptions> markersList = new ArrayList<MarkerOptions>();

        DbMethods dbMethods = new DbMethods(getActivity());
        Cursor cursor = dbMethods.queryContacts(null, null, null, null, null, null);
//        while (cursor.getCount() == 0) {cursor = dbMethods.queryContacts(null, null, null, null, null, null);}
        Log.d("MAP DB ACCESS", cursor.getCount()+"");
        while (cursor.moveToNext()) {
            LatLng latLng = new LatLng(cursor.getDouble(cursor.getColumnIndex(COL_CONTACTS_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(COL_CONTACTS_LONGITUDE)));
            String name = cursor.getString(cursor.getColumnIndex(COL_CONTACTS_NAME));
            String phone = "Phone: " + cursor.getString(cursor.getColumnIndex(COL_CONTACTS_PHONE));
            String officePhone = "Office Phone: " + cursor.getString(cursor.getColumnIndex(COL_CONTACTS_OFFICE_PHONE));
            String snippetText = phone + "  " + officePhone;
            MarkerOptions marker = new MarkerOptions().position(latLng).title(name).snippet(snippetText);
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            markersList.add(marker);
        }

        for (int i=0; i<markersList.size(); i++)
        {
            googleMap.addMarker(markersList.get(i));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}