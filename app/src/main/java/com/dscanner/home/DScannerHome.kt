package com.dscanner.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.dscanner.MainActivity
import com.dscanner.R
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult.Pdf
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DScannerHome() {

    var context = LocalContext.current as MainActivity
    val options = remember { getScannerOptions()}
    val docs = remember { mutableStateListOf<Pdf>() }



    val DScannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ){ result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val documents = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            documents?.pdf?.let {
                docs.add(it)
            }
        }
    }

    val scanner = remember {
        GmsDocumentScanning.getClient(options)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "DScanner") },
                colors = TopAppBarDefaults.mediumTopAppBarColors() )
        },

        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    scanner.getStartScanIntent(context)
                        .addOnSuccessListener { intentSender ->
                            DScannerLauncher.launch(
                                IntentSenderRequest.Builder(intentSender).build()
                            )
                        }
                        .addOnFailureListener {
                            Log.d("TAG", "HomeScreen: ${it.message}")
                        }
                },
                text = {
                    Text(text = stringResource(R.string.scan))

                },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_camera),
                        contentDescription = null,
                    )
                }


            )
        },

        content = {
            Surface(modifier = Modifier.padding(it)) {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(items = docs) {
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(10.dp),
                            onClick = {
                                val pdfUri = it.uri
                                val pdfFileUri = FileProvider.getUriForFile(
                                    context,
                                    context.packageName + ".provider",
                                    pdfUri.toFile()
                                )
                                val browserIntent = Intent(Intent.ACTION_VIEW, pdfFileUri)
                                browserIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                context.startActivity(browserIntent)
                            }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxHeight(),
                                    painter = painterResource(id = R.drawable.ic_pdf),
                                    contentDescription = null,
                                )

                                val filepath = File(it.uri.path.toString())
                                val from = File(filepath, filepath.name)
                                val name = File(filepath, "test.pdf")
                                from.renameTo(name)
                                Text(text = name.name ?: "")
                            }
                        }
                    }
                }

            }
        }
    )

}

fun getScannerOptions():GmsDocumentScannerOptions {
    return GmsDocumentScannerOptions.Builder()
        .setPageLimit(3)
        .setGalleryImportAllowed(true)
        .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .build()
}

