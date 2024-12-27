package com.example.seetoapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.seetoapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class IntroActivity : AppCompatActivity() {
    private val TAG = "IntroActivity"
    private val RC_SIGN_IN = 9001
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInButton: MaterialButton
    private lateinit var appleSignInButton : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        appleSignInButton = findViewById(R.id.apple_btn)
        appleSignInButton.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            Toast.makeText(this, "You don't have an apple account", Toast.LENGTH_LONG).show()
        }
        firebaseAuth = FirebaseAuth.getInstance()

        // Find Google Sign-In button
        googleSignInButton = findViewById(R.id.google_btn)

        // Set up click listener for the Google Sign-In button
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

    }
    private fun signInWithGoogle() {
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Your web client ID from Firebase console
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Start the sign-in process
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign-In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            // Sign in to Firebase with the Google credential
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        // Sign-in success, update UI with the signed-in user's information
                        val user = firebaseAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign-in fails, display a message to the user
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Proceed to your main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Optionally, finish the intro activity
        } else {
            // User is not signed in, show message or stay on sign-in screen
            Toast.makeText(this, "Please sign in to continue", Toast.LENGTH_SHORT).show()
        }
    }




}