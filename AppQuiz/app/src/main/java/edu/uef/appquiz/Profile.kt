package edu.uef.appquiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set up the toolbar
      //  val toolbar: Toolbar = findViewById(R.id.toolbar)
       // setSupportActionBar(toolbar)

        // Enable the Up button in the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    // Handle the Up button click event
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
