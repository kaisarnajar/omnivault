package app.taskvault.data.local

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }
}
