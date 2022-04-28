package com.example.smartchess

import android.widget.Spinner
import java.util.*
import kotlin.math.abs

abstract class Piece(
    val graphic: Int,
    var tile: GameActivity.Tile,
    val board: Array<Array<GameActivity.Tile>>,
    val playerControlled: Boolean,
    val moveStack: Stack<GameActivity.Move>,
) {
    fun undo(){
        val lastMove = this.moveStack.pop()
        lastMove.piece.tile.piece = null
        lastMove.piece.tile = lastMove.startTile
        lastMove.startTile.piece = lastMove.piece
        if (lastMove.piece is Pawn) if (lastMove.piece.hasMoved && Math.abs(
                lastMove.startTile.y - lastMove.newTile.y
            ) == 2
        ) lastMove.piece.hasMoved = false
        if (lastMove.takingMove) {
            if (lastMove.castling) {
                lastMove.takenPiece!!.tile.piece = null
                lastMove.castlingTile!!.piece = lastMove.takenPiece
                lastMove.takenPiece!!.tile = lastMove.castlingTile!!
            } else {
                lastMove.takenPiece!!.tile.piece = lastMove.takenPiece
            }
        }
        lastMove.piece.deselectPossibleMoves(true)
    }
    open fun move(t: GameActivity.Tile) {
        if (t.piece != null) {
            moveStack.push(
                GameActivity.Move(
                    tile,
                    this,
                    t.piece!!,
                    t,
                    "" + this.getChar() + "x" + ('a'.plus(t.x)) + "" + (t.y + 1).toString()
                )
            )
        } else {
            moveStack.push(
                GameActivity.Move(
                    tile,
                    this,
                    t,
                    "" + this.getChar() + 'a'.plus(t.x) + (t.y + 1).toString()
                )
            )
        }
        tile.deselect()
        t.piece = this
        this.tile.piece = null
        this.tile = t

        if (checkForCheck(false)) {
            moveStack.peek().stringRep = moveStack.peek().stringRep + "+"
        }
        deselectPossibleMoves(true)
    }

    fun select() {
        val posMoves = getPossibleMoves()
        deselectPossibleMoves(true)
        for (t in posMoves) {
            t.possibleMove = true
            t.update()
        }
    }

    fun deselectPossibleMoves(update: Boolean) {
        for (line in board) {
            for (t in line) {
                t.possibleMove = false
                if (update) t.update()
            }
        }
    }

    fun getEnemyPieces(): ArrayList<Piece> {
        val enemyPiecesArray = ArrayList<Piece>()
        for (line in board) {
            for (t in line) {
                if (t.piece != null) {
                    if (t.piece!!.playerControlled != this.playerControlled) {
                        enemyPiecesArray.add(t.piece!!)
                    }
                }
            }
        }
        return enemyPiecesArray
    }

    fun getAlliedPieces(): ArrayList<Piece> {
        val alliedPiecesArray = ArrayList<Piece>()
        for (line in board) {
            for (t in line) {
                if (t.piece != null) {
                    if (t.piece!!.playerControlled == this.playerControlled) {
                        alliedPiecesArray.add(t.piece!!)
                    }
                }
            }
        }
        return alliedPiecesArray
    }

    fun checkForControl(tile: GameActivity.Tile): Boolean {
        val p = tile.piece
        tile.piece = this
        for (enemyPiece in getEnemyPieces()) {
            if (enemyPiece !is King) {
                enemyPiece.selectNoCheck()
            } else {
                val x = enemyPiece.tile.x
                val y = enemyPiece.tile.y
                if (x < 7 && y < 7) {
                    if (board[y + 1][x + 1].piece == null || board[y + 1][x + 1].piece?.playerControlled != enemyPiece.playerControlled) {
                        board[y + 1][x + 1].possibleMove = true
                    }
                }
                if (y < 7 && x > 0) {
                    if (board[y + 1][x - 1].piece == null || board[y + 1][x - 1].piece?.playerControlled != enemyPiece.playerControlled) {
                        board[y + 1][x - 1].possibleMove = true
                    }
                }
                if (y < 7) {
                    if (board[y + 1][x].piece == null || board[y + 1][x].piece?.playerControlled != enemyPiece.playerControlled) {
                        board[y + 1][x].possibleMove = true
                    }
                }
                if (x < 7) {
                    if (board[y][x + 1].piece == null || board[y][x + 1].piece?.playerControlled != enemyPiece.playerControlled) {
                        board[y][x + 1].possibleMove = true
                    }
                }
                if (x > 0) {
                    if (board[y][x - 1].piece == null || board[y][x - 1].piece?.playerControlled != enemyPiece.playerControlled) {
                        board[y][x - 1].possibleMove = true
                    }
                }
                if (y > 0 && x > 0) {
                    if (board[y - 1][x - 1].piece == null || board[y - 1][x - 1].piece?.playerControlled != enemyPiece.playerControlled) {
                        board[y - 1][x - 1].possibleMove = true
                    }
                }
                if (y > 0 && x < 7) {
                    if (board[y - 1][x + 1].piece == null || board[y - 1][x + 1].piece?.playerControlled != enemyPiece.playerControlled) {
                        board[y - 1][x + 1].possibleMove = true
                    }
                }
                if (y > 0) {
                    if (board[y - 1][x].piece == null || board[y - 1][x].piece?.playerControlled != enemyPiece.playerControlled) {
                        board[y - 1][x].possibleMove = true
                    }
                }
            }
            if (tile.possibleMove) {
                tile.piece = p
                enemyPiece.deselectPossibleMoves(true)
                return true
            }
            enemyPiece.deselectPossibleMoves(true)
        }
        tile.piece = p
        this.deselectPossibleMoves(true)
        return false
    }

    fun checkForCheck(allied: Boolean): Boolean {
        var king: King? = null
        if (allied) {
            for (p in getAlliedPieces()) {
                if (p is King) {
                    king = p
                    break
                }
            }
            for (p in getEnemyPieces()) {
                if (p !is King) p.selectNoCheck()
                if (king!!.tile.possibleMove) {
                    p.deselectPossibleMoves(false)
                    return true
                }
                p.deselectPossibleMoves(false)
            }
            return false
        } else {
            for (p in getEnemyPieces()) {
                if (p is King) {
                    king = p
                    break
                }
            }
            for (p in getAlliedPieces()) {
                if (p !is King) p.selectNoCheck()
                if (king!!.tile.possibleMove) {
                    p.deselectPossibleMoves(false)
                    return true
                }
                p.deselectPossibleMoves(false)
            }
            return false
        }
    }

    abstract fun selectNoCheck()
    abstract fun getChar(): Char
    abstract fun getPossibleMoves(): ArrayList<GameActivity.Tile>

}

