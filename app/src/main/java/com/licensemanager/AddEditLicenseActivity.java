package com.licensemanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditLicenseActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private License currentLicense;
    private boolean isEditMode = false;
    
    private TextInputEditText nameEditText, expiryEditText, descriptionEditText, customTypeEditText;
    private AutoCompleteTextView typeDropdown;
    private TextInputLayout customTypeLayout;
    private MaterialButton saveButton, cancelButton, deleteButton;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_license);

        initializeViews();
        setupDatabase();
        setupToolbar();
        setupDropdown();
        setupDatePicker();
        setupButtons();
        loadLicenseData();
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        typeDropdown = findViewById(R.id.typeDropdown);
        customTypeEditText = findViewById(R.id.customTypeEditText);
        customTypeLayout = findViewById(R.id.customTypeLayout);
        expiryEditText = findViewById(R.id.expiryEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        deleteButton = findViewById(R.id.deleteButton);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupDropdown() {
        String[] licenseTypes = getResources().getStringArray(R.array.license_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, licenseTypes);
        typeDropdown.setAdapter(adapter);
        
        typeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = (String) parent.getItemAtPosition(position);
            if ("Other".equals(selectedType)) {
                customTypeLayout.setVisibility(View.VISIBLE);
            } else {
                customTypeLayout.setVisibility(View.GONE);
                customTypeEditText.setText("");
            }
        });
    }

    private void setupDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupDatePicker() {
        expiryEditText.setOnClickListener(v -> showDatePicker());
    }

    private void setupButtons() {
        saveButton.setOnClickListener(v -> saveLicense());
        cancelButton.setOnClickListener(v -> onBackPressed());
        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadLicenseData() {
        long licenseId = getIntent().getLongExtra("license_id", -1);
        
        if (licenseId != -1) {
            // Edit mode
            isEditMode = true;
            currentLicense = databaseHelper.getLicense(licenseId);
            
            if (currentLicense != null) {
                nameEditText.setText(currentLicense.getName());
                
                // Set the license type in dropdown
                String licenseType = currentLicense.getType();
                String[] licenseTypes = getResources().getStringArray(R.array.license_types);
                boolean isStandardType = false;
                
                for (String type : licenseTypes) {
                    if (type.equals(licenseType)) {
                        typeDropdown.setText(licenseType, false);
                        isStandardType = true;
                        break;
                    }
                }
                
                // If it's not a standard type, set it as "Other" and show custom field
                if (!isStandardType) {
                    typeDropdown.setText("Other", false);
                    customTypeLayout.setVisibility(View.VISIBLE);
                    customTypeEditText.setText(licenseType);
                } else {
                    customTypeLayout.setVisibility(View.GONE);
                }
                
                expiryEditText.setText(currentLicense.getExpiryDate());
                descriptionEditText.setText(currentLicense.getDescription());
                
                toolbar.setTitle(R.string.edit_license);
                deleteButton.setVisibility(View.VISIBLE);
            }
        } else {
            // Add mode
            isEditMode = false;
            toolbar.setTitle(R.string.add_license);
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        
        // If editing and date exists, pre-select it
        if (isEditMode && currentLicense != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar.setTime(sdf.parse(currentLicense.getExpiryDate()));
            } catch (Exception e) {
                // Use current date if parsing fails
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    expiryEditText.setText(sdf.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void saveLicense() {
        String name = nameEditText.getText().toString().trim();
        String selectedType = typeDropdown.getText().toString().trim();
        String type = selectedType;
        
        // If "Other" is selected, use the custom type instead
        if ("Other".equals(selectedType)) {
            String customType = customTypeEditText.getText().toString().trim();
            if (customType.isEmpty()) {
                customTypeEditText.setError("Custom license type is required");
                customTypeEditText.requestFocus();
                return;
            }
            type = customType;
        }
        
        String expiryDate = expiryEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            nameEditText.setError("Employee name is required");
            nameEditText.requestFocus();
            return;
        }

        if (selectedType.isEmpty()) {
            typeDropdown.setError("License type is required");
            typeDropdown.requestFocus();
            return;
        }

        if (expiryDate.isEmpty()) {
            expiryEditText.setError("Expiry date is required");
            expiryEditText.requestFocus();
            return;
        }

        try {
            if (isEditMode && currentLicense != null) {
                // Update existing license
                currentLicense.setName(name);
                currentLicense.setType(type);
                currentLicense.setExpiryDate(expiryDate);
                currentLicense.setDescription(description);
                
                int rowsUpdated = databaseHelper.updateLicense(currentLicense);
                if (rowsUpdated > 0) {
                    Toast.makeText(this, "Employee updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update employee", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Create new license
                License newLicense = new License(name, type, expiryDate, description);
                long id = databaseHelper.insertLicense(newLicense);
                
                if (id != -1) {
                    Toast.makeText(this, "Employee added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add employee", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error saving employee: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Employee")
                .setMessage("Are you sure you want to delete this employee record? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteLicense())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteLicense() {
        if (currentLicense != null) {
            try {
                databaseHelper.deleteLicense(currentLicense.getId());
                Toast.makeText(this, "Employee deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to delete employee", Toast.LENGTH_SHORT).show();
            }
        }
    }
}