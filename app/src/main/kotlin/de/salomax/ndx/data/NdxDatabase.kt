package de.salomax.ndx.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.arch.persistence.db.SupportSQLiteDatabase
import de.salomax.ndx.R
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Database(entities = [(Filter::class), (Pref::class)], version = 1)
abstract class NdxDatabase : RoomDatabase() {

    abstract fun filterDao(): FilterDao
    abstract fun prefDao(): PrefDao

    companion object {
        private var INSTANCE: NdxDatabase? = null

        fun getInstance(context: Context): NdxDatabase {
            if (INSTANCE == null) {
                synchronized(NdxDatabase::class) {
                    INSTANCE = Room
                            .databaseBuilder(context.applicationContext, NdxDatabase::class.java, "ndx.db")
                            .addCallback(object : RoomDatabase.Callback() {
                                // pre-populate the database
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    Single.fromCallable {
                                        // some filters
                                        getInstance(context)
                                                .filterDao()
                                                .insertAll(listOf(
                                                        Filter(null,
                                                                context.resources.getInteger(R.integer.preset_filterValue1),
                                                                context.getString(R.string.preset_filterName1),
                                                                context.getString(R.string.preset_filterInfo1)),
                                                        Filter(null,
                                                                context.resources.getInteger(R.integer.preset_filterValue2),
                                                                context.getString(R.string.preset_filterName2),
                                                                context.getString(R.string.preset_filterInfo2)),
                                                        Filter(null,
                                                                context.resources.getInteger(R.integer.preset_filterValue3),
                                                                context.getString(R.string.preset_filterName3),
                                                                context.getString(R.string.preset_filterInfo3)),
                                                        Filter(null,
                                                                context.resources.getInteger(R.integer.preset_filterValue4),
                                                                context.getString(R.string.preset_filterName4),
                                                                context.getString(R.string.preset_filterInfo4)),
                                                        Filter(null,
                                                                context.resources.getInteger(R.integer.preset_filterValue5),
                                                                context.getString(R.string.preset_filterName5),
                                                                context.getString(R.string.preset_filterInfo5)),
                                                        Filter(null,
                                                                context.resources.getInteger(R.integer.preset_filterValue6),
                                                                context.getString(R.string.preset_filterName6),
                                                                context.getString(R.string.preset_filterInfo6)))
                                                )
                                        // default preferences
                                        getInstance(context)
                                                .prefDao()
                                                .insertAll(listOf(
                                                        Pref("EV_STEPS", "3"),
                                                        Pref("FILTER_SORT_ORDER", "0"))
                                                )
                                    }.subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe()
                                }
                            })
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}