class Pawn(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>,
    val promotionSpinner: Spinner
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack,
    ) {
    var hasMoved = false

    override fun move(t: GameActivity.Tile) {
        var prevMove: GameActivity.Move? = null
        var enPassant = false
        if (!moveStack.isEmpty()) {
            prevMove = moveStack.peek()
            try {
                if (prevMove.piece is Pawn && abs(prevMove.newTile.x - tile.x) == 1 && abs(prevMove.newTile.y - prevMove.startTile.y) == 2) {
                    if (((tile.y == 4 && playerControlled) || (tile.y == 3 && !playerControlled)) && (t.x == prevMove.newTile.x)) {
                        enPassant = true
                    }
                }
            } catch (e: Exception) {
            }
        }
        if (enPassant) {
            moveStack.push(
                GameActivity.Move(
                    tile,
                    this,
                    prevMove!!.piece,
                    t,
                    "" + this.getChar() + "x" + prevMove.piece.getChar() + "" + (t.y + 1).toString() + " e.p."
                )
            )
            if (checkForCheck(false)) {
                moveStack.peek().stringRep = moveStack.peek().stringRep + " +"
            }
            t.piece = this
            this.tile.piece = null
            prevMove.newTile.piece = null
            this.tile = t
            deselectPossibleMoves(true)
            return
        }
        super.move(t)
        hasMoved = true
        if (!moveStack.peek().takingMove) {
            moveStack.peek().stringRep = moveStack.peek().stringRep.substring(1)
        }

        if ((t.y == 7 && playerControlled) || (t.y == 0 && !playerControlled)) {
            val p = promotionSpinner.selectedItem.toString()
            when (p) {
                "Queen" -> {
                    if (this.graphic == R.drawable.white_pawn) {
                        t.piece =
                            Queen(R.drawable.white_queen, t, board, playerControlled, moveStack)
                    } else {
                        t.piece =
                            Queen(R.drawable.black_queen, t, board, playerControlled, moveStack)
                    }
                }
                "Bishop" -> {
                    if (this.graphic == R.drawable.white_pawn) {
                        t.piece =
                            Bishop(R.drawable.white_bishop, t, board, playerControlled, moveStack)
                    } else {
                        t.piece =
                            Bishop(R.drawable.black_bishop, t, board, playerControlled, moveStack)
                    }
                }
                "Knight" -> {
                    if (this.graphic == R.drawable.white_pawn) {
                        t.piece =
                            Knight(R.drawable.white_knight, t, board, playerControlled, moveStack)
                    } else {
                        t.piece =
                            Knight(R.drawable.black_knight, t, board, playerControlled, moveStack)
                    }
                }
                "Rook" -> {
                    if (this.graphic == R.drawable.white_pawn) {
                        t.piece = Rook(R.drawable.white_rook, t, board, playerControlled, moveStack)
                    } else {
                        t.piece = Rook(R.drawable.black_rook, t, board, playerControlled, moveStack)
                    }
                }
            }
            moveStack.peek().stringRep = moveStack.peek().stringRep + "=" + t.piece!!.getChar()
        }
        deselectPossibleMoves(true)
    }


    override fun selectNoCheck() {
        val x = tile.x
        val y = tile.y
        var d = 1
        if (!playerControlled) d = -1
        if (!hasMoved) {

            if (board[y + d][x].piece == null) {
                board[y + d][x].possibleMove = true
                if (board[y + 2 * d][x].piece == null) {
                    board[y + 2 * d][x].possibleMove = true
                    board[y + 2 * d][x].update()
                }
                board[y + d][x].update()
            }


        } else {
            if (board[y + d][x].piece == null) {
                board[y + d][x].possibleMove = true
                board[y + d][x].update()
            }

        }
        if (!moveStack.isEmpty()) {
            val lastMove = moveStack.peek()
            if (lastMove.piece is Pawn && abs(lastMove.newTile.x - x) == 1 && abs(lastMove.newTile.y - lastMove.startTile.y) == 2) {
                val pawn = lastMove.piece
                if ((y == 4 && playerControlled) || (y == 3 && !playerControlled)) {
                    board[y + d][pawn.tile.x].possibleMove = true
                    board[y + d][pawn.tile.x].update()
                }
            }
        }
        try {
            if (board[y + d][x + 1].piece!!.playerControlled != playerControlled) {
                board[y + d][x + 1].possibleMove = true
                board[y + d][x + 1].update()
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + d][x - 1].piece!!.playerControlled != playerControlled) {
                board[y + d][x - 1].possibleMove = true
                board[y + d][x - 1].update()
            }
        } catch (e: Exception) {
        }
    }

    override fun getChar(): Char {
        return 'a'.plus(tile.x)
    }

    override fun getPossibleMoves(): ArrayList<GameActivity.Tile> {
        val x = tile.x
        val y = tile.y
        var d = 1
        val possibleMoves = ArrayList<GameActivity.Tile>()
        if (!playerControlled) d = -1
        if (!hasMoved) {

            if (board[y + d][x].piece == null) {
                board[y + d][x].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y + d][x])
                }
                this.tile.piece = this
                board[y + d][x].piece = null
                if (board[y + 2 * d][x].piece == null) {
                    board[y + 2 * d][x].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y + 2 * d][x])
                    }
                    this.tile.piece = this
                    board[y + 2 * d][x].piece = null
                    board[y + 2 * d][x].update()
                }
            }


        } else {
            if (board[y + d][x].piece == null) {
                board[y + d][x].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y + d][x])
                }
                this.tile.piece = this
                board[y + d][x].piece = null
            }

        }
        if (!moveStack.isEmpty()) {
            val lastMove = moveStack.peek()
            if (lastMove.piece is Pawn && abs(lastMove.newTile.x - x) == 1 && abs(lastMove.newTile.y - lastMove.startTile.y) == 2) {
                val pawn = lastMove.piece
                if ((y == 4 && playerControlled) || (y == 3 && !playerControlled)) {
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y + d][pawn.tile.x])
                    }
                    this.tile.piece = this
                }
            }
        }
        try {
            if (board[y + d][x + 1].piece!!.playerControlled != playerControlled) {
                this.tile.piece = null
                val p = board[y + d][x + 1].piece
                board[y + d][x + 1].piece = this
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y + d][x + 1])
                }
                this.tile.piece = this
                board[y + d][x + 1].piece = p
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + d][x - 1].piece!!.playerControlled != playerControlled) {
                this.tile.piece = null
                val p = board[y + d][x - 1].piece
                board[y + d][x - 1].piece = this
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y + d][x - 1])
                }
                this.tile.piece = this
                board[y + d][x - 1].piece = p
            }
        } catch (e: Exception) {
        }
        return possibleMoves
    }


}

