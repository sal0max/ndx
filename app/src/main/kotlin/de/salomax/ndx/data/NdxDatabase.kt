package de.salomax.ndx.data

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import de.salomax.ndx.R
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Database(entities = [(Filter::class), (Pref::class)], version = 2)
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
                            .addCallback(init(context))
                            .addMigrations(migration1To2(context))
                            .build()
                }
            }
            return INSTANCE!!
        }

        private fun init(context: Context) = object : RoomDatabase.Callback() {
            // pre-populate the database
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Single.fromCallable {
                    // add some filters
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
                    // add default preferences
                    getInstance(context)
                            .prefDao()
                            .insertAll(listOf(
                                    Pref(Pref.EV_STEPS, "3"),
                                    Pref(Pref.FILTER_SORT_ORDER, "0"))
                            )
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            }
        }

        private fun migration1To2(context: Context): Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Single.fromCallable {
                    // add default preferences
                    getInstance(context)
                            .prefDao()
                            .insertAll(listOf(
                                    Pref(Pref.ALARM_BEEP, "1"),
                                    Pref(Pref.ALARM_VIBRATE, "0"))
                            )
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
            }
        }

    }
}
