fun palindrome(pair: Pair<String, String>): Triple<String, String, Boolean> {
    val bool = pair.first.lowercase() == pair.second.lowercase().reversed()
    return Triple(pair.first, pair.second, bool)
}
