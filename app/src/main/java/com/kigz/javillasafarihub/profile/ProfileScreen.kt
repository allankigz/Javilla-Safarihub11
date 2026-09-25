package com.kigz.javillasafarihub.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.kigz.javillasafarihub.LocalLanguageManager
import com.kigz.javillasafarihub.localization.translate
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onTripsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val language = LocalLanguageManager.current.currentLanguage
    val isInspectionMode = LocalInspectionMode.current
    val currentUser = if (isInspectionMode) null else try { FirebaseAuth.getInstance().currentUser } catch (_: Exception) { null }
    
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var editedPhone by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(currentUser?.photoUrl) }
    var isSaving by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { photoUri = it }
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) tempCameraUri?.let { photoUri = it }
    }

    fun createImageUri(): Uri {
        val directory = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(directory, "temp_profile.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val currentDisplayName = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Safari Explorer"
    val email = currentUser?.email?.takeIf { it.isNotBlank() } ?: "Email not available"

    LaunchedEffect(currentUser?.uid) {
        if (!isInspectionMode) {
            currentUser?.uid?.let { uid ->
                try {
                    FirebaseDatabase.getInstance().reference.child("users").child(uid)
                        .addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                                editedPhone = snapshot.child("phone").getValue(String::class.java).orEmpty()
                            }
                            override fun onCancelled(error: com.google.firebase.database.DatabaseError) { }
                        })
                } catch (_: Exception) {}
            }
        }
    }

    LaunchedEffect(currentUser?.uid, currentDisplayName, email) {
        if (!isInspectionMode) {
            currentUser?.uid?.let { uid ->
                try {
                    FirebaseDatabase.getInstance().reference.child("users").child(uid)
                        .updateChildren(
                            mapOf(
                                "displayName" to currentDisplayName,
                                "email" to email,
                                "updatedAt" to System.currentTimeMillis()
                            )
                        )
                } catch (_: Exception) {}
            }
        }
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(text = translate("My Profile", language)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(enabled = isEditing) { showImageSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile photo",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                if (isEditing) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text(translate("Display Name", language)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = editedPhone,
                    onValueChange = { editedPhone = it },
                    label = { Text(translate("Phone Number", language)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                Text(
                    text = currentDisplayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = translate("Welcome to Javilla Safari Hub", language),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (isEditing) {
                        isSaving = true
                        val profileUpdates = userProfileChangeRequest {
                            displayName = editedName
                            this.photoUri = photoUri
                        }
                        val user = currentUser
                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { task ->
                                isSaving = false
                                if (task.isSuccessful) {
                                    val uid = user.uid
                                    try {
                                        FirebaseDatabase.getInstance().reference.child("users").child(uid)
                                            .updateChildren(mapOf("phone" to editedPhone.trim(), "updatedAt" to System.currentTimeMillis()))
                                    } catch (_: Exception) {}
                                    isEditing = false
                                    Toast.makeText(context, translate("Profile updated", language), Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, translate("Update failed: ", language) + "${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        isEditing = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Save profile" else "Edit profile"
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = if (isEditing) translate("Save Profile", language) else translate("Edit Profile", language))
                }
            }
            
            if (isEditing) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { isEditing = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(translate("Cancel", language))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = translate("Account Information", language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInfoRow(icon = Icons.Default.Email, title = translate("Email", language), value = email)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(icon = Icons.Default.Phone, title = translate("Phone", language), value = editedPhone.ifBlank { translate("Not provided", language) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileActionButton(icon = Icons.Default.Favorite, title = translate("My Favorites", language), onClick = onFavoritesClick)
            ProfileActionButton(icon = Icons.Default.Map, title = translate("My Trips", language), onClick = onTripsClick)
            ProfileActionButton(icon = Icons.Default.Settings, title = translate("Settings", language), onClick = onSettingsClick)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = translate("Logout", language))
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text(translate("Select Profile Photo", language)) },
            text = { Text(translate("Choose a photo from your gallery or take a new one using the camera.", language)) },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    val uri = createImageUri()
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(8.dp))
                    Text(translate("Camera", language))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    imagePicker.launch("image/*")
                }) {
                    Icon(Icons.Default.PhotoLibrary, null)
                    Spacer(Modifier.width(8.dp))
                    Text(translate("Gallery", language))
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.size(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ProfileActionButton(icon: ImageVector, title: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title)
            Spacer(modifier = Modifier.size(12.dp))
            Text(text = title)
        }
    }
}
