package com.hackclub.store

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hackclub.store.ui.theme.HackStoreTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import java.io.File

import android.app.DownloadManager as DownloadManager1


@Composable
fun AppsScreen(apps: List<App>, nav: NavHostController){

    AppsList(

        apps= apps
    )
    newApp(nav= nav)
}



@Composable
fun AppsList(apps: List<App>){

    LazyColumn {
        items(apps) { app ->
            Single(app)
        }
    }




}
@Composable
fun AlertDialogExample(
    onConfirmation: () -> Unit,

    ) {
    AlertDialog(

        title = {
            Text(text = "App is downloading!")
        },
        text = {
            Text(text = "Once your app finished downloading, you'll be able to see it in your notification bar! Click on it to install!")
        },
        onDismissRequest = {
            onConfirmation()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },

        )
}


public fun onClick(appname:String, context: Context){


    var storage = Firebase.storage
    var storageRef = storage.reference
    var apkRef = storageRef.child(appname).child("app.apk")
    val ONE_MEGABYTE: Long = 1024 * 1024
    val localFile = File.createTempFile(appname, "apk")
    apkRef.downloadUrl.addOnSuccessListener {uri ->
        Log.i("firebase", uri.toString())
        val downloader = AndroidDownloader(context)
        downloader.downloadFile(uri.toString())

    }.addOnFailureListener { e->
        // Handle any errors
        Log.i("firebase", "failed to download $e")
    }




}

@Composable
fun newApp(nav: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        ElevatedButton(modifier = Modifier.align(Alignment.BottomEnd), onClick = { nextPage(navController = nav) }) {
            Text("New")
        }
    }
}
fun nextPage(navController: NavHostController){
    navController.navigate(CreateAppScreen)
}



@Composable
fun Single(app: App) {
    val context = LocalContext.current

    var popup = remember { mutableStateOf<Boolean?>(false) }
    fun close(){
        popup.value = false
    }
    OutlinedButton(onClick = {popup.value = true; onClick(app.name.toString(),context)}, modifier = Modifier.padding(top = 8.dp)) {
        Row(modifier = Modifier.padding(all = 8.dp)) {

            if(popup.value == true){
                AlertDialogExample({ close() })
            }


            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_rounded),
                    contentDescription = "App icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)

                )
                Text(
                    text = app.name,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = app.author,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }


        }
    }
}

