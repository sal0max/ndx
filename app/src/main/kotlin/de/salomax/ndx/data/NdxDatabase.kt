package de.salomax.ndx.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.arch.persistence.db.SupportSQLiteDatabase
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
                                                        Filter(null, 3, "Generic circular polarizer", "Polarizers add about 1½ Stops"),
                                                        Filter(null, 4, "Cokin Nuances ND 4", null),
                                                        Filter(null, 64, "Lee Little Stopper", null),
                                                        Filter(null, 1_024, "Lee Big Stopper", null),
                                                        Filter(null, 4_096, "Haida Slim PROII ND3.6", null),
                                                        Filter(null, 65_536, "Formatt Hitech Firecrest 16", "Super dark for long exposures during bright daylight."))
                                                )
                                        // default preferences
                                        getInstance(context)
                                                .prefDao()
                                                .insertAll(listOf(
                                                        Pref("EV_STEPS", "1"),
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
