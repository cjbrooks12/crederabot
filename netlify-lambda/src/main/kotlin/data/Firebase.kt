package com.caseyjbrooks.netlify.data

import com.caseyjbrooks.netlify.app
import kotlinx.coroutines.await
import kotlin.js.Promise

var instance: FirebaseDatabase? =  null

@JsModule("firebase-admin")
@JsNonModule
external object Admin {
    val credential: dynamic
    fun initializeApp(config: dynamic)
    fun database(): FirebaseDatabase
}


external class FirebaseDatabase {
    fun ref(ref: String): FirebaseDatabaseRef
}
operator fun FirebaseDatabase.get(ref: String) = this.ref(ref)

external class FirebaseDatabaseRef {
    fun child(ref: String): FirebaseDatabaseRef
    fun set(data: dynamic, callback: ((dynamic)->Unit)?)

    fun once(ref: String, cb: (FirebaseDatabaseSnapshot)->Unit)
    fun update(data: dynamic, callback: ((dynamic)->Unit)?)
    fun push(): FirebaseDatabaseRef
    fun push(data: dynamic, callback: ((dynamic)->Unit)?)

    fun orderByChild(ref: String): FirebaseDatabaseRef
    fun orderByKey(): FirebaseDatabaseRef
    fun orderByValue(): FirebaseDatabaseRef

    fun limitToFirst(count: Int): FirebaseDatabaseRef
    fun limitToLast(count: Int): FirebaseDatabaseRef
}

operator fun FirebaseDatabaseRef.get(ref: String) = this.child(ref)

fun FirebaseDatabaseRef.set(data: dynamic): Promise<FirebaseDatabaseSnapshot> = Promise { resolve, _ -> this.set(data, resolve) }
suspend fun FirebaseDatabaseRef.setNow(data: dynamic): FirebaseDatabaseSnapshot = this.set(data).await()

fun FirebaseDatabaseRef.once(cb: (FirebaseDatabaseSnapshot)->Unit) = this.once("value", cb)
fun FirebaseDatabaseRef.once(): Promise<FirebaseDatabaseSnapshot> = Promise { resolve, _ -> this.once(resolve) }
suspend fun FirebaseDatabaseRef.onceNow(): FirebaseDatabaseSnapshot = this.once().await()

fun FirebaseDatabaseRef.update(data: dynamic): Promise<FirebaseDatabaseSnapshot> = Promise { resolve, _ -> this.update(data, resolve) }
suspend fun FirebaseDatabaseRef.updateNow(data: dynamic): FirebaseDatabaseSnapshot = this.update(data).await()

fun FirebaseDatabaseRef.push(data: dynamic): Promise<FirebaseDatabaseSnapshot> = Promise { resolve, _ -> this.push(data, resolve) }
suspend fun FirebaseDatabaseRef.pushNow(data: dynamic): FirebaseDatabaseSnapshot = this.push(data).await()

external class FirebaseDatabaseSnapshot {
    fun `val`(): dynamic
    fun forEach(cb: (FirebaseDatabaseSnapshot)->Boolean)
    fun numChildren(): Int
}
fun FirebaseDatabaseSnapshot.get(): dynamic = this.`val`()

fun getFirebaseDatabase() : FirebaseDatabase {
    if(instance == null) {
        val env = app().env

        // Initialize Firebase
        val options: dynamic = object {}
        options["databaseURL"] = env.firebaseUrl
        options["credential"] = Admin.credential.cert(env.firebaseSerivceAccountFile)
        Admin.initializeApp(options)

        instance = Admin.database()
    }

    return instance!!
}