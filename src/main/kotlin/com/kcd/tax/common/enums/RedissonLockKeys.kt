package com.kcd.tax.common.enums

enum class RedissonLockKeys(val key: String) {
    COLLECTION_LOCK("lock:collection:business:%d");

    fun getKey(vararg params: Any): String {
        return String.format(key, *params)
    }
}