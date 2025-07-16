package ge.zgrdzelidze.messenger.utils


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Long.toReadableDate(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 1 -> "ახლახანს"
        minutes < 60 -> "$minutes წუთის წინ"
        hours < 24 -> "$hours საათის წინ"
        else -> SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(this))
    }
}