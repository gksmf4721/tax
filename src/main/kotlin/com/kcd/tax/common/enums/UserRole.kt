package com.kcd.tax.common.enums

import kotlin.collections.first
import kotlin.text.equals

enum class UserRole(val value: Int) {
    ADMIN(1),
    MANAGER(2);

    companion object {
        fun fromName(name: String?): UserRole = UserRole.entries.first { it.name.equals(name, ignoreCase = true) }
    }
}