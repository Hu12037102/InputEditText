package com.huxiaobai.inputedittext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.huxiaobai.inputedit.weight.InputEditTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mIetCodeView1 = findViewById<InputEditTextView>(R.id.ietv_code_1)
        mIetCodeView1.setBackground()
        val mIetCodeView2 = findViewById<InputEditTextView>(R.id.ietv_code_2)
        mIetCodeView2.setBackground()
        val mIetCodeView3 = findViewById<InputEditTextView>(R.id.ietv_code_3)
        mIetCodeView3.setBackground()
    }
}