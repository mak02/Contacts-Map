package com.mayank.contactsmap;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import database.DbConstants;

/**
 * Created by mayank on 6/4/16.
 */
public class AllContactsRecyclerAdapter extends RecyclerView.Adapter<AllContactsRecyclerAdapter.AllContactsViewHolder> implements DbConstants {

    Cursor cursor;
    Context context;

    public AllContactsRecyclerAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public static class AllContactsViewHolder extends RecyclerView.ViewHolder {

        TextView tvInitial, tvName, tvEmail, tvNumber, tvOfficeNumber;

        AllContactsViewHolder(View itemView) {
            super(itemView);
            tvInitial = (TextView)itemView.findViewById(R.id.tv_all_contacts_initial);
            tvName = (TextView)itemView.findViewById(R.id.tv_all_contacts_name);
            tvEmail = (TextView)itemView.findViewById(R.id.tv_all_contacts_email);
            tvNumber = (TextView)itemView.findViewById(R.id.tv_all_contacts_number);
            tvOfficeNumber = (TextView)itemView.findViewById(R.id.tv_all_contacts_office_number);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public AllContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_all_contacts, parent, false);
        return new AllContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllContactsViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.tvInitial.setText((cursor.getString(cursor.getColumnIndex(COL_CONTACTS_NAME))).substring(0, 1).toUpperCase());
        holder.tvName.setText(cursor.getString(cursor.getColumnIndex(COL_CONTACTS_NAME)));
        holder.tvEmail.setText(cursor.getString(cursor.getColumnIndex(COL_CONTACTS_EMAIL)));
        holder.tvNumber.setText(cursor.getString(cursor.getColumnIndex(COL_CONTACTS_PHONE)));
        holder.tvOfficeNumber.setText(cursor.getString(cursor.getColumnIndex(COL_CONTACTS_OFFICE_PHONE)));
        Log.d("ADAPTER", holder.tvName.getText().toString());
    }

}
