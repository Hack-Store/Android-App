package com.hackclub.store

import android.content.ContentValues.TAG
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hackclub.store.ui.theme.HackStoreTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.checkerframework.checker.units.qual.K
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.storage.ktx.storage
import com.hackclub.store.ui.theme.HackStoreTheme
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class App(val name : String, val author: String, val description: String)


@Serializable
object AppsScreen

@Serializable
object CreateAppScreen

class MainActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference






    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        database = Firebase.database.reference
        var apps: List<App> = listOf()
        database.child("Apps").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            var data = it.value as Map<Any, Any>
            for(entry in data.entries.iterator()){
                Log.i("Database Key", entry.key.toString())
                Log.i("Database Value", entry.value.toString())
                var value = entry.value as Map<Any, Any>
                Log.i("Author", value["Author"].toString())
                var app = App(entry.key.toString(),value["Author"].toString(),value["Description"].toString())
                apps = apps + app
            }
            Log.i("APPS", apps.toString())

            setContent {
                HackStoreTheme {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = AppsScreen
                    ){
                        composable<AppsScreen>{

                            AppsScreen(apps = apps, nav = navController)
                        }
                        composable<CreateAppScreen> {
                            AddAppScreen(nav = navController)
                        }
                    }
                }

            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }
}
data class Message(val author: String, val body:String)


