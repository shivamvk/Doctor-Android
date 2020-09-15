package io.shivamvk.networklibrary.api

import android.util.Log
import com.google.gson.JsonObject
import io.shivamvk.networklibrary.models.BaseModel
import io.shivamvk.networklibrary.utils.CustomProgressDialog
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ApiManager
    (
    private val mUrl: String,
    private val apiService: ApiService,
    private val dataModel: BaseModel?,
    private val apiListener: ApiManagerListener,
    private val customProgressDialog: CustomProgressDialog?
) {
    fun doPOSTAPICall(jsonObject: JsonObject) {
        if (customProgressDialog != null)
            customProgressDialog?.show()
        apiService.doPostApiCall(mUrl, jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    customProgressDialog?.dismiss()
                    apiListener.onFailure(dataModel, e)
                }

                override fun onNext(responseBody: ResponseBody?) {
                    customProgressDialog?.dismiss()
                    if (responseBody == null) {
                        return
                    }
                    apiListener.onSuccess(dataModel, responseBody.string());
                }
            })
    }

    fun doPUTAPICall(jsonObject: JsonObject) {
        if (customProgressDialog != null)
            customProgressDialog?.show()
        apiService.doPutApiCall(mUrl, jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    customProgressDialog?.dismiss()
                    apiListener.onFailure(dataModel, e)
                }

                override fun onNext(responseBody: ResponseBody?) {
                    customProgressDialog?.dismiss()
                    if (responseBody == null) {
                        return
                    }
                    apiListener.onSuccess(dataModel, responseBody.string());
                }
            })
    }

    fun doPostAPIMultiPartCall(params: List<MultipartBody.Part>){
        if (customProgressDialog != null)
            customProgressDialog?.show()
        apiService.doPostApiMultipartCall(mUrl, params)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    customProgressDialog?.dismiss()
                    apiListener.onFailure(dataModel, e)
                }

                override fun onNext(responseBody: ResponseBody?) {
                    customProgressDialog?.dismiss()
                    if (responseBody == null) {
                        return
                    }
                    apiListener.onSuccess(dataModel, responseBody.string());
                }
            })
    }

    fun doGETAPICall() {
        if (customProgressDialog != null)
            customProgressDialog?.show()
        apiService.doGetApiCall(mUrl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Subscriber<ResponseBody?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    if (customProgressDialog != null)
                        customProgressDialog?.dismiss()
                    apiListener.onFailure(dataModel, e)
                }

                override fun onNext(responseBody: ResponseBody?) {
                    if (customProgressDialog != null)
                        customProgressDialog?.dismiss()
                    if (responseBody == null) {
                        return
                    }
                    //Log.i("ApiManager", "$mUrl : ${responseBody.string()}")
                    apiListener.onSuccess(dataModel, responseBody.string());
                }
            })
    }

}