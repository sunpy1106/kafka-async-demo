package com.example.kafkaasync;

import org.example.kafkaasync.MockNetworkClient;
import org.example.kafkaasync.RequestFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MockNetworkClientTest {

    private MockNetworkClient client;

    @BeforeEach
    void setUp() {
        client = new MockNetworkClient();
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    void testSuccessfulRequest() throws InterruptedException {
        RequestFuture<String> future = client.send("Test", () -> "Response: Test");

        assertTrue(future.await(2, TimeUnit.SECONDS));
        assertTrue(future.isDone());
        assertEquals("Response: Test", future.value());
    }

    @Test
    void testFailedRequest() throws InterruptedException {
        RequestFuture<String> future = client.send("Fail", () -> {
            throw new RuntimeException("Test failure");
        });

        assertTrue(future.await(2, TimeUnit.SECONDS));
        assertTrue(future.isDone());
        assertThrows(RuntimeException.class, future::value);
        assertEquals("Test failure", future.exception().getMessage());
    }
}