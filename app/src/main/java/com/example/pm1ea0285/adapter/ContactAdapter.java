package com.example.pm1ea0285.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm1ea0285.R;
import com.example.pm1ea0285.model.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnContactSelectedListener listener;

    public interface OnContactSelectedListener {
        void onContactSelected(Contact contact);
    }

    public ContactAdapter(List<Contact> contactList, OnContactSelectedListener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }

    public void updateList(List<Contact> newList) {
        this.contactList = newList;
        this.selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    public Contact getSelectedContact() {
        if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < contactList.size()) {
            return contactList.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getFullPhoneNumber());

        // Highlight selected item with high contrast
        if (selectedPosition == position) {
            holder.layoutCardContent.setBackgroundColor(Color.parseColor("#E0F7FA"));
        } else {
            holder.layoutCardContent.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onContactSelected(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;
        LinearLayout layoutCardContent;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            tvPhone = itemView.findViewById(R.id.tvContactPhone);
            layoutCardContent = itemView.findViewById(R.id.layoutCardContent);
        }
    }
}
