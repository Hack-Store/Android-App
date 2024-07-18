package com.hackclub.store


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.io.File


@Composable
fun AddAppScreen(nav: NavHostController){
    Column {
        navbar(nav = nav)
        form(nav = nav, error = "")
    }

}
@Composable
fun navbar(nav: NavHostController){
    IconButton(onClick = {nav.navigate(AppsScreen) }) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")

    }

}
@Composable
fun form(nav: NavHostController, error:String){
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedFileUri = remember { mutableStateOf<Uri?>(null) }
    val uploadProgress = remember { mutableStateOf(0f) }
    val uploadStatus = remember { mutableStateOf("") }
    val fileName = remember { mutableStateOf(TextFieldValue("")) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedFileUri.value = result.data?.data
        }
    }

    Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("App Name") },
            modifier = Modifier.padding(all = 8.dp),
            maxLines = 1
        )
        TextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Your Name") },
            modifier = Modifier.padding(all = 8.dp),
            maxLines = 1
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("App Description") },
            modifier = Modifier.padding(all = 8.dp),
            maxLines = 10
        )
        Button(onClick = {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/vnd.android.package-archive" // APK file type
            }
            launcher.launch(intent)
        }) {
            Text("Select APK File")
        }
        if (selectedFileUri.value != null) {
            Text("Selected File: ${(selectedFileUri.value!!)}")
        }
        if (uploadProgress.value > 0f) {
            LinearProgressIndicator(progress = uploadProgress.value)
        }
        ElevatedButton(onClick = {
            if (selectedFileUri.value != null) {
                uploadtofirebase(name,author,description, nav= nav, fileUri = selectedFileUri.value!!,uploadProgress=uploadProgress,uploadStatus=uploadStatus)
            }else{
                uploadStatus.value = "Please select an APK file."
            }

        }) {
            Text("Submit App")
        }
        Text(text = uploadStatus.value)

    }

}


fun uploadtofirebase(
    name: String,
    author: String,
    description: String,
    nav: NavHostController,
    fileUri: Uri,
    uploadProgress: MutableState<Float>,
    uploadStatus: MutableState<String>
){

    lateinit var database: DatabaseReference
    database = Firebase.database.reference
    var apps: List<App> = listOf()
    database.child("Apps").child(name).get().addOnSuccessListener {
        Log.i("firebase", "Got value ${it.value}")


        if(it.value==null){
            val map = mapOf("Author" to author, "Description" to description)
            database.child("Apps").child(name).setValue(map)

            var storage = Firebase.storage
            val storageRef = storage.reference
            val fileName = "app.apk"
            val fileRef = storageRef.child("$name/$fileName")

            val uploadTask = fileRef.putFile(fileUri)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                uploadProgress.value = progress
                uploadStatus.value = "Uploading... $progress%"
            }

            uploadTask.addOnSuccessListener {
                uploadStatus.value = "Upload successful!"
                nav.navigate(AppsScreen)

            }





        }else{

        }


    }
}