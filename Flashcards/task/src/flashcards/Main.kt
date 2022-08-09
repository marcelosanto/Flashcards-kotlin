package flashcards

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

fun main() {
    val cards = mutableMapOf<String, String>()
    val statsCards = mutableMapOf<String, Int>()
    val logs = mutableListOf<String>()

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val typeCard = Types.newParameterizedType(
        MutableMap::class.java,
        String::class.java,
        String::class.java
    )

    val typeCardStats = Types.newParameterizedType(
        MutableMap::class.java,
        String::class.java,
        Integer::class.java
    )

    val cardAdapter = moshi.adapter<MutableMap<String, String>>(typeCard)
    val cardStatsAdapter = moshi.adapter<MutableMap<String, Int>>(typeCardStats)

    val typeLog = Types.newParameterizedType(
        MutableList::class.java,
        String::class.java,
    )

    val logsAdapter = moshi.adapter<MutableList<String>>(typeLog)

    while (true) {
        println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        val opt = readln()

        logs.add("\nInput the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        logs.add(opt)

        when (opt) {
            "add" -> cardAdd(cards, logs)
            "remove" -> cardRemove(cards, logs)
            "import" -> cardImport(cardAdapter, cardStatsAdapter, cards, statsCards, logs)
            "export" -> cardExport(cardAdapter, cardStatsAdapter, cards, statsCards, logs)
            "ask" -> cardAsk(cards, statsCards, logs)
            "exit" -> {
                println("Bye bye!")
                logs.add("Bye bye!")
                break
            }
            "log" -> createLog(logsAdapter, logs)
            "hardest card" -> hardestCard(statsCards, logs)
            "reset stats" -> resetStats(statsCards, logs)
            else -> continue
        }
    }

}

fun resetStats(statsCards: MutableMap<String, Int>, logs: MutableList<String>) {
    statsCards.clear()
    println("Card statistics have been reset")
}

fun createLog(logsAdapter: JsonAdapter<MutableList<String>>, logs: MutableList<String>) {
    println("File name:")
    val fileName = readln()
    if (fileName.isNotBlank()) {
        for (i in logs) {
            File(fileName).appendText(i)
            File(fileName).appendText("\n")
        }
        println("The log has been saved.")
    }
}

fun hardestCard(statsCards: MutableMap<String, Int>, logs: MutableList<String>) {

    if (statsCards.isNotEmpty()) {
        val maxValue = statsCards.values.maxOrNull()
        val maxValueKeys: MutableList<String> = mutableListOf()
        for ((key, value) in statsCards) {
            if (value == maxValue) {
                maxValueKeys.add("\"${key}\"")
            }
        }

        val areOrIs = if (maxValueKeys.size > 1) "are" else "is"
        val themOrIt = if (maxValueKeys.size > 1) "them" else "it"
        val cardOrCards = if (maxValueKeys.size > 1) "cards" else "card"

        println("The hardest $cardOrCards $areOrIs ${maxValueKeys.joinToString()}. You have $maxValue errors answering $themOrIt.")
    } else println("There are no cards with errors.")
}

fun cardImport(
    cardAdapter: JsonAdapter<MutableMap<String, String>>,
    cardStatsAdapter: JsonAdapter<MutableMap<String, Int>>,
    cards: MutableMap<String, String>,
    statsCards: MutableMap<String, Int>,
    logs: MutableList<String>
) {

    println("File name:")
    val fileName = readln()
    val fileNameStats = "${fileName}stats"
    val cardsFile = File(fileName)
    val cardsStatsFile = File(fileNameStats)

    if (cardsFile.exists() && cardsStatsFile.exists()) {
        val card = cardsFile.readText()
        val cardStats = cardsStatsFile.readText()
        val cardList = cardAdapter.fromJson(card)
        val cardStatsList = cardStatsAdapter.fromJson(cardStats)

        cardList!!.let {
            cards.putAll(it)
            println("${cardList.size} cards have been loaded.")

        }

        cardStatsList!!.let {
            statsCards.putAll(it)
        }

    } else println("File not found.")
}


fun cardExport(
    cardAdapter: JsonAdapter<MutableMap<String, String>>,
    cardStatsAdapter: JsonAdapter<MutableMap<String, Int>>,
    cards: MutableMap<String, String>,
    statsCards: MutableMap<String, Int>,
    logs: MutableList<String>
) {
    println("File name:")
    val fileName = readln()
    val fileNameStats = "${fileName}stats"

    if (fileName.isNotBlank()) {
        File(fileName).writeText(cardAdapter.toJson(cards))
        File(fileNameStats).writeText(cardStatsAdapter.toJson(statsCards))
        println("${cards.size} cards have been saved.")
    }

}


fun cardAsk(cards: MutableMap<String, String>, statsCards: MutableMap<String, Int>, logs: MutableList<String>) {
    println("How many times to ask?")
    logs.add("How many times to ask?")
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
                wrongAnswer(i, statsCards)
            } else {
                println("Wrong. The right answer is \"$x\".")
                wrongAnswer(i, statsCards)
            }
        }

        count++
    }


}

fun wrongAnswer(i: String, statsCards: MutableMap<String, Int>) {
    if (statsCards.containsKey(i)) {
        val value = statsCards.getValue(i) + 1
        statsCards[i] = value

    } else {
        statsCards[i] = 1
    }
}

fun cardRemove(cards: MutableMap<String, String>, logs: MutableList<String>) {
    println("Which card?")
    val cardRemove = readln()
    if (cards.containsKey(cardRemove)) {
        cards.remove(cardRemove)
        println("The card has been removed.")
        logs.add("The card has been removed.")
    } else {
        println("Can't remove \"$cardRemove\": there is no such card.")
        logs.add("Can't remove \"$cardRemove\": there is no such card.")
    }
}

fun cardAdd(cards: MutableMap<String, String>, logs: MutableList<String>): MutableMap<String, String> {
    println("The Card:")
    logs.add("The Card:")

    val card = readln().let {
        var temp = it
        while (true) {
            if (cards.containsKey(temp)) {
                println("The card \"$it\" already exists.")
                logs.add("The card \"$it\" already exists.")
                temp = "null"
            } else {
                break
            }
        }
        logs.add(temp)
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
                    logs.add("The definition \"$it\" already exists.")
                    temp = "null"
                } else {
                    break
                }
            }
            logs.add(temp)
            temp
        }
    }

    return if (card == "null" || definition == "null") {
        cards
    } else {
        cards[card] = definition
        println("The pair (\"$card\":\"$definition\") has been added.")
        logs.add("The pair (\"$card\":\"$definition\") has been added.")
        cards
    }
}