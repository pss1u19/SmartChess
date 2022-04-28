package com.example.smartchess

import karballo.Board
import karballo.Config
import karballo.Move
import karballo.book.FileBook
import karballo.search.SearchParameters
import karballo.searchEngineBuilder
import karballo.util.JvmPlatformUtils
import karballo.util.Utils
import org.jetbrains.kotlinx.dl.api.core.Sequential
import org.jetbrains.kotlinx.dl.api.core.WritingMode
import org.jetbrains.kotlinx.dl.api.core.activation.Activations
import org.jetbrains.kotlinx.dl.api.core.layer.core.Dense
import org.jetbrains.kotlinx.dl.api.core.layer.core.Input
import org.jetbrains.kotlinx.dl.api.core.layer.reshaping.Flatten
import org.jetbrains.kotlinx.dl.api.core.loss.Losses
import org.jetbrains.kotlinx.dl.api.core.metric.Metrics
import org.jetbrains.kotlinx.dl.api.core.optimizer.Adam
import org.jetbrains.kotlinx.dl.api.inference.TensorFlowInferenceModel
import org.jetbrains.kotlinx.dl.dataset.mnist
import java.io.File
import java.lang.Math.abs
import java.util.*

class AI(name: String) {
    val blackPawn = arrayOf(1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    val whitePawn = arrayOf(0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f)
    val blackKnight = arrayOf(0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    val whiteKnight = arrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f)
    val blackBishop = arrayOf(0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    val whiteBishop = arrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f)
    val blackRook = arrayOf(0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    val whiteRook = arrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f)
    val blackQueen = arrayOf(0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    val whiteQueen = arrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f, 0f)
    val blackKing = arrayOf(0f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 0f)
    val whiteKing = arrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f)
    val empty = arrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    var name: String
    lateinit var model :TensorFlowInferenceModel
    lateinit var trainBoard: Array<Array<AITile>>
    var trainSet = ArrayList<Pair<String, Float>>()

    fun newModel() {
        model = Sequential.of(
            Input(8, 8, 12),
            Flatten(),
            Dense(384),
            Dense(192),
            Dense(96),
            Dense(48),
            Dense(24),
            Dense(12),
            Dense(6),
            Dense(1)
        )
    }

    fun save() {
        model.use {
            (it as Sequential).save(File("models/$name"), writingMode = WritingMode.OVERRIDE)
        }
    }

    fun load() {
        model = TensorFlowInferenceModel.load(File("models/$name")) as Sequential
    }

    init {
        this.name = name
    }

    fun train() {
        Utils.instance = JvmPlatformUtils()
        val config = Config()
        config.book = FileBook("/book_small.bin")
        var searchEngine = searchEngineBuilder(config)
        val searchParam = SearchParameters()
        searchParam.depth = 9
        val board = Board()
        board.startPosition()
        searchEngine.board.fen = board.initialFen
        for (i in 0..160) {
            searchEngine.go(searchParam)
            val t = Thread()
            t.run {
                while (true) {
                    if (!searchEngine.isSearching) {
                        board.doMove(searchEngine.bestMove)
                        searchEngine.board.fen = board.initialFen
                        searchEngine.board.doMoves(board.moves)
                        break
                    }
                }

            }
            if (searchEngine.board.isMate) {
                println(board.toString())
                println(board.moves)
                println(i.toString())
                t.stop()
                break
            }
            println(board.toString())
        }
        /*
        board.generateLegalMoves()
        println(board.generateLegalMoves())
        var i = 0
        while (board.legalMoves[i] != 0) {
            println(Move.toString(board.legalMoves[i]))
            i++
        }
        var c = CompleteEvaluator()
        board.doMove(board.legalMoves[1])
        board.generateLegalMoves()
        board.doMove(board.legalMoves[2])
        println(c.evaluate(board, ai = AttacksInfo()))
        println(board.toString())
         */
    }

    fun convertStrBoardToArray(boardString: String): FloatArray {
        val array = ArrayList<Float>()
        val b = boardString.split("\n")
        for (line in b) {
            for (c in line) {
                when (c) {
                    'p' -> {
                        array.addAll(blackPawn)
                    }
                    'P' -> {
                        array.addAll(whitePawn)
                    }
                    'r' -> {
                        array.addAll(blackRook)
                    }
                    'R' -> {
                        array.addAll(whiteRook)
                    }
                    'n' -> {
                        array.addAll(blackKnight)
                    }
                    'N' -> {
                        array.addAll(whiteKnight)
                    }
                    'b' -> {
                        array.addAll(blackBishop)
                    }
                    'B' -> {
                        array.addAll(whiteBishop)
                    }
                    'q' -> {
                        array.addAll(blackQueen)
                    }
                    'Q' -> {
                        array.addAll(whiteQueen)
                    }
                    'k' -> {
                        array.addAll(blackKing)
                    }
                    'K' -> {
                        array.addAll(whiteKing)
                    }
                    '.' -> {
                        array.addAll(empty)
                    }
                }
            }
        }
        return array.toFloatArray()
    }

