package com.blacksite.clockernewarchitecture.model.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.blacksite.clockernewarchitecture.model.GridItem

@Entity
class Clock(@PrimaryKey
            val uid: Int,
            val image: String,
            val imageWhite: String?,
            val number: Int?,
            val type: Int) {
    fun toGridItem(): GridItem {
        return GridItem(image)
    }

    companion object {
        const val FACE = 1
        const val DIAL = 2
        const val HAND = 3
    }
}