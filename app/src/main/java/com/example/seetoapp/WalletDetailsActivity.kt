package com.example.seetoapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seetoapp.Activity.IntroActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class WalletDetailsActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var signOutButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_details)
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Find the Sign Out button by ID
        signOutButton = findViewById(R.id.sign_out)

        // Set up the click listener for the Sign Out button
        signOutButton.setOnClickListener {
            signOut()
        }

    }

    // Method to sign out the user
    private fun signOut() {
        // Sign out from Firebase Authentication
        mAuth.signOut()

        // Show a Toast to confirm the sign-out
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()

        // Navigate to the Login Activity (or any other appropriate activity)
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
        finish() // Optional: Close the current activity so the user can't go back
    }

    // Check if the user is logged in, if not redirect to login screen
    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = mAuth.currentUser
        if (currentUser == null) {
            // User is not signed in, show the Login screen
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
    }
}