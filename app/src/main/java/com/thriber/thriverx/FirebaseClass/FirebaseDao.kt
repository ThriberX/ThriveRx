package com.thriber.thriverx.FirebaseClass

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.thriber.thriverx.FirebaseClass.DataInterface.DataInterface
import com.thriber.thriverx.PatientItem
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseDao:DataInterface {

     private val authFirebase = FirebaseAuth.getInstance()
     private val db = Firebase.firestore
     private val storageRef= FirebaseStorage.getInstance()
    val storageurl="gs://thriverx-9102f.appspot.com/"


    fun UserId():String{
        val currentUser = authFirebase.currentUser
        var currentUserId= ""
        if (currentUser!=null) {
                currentUserId=currentUser.uid
        }
        return currentUserId
    }

    fun getStorageReference():StorageReference{
        val storage=storageRef.reference
        return storage
    }

    override fun getStorageUrl(patientId: String):StorageReference{
        val url="gs://thriverx-9102f.appspot.com/$patientId/"
        val storageUrl=storageRef.getReferenceFromUrl(url)

        return storageUrl

    }

     //for signup registration screen
  override suspend fun register(username: String, password: String):String {
      val trimusername:String= username.trim()
      val trimpaswword:String= password.trim()

      try {
          val authResult=authFirebase
              .createUserWithEmailAndPassword(trimusername,trimpaswword)
              .await()
          val firebaseUser=authResult.user?:throw Exception("User creation successful")
          firebaseUser.sendEmailVerification().await()

          return firebaseUser.uid

      }
      catch (e: FirebaseAuthUserCollisionException){
          throw Exception("This email is already registered. Please go to login option")
      }
      catch (e:Exception){
          throw Exception("Registration failed ${e.message}")
      }
  }

    //for login registration screen
         override suspend fun login(username: String,password: String):String{
             val trimmedUsername = username.trim()
             val trimmedPassword = password.trim()
            try{
                val authResult=authFirebase.signInWithEmailAndPassword(trimmedUsername,trimmedPassword).await()
                val firebaseuser = authResult.user

                if (firebaseuser!=null){
                    return firebaseuser.uid
                }
                else{
                    throw Exception("login failed")
                }

            }catch (e:FirebaseAuthInvalidUserException){
                throw Exception("Login failed. Invalied user")
            }catch (e:FirebaseAuthInvalidCredentialsException){
                throw Exception("Login failed due to invalid id/password")
            }catch (e:Exception){
                    throw Exception("Login failed due to unexcepted error: ${e.message}")
            }

         }

     //for reseting paword for user needs to be looked after to first verify email

     override suspend fun passwordResetEmail(email: String) {
         try {
             val task = authFirebase.sendPasswordResetEmail(email)
             task.await()

         } catch (e: Exception) {
             throw Exception("Error sending password reset email: ${e.message}")
         }
     }
    // for uploading patient credentials
   override fun uploadPatientCredentials(PatientCredential: PatientItem, patientId:String) {


        val hospitalId = authFirebase.currentUser?.uid ?: throw Exception("User not Authenticated")

            db.collection("hospitals")
                .document(hospitalId)
                .collection("patients")
                .document(patientId)
                .set(PatientCredential)
                .addOnSuccessListener {
                    Log.d("Firestore", "Patient credentials uploaded successfully")
                }


    }


        override suspend fun fetchPatient(text: String): List<PatientItem> {
            val hospitalId = authFirebase.currentUser?.uid ?: throw Exception("User not Authenticated")

            var query = db.collection("hospitals")
                .document(hospitalId)
                .collection("patients")
                .orderBy("formatiodate",Query.Direction.DESCENDING)

            if (text.isNotBlank()) {
                query = query.whereGreaterThanOrEqualTo("Name", text)
                    .whereLessThanOrEqualTo("Name", text + "\uf8ff")
            }

            return suspendCoroutine { continuation ->
                query.get()
                    .addOnSuccessListener { result ->
                        val items = result.map { document ->
                            document.toObject(PatientItem::class.java)
                        }
                        continuation.resume(items) // Resume with the fetched list
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e) // Resume with an exception
                    }
            }
        }

    override suspend fun patinetDetails(patientId: String): Map<String, Any> ?{
        val hospitalId = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            db.collection("hospitals")
                .document(hospitalId!!)
                .collection("patients")
                .document(patientId)
                .get()
                .await()
                .data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun sigout(){
        Firebase.auth.signOut()

    }

}
//clss firebase implement databse
// interface in kotlin