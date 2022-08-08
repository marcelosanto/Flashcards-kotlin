package flashcards

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

fun main() {
    val cards = mutableMapOf<String, String>()

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    val type = Types.newParameterizedType(
        MutableMap::class.java,
        String::class.java,
        String::class.java
    )

    val cardAdapter = moshi.adapter<MutableMap<String, String>>(type)

    while (true) {
        println("Input the action (add, remove, import, export, ask, exit):")
        when (readln()) {
            "add" -> cardAdd(cards)
            "remove" -> cardRemove(cards)
            "import" -> cardImport(cardAdapter, cards)
            "export" -> cardExport(cardAdapter, cards)
            "ask" -> cardAsk(cards)
            "exit" -> {
                println("Bye bye!")
                break
            }
            else -> continue
        }


    }

}

fun cardImport(
    cardAdapter: JsonAdapter<MutableMap<String, String>>,
    cards: MutableMap<String, String>
) {

    println("File name:")
    val fileName = readln()
    val cardsFile = File(fileName)

    if (cardsFile.exists()) {
        val tasks = cardsFile.readText()
        val cardList = cardAdapter.fromJson(tasks)

        cardList!!.let {
            cards.putAll(it)
            println("${cardList.size} cards have been loaded.")

        }
    } else println("File not found.")
}


fun cardExport(
    cardAdapter: JsonAdapter<MutableMap<String, String>>,
    cards: MutableMap<String, String>
) {
    println("File name:")
    val fileName = readln()

    if (fileName.isNotBlank()) {
        File(fileName).writeText(cardAdapter.toJson(cards))
        println("${cards.size} cards have been saved.")
    }

}


fun cardAsk(cards: MutableMap<String, String>) {
    println("How many times to ask?")
    val askTime = readln().toInt()

    var count = 0

    for ((i, x) in cards) {
        if (count == askTime) break

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

        count++
    }


}

fun cardRemove(cards: MutableMap<String, String>) {
    println("Which card?")
    val cardRemove = readln()
    if (cards.containsKey(cardRemove)) {
        cards.remove(cardRemove)
        println("The card has been removed.")
    } else println("Can't remove \"$cardRemove\": there is no such card.")
}

fun cardAdd(cards: MutableMap<String, String>): MutableMap<String, String> {
    println("The Card:")
    val card = readln().let {
        var temp = it
        while (true) {
            if (cards.containsKey(temp)) {
                println("The card \"$it\" already exists.")
                temp = "null"
            } else {
                break
            }
        }
        temp
    }

    var definition = "null"

    if (card != "null") {
        println("The definition of the card:")
        definition = readln().let {
            var temp = it
            while (true) {
                if (cards.containsValue(temp)) {
                    println("The definition \"$it\" already exists.")
                    temp = "null"
                } else {
                    break
                }
            }
            temp
        }
    }

    return if (card == "null" || definition == "null") {
        cards
    } else {
        cards[card] = definition
        println("The pair (\"$card\":\"$definition\") has been added.")
        cards
    }
}