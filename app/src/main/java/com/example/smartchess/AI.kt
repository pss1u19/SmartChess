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
import org.jetbrains.kotlinx.dl.dataset.OnHeapDataset
import org.jetbrains.kotlinx.dl.dataset.mnist
import java.io.File

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
    lateinit var model: Sequential


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
            Dense(1, activation = Activations.Tanh)
        )

        model.compile(Adam(),Losses.MSE,Metrics.ACCURACY)
    }

    fun save() {
        model.use {
            it.save(File("models/$name"), writingMode = WritingMode.OVERRIDE)
        }
    }

    fun load() {
        model = TensorFlowInferenceModel.load(File("models/$name")) as Sequential
        model.compile(Adam(),Losses.MSE,Metrics.ACCURACY)
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
        searchParam.depth = 8
        val board = Board()
        board.startPosition()
        searchEngine.board.fen = board.initialFen
        var trainingArrayList = ArrayList<FloatArray>()
        var answers = ArrayList<Float>()
        var aiwon = false
        var adjustment = 0.05f
        for (i in 0..150) {
            searchEngine.go(searchParam)
            while (true) {
                if (!searchEngine.isSearching) {
                    board.doMove(searchEngine.bestMove)
                    searchEngine.board.fen = board.initialFen
                    val compMove = GetBestMove(board,false)
                    board.doMoves(compMove.second)
                    trainingArrayList.add(convertStrBoardToArray(board.toString()))
                    answers.add(compMove.first)
                    searchEngine.board.doMoves(board.moves)

                    break
                }
            }
            if (searchEngine.board.isMate||searchEngine.board.isDraw) {
                if(!board.turn){aiwon = true}
                println(board.toString())
                println(board.moves)
                println(i.toString())
                break
            }
            println(board.toString())
        }
        if(!aiwon){
            try{
            for(i in 0..answers.size){
                answers[i] = answers[i]+adjustment
            }}catch (e:Exception){}
        }
        var trainingArray = Array(trainingArrayList.size){ i->trainingArrayList[i]}
        var trainingAnswers = FloatArray(answers.size){ i->answers[i]}
        var trainingDataset = OnHeapDataset.create(trainingArray,trainingAnswers)

        model.fit(trainingDataset)

        board.startPosition()
        searchEngine.board.fen = board.initialFen
         trainingArrayList = ArrayList<FloatArray>()
         answers = ArrayList<Float>()
         aiwon = false
         adjustment = 0.05f
        val compMove = GetBestMove(board,false)
        board.doMoves(compMove.second)
        trainingArrayList.add(convertStrBoardToArray(board.toString()))
        answers.add(compMove.first)
        searchEngine.board.doMoves(board.moves)
        for (i in 0..150) {
            searchEngine.go(searchParam)
            while (true) {
                if (!searchEngine.isSearching) {
                    board.doMove(searchEngine.bestMove)
                    searchEngine.board.fen = board.initialFen
                    val compMove = GetBestMove(board,false)
                    board.doMoves(compMove.second)
                    trainingArrayList.add(convertStrBoardToArray(board.toString()))
                    answers.add(compMove.first)
                    searchEngine.board.doMoves(board.moves)

                    break
                }
            }
            if (searchEngine.board.isMate||searchEngine.board.isDraw) {
                if(!board.turn){aiwon = false}
                println(board.toString())
                println(board.moves)
                println(i.toString())
                break
            }
            println(board.toString())
        }
        if(!aiwon){
            try{
                for(i in 0..answers.size){
                    answers[i] = answers[i]+adjustment
                }}catch (e:Exception){}
        }
         trainingArray = Array(trainingArrayList.size){i->trainingArrayList[i]}
         trainingAnswers = FloatArray(answers.size){i->answers[i]}
         trainingDataset = OnHeapDataset.create(trainingArray,trainingAnswers)

        model.fit(trainingDataset)


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

    fun GetBestMove(
        currentBoard: Board,
        whiteturn: Boolean,
    ): Pair<Float, String> {
        var maxEval = -1f
        var maxStr = ""
        var minEval = 1f
        var minStr = ""
        currentBoard.generateLegalMoves()
        val moves = currentBoard.legalMoves
        var i = 0
        while (moves[i] != 0) {
            currentBoard.doMove(currentBoard.legalMoves[i])

            val res = Pair(eval(currentBoard),Move.toString(moves[i]))
            if (res.first > maxEval) {
                maxEval = res.first
                maxStr = Move.toString(moves[i])
            }
            if (res.first < minEval) {
                minEval = res.first
                minStr = Move.toString(moves[i])
            }
            i++
            currentBoard.undoMove()
        }
        if (whiteturn) {
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
        println(train.javaClass.toGenericString())
        println(train.x.javaClass.toGenericString())
        println(train.y.javaClass.toGenericString())
        println(train.x[1].javaClass.toGenericString())
        //it.fit(dataset = train, epochs = 40, batchSize = 100)
    }
}
