package flashcards

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

fun main() {

    val workingDirectory = System.getProperty("user.dir")
    val separator = File.separator

    val cards = mutableMapOf<String, String>()
    val txt = "$workingDirectory${separator}cards.json"
    val taskJson = File(txt)

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    val type = Types.newParameterizedType(
        MutableMap::class.java,
        String::class.java,
        String::class.java
    )

    val taskAdapter = moshi.adapter<MutableMap<String, String>>(type)

    if (taskJson.exists()) {
        val tasks = taskJson.readText()
        val tskList = taskAdapter.fromJson(tasks)

        tskList?.let {
            cards.putAll(it)
        }
    }



    while (true) {
        println("Input the action (add, remove, import, export, ask, exit):")
        when (readln()) {
            "add" -> cardAdd(cards)
            "remove" -> cardRemove(cards)
            "import" -> cardImport(cards)
            "export" -> cardExport(taskAdapter, taskJson, cards)
            "ask" -> cardAsk(cards)
            "exit" -> {
                println("Bye bye!")
                break
            }
            else -> continue
        }
        println(cards)

    }

//    for ((i, x) in cards) {
//        println("Print the definition of \"$i\":")
//        val definition = readln()
//        if (definition == x) println("Correct!")
//        else {
//            if (cards.containsValue(x)) {
//                println(
//                    "Wrong. The right answer is \"$x\", but your definition is correct for \"${
//                        cards.filterValues { definition == it }.keys
//                    }\".".replace("(\\[|\\])".toRegex(), "")
//                )
//            } else println("Wrong. The right answer is \"$x\".")
//        }
//    }

}

fun saveToJson(
    taskAdapter: JsonAdapter<MutableMap<String, String>>,
    taskJson: File,
    cards: MutableMap<String, String>
) {
    taskJson.writeText(taskAdapter.toJson(cards))
}

fun cardExport(
    taskAdapter: JsonAdapter<MutableMap<String, String>>,
    taskJson: File,
    cards: MutableMap<String, String>
) {
    saveToJson(taskAdapter, taskJson, cards)
}

fun cardImport(cards: MutableMap<String, String>) {
    TODO("Not yet implemented")
}

fun cardAsk(cards: MutableMap<String, String>) {
    TODO("Not yet implemented")
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
    println("The Car:")
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