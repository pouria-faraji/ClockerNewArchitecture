package com.blacksite.clockernewarchitecture.model

class GridItem{
    var image: Int? = null
    var imageWhite: Int? = null
    constructor(image: Int) {
        this.image = image
    }
    constructor(image: Int, imageWhite:Int) {
        this.image = image
        this.imageWhite = imageWhite
    }
}