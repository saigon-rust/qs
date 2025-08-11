package com.example.demo_tabseclector

import android.os.Bundle
import android.widget.Button
import android.widget.TabHost
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lấy TabHost từ layout
        val tabHost = findViewById<TabHost>(android.R.id.tabhost)
        tabHost.setup()

        // Tab 1
        var spec: TabHost.TabSpec = tabHost.newTabSpec("Tab One")
        spec.setContent(R.id.tab1) // ID layout của tab1 trong activity_main
        spec.setIndicator("Tab 1")
        tabHost.addTab(spec)

        // Tab 2
        spec = tabHost.newTabSpec("Tab Two")
        spec.setContent(R.id.tab2)
        spec.setIndicator("Tab 2")
        tabHost.addTab(spec)

        // Tab 3
        spec = tabHost.newTabSpec("Tab Three")
        spec.setContent(R.id.tab3)
        spec.setIndicator("Tab 3")
        tabHost.addTab(spec)

        // Nút quay lại Tab 1
        findViewById<Button>(R.id.btnQLT1).setOnClickListener {
            tabHost.currentTab = 0
        }

        // Nút quay lại Tab 2
        findViewById<Button>(R.id.btnQLT2).setOnClickListener {
            tabHost.currentTab = 1
        }
    }
}
