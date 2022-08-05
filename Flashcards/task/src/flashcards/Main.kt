package flashcards

fun main() {

    println("Input the number of cards:")
    val numberOfCards = readln().toInt()
    val cards = mutableMapOf<String, String>()

    var count = 1
    repeat(numberOfCards) {

        println("Card #$count:")
        val card = readln()
        println("The definition for card #$count:")
        val definition = readln()

        cards[card] = definition

        count++
    }

    for ((i, x) in cards) {
        println("Print the definition of \"$i\":")
        val definition = readln()
        if (definition == x) println("Correct!")
        else println("Wrong. The right answer is \"$x\".")
    }

}