class Bishop(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack
    ) {

    override fun selectNoCheck() {
        val x = tile.x
        val y = tile.y
        for (i in 1..6) {
            try {
                if (board[y + i][x + i].piece != null) {
                    if (board[y + i][x + i].piece!!.playerControlled != playerControlled) {
                        board[y + i][x + i].possibleMove = true
                        board[y + i][x + i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x + i].possibleMove = true
                    board[y + i][x + i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y + i][x - i].piece != null) {
                    if (board[y + i][x - i].piece!!.playerControlled != playerControlled) {
                        board[y + i][x - i].possibleMove = true
                        board[y + i][x - i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x - i].possibleMove = true
                    board[y + i][x - i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x + i].piece != null) {
                    if (board[y - i][x + i].piece!!.playerControlled != playerControlled) {
                        board[y - i][x + i].possibleMove = true
                        board[y - i][x + i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x + i].possibleMove = true
                    board[y - i][x + i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x - i].piece != null) {
                    if (board[y - i][x - i].piece!!.playerControlled != playerControlled) {
                        board[y - i][x - i].possibleMove = true
                        board[y - i][x - i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x - i].possibleMove = true
                    board[y - i][x - i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
    }

    override fun getChar(): Char {
        return 'B'
    }

    override fun getPossibleMoves(): ArrayList<GameActivity.Tile> {
        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<GameActivity.Tile>()
        for (i in 1..6) {
            try {
                if (board[y + i][x + i].piece != null) {
                    if (board[y + i][x + i].piece!!.playerControlled != playerControlled) {
                        val p = board[y + i][x + i].piece
                        board[y + i][x + i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y + i][x + i])
                        }
                        board[y + i][x + i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x + i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y + i][x + i])
                    }
                    board[y + i][x + i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y + i][x - i].piece != null) {
                    if (board[y + i][x - i].piece!!.playerControlled != playerControlled) {
                        val p = board[y + i][x - i].piece
                        board[y + i][x - i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y + i][x - i])
                        }
                        board[y + i][x - i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x - i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y + i][x - i])
                    }
                    board[y + i][x - i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x + i].piece != null) {
                    if (board[y - i][x + i].piece!!.playerControlled != playerControlled) {
                        val p = board[y - i][x + i].piece
                        board[y - i][x + i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y - i][x + i])
                        }
                        board[y - i][x + i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x + i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y - i][x + i])
                    }
                    board[y - i][x + i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x - i].piece != null) {
                    if (board[y - i][x - i].piece!!.playerControlled != playerControlled) {
                        val p = board[y - i][x - i].piece
                        board[y - i][x - i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y - i][x - i])
                        }
                        board[y - i][x - i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x - i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y - i][x - i])
                    }
                    board[y - i][x - i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        return possibleMoves
    }
}

