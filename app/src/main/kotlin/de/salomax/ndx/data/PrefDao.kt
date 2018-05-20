package de.salomax.ndx.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface PrefDao {

    @Query("SELECT * FROM prefs")
    fun getAll(): Flowable<List<Pref>>

    @Query("SELECT `VALUE` FROM prefs WHERE `KEY` = 'EV_STEPS' LIMIT 1")
    fun getEvSteps(): Flowable<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pref: Pref)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(prefs: List<Pref>)

    @Query("DELETE FROM prefs")
    fun deleteAll()
}
