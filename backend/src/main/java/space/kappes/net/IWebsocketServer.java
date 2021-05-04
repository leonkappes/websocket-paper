package space.kappes.net;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import space.kappes.WebsocketManager;
import space.kappes.entity.User;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Responsible for handling the Websocketserver, e.g. all incoming connections
 */
public class IWebsocketServer extends WebSocketServer {
    private final int port;

    /**
     * Initializes the Socketserver
     */
    public IWebsocketServer(int port) {
        super(new InetSocketAddress(port));
        this.port = port;
    }


    /**
     * Called when the Socketserver shuts down, stops the server
     * @throws InterruptedException thrown when there was an error closing the Socketserver
     * @throws IOException thrown when there was an error closing the Socketserver
     */
    public void destroy() throws InterruptedException, IOException {
        this.stop();
    }

    /**
     * Called when a new Client opens a connection and the handshake is done
     * @param conn New connection, which has just done the required handshake
     * @param handshake Handshake itself, containing infos over the http connection
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Create new User object
        User user = new User(conn);
        // Add user to main register, used for calling all users
        WebsocketManager.getInstance().addUser(user);
        // Attach the identifier to the client so we can later differentiate the clients
        conn.setAttachment(user.getIdentifier());
    }

    /**
     * @param conn Connection object of the client, many methods not longer working because of disconnected connection
     * @param code disconnect code
     * @param reason Reason for the disconnect
     * @param remote true when the client disconnected himsel, false if the server disconnected him
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Remove the user by its attached identifier
        WebsocketManager.getInstance().removeUser(WebsocketManager.getInstance().getUser(conn.getAttachment()));
    }

    /**
     * Called whenever a message is received by a client
     * @param conn Connection instance of the client
     * @param message Message received from client
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        User user = WebsocketManager.getInstance().getUser(conn.getAttachment());
        WebsocketManager.getInstance().getCommandManager().parseCommand(message, user);
    }

    /**
     * @param conn Connection instance of the client, which ran into an error
     * @param ex Exception which occurred
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        // -
    }

    /**
     * Run when the start method is called on this object
     */
    @Override
    public void onStart() {
        // -
    }

    /**
     * @return the port on which the server listens
     */
    @Override
    public int getPort() {
        return port;
    }
}
