package com.example.demo_chess

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.min

// Mã quân: 'R' = Rook/車, 'H' = Horse/馬, 'E' = Elephant/相/象, 'A' = Advisor/士/仕,
// 'G' = General/將/帥, 'C' = Cannon/砲, 'P' = Pawn/兵/卒
data class Piece(val kind: Char, val isRed: Boolean)

class ChessBoardView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    // === Paints (reuse) ===
    private val paintGrid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK; strokeWidth = 3f; style = Paint.Style.STROKE
    }
    private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK; strokeWidth = 5f; style = Paint.Style.STROKE
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 48f; textAlign = Paint.Align.CENTER
    }
    private val paintFillPiece = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL; color = Color.WHITE
    }
    private val paintHighlight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL; color = Color.argb(90, 0, 200, 0)
    }
    private val paintSelect = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = Color.BLUE; strokeWidth = 6f
    }

    // === Board state (10 rows x 9 cols) ===
    private val board: Array<Array<Piece?>> = Array(10) { Array<Piece?>(9) { null } }

    // Track selection and possible moves
    private var selectedR = -1
    private var selectedC = -1
    private val possibleMoves = mutableListOf<Pair<Int, Int>>()

    // Turn: true = red, false = black
    private var isRedTurn = true

    // Geometry computed in onDraw / reused for touch mapping
    private var cellSize = 0f
    private var startX = 0f
    private var startY = 0f

    init {
        resetBoard()
    }

    fun resetBoard() {
        // Initialize the standard xiangqi starting position
        // Top (black) rows:
        board[0] = arrayOf(
            Piece('R', false), Piece('H', false), Piece('E', false),
            Piece('A', false), Piece('G', false), Piece('A', false),
            Piece('E', false), Piece('H', false), Piece('R', false)
        )
        board[1] = Array(9) { null }
        board[2] = arrayOf(null, Piece('C', false), null, null, null, null, null, Piece('C', false), null)
        board[3] = arrayOf(Piece('P', false), null, Piece('P', false), null, Piece('P', false), null, Piece('P', false), null, Piece('P', false))
        board[4] = Array(9) { null }

        // Bottom (red) rows
        board[9] = arrayOf(
            Piece('R', true), Piece('H', true), Piece('E', true),
            Piece('A', true), Piece('G', true), Piece('A', true),
            Piece('E', true), Piece('H', true), Piece('R', true)
        )
        board[8] = Array(9) { null }
        board[7] = arrayOf(null, Piece('C', true), null, null, null, null, null, Piece('C', true), null)
        board[6] = arrayOf(Piece('P', true), null, Piece('P', true), null, Piece('P', true), null, Piece('P', true), null, Piece('P', true))
        board[5] = Array(9) { null }

        // reset selection and turn
        selectedR = -1; selectedC = -1; possibleMoves.clear()
        isRedTurn = true

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // compute sizes: make board centered and keep aspect ratio
        cellSize = min(width / 9f, height / 10f)
        val boardW = cellSize * 8f
        val boardH = cellSize * 9f
        startX = (width - boardW) / 2f
        startY = (height - boardH) / 2f

        // draw background
        canvas.drawColor(Color.parseColor("#F3E0C0")) // nice beige board color

        // draw horizontal lines (10)
        for (r in 0 until 10) {
            val y = startY + r * cellSize
            canvas.drawLine(startX, y, startX + boardW, y, paintGrid)
        }

        // draw vertical lines (9), with river gap between row 4 and 5
        for (c in 0 until 9) {
            val x = startX + c * cellSize
            if (c == 0 || c == 8) {
                canvas.drawLine(x, startY, x, startY + boardH, paintGrid)
            } else {
                // top part
                canvas.drawLine(x, startY, x, startY + 4f * cellSize, paintGrid)
                // bottom part
                canvas.drawLine(x, startY + 5f * cellSize, x, startY + 9f * cellSize, paintGrid)
            }
        }

        // draw inner border and outer border (offset 0.5 cell outward)
        canvas.drawRect(startX, startY, startX + boardW, startY + boardH, paintBorder)
        val borderOff = cellSize * 0.5f
        canvas.drawRect(startX - borderOff, startY - borderOff, startX + boardW + borderOff, startY + boardH + borderOff, paintBorder)

        // draw palaces diagonal lines
        // top palace
        canvas.drawLine(startX + 3f*cellSize, startY, startX + 5f*cellSize, startY + 2f*cellSize, paintGrid)
        canvas.drawLine(startX + 5f*cellSize, startY, startX + 3f*cellSize, startY + 2f*cellSize, paintGrid)
        // bottom palace
        canvas.drawLine(startX + 3f*cellSize, startY + 7f*cellSize, startX + 5f*cellSize, startY + 9f*cellSize, paintGrid)
        canvas.drawLine(startX + 5f*cellSize, startY + 7f*cellSize, startX + 3f*cellSize, startY + 9f*cellSize, paintGrid)

        // draw river text
        paintText.textSize = cellSize * 0.28f
        paintText.color = Color.DKGRAY
        canvas.drawText("楚河", startX + boardW * 0.25f, startY + 4.5f*cellSize + paintText.textSize/3f, paintText)
        canvas.drawText("漢界", startX + boardW * 0.75f, startY + 4.5f*cellSize + paintText.textSize/3f, paintText)

        // draw highlights (possible moves)
        val radius = cellSize * 0.36f
        for ((r, c) in possibleMoves) {
            val cx = startX + c * cellSize
            val cy = startY + r * cellSize
            canvas.drawCircle(cx, cy, radius, paintHighlight)
        }

        // draw pieces: fill circle -> stroke -> text
        paintText.textSize = cellSize * 0.5f
        for (r in 0 until 10) {
            for (c in 0 until 9) {
                val p = board[r][c] ?: continue
                val cx = startX + c * cellSize
                val cy = startY + r * cellSize

                // piece background
                canvas.drawCircle(cx, cy, radius, paintFillPiece)

                // piece border color by side
                val strokeColor = if (p.isRed) Color.RED else Color.BLACK
                val paintPieceStroke = Paint(paintGrid).apply {
                    color = strokeColor; strokeWidth = 4f; style = Paint.Style.STROKE; isAntiAlias = true
                }
                canvas.drawCircle(cx, cy, radius, paintPieceStroke)

                // draw Chinese char for piece
                paintText.color = strokeColor
                val char = kindToChar(p.kind, p.isRed)
                // vertical adjust: drawText baseline approx
                canvas.drawText(char, cx, cy + paintText.textSize/3f, paintText)
            }
        }

        // draw selection ring
        if (selectedR != -1 && selectedC != -1) {
            val cx = startX + selectedC * cellSize
            val cy = startY + selectedR * cellSize
            canvas.drawCircle(cx, cy, radius + 8f, paintSelect)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val tx = event.x
        val ty = event.y
        val radius = cellSize * 0.36f

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                var clickedRow = -1
                var clickedCol = -1

                // Ưu tiên tìm quân trong phạm vi tròn
                loop@ for (r in 0 until 10) {
                    for (c in 0 until 9) {
                        val piece = board[r][c] ?: continue
                        val cx = startX + c * cellSize
                        val cy = startY + r * cellSize
                        val dx = tx - cx
                        val dy = ty - cy
                        if (dx * dx + dy * dy <= radius * radius) {
                            clickedRow = r
                            clickedCol = c
                            break@loop
                        }
                    }
                }

                if (clickedRow == -1) {
                    val col = ((tx - startX) / cellSize).toInt()
                    val row = ((ty - startY) / cellSize).toInt()
                    if (row in 0..9 && col in 0..8) {
                        clickedRow = row
                        clickedCol = col
                    }
                }

                if (clickedRow == -1) {
                    selectedR = -1
                    selectedC = -1
                    possibleMoves.clear()
                    invalidate()
                    return true
                }

                val clicked = board[clickedRow][clickedCol]

                if (selectedR == -1) {
                    if (clicked != null && clicked.isRed == isRedTurn) {
                        selectedR = clickedRow
                        selectedC = clickedCol
                        possibleMoves.clear()
                        possibleMoves.addAll(getLegalMoves(clickedRow, clickedCol))
                        invalidate()
                    }
                    return true
                }

                val selPiece = board[selectedR][selectedC]
                if (clicked != null && clicked.isRed == selPiece?.isRed) {
                    if (clicked.isRed == isRedTurn) {
                        selectedR = clickedRow
                        selectedC = clickedCol
                        possibleMoves.clear()
                        possibleMoves.addAll(getLegalMoves(clickedRow, clickedCol))
                        invalidate()
                    }
                    return true
                }

                // Không di chuyển lúc này, chờ ACTION_UP
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (selectedR == -1) return true // Không có selection, bỏ qua

                // Xác định ô thả dựa trên vùng bán kính 0.36
                var dropRow = -1
                var dropCol = -1

                loop@ for (r in 0 until 10) {
                    for (c in 0 until 9) {
                        val cx = startX + c * cellSize
                        val cy = startY + r * cellSize
                        val dx = tx - cx
                        val dy = ty - cy
                        if (dx * dx + dy * dy <= radius * radius) {
                            dropRow = r
                            dropCol = c
                            break@loop
                        }
                    }
                }

                if (dropRow == -1) {
                    val col = ((tx - startX) / cellSize).toInt()
                    val row = ((ty - startY) / cellSize).toInt()
                    if (row in 0..9 && col in 0..8) {
                        dropRow = row
                        dropCol = col
                    }
                }

                if (dropRow != -1 && possibleMoves.contains(Pair(dropRow, dropCol))) {
                    board[dropRow][dropCol] = board[selectedR][selectedC]
                    board[selectedR][selectedC] = null
                    isRedTurn = !isRedTurn
                }

                selectedR = -1
                selectedC = -1
                possibleMoves.clear()
                invalidate()
                return true
            }
        }

        return true
    }


    // ----------------- Move generation & validation -----------------

    private fun getLegalMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val p = board[r][c] ?: return emptyList()
        val moves = mutableListOf<Pair<Int, Int>>()
        when (p.kind) {
            'R' -> moves.addAll(rookMoves(r, c))
            'H' -> moves.addAll(horseMoves(r, c))
            'E' -> moves.addAll(elephantMoves(r, c))
            'A' -> moves.addAll(advisorMoves(r, c))
            'G' -> moves.addAll(generalMoves(r, c))
            'C' -> moves.addAll(cannonMoves(r, c))
            'P' -> moves.addAll(pawnMoves(r, c))
        }
        // filter out moves that land on same color
        return moves.filter { (nr, nc) ->
            val dest = board[nr][nc]
            dest == null || dest.isRed != p.isRed
        }
    }

    // Helpers to count pieces on straight line between two points (exclusive)
    private fun countBetween(r1: Int, c1: Int, r2: Int, c2: Int): Int {
        if (r1 != r2 && c1 != c2) return -1
        var cnt = 0
        if (r1 == r2) {
            val start = min(c1, c2) + 1
            val end = maxOf(c1, c2) - 1
            for (cc in start..end) if (board[r1][cc] != null) cnt++
        } else {
            val start = min(r1, r2) + 1
            val end = maxOf(r1, r2) - 1
            for (rr in start..end) if (board[rr][c1] != null) cnt++
        }
        return cnt
    }

    // Rook (車) moves: straight lines, cannot jump
    private fun rookMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        // four directions
        // up
        var rr = r - 1
        while (rr >= 0) {
            if (board[rr][c] == null) list.add(Pair(rr, c)) else {
                list.add(Pair(rr, c)); break
            }
            rr--
        }
        // down
        rr = r + 1
        while (rr <= 9) {
            if (board[rr][c] == null) list.add(Pair(rr, c)) else { list.add(Pair(rr, c)); break }
            rr++
        }
        // left
        var cc = c - 1
        while (cc >= 0) {
            if (board[r][cc] == null) list.add(Pair(r, cc)) else { list.add(Pair(r, cc)); break }
            cc--
        }
        // right
        cc = c + 1
        while (cc <= 8) {
            if (board[r][cc] == null) list.add(Pair(r, cc)) else { list.add(Pair(r, cc)); break }
            cc++
        }
        return list
    }

    // Horse (馬) moves: L-shape but blocked by leg
    private fun horseMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        // 8 possible L moves with corresponding leg check
        val deltas = listOf(
            Pair(-2, -1) to Pair(-1, 0), // up-up-left, leg at up
            Pair(-2, 1) to Pair(-1, 0),
            Pair(2, -1) to Pair(1, 0),
            Pair(2, 1) to Pair(1, 0),
            Pair(-1, -2) to Pair(0, -1),
            Pair(1, -2) to Pair(0, -1),
            Pair(-1, 2) to Pair(0, 1),
            Pair(1, 2) to Pair(0, 1)
        )
        for ((move, leg) in deltas) {
            val nr = r + move.first
            val nc = c + move.second
            val legR = r + leg.first
            val legC = c + leg.second
            if (nr in 0..9 && nc in 0..8) {
                // leg must be empty
                if (board[legR][legC] == null) list.add(Pair(nr, nc))
            }
        }
        return list
    }

    // Elephant (相/象) moves: 2-point diagonal, cannot cross river, blocked by midpoint
    private fun elephantMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        val deltas = listOf(Pair(-2, -2), Pair(-2, 2), Pair(2, -2), Pair(2, 2))
        val piece = board[r][c] ?: return list
        for (d in deltas) {
            val nr = r + d.first
            val nc = c + d.second
            val midR = r + d.first / 2
            val midC = c + d.second / 2
            if (nr in 0..9 && nc in 0..8) {
                // blocked midpoint
                if (board[midR][midC] != null) continue
                // cannot cross river: black (top) must stay row <= 4; red (bottom) must stay row >=5
                if (piece.isRed) {
                    if (nr >= 5) list.add(Pair(nr, nc))
                } else {
                    if (nr <= 4) list.add(Pair(nr, nc))
                }
            }
        }
        return list
    }

    // Advisor (士/仕): one-step diagonal inside palace (3x3)
    private fun advisorMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        val piece = board[r][c] ?: return list
        val deltas = listOf(Pair(-1,-1), Pair(-1,1), Pair(1,-1), Pair(1,1))
        val palaceRows = if (piece.isRed) 7..9 else 0..2
        val palaceCols = 3..5
        for (d in deltas) {
            val nr = r + d.first; val nc = c + d.second
            if (nr in palaceRows && nc in palaceCols) list.add(Pair(nr,nc))
        }
        return list
    }

    // General (將/帥): one-step orthogonal inside palace, plus "face-to-face" rule handled optionally
    private fun generalMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        val piece = board[r][c] ?: return list
        val palaceRows = if (piece.isRed) 7..9 else 0..2
        val palaceCols = 3..5
        val dirs = listOf(Pair(1,0), Pair(-1,0), Pair(0,1), Pair(0,-1))
        for (d in dirs) {
            val nr = r + d.first; val nc = c + d.second
            if (nr in palaceRows && nc in palaceCols) list.add(Pair(nr,nc))
        }
        // also allow direct "facing" capture: if no pieces between generals on same file, they can capture each other
        val other = findOtherGeneral(piece.isRed)
        if (other != null && other.second == c) {
            val between = countBetween(r, c, other.first, other.second)
            if (between == 0) {
                list.add(Pair(other.first, other.second))
            }
        }
        return list
    }

    private fun findOtherGeneral(isRed: Boolean): Pair<Int, Int>? {
        for (r in 0 until 10) for (c in 0 until 9) {
            val p = board[r][c] ?: continue
            if (p.kind == 'G' && p.isRed != isRed) return Pair(r,c)
        }
        return null
    }

    // Cannon (炮/砲): moves like rook, capture requires exactly one piece between
    private fun cannonMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        // straight directions
        // up
        var rr = r - 1
        while (rr >= 0) {
            if (board[rr][c] == null) list.add(Pair(rr, c)) else { // first piece encountered -> scanning for capture beyond
                var rr2 = rr - 1
                while (rr2 >= 0) {
                    if (board[rr2][c] != null) { list.add(Pair(rr2,c)); break }
                    rr2--
                }
                break
            }
            rr--
        }
        // down
        rr = r + 1
        while (rr <= 9) {
            if (board[rr][c] == null) list.add(Pair(rr, c)) else {
                var rr2 = rr + 1
                while (rr2 <= 9) {
                    if (board[rr2][c] != null) { list.add(Pair(rr2,c)); break }
                    rr2++
                }
                break
            }
            rr++
        }
        // left
        var cc = c - 1
        while (cc >= 0) {
            if (board[r][cc] == null) list.add(Pair(r, cc)) else {
                var cc2 = cc - 1
                while (cc2 >= 0) {
                    if (board[r][cc2] != null) { list.add(Pair(r,cc2)); break }
                    cc2--
                }
                break
            }
            cc--
        }
        // right
        cc = c + 1
        while (cc <= 8) {
            if (board[r][cc] == null) list.add(Pair(r, cc)) else {
                var cc2 = cc + 1
                while (cc2 <= 8) {
                    if (board[r][cc2] != null) { list.add(Pair(r,cc2)); break }
                    cc2++
                }
                break
            }
            cc++
        }
        return list
    }

    // Pawn/Tốt rules
    private fun pawnMoves(r: Int, c: Int): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        val p = board[r][c] ?: return list
        if (p.isRed) {
            // red moves up (decreasing row)
            val forward = r - 1
            if (forward >= 0) list.add(Pair(forward, c))
            // if crossed river (row <=4), can move sideways
            if (r <= 4) {
                if (c - 1 >= 0) list.add(Pair(r, c - 1))
                if (c + 1 <= 8) list.add(Pair(r, c + 1))
            }
        } else {
            // black moves down (increasing row)
            val forward = r + 1
            if (forward <= 9) list.add(Pair(forward, c))
            if (r >= 5) {
                if (c - 1 >= 0) list.add(Pair(r, c - 1))
                if (c + 1 <= 8) list.add(Pair(r, c + 1))
            }
        }
        return list
    }

    // Utility: map kind+color to Chinese char for drawing
    private fun kindToChar(kind: Char, isRed: Boolean): String {
        return when (kind) {
            'R' -> "車"
            'H' -> "馬"
            'E' -> if (isRed) "相" else "象"
            'A' -> if (isRed) "仕" else "士"
            'G' -> if (isRed) "帥" else "將"
            'C' -> if (isRed) "砲" else "炮"
            'P' -> if (isRed) "兵" else "卒"
            else -> "?"
        }
    }
}
