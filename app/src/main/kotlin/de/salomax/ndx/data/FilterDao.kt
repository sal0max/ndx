package de.salomax.ndx.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FilterDao {

    @Query("SELECT *" +
            " FROM filters" +
            " ORDER BY (" +
            "    CASE" +
            "       WHEN (SELECT `VALUE` FROM prefs WHERE `KEY` IS '" + Pref.FILTER_SORT_ORDER + "') = '0' THEN FACTOR" +
            "       WHEN (SELECT `VALUE` FROM prefs WHERE `KEY` IS '" + Pref.FILTER_SORT_ORDER + "') = '1' THEN NAME" +
            "    END)" +
            " COLLATE NOCASE")
    fun getAll(): LiveData<List<Filter>?>

    @Query("SELECT * FROM filters WHERE id = :id")
    fun get(id: Long): LiveData<Filter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(filter: Filter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(filter: List<Filter>)

    @Query("DELETE FROM filters WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM filters")
    fun deleteAll()
}
