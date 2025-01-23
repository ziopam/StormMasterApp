package com.example.stormmasterclient.helpers.WebSocket;

import com.example.stormmasterclient.helpers.API.APIConfig;

import okhttp3.*;

public class WebSocketClient {

    private final WebSocket webSocket;
    public IWebSocketListener listener = null;

    public WebSocketClient(String roomCode, String token) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(APIConfig.BASE_URL_WS + "room/" + roomCode + "/")
                .addHeader("Authorization", "Token " + token)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("Connected to WebSocket!");

            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                System.out.println("Received: " + text);
                if(listener != null) {
                    listener.onMessageReceived(text);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println("Closing WebSocket: " + reason);
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
            }
        });
    }

    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send("{\"message\": \"" + message + "\"}");
        }
    }

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Client closing connection");
        }
    }
}

