package com.caseyjbrooks.netlify.data

//
//@JsModule("firebase-admin")
//@JsNonModule
//external object Admin {
//    val credential: dynamic
//    fun initializeApp(config: dynamic)
//    fun database(): FirebaseDatabase
//}
//
//external class FirebaseDatabase {
//    fun ref(ref: String): FirebaseDatabaseRef
//}
//
//external class FirebaseDatabaseRef {
//    fun once(ref: String, cb: (FirebaseDatabaseSnapshot)->Unit)
//    fun child(ref: String): FirebaseDatabaseRef
//    fun set(data: dynamic, callback: ((dynamic)->Unit)?)
//    fun update(data: dynamic, callback: ((dynamic)->Unit)?)
//    fun push(): FirebaseDatabaseRef
//    fun push(data: dynamic, callback: ((dynamic)->Unit)?)
//}
//
//external class FirebaseDatabaseSnapshot {
//    fun `val`(): dynamic
//}
//
//fun getFirebaseDatabase() : FirebaseDatabase? {
//    val env = app().env
//
//    try {
//        // Initialize Firebase
//        Admin.initializeApp(mapOf(
//            "databaseURL" to "https://crederaplusplus-1532310115828.firebaseio.com",
//            "credential" to Admin.credential.cert(env.firebaseSerivceAccountFile)
//        ))
//
//        return Admin.database()
//    }
//    catch (e: Throwable) {
//        println(e.message)
//        return null
//    }
//}