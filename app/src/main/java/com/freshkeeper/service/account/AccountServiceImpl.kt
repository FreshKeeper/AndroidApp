package com.freshkeeper.service.account

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import androidx.core.content.edit
import com.freshkeeper.R
import com.freshkeeper.model.Activity
import com.freshkeeper.model.DownloadableUserData
import com.freshkeeper.model.FoodItem
import com.freshkeeper.model.Household
import com.freshkeeper.model.ImageType
import com.freshkeeper.model.Membership
import com.freshkeeper.model.NotificationSettings
import com.freshkeeper.model.Picture
import com.freshkeeper.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

class AccountServiceImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : AccountService {
        private val auth: FirebaseAuth = Firebase.auth
        private val firestore = Firebase.firestore

        private val _logoutEvents = MutableSharedFlow<Unit>(replay = 0)
        override val logoutEvents: SharedFlow<Unit> = _logoutEvents

        init {
            val languageCode = Locale.getDefault().language
            if (languageCode.isNotEmpty()) {
                FirebaseAuth.getInstance().setLanguageCode(languageCode)
            } else {
                Log.e("AccountServiceImpl", "Language code is empty or null")
            }
        }

        override val currentUser: Flow<User?>
            get() =
                callbackFlow {
                    val listener =
                        FirebaseAuth.AuthStateListener { auth ->
                            val firebaseUser = auth.currentUser
                            if (firebaseUser?.isAnonymous == true && firebaseUser.email == null) {
                                trySend(null)
                            } else {
                                trySend(firebaseUser.toUser())
                            }
                        }
                    auth.addAuthStateListener(listener)
                    awaitClose { auth.removeAuthStateListener(listener) }
                }

        override fun hasUser(): Boolean = auth.currentUser != null

        override fun getUserProfile(): User = auth.currentUser.toUser()

        override fun getEmailForCurrentUser(): String {
            val currentUser = auth.currentUser
            return currentUser?.email
                ?: throw IllegalStateException("Current user is not logged in or email is unavailable")
        }

        override suspend fun getBiometricEnabled(): Boolean {
            val currentUser = auth.currentUser
            return if (currentUser != null) {
                val userId = currentUser.uid
                val userDocumentRef = firestore.collection("users").document(userId)
                try {
                    val userSnapshot = userDocumentRef.get().await()
                    userSnapshot.getBoolean("isBiometricEnabled") == true
                } catch (e: Exception) {
                    Log.e("AccountServiceImpl", "Error fetching biometric status", e)
                    false
                }
            } else {
                false
            }
        }

        override suspend fun getUserObject(): User {
            val currentUser = auth.currentUser
            return if (currentUser != null) {
                val userId = currentUser.uid
                val userDocumentRef = firestore.collection("users").document(userId)
                val userSnapshot = userDocumentRef.get().await()
                userSnapshot.toObject(User::class.java) ?: User()
            } else {
                User()
            }
        }

        override suspend fun getUserProfile(userId: String): User {
            val userDocumentRef = firestore.collection("users").document(userId)
            val userSnapshot = userDocumentRef.get().await()
            return userSnapshot.toObject(User::class.java) ?: User()
        }

        override suspend fun calculateDaysSince(createdAt: Long): Long {
            val creationDate =
                Instant
                    .ofEpochMilli(createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            val currentDate = LocalDate.now()
            return ChronoUnit.DAYS.between(creationDate, currentDate)
        }

        override suspend fun createAnonymousAccount() {
            auth.signInAnonymously().await()
        }

        override suspend fun updateDisplayName(newDisplayName: String) {
            val profileUpdates =
                userProfileChangeRequest {
                    displayName = newDisplayName
                }
            val user = auth.currentUser ?: return

            user.updateProfile(profileUpdates).await()

            firestore
                .collection("users")
                .document(user.uid)
                .update("displayName", newDisplayName)
                .await()
        }

        override suspend fun linkAccountWithEmail(
            email: String,
            password: String,
        ) {
            val credential = EmailAuthProvider.getCredential(email, password)
            auth.currentUser!!
                .linkWithCredential(credential)
                .await()
        }

        override suspend fun signInWithGoogle(idToken: String) {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential).await()
        }

        override suspend fun signUpWithEmail(
            email: String,
            password: String,
        ) {
            auth.createUserWithEmailAndPassword(email, password).await()
        }

