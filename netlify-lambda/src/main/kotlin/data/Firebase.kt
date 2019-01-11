package com.caseyjbrooks.netlify.data

import com.caseyjbrooks.netlify.app
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
    fun once(ref: String, cb: (FirebaseDatabaseSnapshot)->Unit)
    fun child(ref: String): FirebaseDatabaseRef
    fun set(data: dynamic, callback: ((dynamic)->Unit)?)
    fun update(data: dynamic, callback: ((dynamic)->Unit)?)
    fun push(): FirebaseDatabaseRef
    fun push(data: dynamic, callback: ((dynamic)->Unit)?)
}
fun FirebaseDatabaseRef.once(cb: (FirebaseDatabaseSnapshot)->Unit) = this.once("value", cb)
fun FirebaseDatabaseRef.once(): Promise<FirebaseDatabaseSnapshot> = Promise { resolve, _ -> this.once(resolve) }
fun FirebaseDatabaseRef.update(data: dynamic): Promise<FirebaseDatabaseSnapshot> = Promise { resolve, _ -> this.update(data, resolve) }
fun FirebaseDatabaseRef.set(data: dynamic): Promise<FirebaseDatabaseSnapshot> = Promise { resolve, _ -> this.set(data, resolve) }
fun FirebaseDatabaseRef.push(data: dynamic): Promise<FirebaseDatabaseSnapshot> = Promise { resolve, _ -> this.push(data, resolve) }
operator fun FirebaseDatabaseRef.get(ref: String) = this.child(ref)

external class FirebaseDatabaseSnapshot {
    fun `val`(): dynamic
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