# CopenaghenBuzz
CopenhagenBuzz is an Android app developed as part of the Mobile App Development course at the IT University of Copenhagen. This app serves as a platform for sharing and exploring local events in the Copenhagen area, such as festivals and concerts. Users can view upcoming events, manage their favorite events, and interact with the app by adding, editing, or deleting events.

<br>

## Project Purpose 🎯
CopenhagenBuzz aims to connect locals and visitors with events around Copenhagen, enhancing community engagement and providing a platform for event discovery and management.

<br>

## Important Features ✨
- **Event Management**: Users can add, edit, and delete events. Each event includes details such as name, location, date, type, and description.
- **User Authentication**: Implemented using Firebase Authentication. Supports sign-in, sign-up, and sign-out functionalities.
- **Google Maps Integration**: Displays events on a map, allowing users to visually locate them.
- **Real-time Database**: Utilizes Firebase Realtime Database for storing and retrieving event data.
- **Material Design**: Incorporates Material Design components like Material Cards and Snackbar notifications for a cohesive user experience.
- **Photo Capture and Upload**: Integrates with the device’s camera and Google Photos gallery to capture and upload images to Firebase Storage.
- **Responsive Design**: Adapts to various phone and tablet sizes and resolutions.

<br>

## Technologies Used 📡
- Kotlin
- Firebase (Authentication, Realtime Database, Storage)
- Android Jetpack (Navigation, LiveData, ViewModel)
- Google Maps API
- Material Design

<br>

## Requirements 📋
- Android Studio
- Gradle

<br>

## Project Structure 🏗️
- **/app/src/main/java/** - Contains the Kotlin source files.
- **/app/src/main/res/** - Contains all resources like layouts, strings, and drawable assets.
- **/gradle/** - Gradle scripts for project automation.
- **build.gradle** - Gradle build files for app configuration.
- **AndroidManifest.xml** - Specifies the app's required permissions and its components.

<br>














To set up the project locally, follow these steps:
1. Ensure you have Android Studio and Git installed.
2. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_GITHUB/CopenhagenBuzz.git
   ```
3. Open the project in Android Studio.
4. Install all dependencies listed in the `build.gradle` files.
5. Sync the project with Gradle.
6. Run the app on an Android emulator or a physical device.



## Usage 🛠️
To use CopenhagenBuzz:
- **Login/Signup**: Access the app using your credentials or create a new account.
- **Viewing Events**: Navigate through the list or map view to find upcoming events.
- **Managing Events**: Add new events, or edit/delete existing ones from your listings.
- **Favorites**: Bookmark events you are interested in for future reference.








## Contributions 👥
This project was developed by students of the IT University of Copenhagen, under the guidance of the course instructors. For specific contributor details, refer to the commit history.

## License 📄
[MIT License](LICENSE)

## Credits 💳
Inspiration for some UI elements and functionalities was drawn from:
- [Material Design Guidelines](https://material.io/design)
- [Android Official Documentation](https://developer.android.com/docs)



