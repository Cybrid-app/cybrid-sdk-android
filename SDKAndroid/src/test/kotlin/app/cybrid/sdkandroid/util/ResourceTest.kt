package app.cybrid.sdkandroid.util

import org.junit.Test
import org.junit.Assert

class ResourceTest {

    @Test
    fun successTest() {

        val success = Resource.success("success", 200)
        Assert.assertNotNull(success)
        Assert.assertEquals(success.status, Resource.Status.SUCCESS)
        Assert.assertNotNull(success.data)
        Assert.assertEquals(success.data, "success")
        Assert.assertEquals(success.code, 200)
        Assert.assertNull(success.message)
    }

    @Test
    fun errorTest() {

        val error = Resource.error("error", null)
        Assert.assertNotNull(error)
        Assert.assertEquals("error", error.message)
        Assert.assertNull(error.data)
    }

    @Test
    fun resourceLoadingTest() {

        val loading:Resource<String> = Resource.loading()
        Assert.assertNotNull(loading)
    }
}