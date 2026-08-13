package app.taskvault.data.repository

import app.taskvault.data.local.SleepDao
import app.taskvault.data.local.SleepEntryEntity
import app.taskvault.domain.SleepRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepRepositoryImpl @Inject constructor(
    private val sleepDao: SleepDao,
    private val auth: FirebaseAuth
) : SleepRepository {

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    override fun getSleepEntries(): Flow<List<SleepEntryEntity>> {
        val uid = currentUserId
        if (uid.isEmpty()) return emptyFlow()
        return sleepDao.getSleepEntriesForUser(uid)
    }

    override suspend fun insertSleepEntry(entry: SleepEntryEntity) {
        val uid = currentUserId
        if (uid.isEmpty()) return
        sleepDao.insertSleepEntry(entry.copy(userId = uid))
    }

    override suspend fun deleteSleepEntry(id: String) {
        val uid = currentUserId
        if (uid.isEmpty()) return
        sleepDao.deleteSleepEntryById(id, uid)
    }

    override suspend fun seedSampleData() {
        val uid = currentUserId
        if (uid.isEmpty()) return

        val now = System.currentTimeMillis()
        val dayMillis = TimeUnit.DAYS.toMillis(1)

        val samples = listOf(
            SleepEntryEntity(
                userId = uid,
                bedtime = now - dayMillis + TimeUnit.HOURS.toMillis(23), // 11:00 PM yesterday
                wakeTime = now - dayMillis + TimeUnit.HOURS.toMillis(23) + TimeUnit.MINUTES.toMillis(450), // 6:30 AM today
                durationMinutes = 450, // 7h 30m
                sleepQuality = "Excellent",
                notes = "Fell asleep quickly, woke up feeling refreshed!",
                timestamp = now - dayMillis / 2
            ),
            SleepEntryEntity(
                userId = uid,
                bedtime = now - (2 * dayMillis) + TimeUnit.HOURS.toMillis(22) + TimeUnit.MINUTES.toMillis(30), // 10:30 PM
                wakeTime = now - (2 * dayMillis) + TimeUnit.HOURS.toMillis(22) + TimeUnit.MINUTES.toMillis(510), // 7:00 AM
                durationMinutes = 480, // 8h 0m
                sleepQuality = "Good",
                notes = "Solid 8 hours of sleep.",
                timestamp = now - (1.5 * dayMillis).toLong()
            ),
            SleepEntryEntity(
                userId = uid,
                bedtime = now - (3 * dayMillis) + TimeUnit.HOURS.toMillis(24), // 12:00 AM
                wakeTime = now - (3 * dayMillis) + TimeUnit.HOURS.toMillis(24) + TimeUnit.MINUTES.toMillis(390), // 6:30 AM
                durationMinutes = 390, // 6h 30m
                sleepQuality = "Fair",
                notes = "Stayed up late reading.",
                timestamp = now - (2.5 * dayMillis).toLong()
            )
        )

        for (sample in samples) {
            sleepDao.insertSleepEntry(sample)
        }
    }
}
