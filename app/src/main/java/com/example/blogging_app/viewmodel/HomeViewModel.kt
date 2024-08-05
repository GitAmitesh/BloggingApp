package com.example.blogging_app.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blogging_app.model.ThreadModel
import com.example.blogging_app.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.core.Transaction
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class HomeViewModel : ViewModel() {
     private val db = FirebaseDatabase.getInstance()
     private val threadRef = db.getReference("posts")

     private val _threadsAndUsers = MutableLiveData<List<Pair<ThreadModel, UserModel>>>()
     val threadsAndUsers: LiveData<List<Pair<ThreadModel, UserModel>>> = _threadsAndUsers

     init {
          fetchThreadsAndUsers()
     }

     private fun fetchThreadsAndUsers() {
          threadRef.addValueEventListener(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                    val result = mutableListOf<Pair<ThreadModel, UserModel>>()
                    for (threadSnapshot in snapshot.children) {
                         val thread = threadSnapshot.getValue(ThreadModel::class.java)
                         thread?.let {
                              fetchUserFromThread(it) { user ->
                                   result.add(it to user)
                                   _threadsAndUsers.value = result
                              }
                         }
                    }
               }

               override fun onCancelled(error: DatabaseError) {
                    // Handle error
               }
          })
     }

     private fun fetchUserFromThread(thread: ThreadModel, onResult: (UserModel) -> Unit) {
          db.getReference("users").child(thread.userId)
               .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                         val user = snapshot.getValue(UserModel::class.java)
                         user?.let(onResult)
                    }

                    override fun onCancelled(error: DatabaseError) {
                         // Handle error
                    }
               })
     }
}
