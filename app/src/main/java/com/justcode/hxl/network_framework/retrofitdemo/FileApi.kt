package com.ptyh.smartyc.corelib.http

import com.justcode.hxl.network_framework.retrofitdemo.UploadResult
import com.justcode.hxl.networkframework.retrofit.BussinessResult
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface FileApi {

    @Multipart
    @POST("api/file/{bucket}")
    fun upload(@Path("bucket") bucket: String, @Part partList: List<MultipartBody.Part>): Observable<BussinessResult<List<UploadResult>>>

    /**
     * 下载
     */
    @Streaming
    @GET
    fun download(@Url url: String): Observable<ResponseBody>

}