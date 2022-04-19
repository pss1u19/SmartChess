package com.example.smartchess

import android.widget.Spinner
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

abstract class Piece(
    val graphic: Int,
    var tile: GameActivity.Tile,
    val board: Array<Array<GameActivity.Tile>>,
    val playerControlled: Boolean,
    val moveStack: Stack<GameActivity.Move>,
) {
    open fun move(t: GameActivity.Tile){
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

        //if(checkForCheck(false)){
        //    moveStack.peek().stringRep = moveStack.peek().stringRep + " +"
        //}
        tile.deselect()
        t.piece = this
        this.tile.piece = null
        this.tile = t
        deselectPossibleMoves(true)
    }
    abstract fun select()
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
                enemyPiece.select()
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
                if(p !is King)p.select()
                if (king!!.tile.possibleMove) {
                    p.deselectPossibleMoves(true)
                    return true
                }
                p.deselectPossibleMoves(true)
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
                if(p !is King)p.select()
                if (king!!.tile.possibleMove) {
                    p.deselectPossibleMoves(true)
                    return true
                }
                p.deselectPossibleMoves(true)
            }
            return false
        }
    }

    abstract fun getChar(): Char
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
            //if(checkForCheck(false)){
            //    moveStack.peek().stringRep = moveStack.peek().stringRep + " +"
            //}
            t.piece = this
            this.tile.piece = null
            prevMove.newTile.piece = null
            this.tile = t
            deselectPossibleMoves(true)
            return
        }
        super.move(t)

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
                    if(this.graphic == R.drawable.white_pawn){
                        t.piece = Rook(R.drawable.white_rook,t,board,playerControlled,moveStack)
                    }
                    else{
                        t.piece = Rook(R.drawable.black_rook,t,board,playerControlled,moveStack)
                    }
                }
            }
            moveStack.peek().stringRep = moveStack.peek().stringRep+"="+ t.piece!!.getChar()
        }
        deselectPossibleMoves(true)
    }

    override fun select() {
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
    override fun move(t: GameActivity.Tile) {

    }

    override fun select() {
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

    override fun select() {
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
        if(t.x-x == 2){
            if(7-x == 3){
                moveStack.push(GameActivity.Move(this.tile,this,board[y][7].piece!!,t,"0-0"))
                t.piece = this
                this.tile.piece = null
                this.tile = t
                board[y][7].piece!!.tile = board[y][x+1]
                board[y][x+1].piece = board[y][7].piece
                board[y][7].piece = null
            }

        }

        super.move(t)
    }

    override fun select() {
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
            if(board[y][0].piece is Rook){
                if(!(board[y][0].piece as Rook).hasMoved){
                    var possibleLeft = true
                    for(i in 1..(x-1)){
                        if(board[y][i].piece != null){
                            possibleLeft = false
                            break
                        }
                        else{
                            if(checkForControl(board[y][0])){
                                possibleLeft = false
                                break
                            }
                        }
                    }
                    if(possibleLeft){
                        possibleMoves.add(board[y][x-2])
                    }
                }
            }
            if(board[y][7].piece is Rook){
                if(!(board[y][7].piece as Rook).hasMoved){
                    var possibleRight = true
                    for(i in (x+1)..7){
                        if(board[y][i].piece != null){
                            possibleRight = false
                            break
                        }
                        else{
                            if(checkForControl(board[y][0])){
                                possibleRight = false
                                break
                            }
                        }
                    }
                    if(possibleRight){
                        possibleMoves.add(board[y][x+2])
                    }
                }
            }
        }
        for (t in possibleMoves) {
            t.possibleMove = true
            t.update()
        }

    }

    override fun getChar(): Char {
        return 'K'
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

    override fun select() {
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

    override fun select() {
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

}