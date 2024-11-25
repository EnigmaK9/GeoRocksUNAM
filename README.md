# GeoRocksUNAM

GeoRocksUNAM is an Android application designed to explore and learn about various types of rocks. The app provides specific details about each rock, including their physical characteristics, chemical properties, locations where they are found, and now includes video content and audio effects to enhance user experience.

## Features

- **Rock List**: Browse a comprehensive list of rocks fetched from a remote API service.
- **Rock Details**: Tap on a rock to access detailed information such as type, color, hardness, chemical formula, and more.
- **Video Playback**: Each rock detail screen includes a video providing additional information about the rock.
- **Audio Effects**: Enjoy audio effects, like sounds when performing certain actions within the app.
- **User-Friendly Interface**: Navigate easily through a thoughtfully designed and intuitive UI.

## Technologies Used

- **Kotlin**: Programming language used for app development.
- **Retrofit**: Library for consuming REST APIs.
- **Glide**: Used for efficient image loading and caching.
- **ConstraintLayout**: Primary layout for organizing UI components.
- **Apiary**: Mock backend service for testing API responses.
- **VideoView**: Android component for video playback.
- **MediaPlayer**: Used for playing audio effects.
- **AndroidX Libraries**: For enhanced app compatibility and features.

## Instructions for Use

1. **Launch the App**: Upon starting, a splash screen is displayed briefly.
2. **Explore Rock List**: The app automatically loads a list of rocks from the remote API.
3. **View Rock Details**: Tap on any rock in the list to view detailed information.
   - **Watch Video**: A video about the selected rock will play within the detail screen.
   - **Listen to Audio**: Experience audio effects when interacting with the app.
4. **Navigate Back**: Use the back button to return to the rock list and explore more rocks.

## Requirements

- **Android Studio**: For running and modifying the app code.
- **Internet Connection**: Required for fetching data and streaming videos.
- **Android Device or Emulator**: Running Android 5.0 (Lollipop) or higher.

## Example Usage

![GeoRocksUNAM Example](./georocks.gif)

*Note: The GIF demonstrates the new video and audio functionalities within the app.*

## Installation

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/EnigmaK9/GeoRocksUNAM.git
   ```

2. **Open in Android Studio**:

   - Launch Android Studio.
   - Click on **"Open an existing project"**.
   - Navigate to the cloned repository folder and select it.

3. **Build the Project**:

   - Allow Android Studio to download all necessary dependencies.
   - Ensure there are no build errors before proceeding.

4. **Run the App**:

   - Connect an Android device via USB with USB debugging enabled, or set up an Android emulator.
   - Click the **"Run"** button in Android Studio.
   - Select your device or emulator to install and launch the app.

## Additional Information

- **API Service**: The app uses a mock backend provided by Apiary to simulate API responses for testing purposes.
- **Video Content**: Videos are streamed from sample URLs and may require a stable internet connection for smooth playback.
- **Audio Effects**: Audio files are included in the app resources and play during user interactions to enhance engagement.

## Contributing

Contributions are welcome! If you'd like to improve this app, please fork the repository and create a pull request with your changes.

## License

This project is licensed under the MIT License.

---

Feel free to reach out! enigmak9@protonmail.com
