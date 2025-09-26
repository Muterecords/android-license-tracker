package com.licensemanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder> {
    private List<License> licenses;
    private Context context;

    public LicenseAdapter(Context context, List<License> licenses) {
        this.context = context;
        this.licenses = licenses;
    }

    @NonNull
    @Override
    public LicenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_license, parent, false);
        return new LicenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LicenseViewHolder holder, int position) {
        License license = licenses.get(position);
        
        holder.licenseName.setText(license.getName());
        holder.licenseType.setText(license.getType());
        holder.expiryDate.setText(license.getExpiryDate());
        
        // Set status badge
        holder.statusBadge.setText(license.getStatusText());
        holder.statusBadge.setBackgroundColor(Color.parseColor(license.getStatusColor()));
        
        // Set days remaining
        long days = license.getDaysUntilExpiry();
        if (license.isExpired()) {
            holder.daysRemaining.setText("Expired");
            holder.daysRemaining.setTextColor(Color.parseColor("#EF4444"));
        } else if (days == 0) {
            holder.daysRemaining.setText("Today");
            holder.daysRemaining.setTextColor(Color.parseColor("#F59E0B"));
        } else if (days == 1) {
            holder.daysRemaining.setText("1 day");
            holder.daysRemaining.setTextColor(Color.parseColor("#F59E0B"));
        } else if (days <= 30) {
            holder.daysRemaining.setText(days + " days");
            holder.daysRemaining.setTextColor(Color.parseColor("#F59E0B"));
        } else {
            holder.daysRemaining.setText(days + " days");
            holder.daysRemaining.setTextColor(Color.parseColor("#10B981"));
        }
        
        // Show description if available
        if (license.getDescription() != null && !license.getDescription().trim().isEmpty()) {
            holder.description.setText(license.getDescription());
            holder.description.setVisibility(View.VISIBLE);
        } else {
            holder.description.setVisibility(View.GONE);
        }
        
        // Set click listener to edit license
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditLicenseActivity.class);
            intent.putExtra("license_id", license.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return licenses.size();
    }

    public void updateLicenses(List<License> newLicenses) {
        this.licenses = newLicenses;
        notifyDataSetChanged();
    }

    public static class LicenseViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView licenseName, licenseType, expiryDate, daysRemaining, description, statusBadge;

        public LicenseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            licenseName = itemView.findViewById(R.id.licenseName);
            licenseType = itemView.findViewById(R.id.licenseType);
            expiryDate = itemView.findViewById(R.id.expiryDate);
            daysRemaining = itemView.findViewById(R.id.daysRemaining);
            description = itemView.findViewById(R.id.description);
            statusBadge = itemView.findViewById(R.id.statusBadge);
        }
    }
}