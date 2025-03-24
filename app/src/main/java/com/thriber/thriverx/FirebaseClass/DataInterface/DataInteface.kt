package com.thriber.thriverx.FirebaseClass.DataInterface

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.StorageReference
import com.thriber.thriverx.PatientItem

interface DataInterface{
   suspend fun register(username: String,password:String):String


    suspend fun login(username: String,password: String):String

    suspend fun passwordResetEmail(email:String)

     suspend fun fetchPatient(text:String):List<PatientItem>

 fun getStorageUrl(patientId: String): StorageReference

    suspend fun patinetDetails(patientId: String):Map<String,Any>?

    fun uploadPatientCredentials(PatientCredential: PatientItem, patientId:String)

    fun uploadPatientDocument(path: String,patientId: String){

    }
    fun fetchpatientdocument(path: String,patientId: String){

    }
    fun singout(){

    }

}