        override suspend fun signInWithEmail(
            email: String,
            password: String,
        ) {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null && !firebaseUser.isEmailVerified) {
                    throw Exception(
                        "Email address not verified. " +
                            "Please verify your email before signing in.",
                    )
                }
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Sign-in failed", e)
                throw e
            }
        }

        override suspend fun signOut() {
            auth.signOut()
            FirebaseMessaging.getInstance().deleteToken()
            (context as? ComponentActivity)?.viewModelStore?.clear()
            _logoutEvents.emit(Unit)
        }

        override suspend fun changeEmail(newEmail: String) {
            try {
                auth.currentUser
                    ?.verifyBeforeUpdateEmail(newEmail)
                    ?.await()
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Failed to update email address", e)
                throw e
            }
        }

        override suspend fun resetPassword() {
            auth.sendPasswordResetEmail(auth.currentUser!!.email!!).await()
        }

        override suspend fun forgotPassword(email: String) {
            auth.sendPasswordResetEmail(email).await()
        }

        override suspend fun deleteAccount() {
            try {
                val currentUser = auth.currentUser
                val user = getUserObject()
                val userId = user.id

                if (userId.isNotEmpty()) {
                    if (user.householdId != null) {
                        val householdRef =
                            firestore.collection("households").document(user.householdId)
                        val householdDoc = householdRef.get().await()
                        if (householdDoc.exists()) {
                            val usersList = householdDoc.get("users") as? List<Any?> ?: emptyList()
                            val updatedUsersList = usersList.filter { it != userId }
                            householdRef.update("users", updatedUsersList).await()
                        }
                    }

                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .delete()
                        .await()

                    firestore
                        .collection("memberships")
                        .document(userId)
                        .delete()
                        .await()

                    firestore
                        .collection("households")
                        .whereEqualTo("ownerId", userId)
                        .get()
                        .await()
                        .documents
                        .forEach { household ->
                            firestore
                                .collection("households")
                                .document(household.id)
                                .delete()
                                .await()
                        }

                    listOf("foodItems", "activities").forEach { collection ->
                        firestore
                            .collection(collection)
                            .whereEqualTo("userId", userId)
                            .get()
                            .await()
                            .documents
                            .forEach { document ->
                                firestore
                                    .collection(collection)
                                    .document(document.id)
                                    .delete()
                                    .await()
                            }
                    }

                    firestore
                        .collection("users")
                        .document(userId)
                        .delete()
                        .await()

                    currentUser?.delete()?.await()
                    auth.signOut()
                }
            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                Log.e("AccountServiceImpl", "User needs to reauthenticate", e)
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Error deleting account", e)
                throw e
            }
        }

        private fun FirebaseUser?.toUser(): User =
            if (this == null) {
                User()
            } else {
                User(
                    id = this.uid,
                    email = this.email ?: "",
                    provider = this.providerId,
                    displayName = this.displayName ?: "",
                    isAnonymous = this.isAnonymous,
                    isEmailVerified = this.isEmailVerified,
                    isBiometricEnabled = false,
                )
            }

        override suspend fun sendEmailVerification() {
            try {
                val user = auth.currentUser
                user?.reload()?.await()

                if (user != null) {
                    val isVerified = user.isEmailVerified

                    firestore
                        .collection("users")
                        .document(user.uid)
                        .update("isEmailVerified", isVerified)
                        .addOnFailureListener { e ->
                            Log.e(
                                "AccountServiceImpl",
                                "Error updating isEmailVerified: ${e.message}",
                                e,
                            )
                        }

                    if (!isVerified) {
                        user.sendEmailVerification().await()
                    } else {
                        Log.e("AccountServiceImpl", "Email is already verified")
                    }
                }
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Failed to send email verification", e)
                throw e
            }
        }

        override suspend fun updateProfilePicture(base64Image: String) {
            val userId = auth.currentUser?.uid ?: throw Exception("User is not logged in")

            try {
                val profilePicture = Picture(image = base64Image, type = ImageType.BASE64)

                firestore
                    .collection("users")
                    .document(userId)
                    .update("profilePicture", profilePicture)
                    .await()
            } catch (e: Exception) {
                Log.e("AccountServiceImpl", "Error updating profile picture", e)
                throw e
            }
        }

        override suspend fun getProfilePicture(userId: String): Picture? =
            try {
                val userDoc =
                    firestore
                        .collection("users")
                        .document(userId)
                        .get()
                        .await()
                userDoc.get("profilePicture")?.let { map ->
                    val profileMap = map as? Map<*, *>
                    Picture(
                        image = profileMap?.get("image") as? String,
                        type = profileMap?.get("type") as? ImageType,
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    "AccountService",
                    "Error retrieving profile picture for userId: $userId",
                    e,
                )
                null
            }

        override suspend fun downloadUserData(userId: String) {
            val fileName = "user_data.json"

            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { userDoc ->
                    if (userDoc != null) {
                        val user = userDoc.toObject(User::class.java)
                        if (user != null) {
                            val activitiesTask =
                                firestore
                                    .collection("activities")
                                    .whereEqualTo("userId", userId)
                                    .get()

                            val foodItemsTask =
                                firestore
                                    .collection("foodItems")
                                    .whereEqualTo("userId", userId)
                                    .get()

                            val householdTask =
                                user.householdId?.let { householdId ->
                                    firestore
                                        .collection("households")
                                        .document(householdId)
                                        .get()
                                } ?: Tasks.forResult(null)

                            val notificationSettingsTask =
                                firestore
                                    .collection("notificationSettings")
                                    .document(userId)
                                    .get()

                            Tasks
                                .whenAllComplete(
                                    activitiesTask,
                                    foodItemsTask,
                                    householdTask,
                                    notificationSettingsTask,
                                ).addOnCompleteListener {
                                    val activities =
                                        activitiesTask.result?.toObjects(
                                            Activity::class.java,
                                        ) ?: emptyList()
                                    val foodItems =
                                        foodItemsTask.result?.toObjects(
                                            FoodItem::class.java,
                                        ) ?: emptyList()
                                    val household =
                                        householdTask.result?.toObject(
                                            Household::class.java,
                                        )
                                    val notificationSettings =
                                        notificationSettingsTask.result.toObject(
                                            NotificationSettings::class.java,
                                        )

                                    val downloadableData =
                                        DownloadableUserData(
                                            user,
                                            activities,
                                            foodItems,
                                            household,
                                            notificationSettings,
                                        )

                                    val jsonString = Gson().toJson(downloadableData)

                                    val file = File(context.cacheDir, fileName)
                                    file.writeText(jsonString)

                                    val uri =
                                        FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            file,
                                        )

                                    val intent =
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "application/json"
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }

                                    val chooser =
                                        Intent
                                            .createChooser(
                                                intent,
                                                context.getString(R.string.download_data_title),
                                            ).apply {
                                                addFlags(
                                                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                                        Intent.FLAG_ACTIVITY_NEW_TASK,
                                                )
                                            }
                                    context.startActivity(chooser)
                                }
                        }
                    }
                }.addOnFailureListener {
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.download_data_error),
                            Toast.LENGTH_SHORT,
                        ).show()
                }
        }

        override suspend fun saveUserToFirestore(
            userId: String,
            email: String,
        ) {
            val membership = Membership()

            firestore
                .collection("memberships")
                .document(userId)
                .set(membership)
                .addOnSuccessListener {
                    val user =
                        User(
                            id = userId,
                            email = email,
                            createdAt = System.currentTimeMillis(),
                            provider = "email",
                        )

                    firestore
                        .collection("users")
                        .document(userId)
                        .set(user)
                        .addOnFailureListener { e ->
                            Log.e("SignUp", "Error when saving the user: ${e.message}", e)
                        }

                    val notificationSettings =
                        NotificationSettings(
                            dailyNotificationTime = LocalTime.of(12, 0).toString(),
                            timeBeforeExpiration = 2,
                            dailyReminders = false,
                            foodAdded = false,
                            householdChanges = false,
                            foodExpiring = false,
                            tips = false,
                            statistics = false,
                        )

                    firestore
                        .collection("notificationSettings")
                        .document(userId)
                        .set(notificationSettings)
                        .addOnFailureListener { e ->
                            Log.e(
                                "NotificationSettings",
                                "Error saving notification settings: ${e.message}",
                                e,
                            )
                        }
                }.addOnFailureListener { e ->
                    Log.e("SignUp", "Error when creating the membership: ${e.message}", e)
                }
        }

        override suspend fun updateBiometricEnabled(isEnabled: Boolean) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                val userRef = firestore.collection("users").document(userId)
                try {
                    userRef.update("isBiometricEnabled", isEnabled).await()
                    val prefs =
                        context.getSharedPreferences(
                            "user_preferences",
                            Context.MODE_PRIVATE,
                        )
                    prefs.edit { putBoolean("biometric_enabled", isEnabled) }
                } catch (e: Exception) {
                    Log.e("AccountServiceImpl", "Error updating biometric status", e)
                    throw e
                }
            }
        }
    }
