package com.outsystems.odc.wsserver.plugin;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.InetSocketAddress;

/**
 * This class echoes a string called from JavaScript.
 */
public class OdcWsServer extends CordovaPlugin {

    private static final int SERVER_PORT = 8080;
    private static final String TAG = "OdcWsServer";
    private WebSocketServer mServer;
    private boolean isServerRunning = false;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startServer")) {
            String ip = args.getString(0);
            int port = args.getInt(1);
            openConnectionServer(callbackContext, ip, port);
            return true;
        }

        if (action.equals("sendColor")) {
            String color = args.getString(0);
            sendColorMessage(callbackContext, color);
            return true;
        }
        return false;
    }

    public void openConnectionServer(final CallbackContext callbackContext, String ip, int port) {
        if (mServer == null) {
            mServer = new WebSocketServer(new InetSocketAddress(ip, port > 0 ? port : SERVER_PORT)) {

                @Override
                public void onOpen(WebSocket conn, ClientHandshake handshake) {
                    conn.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
                    Log.i(TAG, "new connection to " + conn.getRemoteSocketAddress());
                }

                @Override
                public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                    Log.i(TAG, "closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
                }

                @Override
                public void onMessage(WebSocket conn, final String message) {
                    Log.i(TAG, "received message from " + conn.getRemoteSocketAddress() + ": " + message);
                }

                @Override
                public void onError(WebSocket conn, Exception ex) {
                    if (conn != null) {
                        Log.i(TAG, "an error occured on connection " + conn.getRemoteSocketAddress() + ":" + ex);
                    }
                }

                @Override
                public void onStart() {
                    callbackContext.success();
                    isServerRunning = true;
                    Log.i(TAG, "server started successfully");
                }
            };
            mServer.start();
            Toast.makeText(cordova.getActivity(), "Service Start", Toast.LENGTH_LONG).show();
        }


    }

    private void sendColorMessage(CallbackContext callbackContext, String color) {
        if (mServer != null && isServerRunning) {
            mServer.broadcast(color);
            callbackContext.success();
        } else {
            callbackContext.error("WebSocket Server isn't running.");
        }
    }

}
