package com.rochiee.classsync.taskengine

import java.text.Normalizer
import java.util.Locale

object TaskFingerprintGenerator {
    private val stopWords = setOf(
        "a", "an", "and", "assignment", "class", "coursework", "deadline", "due",
        "for", "homework", "new", "of", "on", "posted", "quiz", "submit", "submission",
        "task", "test", "the", "to", "turn", "upload"
    )

    fun normalizedTitle(title: String): String {
        return tokenize(title).joinToString(" ")
    }

    fun normalizedCourseName(courseName: String): String {
        return normalizeText(courseName)
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    fun fingerprint(title: String, courseName: String, dueDate: Long?): String {
        val titlePart = normalizedTitle(title)
        val coursePart = normalizedCourseName(courseName)
        val duePart = dueDate?.let { it / 3_600_000L } ?: "none"
        return "$titlePart|$coursePart|$duePart"
    }

    fun titleSimilarity(first: String, second: String): Double {
        val firstTokens = tokenize(first).toSet()
        val secondTokens = tokenize(second).toSet()
        if (firstTokens.isEmpty() || secondTokens.isEmpty()) return 0.0
        val intersection = firstTokens.intersect(secondTokens).size.toDouble()
        val union = firstTokens.union(secondTokens).size.toDouble()
        return if (union == 0.0) 0.0 else intersection / union
    }

    private fun tokenize(text: String): List<String> {
        return normalizeText(text)
            .split(Regex("\\s+"))
            .map { it.trim() }
            .filter { it.isNotBlank() && it !in stopWords }
    }

    private fun normalizeText(text: String): String {
        val normalized = Normalizer.normalize(text.lowercase(Locale.getDefault()), Normalizer.Form.NFD)
        return normalized
            .replace("\\p{M}+".toRegex(), "")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }
}
