package minesweeper

import java.util.Optional
import kotlin.random.Random

var inited: Optional<Boolean> = Optional.of(false)
var initialBoard: MutableList<MutableList<Char>> = mutableListOf()
var board: MutableList<MutableList<Char>> = mutableListOf()
var initialMarkedMines: MutableList<Pair<Int, Int>> = mutableListOf()

fun main() {
    fun MutableList<MutableList<Char>>.copy(): MutableList<MutableList<Char>> = copy(this)
    print("How many mines do you want on the field? ")
    val count = readln().toInt()

    initialBoard = initBoard(count)

    printBoard(initialBoard)

    board = initialBoard.copy()

    var value: String
    var a: String
    var b: String
    var opt: String
    var i: Int
    var j: Int

    var valid: Boolean
    var won = Optional.empty<Boolean>()

    print("Set/unset mines marks or claim a cell as free: ")
    value = readln()

    while (!inited.get()) {
        a = value.split(" ")[0]
        b = value.split(" ")[1]
        opt = value.split(" ")[2]
        i = b.toInt() - 1
        j = a.toInt() - 1

        if (opt == "free") {
            if (initialBoard[i][j] == 'X') {
                initialBoard = initBoard(count)
                board = initialBoard.copy()
            } /*else if (initialBoard[i][j].isDigit()) {
                initialBoard = initBoard(count)
                board = initialBoard.copy()
            }*/ else {
                inited = Optional.of(true)
            }
        } else if (opt == "mine") {
            if (board[i][j] != '*') {
                board[i][j] = '*'
                initialMarkedMines.add(Pair(i, j))
            } else {
                board[i][j] = initialBoard[i][j]
                initialMarkedMines.remove(Pair(i, j))
            }

            printBoard(board)

            print("Set/unset mines marks or claim a cell as free: ")
            value = readln()
        }


    }
    for (pair in initialMarkedMines) {
        board[pair.first][pair.second] = '*'
    }

    while (won.isEmpty) {
        if (inited.isEmpty || !inited.get()) {
            print("Set/unset mines marks or claim a cell as free: ")
            value = readln()
        }
        a = value.split(" ")[0]
        b = value.split(" ")[1]
        opt = value.split(" ")[2]
        i = b.toInt() - 1
        j = a.toInt() - 1
        valid = false
        if (opt == "free") {
            if (board[i][j].isDigit()) {
                board[i][j] = initialBoard[i][j]
                valid = true
            } else {
                board[i][j] = '/'
                valid = true
            }

        } else if (opt == "mine") {
            if (board[i][j].isDigit()) {
                println("There is a number here!")
                valid = false
            } else if (board[i][j] == '/') {
                println("This cell is already marked!")
                valid = false
            } else if (board[i][j] == '*') {
                board[i][j] = '.'
                valid = true
            } else if (board[i][j] == '.') {
                board[i][j] = '*'
                valid = true
            } else {
                board[i][j] = '*'
                valid = true
            }

        }
        /*
        if (board[i][j].isDigit()) {
            println("There is a number here!")
            valid = false
        } else if (board[i][j] == '.') {
            board[i][j] = '!'
            valid = true
        } else if (board[i][j] == '!') {
            board[i][j] = '.'
            valid = true
        } else if (board[i][j] == 'X') {
            board[i][j] = '#'
            valid = true
        } else if (board[i][j] == '#') {
            board[i][j] = 'X'
            valid = true
        }
*/
        if (valid) {
            won = controlBoard()
            /*if(revealed) {
                revealBoard()
            }*/
            inited = Optional.empty()
            printBoard(board)

        } else if (!valid) {
            println("not valid move!")
        }
        inited = Optional.empty()
    }
    if (won.get()) {
        println("Congratulations! You found all the mines!")
    } else {
        println("You stepped on a mine and failed!")
    }


}

fun copy(list: MutableList<MutableList<Char>>): MutableList<MutableList<Char>> {
    val newList = mutableListOf<MutableList<Char>>()
    for (i in list.indices) {
        newList.add(mutableListOf<Char>())
        for (j in list[i].indices) {
            newList[i].add(list[i][j])
        }
    }
    return newList

}

fun initBoard(count: Int): MutableList<MutableList<Char>> {
    val board = mutableListOf<MutableList<Char>>()

    for (i in 0 until 9) {
        board.add(mutableListOf<Char>())
        for (j in 0 until 9) {
            board[i].add('.')
        }
    }

    var num: Int
    val numbers = mutableListOf<Int>()
    for (i in 1..count) {
        num = randomize(numbers, 81)/*sequence.elementAt(i - 1)*/
        numbers.add(num)
        //print("$num ")
        board[num / 9][num % 9] = 'X'
    }
    //println()

    setDigits(board)

    return board
}

