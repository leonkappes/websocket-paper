package space.kappes.entity;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Responsible for managing a connection and the messages from one end-user
 */
public class User {
    private final WebSocket socket;
    private final String identifier; // Random string used for differentiating clients
    private final ArrayList<NotificationSettings> subscribedEvents;
    private boolean privileged;
    private String code;

    /**
     * User object representing a client/end-user in the frontend
     * @param socket Socket object received by accepting a connection, used for further communication with the user/client this object represents
     */
    public User(WebSocket socket) {
        this.socket = socket;
        this.identifier = UUID.randomUUID().toString().replace("-","").substring(0,7);
        this.subscribedEvents = new ArrayList<>();
        this.privileged = false;
        this.code = "";
    }

    /**
     * Send instructions to a client
     * @param command invoke for the command sent to the client
     * @param json data which will be send to the client, requires json-format
     */
    public void send(String command, JSONObject json) {
        // Send command-string to client, format: invoke + SPACE + Json-Data
        this.socket.send(command+" "+json.toString());
    }

    public WebSocket getSocket() {
        return socket;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ArrayList<NotificationSettings> getSubscribedEvents() {
        return subscribedEvents;
    }

    /**
     * @param notificationsettings Notificationsettings instance which a user subscribed to
     */
    public void addToSubscribedEvent(NotificationSettings notificationsettings) {
        this.subscribedEvents.add(notificationsettings);
    }

    /**
     * Removes the subscription of a event
     * @param eventIndex index of the event, which should be removed
     */
    public void removeFromSubscribedEvent(int eventIndex) {
        // Remove an instance if the has the same index as the parameter
        this.subscribedEvents.removeIf(i -> i.getEventIndex() == eventIndex);
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }

    public String getCode() {
        return code;
    }

    /**
     * @param code Session identifier, identifying to which conference this user belongs
     */
    public void setCode(String code) {
        this.code = code;
    }
}
