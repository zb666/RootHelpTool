package com.example.mechrevo.roothelptool

import android.content.Intent
import android.net.Uri
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    var lazyValue = lazy { "helloLazyValue" }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.example.mechrevo.roothelptool", appContext.packageName)

        "kotlin".let {
            println("子富川")
            hashCode()
            "$it"
        }

        "kotlin".apply {

        }

        "kotlin".takeUnless {
            it.length>0
        }

        var time = 5
        for (index in 0..time-1){
            val index1 = index;
        }

        lazyValue?.run {
            hashCode()

        }


    }
}