class Knight(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack
    ) {

    override fun selectNoCheck() {
        val x = tile.x
        val y = tile.y
        try {
            if (board[y + 2][x + 1].piece == null || board[y + 2][x + 1].piece?.playerControlled != this.playerControlled) {
                board[y + 2][x + 1].possibleMove = true
                board[y + 2][x + 1].update()
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + 2][x - 1].piece == null || board[y + 2][x - 1].piece?.playerControlled != this.playerControlled) {
                board[y + 2][x - 1].possibleMove = true
                board[y + 2][x - 1].update()
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + 1][x + 2].piece == null || board[y + 1][x + 2].piece?.playerControlled != this.playerControlled) {
                board[y + 1][x + 2].possibleMove = true
                board[y + 1][x + 2].update()
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + 1][x - 2].piece == null || board[y + 1][x - 2].piece?.playerControlled != this.playerControlled) {
                board[y + 1][x - 2].possibleMove = true
                board[y + 1][x - 2].update()
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 2][x + 1].piece == null || board[y - 2][x + 1].piece?.playerControlled != this.playerControlled) {
                board[y - 2][x + 1].possibleMove = true
                board[y - 2][x + 1].update()
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 2][x - 1].piece == null || board[y - 2][x - 1].piece?.playerControlled != this.playerControlled) {
                board[y - 2][x - 1].possibleMove = true
                board[y - 2][x - 1].update()
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 1][x + 2].piece == null || board[y - 1][x + 2].piece?.playerControlled != this.playerControlled) {
                board[y - 1][x + 2].possibleMove = true
                board[y - 1][x + 2].update()
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 1][x - 2].piece == null || board[y - 1][x - 2].piece?.playerControlled != this.playerControlled) {
                board[y - 1][x - 2].possibleMove = true
                board[y - 1][x - 2].update()
            }
        } catch (e: Exception) {
        }

    }

    override fun getChar(): Char {
        return 'N'
    }

    override fun getPossibleMoves(): ArrayList<GameActivity.Tile> {
        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<GameActivity.Tile>()
        try {
            if (board[y + 2][x + 1].piece == null || board[y + 2][x + 1].piece?.playerControlled != this.playerControlled) {
                var p = board[y + 2][x + 1].piece
                board[y + 2][x + 1].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y + 2][x + 1])
                }
                board[y + 2][x + 1].piece = p
                this.tile.piece = this
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + 2][x - 1].piece == null || board[y + 2][x - 1].piece?.playerControlled != this.playerControlled) {
                var p = board[y + 2][x - 1].piece
                board[y + 2][x - 1].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y + 2][x - 1])
                }
                board[y + 2][x - 1].piece = p
                this.tile.piece = this
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + 1][x + 2].piece == null || board[y + 1][x + 2].piece?.playerControlled != this.playerControlled) {
                var p = board[y + 1][x + 2].piece
                board[y + 1][x + 2].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y + 1][x + 2])
                }
                board[y + 1][x + 2].piece = p
                this.tile.piece = this
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + 1][x - 2].piece == null || board[y + 1][x - 2].piece?.playerControlled != this.playerControlled) {
                var p = board[y + 1][x - 2].piece
                board[y + 1][x - 2].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y + 1][x - 2])
                }
                board[y + 1][x - 2].piece = p
                this.tile.piece = this
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 2][x + 1].piece == null || board[y - 2][x + 1].piece?.playerControlled != this.playerControlled) {
                var p = board[y - 2][x + 1].piece
                board[y - 2][x + 1].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y - 2][x + 1])
                }
                board[y - 2][x + 1].piece = p
                this.tile.piece = this
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 2][x - 1].piece == null || board[y - 2][x - 1].piece?.playerControlled != this.playerControlled) {
                var p = board[y - 2][x - 1].piece
                board[y - 2][x - 1].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y - 2][x - 1])
                }
                board[y - 2][x - 1].piece = p
                this.tile.piece = this
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 1][x + 2].piece == null || board[y - 1][x + 2].piece?.playerControlled != this.playerControlled) {
                var p = board[y - 1][x + 2].piece
                board[y - 1][x + 2].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y - 1][x + 2])
                }
                board[y - 1][x + 2].piece = p
                this.tile.piece = this
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 1][x - 2].piece == null || board[y - 1][x - 2].piece?.playerControlled != this.playerControlled) {
                var p = board[y - 1][x - 2].piece
                board[y - 1][x - 2].piece = this
                this.tile.piece = null
                if (!checkForCheck(true)) {
                    possibleMoves.add(board[y - 1][x - 2])
                }
                board[y - 1][x - 2].piece = p
                this.tile.piece = this
            }
        } catch (e: Exception) {
        }
        return possibleMoves

    }

}

