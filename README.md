# TeamProject

## 📱 Project Overview
This project is a comprehensive **Health & Social Android Application** built with **Kotlin** and **Jetpack Compose**. It leverages the **MVVM (Model-View-ViewModel)** architecture to provide a robust and scalable codebase. The app features secure authentication, water intake tracking, body information management, and social interactions.

## 🛠 Tech Stack

### Core
- **Language**: [Kotlin](https://kotlinlang.org/)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)

### UI & UX
- **Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material3)
- **Navigation**: [Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)
- **Icons**: Material Icons Extended
- **Theming**: Custom Material3 Theme (`Color.kt`, `Type.kt`)

### Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **State Management**: ViewModel, StateFlow
- **Dependency Injection**: Manual Injection (via `TeamProjectApplication`)

### Networking
- **Client**: [Retrofit2](https://square.github.io/retrofit/)
- **HTTP Client**: [OkHttp3](https://square.github.io/okhttp/)
- **Interceptors**: `AuthInterceptor` (Token injection), `HttpLoggingInterceptor`
- **Serialization**: Gson

### Local Storage
- **Preferences**: SharedPreferences (Encapsulated in `TokenManager`)

## 📂 Project Structure

```
com.example.teamproject
├── data
│   ├── api
│   │   ├── AuthApiService.kt    # Login, Signup endpoints
│   │   ├── DrinkApiService.kt   # Water tracking endpoints
│   │   ├── UserApiService.kt    # User profile & body info endpoints
│   │   └── RetrofitClient.kt    # Retrofit instance configuration
│   ├── repository               # Data abstraction layer
│   └── TokenManager.kt          # JWT Token management (SharedPreferences)
├── ui
│   ├── screens
│   │   ├── LoginScreen.kt       # User authentication
│   │   ├── SignupScreen.kt      # New user registration
│   │   ├── WaterTrackingScreen.kt # Daily water intake tracker
│   │   ├── BodyInfoScreen.kt    # Body metrics management
│   │   └── FriendsScreen.kt     # Social connections
│   └── theme                    # App styling (Color, Type, Theme)
├── viewmodel                    # UI logic and state holders
└── navigation                   # NavHost and route definitions
```

## � Detailed Features

### 🔐 Authentication
- **Login & Signup**: Dedicated screens (`LoginScreen`, `SignupScreen`) handling user credentials.
- **Token Management**: `TokenManager` securely stores JWT tokens using `SharedPreferences`.
- **Auto-Authentication**: `AuthInterceptor` automatically attaches the Access Token to every API request.

### 💧 Water Tracking
- **Module**: `WaterTrackingScreen`
- **Functionality**: Users can record their daily water intake.
- **Data Source**: `DrinkApiService` handles fetching and updating water records (`DrinkModels`).

### 🏃 Body Information
- **Module**: `BodyInfoScreen`
- **Functionality**: Manage personal health metrics (height, weight, etc.).
- **Data Source**: `UserApiService` syncs body information with the backend.

### 👥 Social
- **Module**: `FriendsScreen`
- **Functionality**: View and interact with a list of friends.

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   ```
2. **Open in Android Studio**
   - Ensure you have the latest version (Ladybug or newer recommended).
3. **Sync Gradle**
   - Wait for dependencies to download.
4. **Run the App**
   - Select an emulator or device with API Level 24+.
