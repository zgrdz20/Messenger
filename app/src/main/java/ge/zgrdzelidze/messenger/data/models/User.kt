package ge.zgrdzelidze.messenger.data.models


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val nickname: String = "",
    val profession: String = "",
    val profileImageUrl: String = ""
) : Parcelable