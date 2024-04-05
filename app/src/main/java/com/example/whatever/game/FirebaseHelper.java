package com.example.whatever.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FirebaseHelper {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FirebaseStorage mStorage;

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return mAuth.getCurrentUser()!= null;
    }

    public String getUserName(){
        if (isLoggedIn()) {
            user = mAuth.getCurrentUser();
            if (!(user.getDisplayName() == null)) {
                return getCurrentUser().getDisplayName();
            } else {
                return "Anonymous?";
            }
        } else {
            return "Guest";
        }
    }

    public void logout() {
        mAuth.signOut();
    }


    public void getEmailFromUsername(String input, Consumer<String> onSuccess) {

            DatabaseReference userRef = mDatabase.child("UserProfile").child(input);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String email = snapshot.child("email").getValue(String.class);
                        onSuccess.accept(email);
                    } else {
                        // in case if that the input is actually an email address
                        onSuccess.accept(input);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    onSuccess.accept(input);
                }
            });

    }

    public void isUserNameOccupied(String input, Consumer<Boolean> onSuccess) {
        mDatabase.child("UserProfile").child(input)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            onSuccess.accept(true);
                        } else {
                            onSuccess.accept(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void updateProfileImage(Bitmap input, Consumer<Boolean> onSuccess) {
        if (isLoggedIn()){
            StorageReference storageRef = mStorage.getReference()
                    .child("UserProfilePics").child(user.getUid() + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            input.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Map<String, Object> userProfilePic = new HashMap<>();
                    userProfilePic.put("profilePicURL", uri.toString());
                    // Now you can save this URL to Firebase Realtime Database or Firestore
                    mDatabase.child("UserProfile")
                            .child(getUserName()).updateChildren(userProfilePic).addOnSuccessListener(e -> {
                                // or use it wherever you need.
                                onSuccess.accept(true);
                            });
                });

            }).addOnFailureListener(e -> {
                onSuccess.accept(false);
            });
        }
    }

    public void downloadProfileImage(Consumer<Bitmap> onSuccess) {
        if (isLoggedIn()){
            user = mAuth.getCurrentUser();
            StorageReference storageRef = mStorage.getReference()
                    .child("UserProfilePics").child(user.getUid() + ".jpg");

            storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap!= null) {
                    onSuccess.accept(bitmap);
                } else {
                    onSuccess.accept(null);
                }

            }).addOnFailureListener(e -> {
                onSuccess.accept(null);
            });
        }
    }

}
