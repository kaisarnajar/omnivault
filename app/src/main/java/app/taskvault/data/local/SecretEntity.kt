package app.taskvault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "secrets")
data class SecretEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val username: String = "",
    val secretValue: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
