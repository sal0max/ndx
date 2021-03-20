package de.salomax.ndx.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.salomax.ndx.BuildConfig
import de.salomax.ndx.R
import java.util.concurrent.Executors

@Database(entities = [(Filter::class)], version = 1, exportSchema = false)
abstract class NdxDatabase : RoomDatabase() {

    abstract fun filterDao(): FilterDao

    companion object {
        private var INSTANCE: NdxDatabase? = null

        /*
         * ATTENTION! Every migration runs only if an older db version was already installed.
         * Hence on db upgrade:
         * 1. provide migration
         * 2. add migration result also to #init()
         */
        fun getInstance(context: Context): NdxDatabase {
            if (INSTANCE == null) {
                synchronized(NdxDatabase::class) {
                    INSTANCE = Room
                            .databaseBuilder(context.applicationContext, NdxDatabase::class.java, "ndx.db")
                            .addCallback(init(context))
                            //.addMigrations(migration1To2(context))
                            .build()
                }
            }
            return INSTANCE!!
        }

        private fun init(context: Context) = object : RoomDatabase.Callback() {
            // pre-populate the database
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // add some filters
                val filters = mutableListOf(
                        Filter(null,
                                context.resources.getInteger(R.integer.preset_filterValue1),
                                context.getString(R.string.preset_filterName1),
                                context.getString(R.string.preset_filterInfo1)),
                        Filter(null,
                                context.resources.getInteger(R.integer.preset_filterValue3),
                                context.getString(R.string.preset_filterName3),
                                context.getString(R.string.preset_filterInfo3))
                )
                // add some more when in debug mode
                if (BuildConfig.DEBUG) {
                    filters.add(Filter(null,
                            context.resources.getInteger(R.integer.preset_filterValue2),
                            context.getString(R.string.preset_filterName2),
                            context.getString(R.string.preset_filterInfo2))
                    )
                    filters.add(Filter(null,
                            context.resources.getInteger(R.integer.preset_filterValue4),
                            context.getString(R.string.preset_filterName4),
                            context.getString(R.string.preset_filterInfo4))
                    )
                    filters.add(Filter(null,
                            context.resources.getInteger(R.integer.preset_filterValue5),
                            context.getString(R.string.preset_filterName5),
                            context.getString(R.string.preset_filterInfo5))
                    )
                    filters.add(Filter(null,
                            context.resources.getInteger(R.integer.preset_filterValue6),
                            context.getString(R.string.preset_filterName6),
                            context.getString(R.string.preset_filterInfo6))
                    )
                }
                // insert
                Executors.newSingleThreadScheduledExecutor().execute {
                    getInstance(context)
                            .filterDao()
                            .insertAll(filters)
                }
            }
        }

//        private fun migration1To2(context: Context): Migration = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                Single.fromCallable {
//                    getInstance(context)
//                            .prefDao()
//                            .insert(Pref(Pref.SHOW_WARNING, "0"))
//                }.subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe()
//            }
//        }

    }
}
