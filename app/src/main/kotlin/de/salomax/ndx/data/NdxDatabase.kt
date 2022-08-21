package de.salomax.ndx.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.salomax.ndx.BuildConfig
import de.salomax.ndx.R
import java.util.concurrent.Executors

@Database(entities = [(Filter::class)], version = 2, exportSchema = false)
abstract class NdxDatabase : RoomDatabase() {

    abstract fun filterDao(): FilterDao

    companion object {
        private var INSTANCE: NdxDatabase? = null

        /*
         * ATTENTION! Every migration runs only if an older db version was already installed.
         * Hence, on db upgrade:
         * 1. provide migration
         * 2. add migration result also to #init()
         */
        fun getInstance(context: Context): NdxDatabase {
            if (INSTANCE == null) {
                synchronized(NdxDatabase::class) {
                    INSTANCE = Room
                            .databaseBuilder(context.applicationContext, NdxDatabase::class.java, "ndx.db")
                            .addCallback(init(context))
                            .addMigrations(MIGRATION_1_2)
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
                                null,
                                context.getString(R.string.preset_filterInfo1)),
                        Filter(null,
                                context.resources.getInteger(R.integer.preset_filterValue3),
                                context.getString(R.string.preset_filterName3),
                                null,
                                context.getString(R.string.preset_filterInfo3))
                )
                // add some more when in debug mode
                if (BuildConfig.DEBUG) {
                    filters.add(Filter(null,
                            context.resources.getInteger(R.integer.preset_filterValue2),
                            context.getString(R.string.preset_filterName2),
                            72,
                            context.getString(R.string.preset_filterInfo2))
                    )
                    filters.add(Filter(null,
                            context.resources.getInteger(R.integer.preset_filterValue4),
                            context.getString(R.string.preset_filterName4),
                            100,
                            context.getString(R.string.preset_filterInfo4))
                    )
                    filters.add(Filter(null,
                            context.resources.getInteger(R.integer.preset_filterValue5),
                            context.getString(R.string.preset_filterName5),
                            100,
                            context.getString(R.string.preset_filterInfo5))
                    )
                    filters.add(Filter(null,
                            context.resources.getInteger(R.integer.preset_filterValue6),
                            context.getString(R.string.preset_filterName6),
                            150,
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

       private val MIGRATION_1_2 = object : Migration(1, 2) {
          override fun migrate(database: SupportSQLiteDatabase) {
             database.execSQL("ALTER TABLE filters ADD COLUMN SIZE INTEGER")
          }
       }

    }
}
