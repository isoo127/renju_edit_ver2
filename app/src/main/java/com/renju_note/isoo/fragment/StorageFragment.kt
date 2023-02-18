package com.renju_note.isoo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.renju_note.isoo.databinding.FragmentStorageBinding

class StorageFragment : Fragment() {

    private lateinit var binding : FragmentStorageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStorageBinding.inflate(inflater, container, false)

        return binding.root
    }

}