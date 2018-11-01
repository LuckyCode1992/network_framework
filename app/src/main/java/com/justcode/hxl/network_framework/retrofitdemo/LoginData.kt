package com.justcode.hxl.network_framework.retrofitdemo

data class LoginRequest(var username: String, var password: String)

data class LoginResult(
        var userId: String? = null,
        var username: String? = null,
        var nickname: String? = null,
        var imAccId: String? = null,
        var imToken: String? = null,
        var headPortrait: String? = null,
        var loginTime: String? = null,
        var oldToken: String? = "ptyh-persistence-oldToken",
        var accessToken: String? = null,
        var realName: String? = null,
        var certNo: String? = null,
        var nation: String? = null,
        var registeredAddr: String? = null,
        var birthday: String? = null,
        var gender: String? = null, //性别 M 男 F女
        var age: String? = null,
        var areaFullName: String? = null
)