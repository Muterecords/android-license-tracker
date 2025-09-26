package com.licensemanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationScheduler {
    private static final int[] NOTIFICATION_DAYS = {90, 60, 30, 14, 7, 1, 0}; // Days before expiry to notify

    public static void scheduleExpiryNotifications(Context context, List<License> licenses) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        for (License license : licenses) {
            if (!license.isExpired()) {
                scheduleNotificationsForLicense(context, alarmManager, license);
            }
        }
    }

    private static void scheduleNotificationsForLicense(Context context, AlarmManager alarmManager, License license) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expiryDate = sdf.parse(license.getExpiryDate());
            
            if (expiryDate == null) return;

            Calendar expiryCalendar = Calendar.getInstance();
            expiryCalendar.setTime(expiryDate);
            
            Calendar today = Calendar.getInstance();
            
            for (int days : NOTIFICATION_DAYS) {
                Calendar notificationDate = (Calendar) expiryCalendar.clone();
                notificationDate.add(Calendar.DAY_OF_YEAR, -days);
                
                // Only schedule if notification date is in the future
                if (notificationDate.after(today)) {
                    scheduleNotification(context, alarmManager, license, notificationDate, days);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void scheduleNotification(Context context, AlarmManager alarmManager, 
                                           License license, Calendar notificationDate, int daysRemaining) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("license_name", license.getName());
        intent.putExtra("days_remaining", daysRemaining);
        
        // Create unique request code for each notification
        int requestCode = (int) (license.getId() * 100 + daysRemaining);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set notification time to 9 AM
        notificationDate.set(Calendar.HOUR_OF_DAY, 9);
        notificationDate.set(Calendar.MINUTE, 0);
        notificationDate.set(Calendar.SECOND, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, 
                             notificationDate.getTimeInMillis(), 
                             pendingIntent);
    }

    public static void cancelNotificationsForLicense(Context context, long licenseId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        for (int days : NOTIFICATION_DAYS) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            int requestCode = (int) (licenseId * 100 + days);
            
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent, 
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            alarmManager.cancel(pendingIntent);
        }
    }
}