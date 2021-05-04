package space.kappes;

import space.kappes.command.CommandManager;
import space.kappes.commands.ControlConference;
import space.kappes.commands.Login;
import space.kappes.commands.SetupConference;
import space.kappes.entity.Conference;
import space.kappes.entity.ConferenceItem;
import space.kappes.entity.User;
import space.kappes.net.IWebsocketServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Main Class of Project, responsible for managing the Socketserver and keeping track of all users/connected clients
 */
public class WebsocketManager {

    private static WebsocketManager instance;
    private final CommandManager commandManager;
    private final Map<String, User> userList;
    private IWebsocketServer server;
    private final Map<String, Conference> conferences;


    /**
     * Assigns the attributes their values, initializes the program
     */
    public WebsocketManager() {
        // Check if Environment-variable is set, abort program if not
        if (System.getenv("WEBSOCKET_PORT") == null) {
            throw new RuntimeException("Serversocket port is not set, aborting Program!");
        }
        // Create Instance of our Websocketserver and start it
        this.server = new IWebsocketServer(Integer.parseInt(System.getenv("WEBSOCKET_PORT")));
        // Start websocketserver and handle incoming connections
        this.server.start();
        this.userList = new HashMap<>();
        this.commandManager = new CommandManager(this);
        this.conferences = new HashMap<>();
        instance = this;
        // register all commands
        this.registerCommands();
        System.out.println("Websocket started, waiting for connections");
    }

    /**
     * @return instance of this class, used on multiple accounts through the program
     */
    public static WebsocketManager getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        new WebsocketManager();
    }

    /**
     * Create instances of all commands an register them for execution
     */
    private void registerCommands() {
        this.commandManager.registerCommands(
                new Login(),
                new SetupConference(),
                new ControlConference()
        );
    }

    /**
     * Responsible for adding users to the User-Map, indexed by their Identifier
     *
     * @param user User to add to the User-Map
     */
    public void addUser(User user) {
        this.userList.put(user.getIdentifier(), user);
    }

    /**
     * Responsible for removing users from the User-Map, indexed by their Identifier
     *
     * @param user User to remove from the User-Map
     */
    public void removeUser(User user) {
        this.userList.remove(user.getIdentifier());
    }

    /**
     * @param identifier Identifier of the user to retrieve
     * @return User instance with given identifier
     */
    public User getUser(String identifier) {
        return this.userList.get(identifier);
    }

    public IWebsocketServer getServer() {
        return server;
    }

    public Map<String, User> getUserList() {
        return userList;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return the Map with all conferences, key is the session identifier code and value is the conference object
     */
    public Map<String, Conference> getConferences() {
        return conferences;
    }



}
