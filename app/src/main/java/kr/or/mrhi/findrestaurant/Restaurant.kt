package kr.or.mrhi.findrestaurant

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
class Restaurant(
    val id: String,
    var language: String,
    var name: String,
    var address: String,
    var subwayInfo: String,
    var openingHours: String,
    var webPage: String,
    var phone: String,
    var menu: String) : Parcelable {

    companion object : Parceler<Restaurant> {
        override fun create(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun Restaurant.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(language)
            parcel.writeString(name)
            parcel.writeString(address)
            parcel.writeString(subwayInfo)
            parcel.writeString(openingHours)
            parcel.writeString(webPage)
            parcel.writeString(phone)
            parcel.writeString(menu)
        }
    }
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
    )
}