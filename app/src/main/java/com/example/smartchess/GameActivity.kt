package com.example.smartchess

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import karballo.Board
import karballo.Config
import karballo.Move
import karballo.book.FileBook
import karballo.search.SearchEngine
import karballo.search.SearchParameters
import karballo.searchEngineBuilder
import karballo.util.JvmPlatformUtils
import karballo.util.Utils
import java.util.*

class GameActivity : AppCompatActivity() {
    lateinit var board: Array<Array<Tile>>
    val moveStack = Stack<Move>()
    lateinit var moveDisplay: TextView
    var selected: Tile? = null
    lateinit var promotionSelector: Spinner
    var won = false
    lateinit var boardEng: Board
    lateinit var search: SearchEngine
    var hasAi = false
    lateinit var AiNN: AI
    var aiNoLoad = false
    var PlayerIsWhite: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        promotionSelector = findViewById(R.id.promotionSpinner)


        Utils.instance = JvmPlatformUtils()
        boardEng = Board()
        boardEng.startPosition()
        val config = Config()
        config.book = FileBook("/book_small.bin")
        val searchParam = SearchParameters()
        searchParam.depth = 8
        search = searchEngineBuilder(config)
        PlayerIsWhite = !(intent.extras?.get("Colour") as Boolean)
        val AIid = intent.extras?.get("AI") as Int
        if (AIid != 0) {
            try {
                AiNN = AI("ai" + AIid)
                AiNN.load()
                hasAi = true
            } catch (e: Error) {aiNoLoad = true
            } catch (e: Exception) {
            }
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.pieces,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            promotionSelector.adapter = adapter
        }
        moveDisplay = findViewById(R.id.moveDisplay)
        moveDisplay.text = "Previous moves: "
        val undoButton = findViewById<Button>(R.id.undoButton)
        undoButton.setOnClickListener {
            if (!won) {
                val moveStackSize = moveStack.size
                if (selected != null) {
                    selected!!.deselect()
                    selected = null
                }
                if (moveStackSize > 1) {
                    for (i in 0..1) {
                        moveStack.peek().piece.undo()
                        boardEng.undoMove()
                    }
                    updateMoveDisplay()
                }
            }
        }
        assignTiles()
        placePieces()
        assignClickListener()
        if (PlayerIsWhite && hasAi) {
            val aimove = AiNN.GetBestMove(boardEng, !PlayerIsWhite)
            boardEng.doMoves(aimove.second)
            val startColumn = (aimove.second[0] - 'a').toInt()
            val startLine = (aimove.second[1] + "").toInt() - 1
            val endColumn = (aimove.second[2] - 'a').toInt()
            val endLine = (aimove.second[3] + "").toInt() - 1
            board[startLine][startColumn].piece?.move(board[endLine][endColumn])
        }
    }

    fun updateMoveDisplay() {
        moveDisplay.text = "Previous moves: "
        val stack = Stack<Move>()
        while (!moveStack.isEmpty() && stack.size < 4) {
            moveDisplay.text = (moveDisplay.text as String + " " + moveStack.peek().stringRep)
            stack.push(moveStack.pop())
        }
        while (!stack.isEmpty()) {
            moveStack.push(stack.pop())
        }
    }

    fun placePieces() {
        if (PlayerIsWhite) {
            board[0][0].piece =
                Rook(R.drawable.white_rook, board[0][0], board, true, moveStack)
            board[0][7].piece =
                Rook(R.drawable.white_rook, board[0][7], board, true, moveStack)
            board[0][1].piece =
                Knight(R.drawable.white_knight, board[0][1], board, true, moveStack)
            board[0][6].piece =
                Knight(R.drawable.white_knight, board[0][6], board, true, moveStack)
            board[0][2].piece =
                Bishop(R.drawable.white_bishop, board[0][2], board, true, moveStack)
            board[0][5].piece =
                Bishop(R.drawable.white_bishop, board[0][5], board, true, moveStack)
            board[0][3].piece =
                Queen(R.drawable.white_queen, board[0][3], board, true, moveStack)
            board[0][4].piece =
                King(R.drawable.white_king, board[0][4], board, true, moveStack)
            for (t in board[1]) {
                t.piece = Pawn(R.drawable.white_pawn, t, board, true, moveStack, promotionSelector)
            }
            board[7][0].piece =
                Rook(R.drawable.black_rook, board[7][0], board, false, moveStack)
            board[7][7].piece =
                Rook(R.drawable.black_rook, board[7][7], board, false, moveStack)
            board[7][1].piece =
                Knight(R.drawable.black_knight, board[7][1], board, false, moveStack)
            board[7][6].piece =
                Knight(R.drawable.black_knight, board[7][6], board, false, moveStack)
            board[7][2].piece =
                Bishop(R.drawable.black_bishop, board[7][2], board, false, moveStack)
            board[7][5].piece =
                Bishop(R.drawable.black_bishop, board[7][5], board, false, moveStack)
            board[7][3].piece =
                Queen(R.drawable.black_queen, board[7][3], board, false, moveStack)
            board[7][4].piece =
                King(R.drawable.black_king, board[7][4], board, false, moveStack)
            for (t in board[6]) {
                t.piece = Pawn(R.drawable.black_pawn, t, board, false, moveStack, promotionSelector)
            }
            for (t in board[0]) t.update()
            for (t in board[1]) t.update()
            for (t in board[6]) t.update()
            for (t in board[7]) t.update()
        } else {
            findViewById<TextView>(R.id.column1).text = "h"
            findViewById<TextView>(R.id.column2).text = "g"
            findViewById<TextView>(R.id.column3).text = "f"
            findViewById<TextView>(R.id.column4).text = "e"
            findViewById<TextView>(R.id.column5).text = "d"
            findViewById<TextView>(R.id.column6).text = "c"
            findViewById<TextView>(R.id.column7).text = "b"
            findViewById<TextView>(R.id.column8).text = "a"
            findViewById<TextView>(R.id.row1).text = "8"
            findViewById<TextView>(R.id.row2).text = "7"
            findViewById<TextView>(R.id.row3).text = "6"
            findViewById<TextView>(R.id.row4).text = "5"
            findViewById<TextView>(R.id.row5).text = "4"
            findViewById<TextView>(R.id.row6).text = "3"
            findViewById<TextView>(R.id.row7).text = "2"
            findViewById<TextView>(R.id.row8).text = "1"

            board[7][0].piece =
                Rook(R.drawable.white_rook, board[7][0], board, false, moveStack)
            board[7][7].piece =
                Rook(R.drawable.white_rook, board[7][7], board, false, moveStack)
            board[7][1].piece =
                Knight(R.drawable.white_knight, board[7][1], board, false, moveStack)
            board[7][6].piece =
                Knight(R.drawable.white_knight, board[7][6], board, false, moveStack)
            board[7][2].piece =
                Bishop(R.drawable.white_bishop, board[7][2], board, false, moveStack)
            board[7][5].piece =
                Bishop(R.drawable.white_bishop, board[7][5], board, false, moveStack)
            board[7][4].piece =
                Queen(R.drawable.white_queen, board[7][4], board, false, moveStack)
            board[7][3].piece =
                King(R.drawable.white_king, board[7][3], board, false, moveStack)
            for (t in board[6]) {
                t.piece = Pawn(R.drawable.white_pawn, t, board, false, moveStack, promotionSelector)
            }
            board[0][0].piece =
                Rook(R.drawable.black_rook, board[0][0], board, true, moveStack)
            board[0][7].piece =
                Rook(R.drawable.black_rook, board[0][7], board, true, moveStack)
            board[0][1].piece =
                Knight(R.drawable.black_knight, board[0][1], board, true, moveStack)
            board[0][6].piece =
                Knight(R.drawable.black_knight, board[0][6], board, true, moveStack)
            board[0][2].piece =
                Bishop(R.drawable.black_bishop, board[0][2], board, true, moveStack)
            board[0][5].piece =
                Bishop(R.drawable.black_bishop, board[0][5], board, true, moveStack)
            board[0][4].piece =
                Queen(R.drawable.black_queen, board[0][4], board, true, moveStack)
            board[0][3].piece =
                King(R.drawable.black_king, board[0][3], board, true, moveStack)
            for (t in board[1]) {
                t.piece = Pawn(R.drawable.black_pawn, t, board, true, moveStack, promotionSelector)
            }
            for (t in board[0]) t.update()
            for (t in board[1]) t.update()
            for (t in board[6]) t.update()
            for (t in board[7]) t.update()
        }

    }

    fun getTile(v: View): Tile? {
        for (i in 0..7) {
            for (j in 0..7) {
                if (board[i][j].button.id == v.id) return board[i][j]
            }
        }
        return null
    }

    fun assignClickListener() {
        for (i in 0..7) {
            for (j in 0..7) {
                board[i][j].button.setOnClickListener {
                    if (!won) {
                        val tile = getTile(it)
                        when (selected) {
                            tile -> {
                                tile!!.deselect()
                                selected = null
                            }
                            null -> {
                                if (tile!!.piece != null) {
                                    if (moveStack.isEmpty()) {
                                        if (tile.piece!!.playerControlled != PlayerIsWhite) {
                                            tile.select()
                                            selected = tile
                                            selected!!.update()
                                        }
                                    } else if ((tile.piece!!.playerControlled && hasAi) || (moveStack.peek().piece.playerControlled != tile.piece!!.playerControlled)) {
                                        tile.select()
                                        selected = tile
                                        selected!!.update()
                                    }
                                }
                            }
                            else -> {
                                if (tile?.possibleMove == true) {
                                    selected!!.piece?.move(tile)
                                    val lastmove = moveStack.peek()
                                    val Line = lastmove.startTile.y + 1
                                    val Column = 'a'.plus(lastmove.startTile.x)
                                    boardEng.doMoves("" + Column + Line + ('a'.plus(tile.x)) + (tile.y + 1))
                                    selected!!.deselect()
                                    selected = null
                                    updateMoveDisplay()
                                    if (moveStack.peek().takingMove) {
                                        val piece = moveStack.peek().takenPiece
                                        if (piece is King) {
                                            won = true
                                            moveDisplay.textSize = 42f
                                            if (intent.extras!!.get("Colour") as Boolean) {
                                                if (piece.playerControlled) {
                                                    moveDisplay.text = "        White won!!!"
                                                } else {
                                                    moveDisplay.text = "        Black won!!!"
                                                }
                                            } else {
                                                if (piece.playerControlled) {
                                                    moveDisplay.text = "        Black won!!!"
                                                } else {
                                                    moveDisplay.text = "        White won!!!"
                                                }
                                            }
                                        }
                                    }
                                    if (hasAi&&!aiNoLoad) {
                                        val aimove = AiNN.GetBestMove(boardEng, !PlayerIsWhite)
                                        boardEng.doMoves(aimove.second)
                                        val startColumn = (aimove.second[0] - 'a').toInt()
                                        val startLine = (aimove.second[1] + "").toInt() - 1
                                        val endColumn = (aimove.second[2] - 'a').toInt()
                                        val endLine = (aimove.second[3] + "").toInt() - 1
                                        board[startLine][startColumn].piece?.move(board[endLine][endColumn])
                                    }else if(hasAi){
                                        val searchParameters = SearchParameters()
                                        searchParameters.depth = 9
                                        search.board.initialFen = boardEng.initialFen
                                        search.board.doMoves(boardEng.moves)
                                        search.go(searchParameters)
                                        val move = karballo.Move.toString(search.bestMove)
                                        val startColumn = (move[0] - 'a').toInt()
                                        val startLine = (move[1] + "").toInt() - 1
                                        val endColumn = (move[2] - 'a').toInt()
                                        val endLine = (move[3] + "").toInt() - 1
                                        board[startLine][startColumn].piece?.move(board[endLine][endColumn])
                                    }
                                    moveStack.peek().piece.deselectPossibleMoves(true)



                                } else {
                                    selected!!.deselect()
                                    selected = null
                                }

                            }
                        }

                    }
                }
            }
        }
    }

    fun assignTiles() {
        board = arrayOf(
            arrayOf(
                Tile(findViewById(R.id.t00), 0, 0),
                Tile(findViewById(R.id.t01), 1, 0),
                Tile(findViewById(R.id.t02), 2, 0),
                Tile(findViewById(R.id.t03), 3, 0),
                Tile(findViewById(R.id.t04), 4, 0),
                Tile(findViewById(R.id.t05), 5, 0),
                Tile(findViewById(R.id.t06), 6, 0),
                Tile(findViewById(R.id.t07), 7, 0)
            ),
            arrayOf(
                Tile(findViewById(R.id.t10), 0, 1),
                Tile(findViewById(R.id.t11), 1, 1),
                Tile(findViewById(R.id.t12), 2, 1),
                Tile(findViewById(R.id.t13), 3, 1),
                Tile(findViewById(R.id.t14), 4, 1),
                Tile(findViewById(R.id.t15), 5, 1),
                Tile(findViewById(R.id.t16), 6, 1),
                Tile(findViewById(R.id.t17), 7, 1)
            ),
            arrayOf(
                Tile(findViewById(R.id.t20), 0, 2),
                Tile(findViewById(R.id.t21), 1, 2),
                Tile(findViewById(R.id.t22), 2, 2),
                Tile(findViewById(R.id.t23), 3, 2),
                Tile(findViewById(R.id.t24), 4, 2),
                Tile(findViewById(R.id.t25), 5, 2),
                Tile(findViewById(R.id.t26), 6, 2),
                Tile(findViewById(R.id.t27), 7, 2)
            ),
            arrayOf(
                Tile(findViewById(R.id.t30), 0, 3),
                Tile(findViewById(R.id.t31), 1, 3),
                Tile(findViewById(R.id.t32), 2, 3),
                Tile(findViewById(R.id.t33), 3, 3),
                Tile(findViewById(R.id.t34), 4, 3),
                Tile(findViewById(R.id.t35), 5, 3),
                Tile(findViewById(R.id.t36), 6, 3),
                Tile(findViewById(R.id.t37), 7, 3)
            ),
            arrayOf(
                Tile(findViewById(R.id.t40), 0, 4),
                Tile(findViewById(R.id.t41), 1, 4),
                Tile(findViewById(R.id.t42), 2, 4),
                Tile(findViewById(R.id.t43), 3, 4),
                Tile(findViewById(R.id.t44), 4, 4),
                Tile(findViewById(R.id.t45), 5, 4),
                Tile(findViewById(R.id.t46), 6, 4),
                Tile(findViewById(R.id.t47), 7, 4)
            ),
            arrayOf(
                Tile(findViewById(R.id.t50), 0, 5),
                Tile(findViewById(R.id.t51), 1, 5),
                Tile(findViewById(R.id.t52), 2, 5),
                Tile(findViewById(R.id.t53), 3, 5),
                Tile(findViewById(R.id.t54), 4, 5),
                Tile(findViewById(R.id.t55), 5, 5),
                Tile(findViewById(R.id.t56), 6, 5),
                Tile(findViewById(R.id.t57), 7, 5)
            ),
            arrayOf(
                Tile(findViewById(R.id.t60), 0, 6),
                Tile(findViewById(R.id.t61), 1, 6),
                Tile(findViewById(R.id.t62), 2, 6),
                Tile(findViewById(R.id.t63), 3, 6),
                Tile(findViewById(R.id.t64), 4, 6),
                Tile(findViewById(R.id.t65), 5, 6),
                Tile(findViewById(R.id.t66), 6, 6),
                Tile(findViewById(R.id.t67), 7, 6)
            ),
            arrayOf(
                Tile(findViewById(R.id.t70), 0, 7),
                Tile(findViewById(R.id.t71), 1, 7),
                Tile(findViewById(R.id.t72), 2, 7),
                Tile(findViewById(R.id.t73), 3, 7),
                Tile(findViewById(R.id.t74), 4, 7),
                Tile(findViewById(R.id.t75), 5, 7),
                Tile(findViewById(R.id.t76), 6, 7),
                Tile(findViewById(R.id.t77), 7, 7)
            )
        )
    }

    class Tile(val button: Button, val x: Int, val y: Int) {
        var selected = false
        var piece: Piece? = null
        var possibleMove = false
        fun deselect() {
            selected = false
            piece?.deselectPossibleMoves(true)
        }

        fun select() {
            selected = true
            piece?.select()
        }

        fun update() {
            if (piece == null) {
                if (possibleMove) {
                    button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.circle, 0, 0)
                } else {
                    button.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                }

            } else {
                if (selected) button.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    R.drawable.select_circle,
                    0,
                    piece!!.graphic
                )
                else {
                    if (possibleMove) button.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        piece!!.graphic,
                        0,
                        R.drawable.under_circle
                    )
                    else button.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        piece!!.graphic,
                        0,
                        0
                    )
                }
            }
        }

    }

    data class Move(
        val startTile: Tile,
        val piece: Piece,
        val newTile: Tile,
        var stringRep: String
    ) {
        var takingMove = false
        var takenPiece: Piece? = null
        var castling = false
        var castlingTile: Tile? = null

        constructor (
            startTile: Tile,
            piece: Piece,
            takenPiece: Piece,
            newTile: Tile,
            stringRep: String
        ) : this(startTile, piece, newTile, stringRep) {
            this.takingMove = true
            this.takenPiece = takenPiece
        }

        constructor (
            startTile: Tile,
            piece: Piece,
            takenPiece: Piece,
            newTile: Tile,
            csTile: Tile?,
            stringRep: String
        ) : this(startTile, piece, newTile, stringRep) {
            castling = true
            castlingTile = csTile
            this.takingMove = true
            this.takenPiece = takenPiece
        }
    }
}