class King(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack
    ) {
    var hasMoved = false
    override fun move(t: GameActivity.Tile) {
        val x = tile.x
        val y = tile.y
        if (t.x - x == 2) {
            if (7 - x == 3) {
                moveStack.push(
                    GameActivity.Move(
                        this.tile,
                        this,
                        board[y][7].piece!!,
                        t,
                        board[y][7],
                        "0-0"
                    )
                )
                t.piece = this
                this.tile.piece = null
                this.tile = t
                board[y][7].piece!!.tile = board[y][x + 1]
                board[y][x + 1].piece = board[y][7].piece
                board[y][7].piece = null
                deselectPossibleMoves(true)
                return
            }
            if (7 - x == 4) {
                moveStack.push(
                    GameActivity.Move(
                        this.tile,
                        this,
                        board[y][7].piece!!,
                        t,
                        board[y][7],
                        "0-0-0"
                    )
                )
                t.piece = this
                this.tile.piece = null
                this.tile = t
                board[y][7].piece!!.tile = board[y][x + 1]
                board[y][x + 1].piece = board[y][7].piece
                board[y][7].piece = null
                deselectPossibleMoves(true)
                return
            }

        }
        if (t.x - x == -2) {
            if (7 - x == 3) {
                moveStack.push(
                    GameActivity.Move(
                        this.tile,
                        this,
                        board[y][0].piece!!,
                        t,
                        board[y][0],
                        "0-0-0"
                    )
                )
                t.piece = this
                this.tile.piece = null
                this.tile = t
                board[y][0].piece!!.tile = board[y][x - 1]
                board[y][x - 1].piece = board[y][0].piece
                board[y][0].piece = null
                deselectPossibleMoves(true)
                return
            }
            if (7 - x == 4) {
                moveStack.push(
                    GameActivity.Move(
                        this.tile,
                        this,
                        board[y][0].piece!!,
                        t,
                        board[y][0],
                        "0-0-0"
                    )
                )
                t.piece = this
                this.tile.piece = null
                this.tile = t
                board[y][0].piece!!.tile = board[y][x - 1]
                board[y][x - 1].piece = board[y][0].piece
                board[y][0].piece = null
                deselectPossibleMoves(true)
                return
            }
        }

        super.move(t)
    }

    override fun selectNoCheck() {
        return
    }

    override fun getChar(): Char {
        return 'K'
    }

    override fun getPossibleMoves(): ArrayList<GameActivity.Tile> {
        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<GameActivity.Tile>()
        if (x < 7 && y < 7) {
            if (board[y + 1][x + 1].piece == null || board[y + 1][x + 1].piece?.playerControlled != playerControlled) {
                if (!checkForControl(board[y + 1][x + 1])) {
                    possibleMoves.add(board[y + 1][x + 1])
                }
            }
        }
        if (y < 7 && x > 0) {
            if (board[y + 1][x - 1].piece == null || board[y + 1][x - 1].piece?.playerControlled != playerControlled) {
                if (!checkForControl(board[y + 1][x - 1])) {
                    possibleMoves.add(board[y + 1][x - 1])
                }
            }
        }
        if (y < 7) {
            if (board[y + 1][x].piece == null || board[y + 1][x].piece?.playerControlled != playerControlled) {
                if (!checkForControl(board[y + 1][x])) {
                    possibleMoves.add(board[y + 1][x])
                }
            }
        }
        if (x < 7) {
            if (board[y][x + 1].piece == null || board[y][x + 1].piece?.playerControlled != playerControlled) {
                if (!checkForControl(board[y][x + 1])) {
                    possibleMoves.add(board[y][x + 1])
                }
            }
        }
        if (x > 0) {
            if (board[y][x - 1].piece == null || board[y][x - 1].piece?.playerControlled != playerControlled) {
                if (!checkForControl(board[y][x - 1])) {
                    possibleMoves.add(board[y][x - 1])
                }
            }
        }
        if (y > 0 && x > 0) {
            if (board[y - 1][x - 1].piece == null || board[y - 1][x - 1].piece?.playerControlled != playerControlled) {
                if (!checkForControl(board[y - 1][x - 1])) {
                    possibleMoves.add(board[y - 1][x - 1])
                }
            }
        }
        if (y > 0 && x < 7) {
            if (board[y - 1][x + 1].piece == null || board[y - 1][x + 1].piece?.playerControlled != playerControlled) {
                if (!checkForControl(board[y - 1][x + 1])) {
                    possibleMoves.add(board[y - 1][x + 1])
                }
            }
        }
        if (y > 0) {
            if (board[y - 1][x].piece == null || board[y - 1][x].piece?.playerControlled != playerControlled) {
                if (!checkForControl(board[y - 1][x])) {
                    possibleMoves.add(board[y - 1][x])
                }
            }
        }
        if (!this.hasMoved) {
            println("a")
            if (board[y][0].piece is Rook) {
                println("b")
                if (!(board[y][0].piece as Rook).hasMoved) {
                    println("c")
                    var possibleLeft = true
                    for (i in 1..(x - 1)) {
                        if (board[y][i].piece != null) {
                            println(i)
                            possibleLeft = false
                            break
                        } else {
                            if (checkForControl(board[y][i])) {
                                println(i + 10)
                                possibleLeft = false
                                break
                            }
                        }
                    }
                    if (possibleLeft) {
                        println("d")
                        possibleMoves.add(board[y][x - 2])
                    }
                }
            }
            if (board[y][7].piece is Rook) {
                println("r1")
                if (!(board[y][7].piece as Rook).hasMoved) {
                    println("r2")
                    var possibleRight = true
                    for (i in ((x + 1)..6)) {
                        println(i.toString())
                        if (board[y][i].piece != null) {
                            println("r" + i.toString())
                            possibleRight = false
                            break
                        } else {
                            if (checkForControl(board[y][i])) {
                                println("1r" + i.toString())
                                possibleRight = false
                                break
                            }
                        }
                    }
                    if (possibleRight) {
                        possibleMoves.add(board[y][x + 2])
                    }
                }
            }
        }
        return possibleMoves

    }
}

