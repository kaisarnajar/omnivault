package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val url: String,
    val category: String,
    val notes: String,
    val timestamp: Long
)
