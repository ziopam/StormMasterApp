package com.example.stormmasterclient.helpers.WebSocket;

import android.os.Handler;
import android.os.Looper;

import com.example.stormmasterclient.helpers.API.APIConfig;

import java.io.Serializable;

import okhttp3.*;

/**
 * Client for interacting with the WebSocket.
 */
public class WebSocketClient {

    private WebSocket webSocket;
    private final OkHttpClient client = new OkHttpClient();
    private boolean isConnectionFailed = false;
    private final Request request;
    public IWebSocketMessageListener listener = null;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            webSocket = startNewConnection();
        }
    };

    public WebSocketClient(String roomCode, String token) {
        request = new Request.Builder()
                .url(APIConfig.BASE_URL_WS + "room/" + roomCode + "/")
                .addHeader("Authorization", "Token " + token)
                .build();
        webSocket = startNewConnection();
    }

    /**
     * Starts a new WebSocket connection.
     *
     * @return The WebSocket object.
     */
    private WebSocket startNewConnection(){
         return client.newWebSocket(request, new WebSocketListener() {
            /**
            * Called when the connection is opened.
            *
            * @param webSocket The WebSocket that was opened.
            * @param response  The response from the server.
            */
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                isConnectionFailed = false;
            }

            /**
             * Called when a message is received.
             *
             * @param webSocket The WebSocket that received the message.
             * @param text      The message that was received.
             */
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                sendMessageToListener(text);
            }

            /**
             * Called when the connection is closed.
             *
             * @param webSocket The WebSocket that was closed.
             * @param code      The status code of the closure.
             * @param reason    The reason for the closure.
             */
            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                sendMessageToListener("{'type': 'error', 'error_code':" + code + "}");
                webSocket.close(1000, null);
            }

            /**
             * Called when the connection fails.
             *
             * @param webSocket The WebSocket that failed.
             * @param t         The exception that caused the failure.
             * @param response  The response that was received.
             */
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();

                // Inform the user about the connection failure. Do it only once.
                if (!isConnectionFailed) {
                    sendMessageToListener("{'type': 'error', 'error_code': 4000}");
                }
                isConnectionFailed = true;

                reconnect();
            }
        });
    }

    /**
     * Reconnects to the WebSocket. Takes 3 seconds delay before reconnecting.
     */
    public void reconnect(){
        handler.postDelayed(reconnectRunnable, 3000);
    }

    /**
     * Sends a message to the listener.
     *
     * @param message The message to send.
     */
    private void sendMessageToListener(String message) {
        if (listener != null) {
            listener.onMessageReceived(message);
        }
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    /**
     * Closes the WebSocket connection.
     */
    public void closeWebSocket() {
        if (webSocket != null) {
            // Stop the reconnection process
            handler.removeCallbacks(reconnectRunnable);

            webSocket.close(1000, "Client closing connection");
        }
    }
}

