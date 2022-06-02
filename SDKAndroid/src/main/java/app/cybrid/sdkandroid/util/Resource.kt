package app.cybrid.sdkandroid.util

data class Resource<out T>(val status: Status, val data: T?, val message: String?, val code: Int?) {

  enum class Status { SUCCESS, ERROR, LOADING }

  companion object {

    fun <T> success(data: T, code: Int) : Resource<T> {
      return Resource(Status.SUCCESS, data, null, code)
    }

    fun <T> error(message:String, data: T? = null, code: Int? = 500) : Resource<T> {
      return Resource(Status.ERROR, data, message, code)
    }

    fun <T> loading() : Resource<T> {
      return Resource(Status.LOADING, null, null, null)
    }
  }
}