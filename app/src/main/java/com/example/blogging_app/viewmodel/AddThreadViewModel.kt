package com.example.blogging_app.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blogging_app.model.ThreadModel
import com.example.blogging_app.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddThreadViewModel : ViewModel() {

     private val db = FirebaseDatabase.getInstance()
     private val userRef = db.getReference("posts")
     private val storageRef = FirebaseStorage.getInstance().reference

     private val _userData = MutableLiveData<UserModel>()
     val userData: LiveData<UserModel> = _userData

     private val _error = MutableLiveData<String>()
     val error: LiveData<String> = _error

     private val _isPosted = MutableLiveData<Boolean>()
     val isPosted: LiveData<Boolean> = _isPosted

     fun saveImage(
          title: String,
          description: String,
          userId: String,
          imageUri: Uri
     ) {
          val imageRef = storageRef.child("posts/${UUID.randomUUID()}.jpg")
          val uploadTask = imageRef.putFile(imageUri)
          uploadTask.addOnCompleteListener { task ->
               imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveData(title, description, userId, uri.toString())
               }
          }
     }

     fun saveData(
          title: String,
          description: String,
          userId: String,
          image: String
     ) {
          val postId = userRef.push().key ?: return
          val threadData = ThreadModel(
               id = postId,
               title = title,
               description = description,
               image = image,
               userId = userId,
               timeStamp = System.currentTimeMillis().toString()
          )

          userRef.child(postId).setValue(threadData)
               .addOnSuccessListener {
                    _isPosted.postValue(true)
               }
               .addOnFailureListener {
                    _isPosted.postValue(false)
               }
     }

     internal fun fetchUserData(uid: String) {
          userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    user?.let {
                         _userData.postValue(it)
                    } ?: _error.postValue("User not found")
               }

               override fun onCancelled(error: DatabaseError) {
                    _error.postValue(error.message)
               }
          })
     }

     fun postThread(title: String, description: String, imageUri: Uri?, userId: String) {
          if (imageUri != null) {
               saveImage(title, description, userId, imageUri)
          } else {
               saveData(title, description, userId, "")
          }
     }

     fun clearInputs() {
          _isPosted.value = false
     }

     //for edit the post fetch the post to editpost page
     fun fetchThread(threadId: String) {
          userRef.child(threadId).addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                    val thread = snapshot.getValue(ThreadModel::class.java)
                    thread?.let {
                         // Update UI state here
                    } ?: _error.postValue("Thread not found")
               }

               override fun onCancelled(error: DatabaseError) {
                    _error.postValue(error.message)
               }
          })
     }
     //edit
     private val _isUpdated = MutableLiveData<Boolean>()
     val isUpdated: LiveData<Boolean> get() = _isUpdated

     fun updateThread(threadId: String, title: String, description: String, imageUri: Uri?, userId: String) {
          val threadRef = userRef.child(threadId)
          val threadUpdates = mapOf<String, Any>(
               "title" to title,
               "description" to description,
               "image" to (imageUri?.toString() ?: "")
          )
          threadRef.updateChildren(threadUpdates)
               .addOnSuccessListener {
                    _isUpdated.value = true
               }
               .addOnFailureListener {
                    _isUpdated.value = false
               }
     }
     // to fetch the existing data to the edit post page
     fun getThreadById(threadId: String, onResult: (ThreadModel?) -> Unit) {
          val threadRef = userRef.child(threadId)
          threadRef.addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                    val thread = snapshot.getValue(ThreadModel::class.java)
                    onResult(thread)
               }

               override fun onCancelled(error: DatabaseError) {
                    onResult(null)
               }
          })
     }
}


