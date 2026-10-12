package com.nhuanmai.alittlecare.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.nhuanmai.alittlecare.R
import kotlinx.coroutines.tasks.await

// Nơi duy nhất trong app làm việc với Firebase Authentication và Google.
class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    // Người dùng đang đăng nhập, hoặc null nếu chưa đăng nhập.
    // Firebase tự nhớ phiên đăng nhập, mở lại app vẫn còn.
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Đăng nhập bằng Google. Lỗi (người dùng huỷ, mất mạng...) được ném ra để ViewModel xử lý.
    suspend fun signInWithGoogle(context: Context): FirebaseUser {
        // Bước 1: mở hộp chọn tài khoản Google, nhận ID token
        val googleOption = GetSignInWithGoogleOption
            .Builder(context.getString(R.string.default_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()
        val credential = CredentialManager.create(context)
            .getCredential(context, request)
            .credential

        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            throw IllegalStateException("Không nhận được thông tin đăng nhập Google")
        }
        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken

        // Bước 2: đưa ID token cho Firebase để đăng nhập vào app
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(firebaseCredential).await()
        return result.user ?: throw IllegalStateException("Firebase không trả về người dùng")
    }
}