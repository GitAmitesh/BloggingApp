package com.example.blogging_app.item_view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.blogging_app.R
import com.example.blogging_app.model.ThreadModel
import com.example.blogging_app.model.UserModel
import com.example.blogging_app.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ThreadItem(
    thread: ThreadModel,
    users: UserModel,
    navHostController: NavHostController,
    userId: String
) {
    val auth = FirebaseAuth.getInstance()
    val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("posts/${thread.id}/likes")
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(thread.likes.size) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = thread.id) {
        val userLike = thread.likes.contains(userId)
        isLiked = userLike
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .background(Color.White)
            .clickable {
                val routes = Routes.OtherUsers.route.replace("{data}", users.uid)
                navHostController.navigate(routes)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = users.imageUrl),
                contentDescription = "user image",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = users.username,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatTime(thread.timeStamp),
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = formatDate(thread.timeStamp),
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = thread.title,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                fontSize = 18.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = thread.description,
            style = TextStyle(
                fontSize = 17.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (thread.image.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = thread.image),
                    contentDescription = "thread image",
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        //for like button
        Row(
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                scope.launch {
                    if (isLiked) {
                        db.child(userId).removeValue()
                        likesCount -= 1
                    } else {
                        db.child(userId).setValue(true)
                        likesCount += 1
                    }
                    isLiked = !isLiked
                }
            }) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "like icon",
                    tint = if (isLiked) Color.Red else Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Icon(
                painter = painterResource(id = R.drawable.baseline_bookmarks_24),
                contentDescription = "Bookmark",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
                    .clickable {  }
            )
        }
//            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text ="${likesCount} likes",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ), modifier = Modifier.padding(horizontal = 13.dp, )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
    }


fun formatTime(timestamp: String): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp.toLong()))
}

fun formatDate(timestamp: String): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp.toLong()))
}
