package se.sigtunanaturskola.mobilskattjakt.util


class Game(val data: DataProvider) {

    lateinit var animalsLeft: MutableList<Int>
    var nextAnimal: Int? = null

    fun initialize(): Game {
        animalsLeft = data.coordDataMap.keys.toMutableList()
        animalsLeft.shuffle()
        approveAnimal()
        return this
    }

    fun isNextAnimal(animalId: Int): Boolean {
        return nextAnimal == animalId
    }

    fun approveAnimal() {
        nextAnimal = animalsLeft.getOrNull(0)
        if (!isWinner()) animalsLeft = animalsLeft.drop(1).toMutableList()
        saveGame()
    }

    fun isWinner(): Boolean {
        return nextAnimal == null
    }

    override fun toString(): String {
        return "animals: $animalsLeft ; nextAnimal: ${nextAnimal.toString()}"
    }

    fun saveGame() {
        data.saveGameStatus(this)
    }

    fun loadGameData(): Game {
        data.loadGameStatus()
        animalsLeft = data.gameAnimalsLeft
        nextAnimal = data.gameNextAnimal
        return this
    }


}