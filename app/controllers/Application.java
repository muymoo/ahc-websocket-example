package controllers;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import play.Logger;
import play.libs.ws.WS;
import play.mvc.Controller;
import play.mvc.Result;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.websocket.WebSocket;
import com.ning.http.client.websocket.WebSocketTextListener;
import com.ning.http.client.websocket.WebSocketUpgradeHandler;

public class Application extends Controller
{

    public static Result socket()
    {

        BoundRequestBuilder brb = ((AsyncHttpClient) WS.client().getUnderlying()).prepareGet("ws://echo.websocket.org");

        WebSocketUpgradeHandler.Builder webSocketBuilder = new WebSocketUpgradeHandler.Builder();

        WebSocketTextListener webSocketTextListener = new WebSocketTextListener()
        {

            @Override
            public void onOpen(WebSocket websocket)
            {
                Logger.debug("WebSocket opened! Sending message to echo...");
                websocket.sendTextMessage("Echo me back.");
            }

            @Override
            public void onError(Throwable t)
            {
                Logger.error("Error: " + t.getMessage());
            }

            @Override
            public void onClose(WebSocket websocket)
            {
                Logger.debug("Closed Websocket");
            }

            @Override
            public void onMessage(String message)
            {
                Logger.debug("Got message: " + message);
            }

            @Override
            public void onFragment(String fragment, boolean last)
            {
                // Do nothing
            }
        };

        WebSocketUpgradeHandler webSocketUpgradeHandler = webSocketBuilder.addWebSocketListener(webSocketTextListener)
                .build();
        try
        {
            brb.execute(webSocketUpgradeHandler).get();
        }
        catch (InterruptedException | ExecutionException | IOException e)
        {
            Logger.error("Error upgrading to WebSocket: " + e.getMessage());
        }
        return ok("Creating WebSocket");
    }
}
