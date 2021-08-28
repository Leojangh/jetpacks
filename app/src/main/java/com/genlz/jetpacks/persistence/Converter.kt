package com.genlz.jetpacks.persistence

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ProvidedTypeConverter
class Converter @Inject constructor(
    private val offset: ZoneOffset,
) {

    companion object {
        private const val TAG = "Converter"
    }

    @TypeConverter
    fun LocalDateTime.toEpochSecond(): Long {
        return this.toEpochSecond(offset)
    }

    @TypeConverter
    fun Long.toLocalDataTime(): LocalDateTime? {
        return LocalDateTime.ofEpochSecond(this, 0, offset)
    }
}