package com.bpapps.billingtest1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
    }
}