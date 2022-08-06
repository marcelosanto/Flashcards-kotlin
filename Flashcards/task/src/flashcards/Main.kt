package flashcards

fun main() {

    println("Input the number of cards:")
    val numberOfCards = readln().toInt()
    val cards = mutableMapOf<String, String>()

    var count = 1
    repeat(numberOfCards) {

        println("Card #$count:")
        val card = readln().let {
            var temp = it
            while (true) {
                if (cards.containsKey(temp)) {
                    println("The term \"$it\" already exists. Try again:")
                    temp = readln()
                } else {
                    break
                }
            }
            temp
        }

        println("The definition for card #$count:")
        val definition = readln().let {
            var temp = it
            while (true) {
                if (cards.containsValue(temp)) {
                    println("The definition \"$it\" already exists. Try again:")
                    temp = readln()
                } else {
                    break
                }
            }
            temp
        }

        cards[card] = definition
        count++
    }


    for ((i, x) in cards) {
        println("Print the definition of \"$i\":")
        val definition = readln()
        if (definition == x) println("Correct!")
        else {
            if (cards.containsValue(x)) {
                println(
                    "Wrong. The right answer is \"$x\", but your definition is correct for \"${
                        cards.filterValues { definition == it }.keys
                    }\".".replace("(\\[|\\])".toRegex(), "")
                )
            } else println("Wrong. The right answer is \"$x\".")
        }
    }

}