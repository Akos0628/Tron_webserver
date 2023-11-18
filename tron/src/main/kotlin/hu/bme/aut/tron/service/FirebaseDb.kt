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
        return collection.get().get().toObjects(BoardRecord::class.java).take(limit)
    }

    override suspend fun setBoard(leaderboard: List<BoardRecord>): Unit = coroutineScope {
        launch {
            val batch = db.batch()

            val docs = collection.get().get().documents
            leaderboard
                .take(limit)
                .mapIndexed { index, record ->
                    if (docs.size > index)
                        batch.set(docs[index].reference, record)
                    else
                        batch.create(collection.document(), record)
                }

            batch.commit()

            println("Leaderboard updated!!!")
        }
    }
}