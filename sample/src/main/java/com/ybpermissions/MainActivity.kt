package com.ybpermissions

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.byb.ybpermissions.R

class MainActivity : AppCompatActivity() {

    private lateinit var activityBtn: Button
    private lateinit var fragmentBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initData()
    }

    private fun initData() {
        activityBtn.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
        fragmentBtn.setOnClickListener {

            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .add(R.id.frame,TestFragment())
                .commit()
        }

    }

    private fun initView() {
        activityBtn = findViewById(R.id.activity_btn)
        fragmentBtn = findViewById(R.id.fragment_btn)

    }
}