package com.ruimendes.chat.data.database

import androidx.sqlite.SQLiteException
import com.ruimendes.core.domain.util.DataError
import com.ruimendes.core.domain.util.Result

suspend inline fun <T> safeDatabaseUpdate(update: () -> T): Result<T, DataError.Local> {
    return try {
        Result.Success(update())
    } catch (_: SQLiteException) {
        Result.Failure(DataError.Local.DISK_FULL)
    }
}