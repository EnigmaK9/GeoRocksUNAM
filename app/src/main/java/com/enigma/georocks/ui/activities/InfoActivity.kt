// File path: app/src/main/java/com/enigma/georocks/ui/activities/InfoActivity.kt

package com.enigma.georocks.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.enigma.georocks.R

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        // Initialize Toolbar if needed
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbarInfo)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.mock_backend_info)

        // Additional setup to display backend information
    }


}
