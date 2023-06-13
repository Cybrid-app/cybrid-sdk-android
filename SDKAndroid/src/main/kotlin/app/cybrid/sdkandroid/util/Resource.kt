package app.cybrid.sdkandroid.util

import okhttp3.Response
import okhttp3.ResponseBody

data class Resource<out T>(val status: Status, val data: T?, val message: String?, val code: Int?, val raw: Response?) {

  enum class Status { SUCCESS, ERROR, LOADING }

  companion object {

    fun <T> success(data: T, code: Int) : Resource<T> {
      return Resource(Status.SUCCESS, data, null, code, null)
    }

    fun <T> error(message:String, data: T? = null, code: Int? = 500, raw: Response? = null) : Resource<T> {
      return Resource(Status.ERROR, data, message, code, raw)
    }

    fun <T> loading() : Resource<T> {
      return Resource(Status.LOADING, null, null, null, null)
    }
  }
}