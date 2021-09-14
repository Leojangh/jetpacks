package com.genlz.jetpacks

import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MockServerTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    "/api/recommend" -> MockResponse().apply {
                        setBody("")
                        addHeader("Content-Type", "application/json")
                    }
                    else -> MockResponse().apply {
                        setBody("")
                        addHeader("Content-Type", "application/json")
                    }
                }
            }
        }
    }

    fun mockResponse() {

    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}