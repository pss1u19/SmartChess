package com.example.smartchess

import java.lang.Exception
import java.lang.Integer.max
import java.lang.Integer.min
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
    abstract fun move(t: GameActivity.Tile)
    abstract fun select()
    fun deselectPossibleMoves(update:Boolean) {
        for (line in board) {
            for (t in line) {
                t.possibleMove = false
                if(update)t.update()
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
    fun checkForControl(tile:GameActivity.Tile):Boolean{
        val p = tile.piece
        tile.piece = this
        for(enemyPiece in getEnemyPieces()){
            enemyPiece.select()
            if(tile.possibleMove){
                enemyPiece.deselectPossibleMoves(false)
                tile.piece=p
                return true
            }
        }
        tile.piece = p
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
                p.select()
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
                p.select()
                if (king!!.tile.possibleMove) {
                    p.deselectPossibleMoves(false)
                    return true
                }
                p.deselectPossibleMoves(false)
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
                    "" + this.getChar() + (t.y + 1).toString()
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
        hasMoved = true
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
                println(y == 5)
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
    moveStack: Stack<GameActivity.Move>
) :
    Piece(
        graphic, tile, board,
        playerControlled, moveStack
    ) {
    override fun move(t: GameActivity.Tile) {
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

    override fun select() {
        val x = tile.x
        val y = tile.y
        for (i in 1..6) {
            try {
                println("a")
                if (board[y + i][x + i].piece != null) {
                    println("b")
                    if (board[y + i][x + i].piece!!.playerControlled != playerControlled) {
                        println("c")
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
    override fun move(t: GameActivity.Tile) {
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
    override fun move(t: GameActivity.Tile) {
        //TODO("Not yet implemented")
    }

    override fun select() {
        TODO("Not yet implemented")
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
    override fun move(t: GameActivity.Tile) {
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
    override fun move(t: GameActivity.Tile) {
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