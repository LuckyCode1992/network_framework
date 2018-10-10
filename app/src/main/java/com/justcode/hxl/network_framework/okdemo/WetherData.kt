package com.justcode.hxl.network_framework.okdemo

import java.util.*

class WetherData {
    var result: Result? = null
    var reason: String? = null

    constructor()

}


class Result {
    var data: Data? = null

    constructor()
}

class Data {

    var realtime: Realtime? = null

    constructor()

}

class Realtime {
    var moon: String? = null
    var time: String? = null

    constructor()

}