fun resume(marks: Triple<String, Int, List<Double>>): Pair<String, Double> {
    val average = marks.third.sum() / marks.third.size
    return Pair(marks.first, average)
}