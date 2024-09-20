package com.huxiaobai.inputedittext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.huxiaobai.inputedit.weight.InputEditTextView
import com.huxiaobai.inputedit.weight.InputEditTextView2

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mIetCodeView1 = findViewById<InputEditTextView2>(R.id.ietv_code_1)
        mIetCodeView1.setBackground()
         val mIetCodeView2 = findViewById<InputEditTextView2>(R.id.ietv_code_2)
         mIetCodeView2.setBackground()
         val mIetCodeView3 = findViewById<InputEditTextView>(R.id.ietv_code_3)
         mIetCodeView3.setBackground()
        arrayListOf("1").also {

            it.forEach { item ->
                when (it.indexOf(item)) {
                    0 -> {
                        Log.w("MainActivity--", "我是0")

                    }

                    it.size - 1 -> {
                        Log.w("MainActivity--", "我是it.size")
                    }

                    else -> {
                        Log.w("MainActivity--", "我是else")
                    }
                }
            }

        }
    }

}