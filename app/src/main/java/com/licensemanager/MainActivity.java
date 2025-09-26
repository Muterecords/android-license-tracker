package com.licensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private RecyclerView licenseRecyclerView;
    private LicenseAdapter licenseAdapter;
    private List<License> allLicenses;
    private List<License> filteredLicenses;
    
    private TextView totalCount, activeCount, expiringCount, expiredCount;
    private TextInputEditText searchEditText;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupDatabase();
        setupRecyclerView();
        setupSearchFunctionality();
        setupFAB();
        loadLicenses();
        scheduleNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLicenses(); // Refresh data when returning from add/edit screen
    }

    private void initializeViews() {
        totalCount = findViewById(R.id.totalCount);
        activeCount = findViewById(R.id.activeCount);
        expiringCount = findViewById(R.id.expiringCount);
        expiredCount = findViewById(R.id.expiredCount);
        searchEditText = findViewById(R.id.searchEditText);
        emptyState = findViewById(R.id.emptyState);
        licenseRecyclerView = findViewById(R.id.licenseRecyclerView);
    }

    private void setupDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        allLicenses = new ArrayList<>();
        filteredLicenses = new ArrayList<>();
        licenseAdapter = new LicenseAdapter(this, filteredLicenses);
        
        licenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        licenseRecyclerView.setAdapter(licenseAdapter);
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLicenses(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFAB() {
        FloatingActionButton fabAddLicense = findViewById(R.id.fabAddLicense);
        fabAddLicense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditLicenseActivity.class);
            startActivity(intent);
        });
    }

    private void loadLicenses() {
        allLicenses = databaseHelper.getAllLicenses();
        filterLicenses(searchEditText.getText().toString().trim());
        updateStatistics();
        updateEmptyState();
    }

    private void filterLicenses(String query) {
        filteredLicenses.clear();
        
        if (query.isEmpty()) {
            filteredLicenses.addAll(allLicenses);
        } else {
            for (License license : allLicenses) {
                if (license.getName().toLowerCase().contains(query.toLowerCase()) ||
                    license.getType().toLowerCase().contains(query.toLowerCase())) {
                    filteredLicenses.add(license);
                }
            }
        }
        
        licenseAdapter.updateLicenses(filteredLicenses);
        updateEmptyState();
    }

    private void updateStatistics() {
        int total = allLicenses.size();
        int active = 0;
        int expiring = 0;
        int expired = 0;

        for (License license : allLicenses) {
            if (license.isExpired()) {
                expired++;
            } else if (license.isExpiringSoon()) {
                expiring++;
            } else {
                active++;
            }
        }

        totalCount.setText(String.valueOf(total));
        activeCount.setText(String.valueOf(active));
        expiringCount.setText(String.valueOf(expiring));
        expiredCount.setText(String.valueOf(expired));
    }

    private void updateEmptyState() {
        if (filteredLicenses.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            licenseRecyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            licenseRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void scheduleNotifications() {
        // Schedule notifications for licenses expiring soon
        NotificationScheduler.scheduleExpiryNotifications(this, allLicenses);
    }
}