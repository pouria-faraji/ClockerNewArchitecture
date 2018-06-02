package com.blacksite.clockernewarchitecture.model.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Clock(@PrimaryKey
            val uid: Int,
            val image: String,
            val imageWhite: String?,
            val number: Int?,
            val type: Int,
            val premium: Boolean = false) {
//    fun toGridItem(): GridItem {
//        return GridItem(image)
//    }

    companion object {
        const val FACE = 1
        const val DIAL = 2
        const val HAND = 3
    }
}