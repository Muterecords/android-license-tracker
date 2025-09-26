package com.licensemanager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class License {
    private long id;
    private String name;
    private String type;
    private String expiryDate;
    private String description;

    public License() {}

    public License(String name, String type, String expiryDate, String description) {
        this.name = name;
        this.type = type;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    public License(long id, String name, String type, String expiryDate, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.expiryDate = expiryDate;
        this.description = description;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Utility methods
    public boolean isExpired() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expiry = sdf.parse(expiryDate);
            Date today = new Date();
            return expiry != null && expiry.before(today);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExpiringSoon() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expiry = sdf.parse(expiryDate);
            Date today = new Date();
            if (expiry != null && expiry.after(today)) {
                long diff = expiry.getTime() - today.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                return days <= 90; // 3 months
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public long getDaysUntilExpiry() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expiry = sdf.parse(expiryDate);
            Date today = new Date();
            if (expiry != null) {
                long diff = expiry.getTime() - today.getTime();
                return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public String getStatusColor() {
        if (isExpired()) {
            return "#EF4444"; // red
        } else if (isExpiringSoon()) {
            return "#F59E0B"; // yellow
        } else {
            return "#10B981"; // green
        }
    }

    public String getStatusText() {
        if (isExpired()) {
            return "Expired";
        } else if (isExpiringSoon()) {
            return "Expiring Soon";
        } else {
            return "Active";
        }
    }
}