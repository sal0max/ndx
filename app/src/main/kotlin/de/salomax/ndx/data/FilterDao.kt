package de.salomax.ndx.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface FilterDao {

    @Query("SELECT *" +
            " FROM filters" +
            " ORDER BY (" +
            "    CASE" +
            "       WHEN (SELECT `VALUE` FROM prefs WHERE `KEY` IS 'FILTER_SORT_ORDER') = '0' THEN FACTOR" +
            "       WHEN (SELECT `VALUE` FROM prefs WHERE `KEY` IS 'FILTER_SORT_ORDER') = '1' THEN NAME" +
            "    END)" +
            " COLLATE NOCASE")
    fun getAll(): Flowable<List<Filter>>

    @Query("SELECT * FROM filters WHERE id = :id")
    fun get(id: Long): Flowable<Filter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(filter: Filter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(filter: List<Filter>)

    @Query("DELETE FROM filters WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM filters")
    fun deleteAll()
}
