package com.example.smartchess

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
    val white:Boolean
) {
    abstract fun move(t: GameActivity.Tile)
    abstract fun select()
    fun deselectPossibleMoves() {
        for (line in board) {
            for (t in line) {
                t.possibleMove = false
                t.update()
            }
        }
    }
    fun getEnemyPieces(): ArrayList<Piece> {
        val enemyPiecesArray = ArrayList<Piece>()
        for(line in board){
            for(t in board){

            }
        }
        return enemyPiecesArray
    }
    abstract fun getChar():Char
}

class Pawn(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>, white: Boolean
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack, white
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
            t.piece = this
            this.tile.piece = null
            prevMove.newTile.piece = null
            this.tile = t
            deselectPossibleMoves()
            return
        }
        if(t.piece!=null){

            moveStack.push(
                GameActivity.Move(
                    tile,
                    this,
                    t.piece!!,
                    t,
                    "" + this.getChar() + "x" + ('a'.plus(t.x)) + "" + (t.y + 1).toString()
                )
            )
        }
        else{
            moveStack.push(
                GameActivity.Move(
                tile,
                this,
                t,
                ""+this.getChar()+ (t.y + 1).toString()
            ))
        }
        tile.deselect()
        t.piece = this
        this.tile.piece = null
        this.tile = t
        hasMoved = true
        deselectPossibleMoves()
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
            println(lastMove)
            println(lastMove.piece is Pawn)
            println(abs(lastMove.newTile.x - x)==1)
            println(abs(lastMove.newTile.y-lastMove.startTile.y)==2)
            if (lastMove.piece is Pawn && abs(lastMove.newTile.x - x) == 1 && abs(lastMove.newTile.y - lastMove.startTile.y) == 2) {
                val pawn = lastMove.piece
                println(y==5)
                println(playerControlled)
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
    moveStack: Stack<GameActivity.Move>, white: Boolean
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack, white
    ) {
    override fun move(t: GameActivity.Tile) {
        //TODO("Not yet implemented")
    }

    override fun select() {
        TODO("Not yet implemented")
    }

    override fun getChar(): Char {
        TODO("Not yet implemented")
    }
}

class Knight(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>, white: Boolean
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack, white
    ) {
    override fun move(t: GameActivity.Tile) {
        //TODO("Not yet implemented")
    }

    override fun select() {
        val x = tile.x
        val y = tile.y

    }

    override fun getChar(): Char {
        TODO("Not yet implemented")
    }

}

class King(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>, white: Boolean
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack, white
    ) {
    override fun move(t: GameActivity.Tile) {
        //TODO("Not yet implemented")
    }

    override fun select() {
        TODO("Not yet implemented")
    }

    override fun getChar(): Char {
        TODO("Not yet implemented")
    }
}

class Queen(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>, white: Boolean
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack, white
    ) {
    override fun move(t: GameActivity.Tile) {
        //TODO("Not yet implemented")
    }

    override fun select() {
        TODO("Not yet implemented")
    }

    override fun getChar(): Char {
        TODO("Not yet implemented")
    }

}

class Rook(
    graphic: Int,
    tile: GameActivity.Tile,
    board: Array<Array<GameActivity.Tile>>,
    playerControlled: Boolean,
    moveStack: Stack<GameActivity.Move>, white: Boolean
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack, white
    ) {
    override fun move(t: GameActivity.Tile) {
        //TODO("Not yet implemented")
    }

    override fun select() {
        TODO("Not yet implemented")
    }

    override fun getChar(): Char {
        TODO("Not yet implemented")
    }

}