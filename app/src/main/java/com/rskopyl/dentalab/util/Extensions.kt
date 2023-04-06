package com.rskopyl.dentalab.util

import android.app.Activity
import android.content.res.Resources
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.annotation.AttrRes
import androidx.core.content.getSystemService
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun <T> Flow<T>.collectOnLifecycle(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state) {
            collect(collector)
        }
    }
}

operator fun <T : ViewModel> ViewModelProvider.Factory.Companion.invoke(
    provider: () -> T
): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return provider() as T
        }
    }
}

fun Activity.hideSoftKeyboard() {
    val focusedView = currentFocus ?: return
    getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(
        focusedView.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

fun Resources.Theme.resolveAttribute(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}

fun <T> ArrayAdapter<T>.submitItems(items: Collection<T>) {
    clear()
    addAll(items)
    notifyDataSetChanged()
}

fun SimpleDateFormat.parseOrNull(source: String): Date? {
    return try {
        parse(source)
    } catch (e: ParseException) {
        null
    }
}

fun Date.toLocalDateTime(timeZone: TimeZone): LocalDateTime =
    Instant.fromEpochMilliseconds(time).toLocalDateTime(timeZone)

fun LocalDate.toJavaDate(): Date =
    Date(atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds())

fun LocalTime.toJavaDate(): Date =
    Date(toMillisecondOfDay().toLong())