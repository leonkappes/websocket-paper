package space.kappes.commands;

import org.json.JSONArray;
import org.json.JSONObject;
import space.kappes.WebsocketManager;
import space.kappes.command.Command;
import space.kappes.entity.Conference;
import space.kappes.entity.ConferenceItem;
import space.kappes.entity.User;

public class SetupConference extends Command {

    /**
     * Used for setting up a conference, need argument with conference object
     * @see Command#Command(String, String[])
     */
    public SetupConference() {
        super("setupConference", new String[]{"conference", "code"});
    }

    /**
     * @see Command#run(WebsocketManager, User, JSONObject)
     */
    @Override
    public void run(WebsocketManager manager, User user, JSONObject args) {
        // The json-data contains an key named conference which holds an array of conference items as its values
        JSONArray jsonArray = args.getJSONArray("conference");
        // Create new instance of the conference class
        Conference conference = new Conference();
        // Run the follow for each conference-item in the array
        jsonArray.forEach((item) -> {
            // Cast the object to an jsonobject
            JSONObject object = (JSONObject) item;
            // Check if the description is empty, if not proceed. Frontend should never send an item without description so we can be sure that if it is available the rest works too
            if(object.has("Beschreibung") && !object.isEmpty() && !object.getString("Beschreibung").isEmpty())
                // Add new item to the list of conference-items, fill its values by retrieving them from the jsonobject
                conference.getConferenceItems().add(new ConferenceItem(object.getString("Beschreibung"), object.getString("Start"), object.getInt("Dauer")));
        });
        // Set the first conference item as the currently active
        conference.setCurrentItem(conference.getConferenceItems().get(0));
        // Add the newly created conference to the conference-pool
        manager.getConferences().put(args.getString("code"), conference);
    }
}
