package com.github.szymonrudnicki.compassproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.szymonrudnicki.compassproject.ui.compass.CompassFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CompassFragment())
                    .commitNow()
        }
    }
    
}
