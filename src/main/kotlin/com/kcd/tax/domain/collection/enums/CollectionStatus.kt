package com.kcd.tax.domain.collection.enums

enum class CollectionStatus(val value: Int) {
    NOT_REQUESTED(1),
    COLLECTING(2),
    COLLECTED(3);

    companion object {
        fun fromValue(value: Int) = CollectionStatus.entries.first { it.value == value }
    }
}