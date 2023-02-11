package com.renju_note.isoo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.renju_note.isoo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainContainerCl.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            Toast.makeText(applicationContext, "width : ${binding.mainContainerCl.width}" +
                    ", height : ${binding.mainContainerCl.height}", Toast.LENGTH_LONG).show()
        }
    }

}