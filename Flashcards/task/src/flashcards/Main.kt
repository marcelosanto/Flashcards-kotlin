package flashcards

fun main() {
    val term = readln()
    val definition = readln()
    val answer = readln()

    if (definition == answer) println("Your answer is right")
    else println("Your answer is wrong...")
}
