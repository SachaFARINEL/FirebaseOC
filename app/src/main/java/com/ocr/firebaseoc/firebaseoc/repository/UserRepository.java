package com.ocr.firebaseoc.firebaseoc.repository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ocr.firebaseoc.firebaseoc.model.User;

import java.util.Objects;


public final class UserRepository {
    //Model
    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";
    private static final String IS_MENTOR_FIELD = "isMentor";

    private static volatile UserRepository instance;

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }


    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    // Get the Collection Reference
    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create User in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, urlPicture);

            Task<DocumentSnapshot> userData = getUserData();
            // If the user already exist in Firestore, we get his data (isMentor)
            userData.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains(IS_MENTOR_FIELD)) {
                    userToCreate.setIsMentor((Boolean) documentSnapshot.get(IS_MENTOR_FIELD));
                }
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        }
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        return this.getUsersCollection().document(Objects.requireNonNull(this.getCurrentUserUID())).get();
    }


    // Update User Username
    public Task<Void> updateUsername(String username) {
        return this.getUsersCollection().document(Objects.requireNonNull(this.getCurrentUserUID())).update(USERNAME_FIELD, username);
    }

    // Update User isMentor
    public void updateIsMentor(Boolean isMentor) {
        this.getUsersCollection().document(Objects.requireNonNull(this.getCurrentUserUID())).update(IS_MENTOR_FIELD, isMentor);
    }

    // Delete the User from Firestore
    public void deleteUserFromFirestore() {
        this.getUsersCollection().document(Objects.requireNonNull(this.getCurrentUserUID())).delete();
    }
}