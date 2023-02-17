package com.renju_note.isoo.util

import com.renju_note.isoo.SeqTree

class SeqTreeBoardManager : BoardManager {

    private var seqTree = SeqTree()
    val boardSize = 15
    private var nowIndex = 1
    private var sequence = ArrayList<Pair<Int, Int>>()

    fun getSeqTree() = seqTree

    override fun getNowBoardStatus(): Array<Array<String>> {
        val array = Array(boardSize) { Array(boardSize) { "" } }
        for(i in 0..14) {
            for(j in 0..14) {
                if(seqTree.now_board[i][j] > 0) {
                    array[i][j] = generateInfoString(BoardManager.ElementType.BLACK, i, j, (seqTree.now_board[i][j] - 1).toString())
                } else if(seqTree.now_board[i][j] < 0) {
                    array[i][j] = generateInfoString(BoardManager.ElementType.WHITE, i, j, (seqTree.now_board[i][j] * (-1) - 1).toString())
                }
            }
        }
        var tmp = seqTree.now.child
        while(tmp != null) {
            val text = if(tmp.text == null) "" else tmp.text
            array[tmp.x][tmp.y] = generateInfoString(BoardManager.ElementType.CHILD, tmp.x, tmp.y, text)
            tmp = tmp.next
        }
        return array
    }

    override fun setNowTextBoxString(text: String) {
        seqTree.now.boxText = text
    }

    override fun getNowTextBoxString(): String {
        return if(seqTree.now.boxText != null) seqTree.now.boxText else ""
    }

    override fun getSequence(): ArrayList<Pair<Int, Int>> {
        return sequence
    }

    override fun getChangeStatus(
        before: Array<Array<String>>,
        after: Array<Array<String>>
    ): Pair<ArrayList<String>, ArrayList<String>> {
        val delete = ArrayList<String>()
        val add = ArrayList<String>()
        for(i in 0..14) {
            for(j in 0..14) {
                if(before[i][j] != after[i][j]) {
                    if(after[i][j].isBlank())
                        delete.add(before[i][j])
                    else {
                        add.add(after[i][j])
                    }
                }
            }
        }
        return Pair(add, delete)
    }

    fun getAllElementStatus(): ArrayList<String> {
        val elementsStatus = ArrayList<String>()
        val nowBoardStatus = getNowBoardStatus()
        for(i in 0..14) {
            for(j in 0..14) {
                if(nowBoardStatus[i][j].isNotBlank()) {
                    elementsStatus.add(nowBoardStatus[i][j])
                }
            }
        }
        return elementsStatus
    }

    override fun generateInfoString(
        type: BoardManager.ElementType,
        x: Int,
        y: Int,
        text: String
    ): String {
        return when(type) {
            BoardManager.ElementType.BLACK -> "black/$x/$y/$text"
            BoardManager.ElementType.WHITE -> "white/$x/$y/$text"
            BoardManager.ElementType.CHILD -> "child/$x/$y/$text"
        }
    }

    override fun infoString2Info(infoString: String): Pair<Pair<Int, Int>, Pair<BoardManager.ElementType, String>> {
        val parts = infoString.split("/")

        val type = when(parts[0]) {
            "black" -> BoardManager.ElementType.BLACK
            "white" -> BoardManager.ElementType.WHITE
            "child" -> BoardManager.ElementType.CHILD
            else -> BoardManager.ElementType.CHILD
        }
        val x = parts[1].toIntOrNull() ?: 0
        val y = parts[2].toIntOrNull() ?: 0
        val text = parts[3]

        return Pair(Pair(x, y), Pair(type, text))
    }

    override fun getNowIndex(): Int {
        return nowIndex
    }

    override fun undo(): Boolean {
        if(nowIndex != 1) {
            sequence.removeLast()
            seqTree.undo(seqTree.now)
            nowIndex--
            return true
        }
        return false
    }

    override fun undoAll(): Boolean {
        val result = undo()
        while (seqTree.now !== seqTree.head && seqTree.now.child.next == null) {
            undo()
        }
        return result
    }

    override fun redo(): Boolean {
        if (seqTree.now.child != null && seqTree.now.child.next == null) {
            nowIndex++
            seqTree.redo(seqTree.now, nowIndex)
            sequence.add(Pair(seqTree.now.x, seqTree.now.y))
            return true
        }
        return false
    }

    override fun deleteBranch(): Boolean {
        if(nowIndex != 1) {
            sequence.removeLast()
            seqTree.delete(seqTree.now)
            nowIndex--
            return true
        }
        return false
    }

    override fun putStone(x: Int, y: Int): Boolean {
        if(seqTree.now_board[x][y] == 0) {
            nowIndex++
            seqTree.next(seqTree.now, x, y, nowIndex)
            sequence.add(Pair(seqTree.now.x, seqTree.now.y))
            return true
        }
        return false
    }

    override fun addNewChild(x: Int, y: Int, text: String): Boolean {
        if(seqTree.now_board[x][y] == 0) {
            if (seqTree.now.child == null) {
                seqTree.createChild(seqTree.now, x, y)
                seqTree.now.child.text = text
            } else {
                var temp = seqTree.now.child
                while (temp != null) {
                    if (temp.x == x && temp.y == y) {
                        temp.text = text
                        break
                    }
                    if (temp.next == null) {
                        seqTree.createNext(temp, seqTree.now, x, y)
                        temp.next.text = text
                        break
                    }
                    temp = temp.next
                }
            }
            return true
        }
        return false
    }

    override fun <T> loadNodes(tree: T) {
        if(tree is SeqTree)
            seqTree = tree
        seqTree.setNow_boardTo0()
        seqTree.now = seqTree.head
        if (seqTree.text_box != null) { // for loading last ver text
            seqTree.now.boxText = seqTree.text_box
        }
        nowIndex = 1
        sequence.clear()
    }

}