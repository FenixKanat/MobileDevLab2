package com.example.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab2.ui.theme.Lab2Theme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Company(
    val id: Int,
    val title: String,
    val city: String,
    val webpage: String
)

interface FirebaseAPI {
    @GET("/companies.json")
    suspend fun getCompanies(): List<Company>
}

object RetrofitBuilder {
    private const val BASE_URL = "https://lab2-d2d81-default-rtdb.europe-west1.firebasedatabase.app/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: FirebaseAPI = retrofit.create(FirebaseAPI::class.java)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompaniesScreen()
                }
            }
        }
    }
}

@Composable
fun CompaniesScreen() {
    val coroutineScope = rememberCoroutineScope()
    var companies by remember { mutableStateOf<List<Company>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            try {
                companies = RetrofitBuilder.api.getCompanies()
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    if (error != null) {
        Text(text = error!!)
    } else if (companies != null && companies!!.isNotEmpty()) {
        DisplayCompanies(companies!!)
    } else {
        Text(text = "Loading...")
    }
}

@Composable
fun DisplayCompanies(companies: List<Company>) {

    Column(modifier = Modifier.padding(16.dp)){
    for(company in companies) {
        Text(text = "City: ${company.city}")
        Text("ID: ${company.id}")
        Text("Title: ${company.title}")
        Text("Webpage: ${company.webpage}")
    }
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab2Theme {
        Text("Preview")
    }
}
