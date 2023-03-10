package com.renju_note.isoo.fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.renju_note.isoo.R
import com.renju_note.isoo.RenjuEditApplication.Companion.boardManager
import com.renju_note.isoo.RenjuEditApplication.Companion.pref
import com.renju_note.isoo.RenjuEditApplication.Companion.settings
import com.renju_note.isoo.SeqTree
import com.renju_note.isoo.data.Stone
import com.renju_note.isoo.databinding.FragmentBoardBinding
import com.renju_note.isoo.databinding.PopupMenuBoardBinding
import com.renju_note.isoo.dialog.ConfirmDialog
import com.renju_note.isoo.dialog.PutTextDialog
import com.renju_note.isoo.util.BoardLayout
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BoardFragment : Fragment() {

    private lateinit var binding : FragmentBoardBinding

    enum class EditMode {
        PUT_STONE, ADD_TEXT
    }
    private var mode = EditMode.PUT_STONE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoardBinding.inflate(inflater, container, false)
        setTextSize()
        binding.boardToolbar.inflateMenu(R.menu.board_toolbar)
        binding.boardToolbar.title = resources.getString(R.string.app_name)
        toolbarItemSelected()

        boardClicked()
        buttonsClicked()
        editingTextArea()

        binding.boardBoard.updateBoardStatus(settings.boardSetting, settings.sequenceSetting)

        return binding.root
    }

    private fun buttonsClicked() {
        binding.boardMenuBtn.setOnClickListener {
            popupMenu()
        }

        binding.boardUndoAllBtn.setOnClickListener {
            val before = boardManager.getNowBoardStatus()
            if(boardManager.undoAll()) {
                val after = boardManager.getNowBoardStatus()
                updateBoard(before, after)
            }
        }

        binding.boardUndoBtn.setOnClickListener {
            val before = boardManager.getNowBoardStatus()
            if(boardManager.undo()) {
                val after = boardManager.getNowBoardStatus()
                updateBoard(before, after)
            }
        }

        binding.boardRedoBtn.setOnClickListener {
            val before = boardManager.getNowBoardStatus()
            if(boardManager.redo()) {
                val after = boardManager.getNowBoardStatus()
                updateBoard(before, after)
            }
        }

        binding.boardRedoAllBtn.setOnClickListener {
            val before = boardManager.getNowBoardStatus()
            while (true) {
                if (!boardManager.redo()) break
            }
            val after = boardManager.getNowBoardStatus()
            updateBoard(before, after)
        }

        binding.boardModeBtn.setOnClickListener {
            mode = when(mode) {
                EditMode.PUT_STONE -> {
                    binding.boardModeBtn.setImageResource(R.drawable.board_text_mode)
                    EditMode.ADD_TEXT
                }
                EditMode.ADD_TEXT -> {
                    binding.boardModeBtn.setImageResource(R.drawable.board_stone_mode)
                    EditMode.PUT_STONE
                }
            }
        }
    }

    private fun popupMenu() {
        val popupBinding = PopupMenuBoardBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupBinding.popupDeleteBtn.setOnClickListener {
            delete()
        }
        popupBinding.popupIndexHereBtn.setOnClickListener {
            startIndexHere()
        }

        binding.boardBoard.draw(Canvas())

        popupWindow.showAsDropDown(binding.boardMenuBtn)
    }

    private fun startIndexHere() {
        val confirmDialog = ConfirmDialog(requireContext(), resources.getString(R.string.start_index_confirm))
        confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        confirmDialog.setOnResponseListener(object : ConfirmDialog.OnResponseListener {
            override fun confirm() {
                confirmDialog.dismiss()
                settings.sequenceSetting.startPoint = boardManager.getNowIndex() - 1
                binding.boardBoard.updateBoardStatus(settings.boardSetting, settings.sequenceSetting)
                settings.save(pref)
            }

            override fun refuse() {
                confirmDialog.dismiss()
            }
        })
        confirmDialog.show()
    }

    private fun delete() {
        if(boardManager.getNowIndex() != 1) {
            val confirmDialog = ConfirmDialog(requireContext(), resources.getString(R.string.delete_confirm))
            confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            confirmDialog.setOnResponseListener(object : ConfirmDialog.OnResponseListener {
                override fun confirm() {
                    confirmDialog.dismiss()
                    val before = boardManager.getNowBoardStatus()
                    boardManager.deleteBranch()
                    val after = boardManager.getNowBoardStatus()
                    updateBoard(before, after)
                }

                override fun refuse() {
                    confirmDialog.dismiss()
                }
            })
            confirmDialog.show()
        }
    }

    private fun boardClicked() {
        binding.boardBoard.setOnBoardTouchListener(object : BoardLayout.OnBoardTouchListener() {
            override fun getCoordinates(x: Int, y: Int) {
                val before = boardManager.getNowBoardStatus()
                if(mode == EditMode.ADD_TEXT) {
                    val putTextDialog = PutTextDialog(requireContext())
                    putTextDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    putTextDialog.setOnResponseListener(object : PutTextDialog.OnResponseListener {
                        override fun cancel() {
                            putTextDialog.dismiss()
                        }

                        override fun ok(text: String) {
                            putTextDialog.dismiss()
                            if(boardManager.addNewChild(x, y, text)) {
                                val after = boardManager.getNowBoardStatus()
                                updateBoard(before, after)
                            }
                        }
                    })
                    putTextDialog.show()
                } else {
                    if (boardManager.putStone(x, y)) {
                        val after = boardManager.getNowBoardStatus()
                        updateBoard(before, after)
                    }
                }
            }
        })
    }

    private fun editingTextArea() {
        val activityRootView = requireActivity().window.decorView.rootView
        val rect1 = Rect()
        activityRootView.getWindowVisibleDisplayFrame(rect1)
        val initialHeight = rect1.bottom
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect2 = Rect()
            activityRootView.getWindowVisibleDisplayFrame(rect2)
            binding.boardTextAreaEt.isCursorVisible = rect2.bottom < initialHeight
        }

        binding.boardTextAreaEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                // save boxText to seqTree
                boardManager.setNowTextBoxString(s.toString())
            }
        })
        updateTextAreaStatus()
    }

    fun updateTextAreaStatus() {
        binding.boardTextAreaEt.isVisible = settings.textAreaSetting.isVisible
        binding.boardTextAreaEt.background = makeTextAreaDrawable(settings.textAreaSetting.backgroundColor, settings.textAreaSetting.strokeColor)
        binding.boardTextAreaEt.setTextColor(Color.parseColor(blendColors("#FFFFFF", settings.textAreaSetting.textColor)))
    }

    fun updateBoard() {
        binding.boardBoard.updateBoardStatus(settings.boardSetting, settings.sequenceSetting)
    }

    private fun makeTextAreaDrawable(backgroundColor : String, strokeColor : String) : GradientDrawable {
        val drawable1 = GradientDrawable()
        drawable1.setColor(Color.parseColor(backgroundColor))
        drawable1.setStroke(3, Color.parseColor(strokeColor))
        drawable1.shape = GradientDrawable.RECTANGLE
        return drawable1
    }

    private fun blendColors(baseColor: String, blendColor: String): String {
        val base = Color.parseColor(baseColor)
        val blend = Color.parseColor(blendColor)
        val alpha = Color.alpha(blend) / 255f
        val red = (1 - alpha) * Color.red(base) + alpha * Color.red(blend)
        val green = (1 - alpha) * Color.green(base) + alpha * Color.green(blend)
        val blue = (1 - alpha) * Color.blue(base) + alpha * Color.blue(blend)
        val alphaInt = (alpha * 255).toInt()
        val colorInt = Color.argb(alphaInt, red.toInt(), green.toInt(), blue.toInt())
        return String.format("#%06X", 0xFFFFFF and colorInt)
    }

    private fun updateBoard(before : ArrayList<Stone>, after : ArrayList<Stone>) {
        binding.boardTextAreaEt.setText(boardManager.getNowTextBoxString())
        binding.boardSequenceTv.text = (boardManager.getNowIndex() - 1).toString()

        val addStone = ArrayList<BoardLayout.StoneView>()
        val deleteStone = ArrayList<String>()
        val childs = ArrayList<BoardLayout.Child>()

        // update child nodes view
        after.forEach { stone ->
            if(stone.type == Stone.Type.CHILD) {
                val child = binding.boardBoard.Child(stone.text, stone.x, stone.y)
                childs.add(child)
            }
        }

        val beforeSequence = boardManager.getSequence(before)
        val afterSequence = boardManager.getSequence(after)
        var isAdd = true // after board has more stones than before
        val changeSequence = if(beforeSequence.size > afterSequence.size) {
            isAdd = false
            beforeSequence.removeAll(afterSequence.toSet())
            beforeSequence
        } else {
            afterSequence.removeAll(beforeSequence.toSet())
            afterSequence
        }

        if(isAdd) {
            changeSequence.forEach { stone ->
                val stoneView = when (stone.type) {
                    Stone.Type.BLACK -> binding.boardBoard.StoneView(
                        BoardLayout.StoneViewType.BLACK, stone.text.toInt(),
                        stone.x, stone.y)

                    Stone.Type.WHITE -> binding.boardBoard.StoneView(
                        BoardLayout.StoneViewType.WHITE, stone.text.toInt(),
                        stone.x, stone.y)

                    Stone.Type.CHILD -> binding.boardBoard.StoneView(
                        BoardLayout.StoneViewType.BLACK, -1,
                        stone.x, stone.y)
                }
                addStone.add(stoneView)
            }
        } else {
            changeSequence.forEach { stone ->
                deleteStone.add(binding.boardBoard.getStoneID(stone.x, stone.y))
            }
        }

        binding.boardBoard.placeStones(addStone, deleteStone, childs)
    }

    private fun toolbarItemSelected() {
        binding.boardToolbar.setOnMenuItemClickListener { item ->
            if (item != null) {
                when (item.itemId) {
                    R.id.action_capture -> {
                        val confirmDialog = ConfirmDialog(requireContext(), resources.getString(R.string.capture_confirm))
                        confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        confirmDialog.setOnResponseListener(object : ConfirmDialog.OnResponseListener {
                            override fun confirm() {
                                confirmDialog.dismiss()
                                captureBoard()
                            }
                            override fun refuse() { confirmDialog.dismiss() }
                        })
                        confirmDialog.show()
                    }
                    R.id.save_board -> {

                    }
                    R.id.new_board -> {
                        val confirmDialog = ConfirmDialog(requireContext(), resources.getString(R.string.new_file_confirm))
                        confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        confirmDialog.setOnResponseListener(object : ConfirmDialog.OnResponseListener {
                            override fun confirm() {
                                confirmDialog.dismiss()
                                boardManager.loadNodes(SeqTree())
                                binding.boardBoard.removeAllStones()
                            }
                            override fun refuse() { confirmDialog.dismiss() }
                        })
                        confirmDialog.show()
                    }
                    R.id.save_board_as -> {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "application/*"
                        intent.putExtra(Intent.EXTRA_TITLE, "write_your_file_name")
                        startActivityResultSave.launch(intent)
                    }
                    R.id.load_board -> {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "application/*"
                        startActivityResultLoad.launch(intent)
                    }
                    R.id.about -> {

                    }
                }
            }
            true
        }
    }

    private fun captureBoard() {
        val layout = binding.boardContainerCl
        val width = layout.width
        val height = layout.height

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        layout.draw(canvas)

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val dateTimeString = currentDateTime.format(formatter)

        // Save the bitmap to the gallery
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "renju_edit$dateTimeString")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, width)
            put(MediaStore.Images.Media.HEIGHT, height)
        }
        val imageUri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let { uri ->
            requireActivity().contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }

        Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
    }

    private var startActivityResultSave = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri?
            if (result.data != null) {
                uri = result.data!!.data
                try {
                    val outputStream: OutputStream = requireActivity().contentResolver.openOutputStream(uri!!)!!
                    val os = ObjectOutputStream(outputStream)
                    os.writeObject(boardManager.getSeqTree())
                    os.close()
                    outputStream.close()
                    Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(context, "Failed to save!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private var startActivityResultLoad = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri?
            if (result.data != null) {
                uri = result.data!!.data
                try {
                    val inputStream: InputStream = requireActivity().contentResolver.openInputStream(uri!!)!!
                    val ois = ObjectInputStream(inputStream)
                    val tree : Any = ois.readObject() as SeqTree
                    boardManager.loadNodes(tree)
                    ois.close()
                    inputStream.close()
                    Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show()

                    binding.boardBoard.removeAllStones()
                    val before = ArrayList<Stone>()
                    val after = boardManager.getNowBoardStatus()
                    updateBoard(before, after)
                } catch (e: IOException) {
                    Toast.makeText(context, "Failed to load!", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setTextSize() {
        binding.boardButtonContainerLl.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val density = requireContext().resources.displayMetrics.density
            val textSize : Int = (binding.boardBoard.width / 22.8 / density + 0.5).toInt()
            binding.boardTextAreaEt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (binding.boardBoard.width / 27.4 / density + 0.5).toInt().toFloat())
            binding.boardUndoBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
            binding.boardUndoAllBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
            binding.boardRedoBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
            binding.boardRedoAllBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
            binding.boardSequenceTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
        }
    }

}