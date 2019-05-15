package de.salomax.ndx.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filters")
data class Filter(@PrimaryKey(autoGenerate = true) var id: Long?,
                  @ColumnInfo(name = "FACTOR") val factor: Int,
                  @ColumnInfo(name = "NAME") val name: String,
                  @ColumnInfo(name = "INFO") val info: String?) : Parcelable, Comparable<Filter> {

    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeInt(factor)
        parcel.writeString(name)
        parcel.writeString(info)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Filter> {
        override fun createFromParcel(parcel: Parcel): Filter {
            return Filter(parcel)
        }

        override fun newArray(size: Int): Array<Filter?> {
            return arrayOfNulls(size)
        }
    }

    override fun compareTo(other: Filter): Int {
        if (this.id != null && other.id != null) {
            if (this.id!! > other.id!!) return 1
            if (this.id!! < other.id!!) return -1
        } else if (this.id != null && other.id == null) {
            return 1
        } else if (this.id == null && other.id != null) {
            return -1
        }
        return 0
    }

}
