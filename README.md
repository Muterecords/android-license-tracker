# Native Android License Manager

A completely standalone native Android application for managing software licenses and tracking expiry dates.

## 📱 App Features

### ✅ Complete Offline Functionality
- Works without internet connection
- All data stored locally using SQLite database
- No server or web dependencies

### 🎯 Core Features
- **Dashboard**: View license statistics with visual cards
- **Add/Edit Licenses**: Create and modify license records
- **Search & Filter**: Find licenses by name or type
- **Expiry Notifications**: Automatic alerts 30, 14, 7, and 1 day before expiry
- **Status Tracking**: Visual indicators for active, expiring, and expired licenses

## 📁 Project Structure

```
native-android/
├── app/
│   ├── src/main/
│   │   ├── java/com/licensemanager/
│   │   │   ├── MainActivity.java           # Main dashboard screen
│   │   │   ├── AddEditLicenseActivity.java # Add/edit license form
│   │   │   ├── License.java                # License data model
│   │   │   ├── DatabaseHelper.java         # SQLite database operations
│   │   │   ├── LicenseAdapter.java         # RecyclerView adapter
│   │   │   ├── NotificationReceiver.java   # Handles notifications
│   │   │   └── NotificationScheduler.java  # Schedules expiry alerts
│   │   ├── res/
│   │   │   ├── layout/                     # XML layout files
│   │   │   ├── values/                     # Colors, strings, styles
│   │   │   ├── drawable/                   # Icons and graphics
│   │   │   └── mipmap-*/                   # App launcher icons
│   │   └── AndroidManifest.xml             # App configuration
│   └── build.gradle                        # App build configuration
├── build.gradle                            # Project build configuration
├── settings.gradle                         # Project settings
└── gradle/                                 # Gradle wrapper files
```

## 🚀 Building the APK

### Requirements
- Android Studio (latest version)
- Android SDK 24+ (Android 7.0)
- Java Development Kit (JDK) 8+

### Build Steps

1. **Download the project** files to your computer

2. **Open Android Studio** and select "Open an existing project"

3. **Navigate to** the `native-android` folder and open it

4. **Wait for Gradle sync** to complete (first time may take several minutes)

5. **Build the APK**:
   - Go to `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - Or run in terminal: `./gradlew assembleDebug`

6. **Find your APK** in `app/build/outputs/apk/debug/`

### Alternative Build Methods

**Command Line (with Android SDK installed):**
```bash
cd native-android
./gradlew assembleDebug
```

**GitHub Actions (Automated Build):**
- Push the `native-android` folder to a GitHub repository
- Set up GitHub Actions workflow to build APK automatically
- Download the built APK from the Actions artifacts

## 📲 App Usage

### Dashboard Screen
- View total, active, expiring, and expired license counts
- Search licenses using the search bar
- Tap any license card to edit it
- Use the + button to add new licenses

### Add/Edit License
- **License Name**: Software or service name
- **License Type**: Category (e.g., "Software License", "Subscription")
- **Expiry Date**: When the license expires (date picker)
- **Description**: Optional notes about the license

### Notifications
- Automatic alerts sent at 30, 14, 7, and 1 day before expiry
- Notifications appear at 9:00 AM on notification days
- Tap notification to open the app

## 🛠 Technical Details

### Database Schema
```sql
CREATE TABLE licenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    type TEXT NOT NULL,
    expiry_date TEXT NOT NULL,
    description TEXT
);
```

### Key Classes
- **License**: Data model with utility methods for status checking
- **DatabaseHelper**: SQLite operations (CRUD, search, statistics)
- **MainActivity**: Dashboard with RecyclerView and statistics
- **AddEditLicenseActivity**: Form handling with date picker
- **NotificationScheduler**: Alarm management for expiry alerts

### Permissions
- `POST_NOTIFICATIONS`: For expiry alerts
- `SCHEDULE_EXACT_ALARM`: For precise notification timing

## 🎨 Design System

### Colors
- **Primary**: Blue (#3B82F6)
- **Success**: Green (#10B981) - Active licenses
- **Warning**: Yellow (#F59E0B) - Expiring licenses  
- **Error**: Red (#EF4444) - Expired licenses

### Material Design
- Uses Material Design 3 components
- Adaptive icons for modern Android versions
- Card-based layout with proper elevation
- Responsive design for different screen sizes

## 📋 License Status Logic

- **Active**: More than 30 days until expiry
- **Expiring Soon**: 1-30 days until expiry
- **Expired**: Past the expiry date

## 🔧 Customization

### Notification Schedule
Edit `NotificationScheduler.java` and modify the `NOTIFICATION_DAYS` array to change when alerts are sent.

### Color Scheme
Modify `res/values/colors.xml` to change the app's color palette.

### App Icon
Replace the icon files in `res/drawable/` and `res/mipmap-*/` directories.

## 🐛 Troubleshooting

### Build Issues
- Ensure Android SDK is properly installed
- Check that ANDROID_HOME environment variable is set
- Try cleaning the project: `./gradlew clean`

### Runtime Issues
- Grant notification permissions in device settings
- Check that the device allows scheduling exact alarms
- Verify date formats are correct (YYYY-MM-DD)

---

This is a complete, production-ready native Android application that works entirely offline without any web or server dependencies.