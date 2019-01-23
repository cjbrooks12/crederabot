package com.caseyjbrooks.netlify.data

import com.caseyjbrooks.netlify.app
import kotlinx.coroutines.await
import kotlin.js.Promise

var initialized: Boolean = false
var firebaseDatabase: FirebaseDatabase? = null

@JsModule("firebase-admin")
external object Admin {
    val credential: dynamic
    fun initializeApp(config: dynamic)
    fun database(): FirebaseDatabase
}

// Initialize
//----------------------------------------------------------------------------------------------------------------------

fun initializeFirebase() {
    if (!initialized) {
        val env = app().env
        val options: dynamic = object {}
        options["databaseURL"] = env.firebaseUrl
        options["credential"] = Admin.credential.cert(env.firebaseSerivceAccountFile)
        Admin.initializeApp(options)
    }
}

// Firebase Database
//----------------------------------------------------------------------------------------------------------------------

external class FirebaseDatabase {
    fun ref(ref: String): FirebaseDatabaseRef
}

operator fun FirebaseDatabase.get(ref: String) = this.ref(ref)

external class FirebaseDatabaseRef {
    fun child(ref: String): FirebaseDatabaseRef
    fun set(data: dynamic, callback: ((dynamic) -> Unit)?)

    fun once(ref: String, cb: (FirebaseDatabaseSnapshot) -> Unit)
    fun update(data: dynamic, callback: ((dynamic) -> Unit)?)
    fun push(): FirebaseDatabaseRef
    fun push(data: dynamic, callback: ((dynamic) -> Unit)?)

    fun orderByChild(ref: String): FirebaseDatabaseRef
    fun orderByKey(): FirebaseDatabaseRef
    fun orderByValue(): FirebaseDatabaseRef

    fun limitToFirst(count: Int): FirebaseDatabaseRef
    fun limitToLast(count: Int): FirebaseDatabaseRef
}

operator fun FirebaseDatabaseRef.get(ref: String) = this.child(ref)

fun FirebaseDatabaseRef.set(data: dynamic): Promise<FirebaseDatabaseSnapshot> =
    Promise { resolve, _ -> this.set(data, resolve) }

suspend fun FirebaseDatabaseRef.setNow(data: dynamic): FirebaseDatabaseSnapshot = this.set(data).await()

fun FirebaseDatabaseRef.once(cb: (FirebaseDatabaseSnapshot) -> Unit) = this.once("value", cb)
fun FirebaseDatabaseRef.once(): Promise<FirebaseDatabaseSnapshot> = Promise { resolve, _ -> this.once(resolve) }
suspend fun FirebaseDatabaseRef.onceNow(): FirebaseDatabaseSnapshot = this.once().await()

fun FirebaseDatabaseRef.update(data: dynamic): Promise<FirebaseDatabaseSnapshot> =
    Promise { resolve, _ -> this.update(data, resolve) }

suspend fun FirebaseDatabaseRef.updateNow(data: dynamic): FirebaseDatabaseSnapshot = this.update(data).await()

fun FirebaseDatabaseRef.push(data: dynamic): Promise<FirebaseDatabaseSnapshot> =
    Promise { resolve, _ -> this.push(data, resolve) }

suspend fun FirebaseDatabaseRef.pushNow(data: dynamic): FirebaseDatabaseSnapshot = this.push(data).await()

external class FirebaseDatabaseSnapshot {
    fun `val`(): dynamic
    fun exists(): Boolean
    fun forEach(cb: (FirebaseDatabaseSnapshot) -> Boolean)
    fun numChildren(): Int
    fun child(ref: String): FirebaseDatabaseSnapshot
}

fun FirebaseDatabaseSnapshot.get(): dynamic = this.`val`()
fun FirebaseDatabaseSnapshot.asList(
    count: Int? = null,
    reversed: Boolean = false,
    filter: ((dynamic) -> Boolean)? = null
): List<dynamic> {
    val list = mutableListOf<dynamic>()
    this.forEach { snapshop ->
        list.add(snapshop.get())
        false
    }

    val list2 = if (reversed) list.reversed() else list
    val list3 = if(filter != null) list2.filter(filter) else list2
    val list4 = if (count != null) list3.take(count) else list3

    return list4
}

fun getFirebaseDatabase(): FirebaseDatabase {
    if (firebaseDatabase == null) {
        initializeFirebase()
        firebaseDatabase = Admin.database()
    }

    return firebaseDatabase!!
}