    fun ABSearch(
        currentBoard: Board,
        alpha: Float,
        beta: Float,
        aiturn: Boolean,
        curentDepth: Int,
        targetDepth: Int
    ): Pair<Float, String> {
        if (curentDepth == targetDepth) {
            return Pair(eval(currentBoard), "")
        }
        val possibleBoardStates = ArrayList<Board>()
        var maxEval = -1f
        var maxStr = ""
        var minEval = 1f
        var minStr = ""
        currentBoard.generateLegalMoves()
        var moves = currentBoard.legalMoves
        var i = 0
        while (moves[i] != 0) {
            currentBoard.doMove(currentBoard.legalMoves[i])
            var newBoard = Board()
            newBoard.setFenMove(currentBoard.fen, Move.toString(moves[i]))

            possibleBoardStates.add(newBoard)
            var res = ABSearch(newBoard, alpha, beta, !aiturn, curentDepth + 1, targetDepth)
            if (res.first > maxEval) {
                maxEval = res.first
                maxStr = Move.toString(moves[i])
            }
            if (res.first < minEval) {
                minEval = res.first
                minStr = Move.toString(moves[i])
            }
            i++
        }
        if (aiturn) {
            return Pair(maxEval, maxStr)
        } else {
            return Pair(minEval, minStr)
        }
    }


    fun eval(b: Board): Float {
        model.use {
            it.reshape(8, 8, 12)
            return it.predictSoftly(convertStrBoardToArray(b.toString()))[0]
        }
    }


}

class AITile(val x: Int, val y: Int) {
    var piece: AIPiece? = null
    var possibleMove = false
}

data class AIMove(
    val startTile: AITile,
    val piece: AIPiece,
    val newTile: AITile,
    var stringRep: String
) {
    var takingMove = false
    var takenPiece: AIPiece? = null
    var castling = false
    var castlingTile: AITile? = null

    constructor (
        startTile: AITile,
        piece: AIPiece,
        takenPiece: AIPiece,
        newTile: AITile,
        stringRep: String
    ) : this(startTile, piece, newTile, stringRep) {
        this.takingMove = true
        this.takenPiece = takenPiece
    }

    constructor (
        startTile: AITile,
        piece: AIPiece,
        takenPiece: AIPiece,
        newTile: AITile,
        csTile: AITile,
        stringRep: String
    ) : this(startTile, piece, newTile, stringRep) {
        castling = true
        castlingTile = csTile
        this.takingMove = true
        this.takenPiece = takenPiece
    }
}