fun randomize(list: MutableList<Int>, num: Int): Int {
    var new = Random.nextInt(81)
    while (list.contains(new)) {
        new = Random.nextInt(81)
    }
    return new
}

fun setDigits(initBoard: MutableList<MutableList<Char>>) {
    var mCount: Int
    for (i in 0 until initBoard.size) {
        for (j in 0 until initBoard[i].size) {
            if (initBoard[i][j] == '.') {
                mCount = 0
                val minX = if (i == 0) 0 else i - 1
                val maxX = if (i == initBoard.size - 1) i else i + 1
                val minY = if (j == 0) 0 else j - 1
                val maxY = if (j == initBoard[i].size - 1) j else j + 1
                for (k in minX..maxX) {
                    for (l in minY..maxY) {
                        if ((k != i || l != j) && initBoard[k][l] == 'X') {
                            mCount++
                        }
                    }
                }
                if (mCount != 0) {
                    initBoard[i][j] = mCount.digitToChar()
                }
            }
        }

    }
}

fun exploreBoard(i: Int, j: Int) {
    if (explored.contains(Pair(i, j))) {
        return
    }
    println("$i,$j ")
    var mCount: Int = 0
    val minX = if (i == 0) 0 else i - 1
    val maxX = if (i == board.size - 1) i else i + 1
    val minY = if (j == 0) 0 else j - 1
    val maxY = if (j == board[i].size - 1) j else j + 1
    for (k in minX..maxX) {
        for (l in minY..maxY) {
            if ((k != i || l != j) && board[k][l] == 'X') {
                mCount++
            }
            if ((k != i || l != j) && board[k][l] != 'X' && board[k][l] != '/' && !board[k][l].isDigit()) {
                board[k][l] = '/'
                if (!remainingCells.contains(Pair(k, l))) {
                    remainingCells.add(Pair(k, l))
                }

                exploreBoard(k, l)
                if (!explored.contains(Pair(k, l))) {
                    explored.add(Pair(k, l))
                }
            }

        }
    }

    if (mCount != 0) {
        board[i][j] = mCount.digitToChar()
    }


}

val explored = mutableListOf<Pair<Int, Int>>()
var remainingCells = mutableListOf<Pair<Int, Int>>()

/**
 * true -> won
 * false -> lost
 * empty -> continue
 */
fun controlBoard(): Optional<Boolean> {
    var validated = false
    val mines = mutableListOf<Pair<Int, Int>>()
    for (i in initialBoard.indices) {
        for (j in initialBoard[i].indices) {
            if (initialBoard[i][j] == 'X') {
                mines.add(Pair(i, j))
            }
        }
    }

    //val minesString = mines.joinToString(",")
    val foundMines = mutableListOf<Pair<Int, Int>>()
    remainingCells = mutableListOf<Pair<Int, Int>>()
    for (i in board.indices) {
        for (j in board[i].indices) {
            if (board[i][j] == '/') {
                if (initialBoard[i][j] == 'X') {
                    board[i][j] = 'X'
                    return Optional.of(false)
                } else {
                    exploreBoard(i, j)
                    explored.add(Pair(i, j))
                }
                remainingCells.add(Pair(i, j))
            } else if (board[i][j] == '*') {
                //TODO: continue

                foundMines.add(Pair(i, j))
            } else if (board[i][j] == '.') {
                //remainingCells.add(Pair(i, j))
            } else if (board[i][j].isDigit()) {
                remainingCells.add(Pair(i, j))
            }
        }
    }


    if (mines.size == foundMines.size) {
        for (pair in mines) {
            if (!foundMines.contains(Pair(pair.first, pair.second))) {
                return Optional.empty()
            }
        }

        return Optional.of(true)
    } else if (mines.size == board.size * board[0].size - remainingCells.size) {
        for (pair in mines) {
            if (remainingCells.contains(Pair(pair.first, pair.second))) {
                return Optional.empty()
            }
        }

        return Optional.of(true)
    } else {
        return Optional.empty()
    }


}

fun printBoard(board: MutableList<MutableList<Char>>) {
    println(" │123456789│")
    println("—│—————————│")
    if (inited.isEmpty || inited.get()) {
        for (i in 0 until board.size) {
            println("${(i + 1)}│${board[i].joinToString("").replace('X', '.')}│")
        }
    } else {
        for (i in 0 until board.size) {
            println(
                "${(i + 1)}│${
                    //listOf('.', '.', '.', '.', '.', '.', '.', '.', '.').joinToString("")
                    board[i].joinToString("").replace('X', '.').replace("[1-9]".toRegex(), ".")
                }│"
            )
        }
    }
    println("—│—————————│")
}