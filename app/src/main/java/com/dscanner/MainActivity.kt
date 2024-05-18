package com.dscanner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dscanner.home.DScannerHome
import com.dscanner.ui.theme.DScannerTheme
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DScannerTheme {
                Surface(

                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    if (checkPlayServices()){DScannerHome()}

                }
            }
        }
    }
}

@Composable
private fun checkPlayServices(): Boolean {
    val context = LocalContext.current as MainActivity
    val apiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
    if (resultCode != ConnectionResult.SUCCESS) {
        if (apiAvailability.isUserResolvableError(resultCode)) {
            apiAvailability.getErrorDialog(context, resultCode, 0)?.show()
        } else {
            Log.i("TAG", "This device is not supported.")
            context.finish()
        }
        return false
    }
    return true
}
