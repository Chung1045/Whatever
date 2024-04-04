package com.example.whatever.game;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;

public class FirebaseHelper {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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

}