abstract class AIPiece(
    var tile: AITile,
    val board: Array<Array<AITile>>,
    val playerControlled: Boolean,
    val moveStack: Stack<AIMove>,
) {
    fun undo() {
        val lastMove = this.moveStack.pop()
        lastMove.piece.tile.piece = null
        lastMove.piece.tile = lastMove.startTile
        lastMove.startTile.piece = lastMove.piece
        if (lastMove.piece is AIPawn) if (lastMove.piece.hasMoved && Math.abs(
                lastMove.startTile.y - lastMove.newTile.y
            ) == 2
        ) (lastMove.piece as Pawn).hasMoved = false
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

    open fun move(t: AITile) {
        if (t.piece != null) {
            moveStack.push(
                AIMove(
                    tile,
                    this,
                    t.piece!!,
                    t,
                    "" + this.getChar() + "x" + ('a'.plus(t.x)) + "" + (t.y + 1).toString()
                )
            )
        } else {
            moveStack.push(
                AIMove(
                    tile,
                    this,
                    t,
                    "" + this.getChar() + 'a'.plus(t.x) + (t.y + 1).toString()
                )
            )
        }
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
        }
    }

    fun deselectPossibleMoves(update: Boolean) {
        for (line in board) {
            for (t in line) {
                t.possibleMove = false
            }
        }
    }

    fun getEnemyPieces(): ArrayList<AIPiece> {
        val enemyPiecesArray = ArrayList<AIPiece>()
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

    fun getAlliedPieces(): ArrayList<AIPiece> {
        val alliedPiecesArray = ArrayList<AIPiece>()
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

    fun checkForControl(tile: AITile): Boolean {
        val p = tile.piece
        tile.piece = this
        for (enemyPiece in getEnemyPieces()) {
            if (enemyPiece !is AIKing) {
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
        var king: AIKing? = null
        if (allied) {
            for (p in getAlliedPieces()) {
                if (p is AIKing) {
                    king = p
                    break
                }
            }
            for (p in getEnemyPieces()) {
                if (p !is AIKing) p.selectNoCheck()
                if (king!!.tile.possibleMove) {
                    p.deselectPossibleMoves(false)
                    return true
                }
                p.deselectPossibleMoves(false)
            }
            return false
        } else {
            for (p in getEnemyPieces()) {
                if (p is AIKing) {
                    king = p
                    break
                }
            }
            for (p in getAlliedPieces()) {
                if (p !is AIKing) p.selectNoCheck()
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
    abstract fun getPossibleMoves(): ArrayList<AITile>

}

class AIPawn(
    tile: AITile,
    board: Array<Array<AITile>>,
    playerControlled: Boolean,
    moveStack: Stack<AIMove>
) :
    AIPiece(
        tile, board,
        playerControlled, moveStack,
    ) {
    var hasMoved = false

    override fun move(t: AITile) {
        var prevMove: AIMove? = null
        var enPassant = false
        if (!moveStack.isEmpty()) {
            prevMove = moveStack.peek()
            try {
                if (prevMove.piece is AIPawn && abs(prevMove.newTile.x - tile.x) == 1 && abs(
                        prevMove.newTile.y - prevMove.startTile.y
                    ) == 2
                ) {
                    if (((tile.y == 4 && playerControlled) || (tile.y == 3 && !playerControlled)) && (t.x == prevMove.newTile.x)) {
                        enPassant = true
                    }
                }
            } catch (e: Exception) {
            }
        }
        if (enPassant) {
            moveStack.push(
                AIMove(
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
                }
            }


        } else {
            if (board[y + d][x].piece == null) {
                board[y + d][x].possibleMove = true
            }

        }
        if (!moveStack.isEmpty()) {
            val lastMove = moveStack.peek()
            if (lastMove.piece is AIPawn && abs(lastMove.newTile.x - x) == 1 && abs(lastMove.newTile.y - lastMove.startTile.y) == 2) {
                val pawn = lastMove.piece
                if ((y == 4 && playerControlled) || (y == 3 && !playerControlled)) {
                    board[y + d][pawn.tile.x].possibleMove = true
                }
            }
        }
        try {
            if (board[y + d][x + 1].piece!!.playerControlled != playerControlled) {
                board[y + d][x + 1].possibleMove = true
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + d][x - 1].piece!!.playerControlled != playerControlled) {
                board[y + d][x - 1].possibleMove = true
            }
        } catch (e: Exception) {
        }
    }

    override fun getChar(): Char {
        return 'a'.plus(tile.x)
    }

    override fun getPossibleMoves(): ArrayList<AITile> {
        val x = tile.x
        val y = tile.y
        var d = 1
        val possibleMoves = ArrayList<AITile>()
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
            if (lastMove.piece is AIPawn && abs(lastMove.newTile.x - x) == 1 && abs(lastMove.newTile.y - lastMove.startTile.y) == 2) {
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

class AIBishop(
    tile: AITile,
    board: Array<Array<AITile>>,
    playerControlled: Boolean,
    moveStack: Stack<AIMove>
) :
    AIPiece(
        tile, board,
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x + i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x - i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x + i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x - i].possibleMove = true
                }
            } catch (e: Exception) {
                break
            }
        }
    }

    override fun getChar(): Char {
        return 'B'
    }

    override fun getPossibleMoves(): ArrayList<AITile> {
        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<AITile>()
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

class AIKnight(
    tile: AITile,
    board: Array<Array<AITile>>,
    playerControlled: Boolean,
    moveStack: Stack<AIMove>
) :
    AIPiece(
        tile, board,
        playerControlled, moveStack
    ) {

    override fun selectNoCheck() {
        val x = tile.x
        val y = tile.y
        try {
            if (board[y + 2][x + 1].piece == null || board[y + 2][x + 1].piece?.playerControlled != this.playerControlled) {
                board[y + 2][x + 1].possibleMove = true
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + 2][x - 1].piece == null || board[y + 2][x - 1].piece?.playerControlled != this.playerControlled) {
                board[y + 2][x - 1].possibleMove = true
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + 1][x + 2].piece == null || board[y + 1][x + 2].piece?.playerControlled != this.playerControlled) {
                board[y + 1][x + 2].possibleMove = true
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y + 1][x - 2].piece == null || board[y + 1][x - 2].piece?.playerControlled != this.playerControlled) {
                board[y + 1][x - 2].possibleMove = true
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 2][x + 1].piece == null || board[y - 2][x + 1].piece?.playerControlled != this.playerControlled) {
                board[y - 2][x + 1].possibleMove = true
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 2][x - 1].piece == null || board[y - 2][x - 1].piece?.playerControlled != this.playerControlled) {
                board[y - 2][x - 1].possibleMove = true
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 1][x + 2].piece == null || board[y - 1][x + 2].piece?.playerControlled != this.playerControlled) {
                board[y - 1][x + 2].possibleMove = true
            }
        } catch (e: Exception) {
        }
        try {
            if (board[y - 1][x - 2].piece == null || board[y - 1][x - 2].piece?.playerControlled != this.playerControlled) {
                board[y - 1][x - 2].possibleMove = true
            }
        } catch (e: Exception) {
        }

    }

    override fun getChar(): Char {
        return 'N'
    }

    override fun getPossibleMoves(): ArrayList<AITile> {
        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<AITile>()
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

class AIKing(
    tile: AITile,
    board: Array<Array<AITile>>,
    playerControlled: Boolean,
    moveStack: Stack<AIMove>
) :
    AIPiece(
        tile, board,
        playerControlled, moveStack
    ) {
    var hasMoved = false
    override fun move(t: AITile) {
        val x = tile.x
        val y = tile.y
        if (t.x - x == 2) {
            if (7 - x == 3) {
                moveStack.push(
                    AIMove(
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
                    AIMove(
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
                    AIMove(
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
                    AIMove(
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

    override fun getPossibleMoves(): ArrayList<AITile> {
        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<AITile>()
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
            if (board[y][0].piece is AIRook) {
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
            if (board[y][7].piece is AIRook) {
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

class AIQueen(
    graphic: Int,
    tile: AITile,
    board: Array<Array<AITile>>,
    playerControlled: Boolean,
    moveStack: Stack<AIMove>
) :
    AIPiece(
        tile, board,
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x + i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x - i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x + i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x - i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x + i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x - i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x].possibleMove = true
                }
            } catch (e: Exception) {
                break
            }
        }
    }

    override fun getChar(): Char {
        return 'Q'
    }

    override fun getPossibleMoves(): ArrayList<AITile> {

        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<AITile>()
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

class AIRook(
    tile: AITile,
    board: Array<Array<AITile>>,
    playerControlled: Boolean,
    moveStack: Stack<AIMove>
) :
    AIPiece(
        tile, board,
        playerControlled, moveStack
    ) {
    var hasMoved = false
    override fun move(t: AITile) {
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x + i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y][x - i].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y - i][x].possibleMove = true
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
                        break
                    } else {
                        break
                    }
                } else {
                    board[y + i][x].possibleMove = true
                }
            } catch (e: Exception) {
                break
            }
        }
    }

    override fun getChar(): Char {
        return 'R'
    }

    override fun getPossibleMoves(): ArrayList<AITile> {
        val x = tile.x
        val y = tile.y
        val possibleMoves = ArrayList<AITile>()
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


fun main() {
    val (train, test) = mnist()
    /*
    val model2 = TensorFlowInferenceModel.load(File("res/models")).use {
        it.reshape(28,28,1)
        val prediction = it.predict(test.getX(14))
        val tr = test.getY(20)
        println(""+it.predict(test.getX(20))+"  "+tr +"  "+test.x[20])
        println(test.x[20][10].javaClass.toGenericString())

    }
println(model2.javaClass.toGenericString())
*/

    val model = Sequential.of(
        Input(28, 28, 1),
        Flatten(),
        Dense(100),
        Dense(50),
        Dense(10, activation = Activations.Tanh)
    )
    model.use {
        it.compile(
            optimizer = Adam(),
            loss = Losses.MSE,
            metric = Metrics.ACCURACY
        )
        it.fit(dataset = train, epochs = 1)
        println(it.predictSoftly(test.getX(1)))
        for (i in it.predictSoftly(test.getX(1))) {
            println(i)
        }
        println(test.getY(1))
        println(it.predict(test.getX(1)))
        println(it.predictAndGetActivations(test.getX(1)).second[1])
        //it.fit(dataset = train, epochs = 40, batchSize = 100)
    }
}
