package minesweeper

import kotlin.random.Random

fun main() {
    println("How many mines do you want on the field?")
    print("> ")
    val mines = readln().toInt()
    val size = 9
    val playerField = Array(size) { CharArray(size) { '.' } }
    val mineField = Array(size) { CharArray(size) { '.' } }
    val numbersField = Array(size) { CharArray(size) { '.' } }
    var firstMove = true

    printField(playerField)

    while (true) {
        println("Set/unset mine marks or claim a cell as free:")
        val input = readln().trim().split(" ")
        if (!isValidInput(input, size)) {
            println("Invalid input")
            continue
        }
        // Leitura corrigida: Coluna, Linha
        val (y, x) = input.take(2).map { it.toInt() - 1 }
        val action = input[2]

        if (firstMove && action == "free") {
            placeMines(mineField, numbersField, mines, x, y, size)
            firstMove = false
        }

        when (action) {
            "mine" -> toggleMark(x, y, playerField)
            "free" -> {
                if (!openCell(x, y, playerField, mineField, numbersField)) {
                    revealMines(mineField, playerField, size)
                    printField(playerField)
                    println("You stepped on a mine and failed!")
                    break
                }
            }
            else -> println("Invalid input")
        }

        printField(playerField)

        if (checkWin(playerField, mineField, mines)) {
            println("Congratulations! You found all the mines!")
            break
        }
    }
}

fun checkWin(field: Array<CharArray>, mineField: Array<CharArray>, totalMines: Int): Boolean {
    var correctlyMarkedMines = 0
    var revealedNonMines = 0
    for (i in field.indices) {
        for (j in field[i].indices) {
            if (mineField[i][j] == 'X') {
                if (field[i][j] == '*') {
                    correctlyMarkedMines++
                }
            } else {
                if (field[i][j] in setOf('/', '1', '2', '3', '4', '5', '6', '7', '8')) {
                    revealedNonMines++
                }
            }
        }
    }
    val totalSafeCells = (field.size * field[0].size) - totalMines
    return correctlyMarkedMines == totalMines || revealedNonMines == totalSafeCells
}

fun isValidInput(input: List<String>, size: Int): Boolean {
    if (input.size != 3 || input[2] !in listOf("mine", "free") || !input.take(2).all { it.toIntOrNull() != null }) {
        return false
    }
    val (y, x) = input.take(2).map { it.toInt() - 1 }
    return (x in 0 until size && y in 0 until size)
}

fun placeMines(mineField: Array<CharArray>, numbersField: Array<CharArray>, mines: Int, exemptX: Int, exemptY: Int, size: Int) {
    var placed = 0
    while (placed < mines) {
        val row = Random.nextInt(size)
        val col = Random.nextInt(size)
        if (mineField[row][col] != 'X' && (row != exemptX || col != exemptY)) {
            mineField[row][col] = 'X'
            placed++
        }
    }
    fillNumbersField(mineField, numbersField, size)
}

fun fillNumbersField(mineField: Array<CharArray>, numbersField: Array<CharArray>, size: Int) {
    for (i in 0 until size) {
        for (j in 0 until size) {
            if (mineField[i][j] == 'X') continue
            var mineCount = 0
            for (dx in -1..1) {
                for (dy in -1..1) {
                    val newRow = i + dx
                    val newCol = j + dy
                    if (newRow in 0 until size && newCol in 0 until size && mineField[newRow][newCol] == 'X') {
                        mineCount++
                    }
                }
            }
            if (mineCount > 0) {
                numbersField[i][j] = mineCount.toString()[0]
            }
        }
    }
}

fun toggleMark(x: Int, y: Int, field: Array<CharArray>) {
    if (field[x][y] == '.') {
        field[x][y] = '*'
    } else if (field[x][y] == '*') {
        field[x][y] = '.'
    }
}

fun revealMines(mineField: Array<CharArray>, field: Array<CharArray>, size: Int) {
    for (i in 0 until size) {
        for (j in 0 until size) {
            if (mineField[i][j] == 'X') {
                field[i][j] = 'X'
            }
        }
    }
}

fun openCell(x: Int, y: Int, field: Array<CharArray>, mineField: Array<CharArray>, numbersField: Array<CharArray>): Boolean {
    if (mineField[x][y] == 'X') {
        return false
    }
    revealCell(x, y, field, numbersField)
    return true
}

fun revealCell(x: Int, y: Int, field: Array<CharArray>, numbersField: Array<CharArray>) {
    if (field[x][y] != '.' && field[x][y] != '*') return
    if (numbersField[x][y] == '.' || numbersField[x][y] in '1'..'8') {
        field[x][y] = if (numbersField[x][y] == '.') '/' else numbersField[x][y]
        if (field[x][y] == '/') {
            for (dx in -1..1) {
                for (dy in -1..1) {
                    val newX = x + dx
                    val newY = y + dy
                    if (newX in 0 until field.size && newY in 0 until field.size) {
                        revealCell(newX, newY, field, numbersField)
                    }
                }
            }
        }
    }
}

fun printField(field: Array<CharArray>) {
    println(" │123456789│")
    println("—│—————————│")
    for (i in field.indices) {
        print("${i + 1}│")
        for (j in field[i]) {
            print(j)
        }
        println("│")
    }
    println("—│—————————│")
}