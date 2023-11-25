package hu.bme.aut.tron.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import hu.bme.aut.tron.api.BoardRecord
import hu.bme.aut.tron.service.Config.requireProperty
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object FirebaseDb : BoardDatabase {
    private val projectId = requireProperty("ktor.database.projectId")
    private val collectionName = requireProperty("ktor.database.collectionName")
    private val limit = requireProperty("ktor.database.limit").toInt()

    private var firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
        .setProjectId(projectId)
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build()
    private val db: Firestore = firestoreOptions.getService()
    private var collection = db.collection(collectionName)

    init {
        println("project id is set to:    $projectId")
    }

    override fun getBoard(): List<BoardRecord> {
        println("Leaderboard queried!!!")
        return collection.limit(limit).get().get().toObjects(BoardRecord::class.java).sortedByDescending {
            it.score
        }
    }

    override suspend fun updateBoardWithWinner(winner: BoardRecord): Unit = coroutineScope {
        launch {
            val original = collection.limit(limit).get().get()
            val originalBoard = original.toObjects(BoardRecord::class.java)
            val emptyPlace = limit - originalBoard.size
            if (emptyPlace > 0) {
                collection.add(winner)
            } else {
                val originalDocuments = collection.limit(limit).get().get().documents
                val zipped = originalDocuments.zip(originalBoard).sortedBy {
                    it.second.score
                }

                if (zipped.first().second.score < winner.score) {
                    zipped.first().first.reference.set(winner).get()
                    println("Leaderboard updated!!!")
                }
            }
        }
    }
}