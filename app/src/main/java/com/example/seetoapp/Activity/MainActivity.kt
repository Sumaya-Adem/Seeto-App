package com.example.seetoapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.seetoapp.ApiResponse
import com.example.seetoapp.ApiService
import com.example.seetoapp.R
import com.example.seetoapp.RetrofitClient
import com.example.seetoapp.WalletDetailsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var symbolTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var symbolTextView2: TextView
    private lateinit var priceTextView2: TextView

    private lateinit var nameTextView: TextView
    private lateinit var welcomeTextView: TextView
    private lateinit var firstImageView: ImageView
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val btnNavigation : FloatingActionButton = findViewById(R.id.floating_btn)

        btnNavigation.setOnClickListener {
            val intent = Intent(this , WalletDetailsActivity::class.java)
            startActivity(intent)
        }

        // Initialize the views
        symbolTextView = findViewById(R.id.symbolTextView)
        priceTextView = findViewById(R.id.priceTextView)
        symbolTextView2 = findViewById(R.id.symbolTextView2)
        priceTextView2 = findViewById(R.id.priceTextView2)

        // Fetch data from the API
        fetchCryptoData()


        nameTextView = findViewById(R.id.name)
        welcomeTextView = findViewById(R.id.welcome_text)
        firstImageView = findViewById(R.id.first_image)

        user = FirebaseAuth.getInstance().currentUser!!

        if (user != null) {
            // User is signed in, update UI with their info
            updateUI(user)
        } else {
            // If no user is signed in, redirect to IntroActivity (login screen)
            startActivity(Intent(this, IntroActivity::class.java))
            finish()  // Finish the MainActivity so it doesn't stay in the stack
        }
    }

    private fun updateUI(user: FirebaseUser) {
        // Update the TextViews with the user's information
        nameTextView.text = "Hi ${user.displayName}"
        welcomeTextView.text = "Welcome back"

        // Optionally, you can load the user's profile picture into the ImageView
        val profilePictureUrl = user.photoUrl
        if (profilePictureUrl != null) {
            Picasso.get().load(profilePictureUrl).into(firstImageView)  // Use Picasso for image loading
        }


    }


    private fun fetchCryptoData() {
        // Initialize Retrofit
        val retrofit = RetrofitClient.retrofitInstance
        val apiService = retrofit.create(ApiService::class.java)

        // Make the network request
        val call = apiService.getCryptoData()
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    // Get the first and second cryptocurrency data
                    val cryptoDataList = response.body()?.data

                    // Ensure we have at least two cryptocurrencies in the list
                    if (cryptoDataList != null && cryptoDataList.size >= 2) {
                        val crypto1 = cryptoDataList[0]
                        val crypto2 = cryptoDataList[1]

                        // Update the UI with data for both cryptocurrencies
                        runOnUiThread {
                            symbolTextView.text = crypto1.symbol
                            priceTextView.text = "$${crypto1.price_usd}"

                            symbolTextView2.text = crypto2.symbol
                            priceTextView2.text = "$${crypto2.price_usd}"
                        }
                    }
                }  else {
                    // Handle API error
                    Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Handle network failure
                Toast.makeText(this@MainActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




}