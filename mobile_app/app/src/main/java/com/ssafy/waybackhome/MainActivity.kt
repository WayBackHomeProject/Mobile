package com.ssafy.waybackhome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import com.ssafy.waybackhome.databinding.ActivityMainBinding
import java.util.Date

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var prevEventTime : Date? = null
    private fun onBackButtonEvent(){
        val current = Date()
        if(prevEventTime == null || (prevEventTime!!.time - current.time)/1000 > 60){
            prevEventTime = current
            Toast.makeText(this, "'뒤로가기' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            prevEventTime = null
            finish()
            //Toast.makeText(this, "두번 눌렸습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun initListner(){
        onBackPressedDispatcher.addCallback(this){
            onBackButtonEvent()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initListner()
    }
}