package com.hackclub.store

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.RowScopeInstance.align
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.serialization.json.Json.Default.configuration
import java.io.File


@Composable
fun AppsScreen(app: List<App>, nav: NavHostController, login: Boolean, user: User?){
    var apps = remember{mutableStateOf<List<App>>(app)}

    Column {
        TopBar(apps = apps, allapps = app, nav = nav, login = login, user = user)
        AppsList(

            apps= apps, search = false
        )
    }

    newApp(nav= nav)
}



@Composable
fun AppsList(apps: MutableState<List<App>>, search: Boolean){
    if(search){
        LazyColumn {
            items(apps.value) { app ->
                Single(app)
            }
        }
    }else{
        LazyColumn {
            items(apps.value) { app ->
                Single(app)

            }
        }
    }





}
@Composable
fun TopBar(apps: MutableState<List<App>>, allapps: List<App>, nav: NavHostController, login: Boolean, user: User?){
    val configuration = LocalConfiguration.current
    ;
    val screenWidth = configuration.screenWidthDp.dp
    var widths = remember{mutableStateOf<List<Dp>>(listOf(screenWidth/2, screenWidth/8, screenWidth * 3/8))}
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){

        SearchBar(apps = apps, allapps = allapps, nav = nav, lengths = widths, login = login)
        Profile(loginedin = login, user = user, widths = widths)

    }

}
@Composable
fun SearchBar(apps: MutableState<List<App>>, allapps: List<App>, nav: NavHostController, lengths: MutableState<List<Dp>>, login: Boolean){
    apps.value = allapps
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    var searchQuery = remember{mutableStateOf<String>("")}
    var index = 0
    var temp: List<App> = listOf()
    for(item in apps.value){
        if(item.searchQuery(searchQuery.value)){
            temp = temp+item
        }
    }

    apps.value = temp

        TextField(value = searchQuery.value,
            onValueChange ={searchQuery.value = it
                            if(it != ""){

                                lengths.value = listOf(screenWidth, screenWidth * 0.1F  ,screenWidth * 0.1F)
                            }else{

                                lengths.value = listOf(screenWidth/2, screenWidth/8, screenWidth * 3/8)
                            }
                           },
            leadingIcon = {Row(){Icon(Icons.Filled.Search,
                contentDescription = "Search")} },
            trailingIcon = {
                if(searchQuery.value!=""){
                    IconButton(onClick = { searchQuery.value = ""
                        lengths.value = listOf(screenWidth/2, screenWidth/8, screenWidth * 3/8)
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                    }

                }
            },
            modifier = Modifier
                .padding(12.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .width(lengths.value[0])
        )
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .wrapContentHeight(align = Alignment.CenterVertically)
                .width(lengths.value[1])

        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Filter")
        }





}
@Composable
fun Profile(user: User? = null, loginedin: Boolean, widths: MutableState<List<Dp>>){
    if(loginedin){

    }else{
        ElevatedButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.width(widths.value[2])
            ) {

                Text(text = "Sign In")

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

