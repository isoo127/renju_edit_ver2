package com.renju_note.isoo.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.renju_note.isoo.databinding.FragmentBoardBinding
import com.renju_note.isoo.util.BoardLayout

class BoardFragment : Fragment() {

    private lateinit var binding : FragmentBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardBinding.inflate(inflater, container, false)
        setTextSize()

        binding.boardBoard.setOnBoardTouchListener(object : BoardLayout.OnBoardTouchListener() {
            override fun getCoordinates(x: Int, y: Int, realX: Float, realY: Float) {
                val id = binding.boardBoard.generateStoneID(x, y)
                val stone = binding.boardBoard.Stone(BoardLayout.StoneType.BLACK, "1", realX, realY)
                val list = ArrayList<Pair<String, BoardLayout.Stone>>()
                list.add(Pair(id, stone))
                binding.boardBoard.placeStones(list, null)
            }
        })

        binding.boardUndoAllBtn.setOnClickListener {
            binding.boardBoard.removeAllStones()
        }

        return binding.root
    }

    private fun setTextSize() {
        binding.boardButtonContainerLl.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val density = requireContext().resources.displayMetrics.density
            val textSize : Int = (binding.boardBoard.width / 22.8 / density + 0.5).toInt()
            binding.boardUndoButtonBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
            binding.boardUndoAllBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
            binding.boardRedoBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
            binding.boardRedoAllBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
            binding.boardSequenceTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
        }
    }

}