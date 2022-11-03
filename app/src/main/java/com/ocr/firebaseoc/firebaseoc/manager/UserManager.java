package com.ocr.firebaseoc.firebaseoc.manager;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.ocr.firebaseoc.firebaseoc.model.User;
import com.ocr.firebaseoc.firebaseoc.repository.UserRepository;

public class UserManager {

    private static volatile UserManager instance;
    private UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }

    public void createUser() {
        userRepository.createUser();
    }

    public Task<User> getUserData() {
        // Get the user from Firestore and cast it to a User model Object
        return userRepository.getUserData().continueWith(task -> task.getResult().toObject(User.class));
    }

    public Task<Void> updateUsername(String username) {
        return userRepository.updateUsername(username);
    }

    public void updateIsMentor(Boolean isMentor) {
        userRepository.updateIsMentor(isMentor);
    }

    public Task<Void> deleteUser(Context context) {
        userRepository.deleteUserFromFirestore();
        return userRepository.deleteUser(context);
    }

}