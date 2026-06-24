package com.rochiee.classsync.domain.model

enum class TaskPriority(val score: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4);

    companion object {
        fun fromScore(score: Int): TaskPriority {
            return entries.maxByOrNull { priority ->
                if (priority.score <= score) priority.score else Int.MIN_VALUE
            } ?: MEDIUM
        }
    }
}
