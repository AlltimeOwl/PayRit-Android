package com.alltimeowl.payrit.data.model

import android.os.Parcel
import android.os.Parcelable

data class PromiseData(
    val name: String?,
    val phoneNumber: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(phoneNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PromiseData> {
        override fun createFromParcel(parcel: Parcel): PromiseData {
            return PromiseData(parcel)
        }

        override fun newArray(size: Int): Array<PromiseData?> {
            return arrayOfNulls(size)
        }
    }
}
