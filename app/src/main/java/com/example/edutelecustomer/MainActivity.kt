package com.example.edutelecustomer


import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.edutelecustomer.ui.screens.cards.CardsScreen
import com.example.edutelecustomer.ui.screens.childcardhistory.ChildCardHistoryScreen
import com.example.edutelecustomer.ui.screens.historyscreen.HistoryScreen
import com.example.edutelecustomer.ui.screens.homescreen.HomeScreen
import com.example.edutelecustomer.ui.screens.loginscreen.LoginScreen
import com.example.edutelecustomer.ui.screens.qrcode.QrCodeScreen
import com.example.edutelecustomer.ui.theme.EduteleCustomerTheme


class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        setContent {
            EduteleCustomerTheme {
                SetStatusBarColor()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier
                            .padding(innerPadding)

                    )
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(modifier: Modifier){
    val navController = rememberNavController()
    val rootNavController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ){
        composable(route = "login"){
            LoginScreen(onLoginSuccess = {
                navController.navigate("home"){
                    popUpTo("login"){inclusive = true} // remove login from back stack
                }
            })
        }
        composable("home"){
            HomeScreen( navController = navController)
        }
        composable("qrCode"){
            QrCodeScreen(navController)
        }
        composable("history") {
            HistoryScreen(navController = navController)
        }
        composable("cards") {
            CardsScreen(navController)
        }
        composable("cards"){
            CardsScreen(navController)
        }
        composable("childCardHistory/{childId}"){ backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            ChildCardHistoryScreen(navController, childId )
        }
    }
}

@Composable
fun SetStatusBarColor() {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // 🔵 Change this to your desired color
            window.statusBarColor = Color(0xFF012A56).toArgb()

            // ⚪ false = white icons, true = dark icons
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }
}

