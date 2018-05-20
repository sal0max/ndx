package de.salomax.ndx.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "prefs")
data class Pref(@PrimaryKey @ColumnInfo(name = "KEY") val key: String,
                @ColumnInfo(name = "VALUE") val value: String)
