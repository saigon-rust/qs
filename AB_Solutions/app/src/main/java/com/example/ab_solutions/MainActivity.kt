package com.example.ab_solutions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TabHost

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabHost = findViewById<TabHost>(android.R.id.tabhost)
        tabHost.setup()

        tabHost.addTab(
            tabHost.newTabSpec("tab1")
                .setIndicator("Tổng quan")
                .setContent(R.id.tab1)
        )

        tabHost.addTab(
            tabHost.newTabSpec("tab2")
                .setIndicator("Thiết bị - Vật tư")
                .setContent(R.id.tab2)
        )

        tabHost.addTab(
            tabHost.newTabSpec("tab3")
                .setIndicator("Chấm công")
                .setContent(R.id.tab3)
        )
    }
}