class Queen(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack
    ) {

    override fun selectNoCheck() {
        val x = tile.x
        val y = tile.y
        for (i in 1..6) {
            try {
                if (board[y + i][x + i].piece != null) {
                    if (board[y + i][x + i].piece!!.playerControlled != playerControlled) {
                        board[y + i][x + i].possibleMove = true
                        board[y + i][x + i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x + i].possibleMove = true
                    board[y + i][x + i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y + i][x - i].piece != null) {
                    if (board[y + i][x - i].piece!!.playerControlled != playerControlled) {
                        board[y + i][x - i].possibleMove = true
                        board[y + i][x - i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x - i].possibleMove = true
                    board[y + i][x - i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x + i].piece != null) {
                    if (board[y - i][x + i].piece!!.playerControlled != playerControlled) {
                        board[y - i][x + i].possibleMove = true
                        board[y - i][x + i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x + i].possibleMove = true
                    board[y - i][x + i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x - i].piece != null) {
                    if (board[y - i][x - i].piece!!.playerControlled != playerControlled) {
                        board[y - i][x - i].possibleMove = true
                        board[y - i][x - i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x - i].possibleMove = true
                    board[y - i][x - i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y][x + i].piece != null) {
                    if (board[y][x + i].piece!!.playerControlled != playerControlled) {
                        board[y][x + i].possibleMove = true
                        board[y][x + i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x + i].possibleMove = true
                    board[y][x + i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y][x - i].piece != null) {
                    if (board[y][x - i].piece!!.playerControlled != playerControlled) {
                        board[y][x - i].possibleMove = true
                        board[y][x - i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x - i].possibleMove = true
                    board[y][x - i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x].piece != null) {
                    if (board[y - i][x].piece!!.playerControlled != playerControlled) {
                        board[y - i][x].possibleMove = true
                        board[y - i][x].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x].possibleMove = true
                    board[y - i][x].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y + i][x].piece != null) {
                    if (board[y + i][x].piece!!.playerControlled != playerControlled) {
                        board[y + i][x].possibleMove = true
                        board[y + i][x].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x].possibleMove = true
                    board[y + i][x].update()
                }
            } catch (e: Exception) {
                break
            }
        }
    }

    override fun getChar(): Char {
        return 'Q'
    }

    override fun getPossibleMoves(): ArrayList<GameActivity.Tile> {

        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<GameActivity.Tile>()
        for (i in 1..6) {
            try {
                if (board[y + i][x + i].piece != null) {
                    if (board[y + i][x + i].piece!!.playerControlled != playerControlled) {
                        val p = board[y + i][x + i].piece
                        board[y + i][x + i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y + i][x + i])
                        }
                        board[y + i][x + i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x + i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y + i][x + i])
                    }
                    board[y + i][x + i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y + i][x - i].piece != null) {
                    if (board[y + i][x - i].piece!!.playerControlled != playerControlled) {
                        val p = board[y + i][x - i].piece
                        board[y + i][x - i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y + i][x - i])
                        }
                        board[y + i][x - i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x - i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y + i][x - i])
                    }
                    board[y + i][x - i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x + i].piece != null) {
                    if (board[y - i][x + i].piece!!.playerControlled != playerControlled) {
                        val p = board[y - i][x + i].piece
                        board[y - i][x + i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y - i][x + i])
                        }
                        board[y - i][x + i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x + i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y - i][x + i])
                    }
                    board[y - i][x + i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x - i].piece != null) {
                    if (board[y - i][x - i].piece!!.playerControlled != playerControlled) {
                        val p = board[y - i][x - i].piece
                        board[y - i][x - i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y - i][x - i])
                        }
                        board[y - i][x - i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x - i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y - i][x - i])
                    }
                    board[y - i][x - i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y][x + i].piece != null) {
                    if (board[y][x + i].piece!!.playerControlled != playerControlled) {
                        var p = board[y][x + i].piece
                        board[y][x + i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y][x + i])
                        }
                        board[y][x + i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x + i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y][x + i])
                    }
                    board[y][x + i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y][x - i].piece != null) {
                    if (board[y][x - i].piece!!.playerControlled != playerControlled) {
                        var p = board[y][x - i].piece
                        board[y][x - i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y][x - i])
                        }
                        board[y][x - i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x - i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y][x - i])
                    }
                    board[y][x - i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x].piece != null) {
                    if (board[y - i][x].piece!!.playerControlled != playerControlled) {
                        var p = board[y - i][x].piece
                        board[y - i][x].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y - i][x])
                        }
                        board[y - i][x].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y - i][x])
                    }
                    board[y - i][x].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y + i][x].piece != null) {
                    if (board[y + i][x].piece!!.playerControlled != playerControlled) {
                        var p = board[y + i][x].piece
                        board[y + i][x].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y + i][x])
                        }
                        board[y + i][x].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y + i][x])
                    }
                    board[y + i][x].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        return possibleMoves

    }

}

