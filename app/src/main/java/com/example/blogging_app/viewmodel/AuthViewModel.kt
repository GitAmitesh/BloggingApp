package com.example.blogging_app.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavHostController
import com.example.blogging_app.model.UserModel
import com.example.blogging_app.utils.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import java.util.UUID

class AuthViewModel : ViewModel() {
     private val auth = FirebaseAuth.getInstance()
     private val db = FirebaseDatabase.getInstance()
     private val userRef = db.getReference("users")
     private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
     private val storageRef = FirebaseStorage.getInstance().reference

     private val _firebaseUser = MutableLiveData<FirebaseUser?>()
     val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

     private val _userData = MutableLiveData<UserModel?>()
     val userData: LiveData<UserModel?> = _userData

     private val _error = MutableLiveData<String>()
     val error: LiveData<String> = _error

     init {
          _firebaseUser.value = auth.currentUser
     }

     fun login(email: String, password: String, context: Context) {
          auth.signInWithEmailAndPassword(email, password)
               .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                         val user = auth.currentUser
                         if (user != null && user.isEmailVerified) {
                              _firebaseUser.postValue(user)
                              getData(user.uid, context)
                         } else {
                              auth.signOut()
                              _error.postValue("Please verify your email first.")
                         }
                    } else {
                         _error.postValue(task.exception?.message)
                    }
               }
     }

     fun resetPassword(email: String, context: Context) {
          auth.sendPasswordResetEmail(email)
               .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                         Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
                    } else {
                         _error.postValue(task.exception?.message)
                    }
               }
     }

     private fun getData(uid: String, context: Context) {
          userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(UserModel::class.java)
                    if (userData != null) {
                         SharedPref.storeData(userData.username, userData.email, userData.password, userData.imageUrl, context)
                         _userData.postValue(userData)
                    } else {
                         _error.postValue("User data not found")
                    }
               }

               override fun onCancelled(error: DatabaseError) {
                    _error.postValue(error.message)
               }
          })
     }

     fun register(
          email: String,
          password: String,
          username: String,
          imageUri: Uri,
          context: android.content.Context,
          navController: NavHostController
     ) {
          auth.createUserWithEmailAndPassword(email, password)
               .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                         Log.d("AuthViewModel", "User created successfully")
                         saveImage(email, password, username, imageUri, auth.currentUser?.uid, context, navController)
                    } else {
                         Log.e("AuthViewModel", "User couldn't be created: ${task.exception?.message}")
                         _error.postValue(task.exception?.message)
                    }
               }
     }

     private fun saveImage(
          email: String,
          password: String,
          username: String,
          imageUri: Uri,
          uid: String?,
          context: android.content.Context,
          navController: NavHostController
     ) {
          val imageRef = storageRef.child("users/${UUID.randomUUID()}.jpg")
          val uploadTask = imageRef.putFile(imageUri)
          uploadTask.addOnCompleteListener { task ->
               imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("AuthViewModel", "Image uploaded successfully")
                    saveData(email, password, username, uri.toString(), uid, context, navController)
               }.addOnFailureListener {
                    Log.e("AuthViewModel", "Failed to get download URL: ${it.message}")
               }
          }.addOnFailureListener {
               Log.e("AuthViewModel", "Image upload failed: ${it.message}")
          }
     }

     private fun saveData(
          email: String,
          password: String,
          username: String,
          imageUrl: String,
          uid: String?,
          context: android.content.Context,
          navController: NavHostController
     ) {
          val userData = UserModel(email, password, username, imageUrl, uid!!)
          userRef.child(uid).setValue(userData)
               .addOnSuccessListener {
                    Log.d("AuthViewModel", "User data saved successfully")
                    SharedPref.storeData(username, email, password, imageUrl, context)
                    auth.currentUser?.sendEmailVerification()
                         ?.addOnSuccessListener {
                              Log.d("AuthViewModel", "Verification email sent successfully")
                              Toast.makeText(context, "Please verify your email", Toast.LENGTH_SHORT).show()
                              Handler(Looper.getMainLooper()).postDelayed({
                                   logout()
                                   navController.navigate("signin") {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                   }
                              }, 3000) // 3 seconds delay
                         }
                         ?.addOnFailureListener {
                              Log.e("AuthViewModel", "Failed to send verification email: ${it.message}")
                              _error.postValue("Failed to send verification email")
                              logout()
                         }
               }
               .addOnFailureListener {
                    Log.e("AuthViewModel", "Failed to save user data: ${it.message}")
                    _error.postValue(it.message)
               }
     }

     fun logout() {
          auth.signOut()
          _firebaseUser.postValue(null)
     }

     internal fun fetchUserData(uid: String) {
          userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                         _userData.postValue(user)
                    } else {
                         _error.postValue("User data not found")
                    }
               }

               override fun onCancelled(error: DatabaseError) {
                    _error.postValue(error.message)
               }
          })
     }

     @SuppressLint("NullSafeMutableLiveData")
     fun clearError() {
          _error.value = null
     }

     val loggedInUserId = liveData(Dispatchers.IO) {
          val userId = firebaseAuth.currentUser?.uid ?: ""
          emit(userId)
     }
}
