package com.programmergabut.androidsocketmvvm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.programmergabut.androidsocketmvvm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding.btnEnter.setOnClickListener {
            Intent(this@MainActivity, ChatActivity::class.java).also {
                it.putExtra("name", binding.etName.text.toString())
                this@MainActivity.startActivity(it)
            }
        }
    }
}