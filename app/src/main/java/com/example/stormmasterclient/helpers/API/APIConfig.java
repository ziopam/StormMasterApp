package com.example.stormmasterclient.helpers.API;

/**
 * Configuration class for API settings.
 */
public class APIConfig {

    /**
     * The base URL for the API.
     */
    public static final String BASE_URL = "http://158.160.130.4:8000/";

    /**
     * The base URL for the WebSocket.
     */
    public static final String BASE_URL_WS = "ws://" + BASE_URL.replace("http://", "") + "ws/";
}
