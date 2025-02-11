# Chat SDK and Example Application

## Overview
This repository contains a **Chat SDK** and an example **Android application** that demonstrates its functionality. The SDK provides an easy-to-use interface for integrating chat features into Android applications, while the example app showcases how to use it.

## Technologies Used
- **Android SDK** (API Level 24+)
- **Java & Kotlin**
- **Retrofit** (REST API calls)
- **WebSocket** (real-time messaging)
- **RecyclerView** (chat UI)
- **Material Design Components**
- **Gradle Build System**

## Project Structure
```
ChatSDK/
├── ChatLibrary/  # Chat SDK module
│   ├── src/main/java/com/example/chatlibrary/
│   │   ├── models/       # Data models (User, Chat, Message)
│   │   ├── network/      # API service using Retrofit
│   │   ├── ChatSdk.java  # SDK main class for chat operations
├── app/  # Example application using the SDK
│   ├── src/main/java/com/example/chatsdk/
│   │   ├── activities/  # UI components (Login, Chat, Register, Menu)
│   │   ├── adapters/    # RecyclerView adapters for messages & users
│   │   ├── MainActivity.java  # Entry point of the application
```

## Setup and Installation
### Prerequisites
Ensure you have the following installed:
- **Android Studio Arctic Fox (or later)**
- **Java 11+**
- **Internet access** (for API communication)

### Running the Example Application
1. **Clone the repository:**
   ```bash
   git clone https://github.com/NoaGilboa/ChatSdk
   cd ChatSDK
   ```

2. **Open in Android Studio** and let it sync dependencies.

3. **Run the backend server** (see [Chat Server Readme](../ChatServer/README.md))

4. **Run the application** on an emulator or real device.

## SDK Integration Guide
### Add Dependency
To integrate the **Chat SDK** in your project, include it as a module:
```gradle
implementation project(":ChatLibrary")
```

### Initialize SDK
```java
ChatSdk chatSdk = ChatSdk.getInstance();
```

### User Management
#### Register a User
```java
chatSdk.registerUser("username", "password", new Callback<User>() {
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if (response.isSuccessful()) {
            User user = response.body();
            Log.d("ChatSDK", "User registered: " + user.getUsername());
        }
    }
    @Override
    public void onFailure(Call<User> call, Throwable t) {
        Log.e("ChatSDK", "Error: " + t.getMessage());
    }
});
```

#### Login User
```java
chatSdk.loginUser("username", "password", callback);
```

### Chat Management
#### Create or Load a Chat
```java
chatSdk.createChat(user1Id, user2Id, callback);
```

#### Send a Message
```java
chatSdk.sendMessage(chatId, senderId, receiverId, "Hello!", callback);
```

#### Retrieve Messages
```java
chatSdk.getMessagesByChatId(chatId, callback);
```

## Example Application Features
- **User Authentication** (Register/Login/Logout)
- **Chat List** (Displays all active chats)
- **Real-time Messaging** (Send/receive messages instantly)
- **User List** (Start a chat with any user)

## Future Enhancements
- **Push Notifications** for new messages
- **Group Chat Support**
- **Message Read Receipts**

## License
Copyright (c) 2025 Noa Gilboa | Yarden Cherry

## Video demonstration


https://github.com/user-attachments/assets/257d2a3b-c261-4aae-a2ee-0da95e78043d
