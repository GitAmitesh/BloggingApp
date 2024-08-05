package com.example.blogging_app.Screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.blogging_app.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.blogging_app.model.ThreadModel
import com.example.blogging_app.model.UserModel
import com.example.blogging_app.navigation.Routes
import com.example.blogging_app.utils.SharedPref
import com.example.blogging_app.viewmodel.AddThreadViewModel
import com.example.blogging_app.viewmodel.AuthViewModel
import com.example.blogging_app.viewmodel.UserViewModel
import com.google.firebase.database.FirebaseDatabase

@Composable
fun Profile(navHostController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel()
    val addThreadViewModel: AddThreadViewModel = viewModel()
    val threads by userViewModel.threads.observeAsState(null)
    val user = UserModel(
        username = SharedPref.getUserName(context),
        imageUrl = SharedPref.getImage(context)
    )

    val username = remember { SharedPref.getUserName(context) }
    val imageUri = remember { SharedPref.getImage(context) }

    LaunchedEffect(firebaseUser) {
        if (firebaseUser == null) {
            navHostController.navigate("signin") {
                popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            firebaseUser?.uid?.let {
                authViewModel.fetchUserData(it)
                userViewModel.fetchPost(it)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0ECEC))
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back",
                    modifier = Modifier.clickable {
                        navHostController.navigate(Routes.BottomNav.route)
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                androidx.compose.material3.Text(
                    text = "Profile",
                    style = TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color.Black
                    ),
                )
                Spacer(modifier = Modifier.weight(1f))

                var expanded by remember { mutableStateOf(false) }

                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_more_vert_24),
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            authViewModel.logout()
                            navHostController.navigate("signin") {
                                popUpTo(navHostController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }) {
                            Text("Logout")
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (imageUri.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(228.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = username,
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    color = Color.DarkGray,
                    thickness = 2.dp,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(17.dp))
            }
        }

        items(threads ?: emptyList()) { thread ->
            ThreadTitleDescriptionItem(
                thread = thread,
                userViewModel = userViewModel,
                context = context,
                navHostController = navHostController
            )
        }
    }
}

@Composable
fun ThreadTitleDescriptionItem(
    thread: ThreadModel,
    userViewModel: UserViewModel,
    context: Context,
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Color(0xFFF0ECEC))
    ) {
        if (thread.image.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(thread.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = thread.title,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = thread.description,
            style = MaterialTheme.typography.body2
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    navHostController.navigate(Routes.Edit.route.replace("{threadId}",thread.id))
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Edit", color = Color.White)
            }
            Button(
                onClick = {
                    FirebaseDatabase.getInstance().getReference("posts")
                        .child(thread.id)
                        .removeValue()
                        .addOnSuccessListener {
                            userViewModel.removeThread(thread)
                            Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()
                        }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text("Delete", color = Color.White)
            }
        }
    }
}