class Rook(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack
    ) {
    var hasMoved = false
    override fun move(t: GameActivity.Tile) {
        hasMoved = true
        super.move(t)
    }

    override fun selectNoCheck() {
        val x = tile.x
        val y = tile.y
        for (i in 1..6) {
            try {
                if (board[y][x + i].piece != null) {
                    if (board[y][x + i].piece!!.playerControlled != playerControlled) {
                        board[y][x + i].possibleMove = true
                        board[y][x + i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x + i].possibleMove = true
                    board[y][x + i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y][x - i].piece != null) {
                    if (board[y][x - i].piece!!.playerControlled != playerControlled) {
                        board[y][x - i].possibleMove = true
                        board[y][x - i].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x - i].possibleMove = true
                    board[y][x - i].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x].piece != null) {
                    if (board[y - i][x].piece!!.playerControlled != playerControlled) {
                        board[y - i][x].possibleMove = true
                        board[y - i][x].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x].possibleMove = true
                    board[y - i][x].update()
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y + i][x].piece != null) {
                    if (board[y + i][x].piece!!.playerControlled != playerControlled) {
                        board[y + i][x].possibleMove = true
                        board[y + i][x].update()
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x].possibleMove = true
                    board[y + i][x].update()
                }
            } catch (e: Exception) {
                break
            }
        }
    }

    override fun getChar(): Char {
        return 'R'
    }

    override fun getPossibleMoves(): ArrayList<GameActivity.Tile> {
        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<GameActivity.Tile>()
        for (i in 1..6) {
            try {
                if (board[y][x + i].piece != null) {
                    if (board[y][x + i].piece!!.playerControlled != playerControlled) {
                        var p = board[y][x + i].piece
                        board[y][x + i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y][x + i])
                        }
                        board[y][x + i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x + i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y][x + i])
                    }
                    board[y][x + i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y][x - i].piece != null) {
                    if (board[y][x - i].piece!!.playerControlled != playerControlled) {
                        var p = board[y][x - i].piece
                        board[y][x - i].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y][x - i])
                        }
                        board[y][x - i].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x - i].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y][x - i])
                    }
                    board[y][x - i].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y - i][x].piece != null) {
                    if (board[y - i][x].piece!!.playerControlled != playerControlled) {
                        var p = board[y - i][x].piece
                        board[y - i][x].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y - i][x])
                        }
                        board[y - i][x].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y - i][x])
                    }
                    board[y - i][x].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        for (i in 1..6) {
            try {
                if (board[y + i][x].piece != null) {
                    if (board[y + i][x].piece!!.playerControlled != playerControlled) {
                        var p = board[y + i][x].piece
                        board[y + i][x].piece = this
                        this.tile.piece = null
                        if (!checkForCheck(true)) {
                            possibleMoves.add(board[y + i][x])
                        }
                        board[y + i][x].piece = p
                        this.tile.piece = this
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x].piece = this
                    this.tile.piece = null
                    if (!checkForCheck(true)) {
                        possibleMoves.add(board[y + i][x])
                    }
                    board[y + i][x].piece = null
                    this.tile.piece = this
                }
            } catch (e: Exception) {
                break
            }
        }
        return possibleMoves
    }

}