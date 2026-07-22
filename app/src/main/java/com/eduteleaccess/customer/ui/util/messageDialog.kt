package com.eduteleaccess.customer.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DismissSuccessDialog(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit,
    title: String = "Success",
) {

    if (showDialog) {

        AlertDialog(

            onDismissRequest = {
                onDismiss()
            },

            icon = {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(52.dp)
                )
            },

            title = {

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },

            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = message,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }

            },

            confirmButton = {
                Button(
                    onClick = {
                        // dismiss dialog
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        text = "Done",
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            shape = RoundedCornerShape(8.dp),

            containerColor = Color.White
        )
    }
}

@Composable
fun DismissFailureDialog(
    showDialog: Boolean,
    message: String,
    onDismiss: () -> Unit,
    title: String = "Failed"
) {


    if (showDialog) {

        AlertDialog(

            onDismissRequest = {
                onDismiss()
            },

            icon = {

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color(0xFF990000),
                    modifier = Modifier.size(52.dp)
                )
            },

            title = {

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },

            text = {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = message,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }


            },

            confirmButton = {
                Button(
                    onClick = {
                        // dismiss dialog
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF990000),
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        text = "Close",
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            shape = RoundedCornerShape(8.dp),

            containerColor = Color.White
        )
    }
}