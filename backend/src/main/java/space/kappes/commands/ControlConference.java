package space.kappes.commands;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import space.kappes.WebsocketManager;
import space.kappes.command.Command;
import space.kappes.entity.Conference;
import space.kappes.entity.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControlConference extends Command {


    /**
     * Used for navigating inside a conference, need argument of which item to switch to
     * @see Command#Command(String, String[]) 
     */
    public ControlConference() {
        super("controlConference", new String[]{"switchTo", "code"});
    }

    /**
     * @see Command#run(WebsocketManager, User, JSONObject)
     */
    @Override
    public void run(WebsocketManager manager, User user, JSONObject args) {
        String code = args.getString("code");
        // Abort if user has no permission
        if(!user.isPrivileged())
            return;
        // Abort if no conference is set, yet
        if(manager.getConferences().get(code) == null)
            return;
        Conference conference = manager.getConferences().get(code);
        // Get the conference by its code, then set the provided index as current
        conference.setCurrentItem(manager.getConferences().get(code).getConferenceItems().get(args.getInt("switchTo")));
        // Formatter is set to the time-format we expect ( full hour:full minutes )
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
        // Parse our starting time to the format
        DateTime start = formatter.parseDateTime(conference.getCurrentItem().getStart());
        // Reformat the current time to the format
        DateTime current = formatter.parseDateTime(new DateTime().toString(formatter));
        // calculate the duration between both times
        Duration duration = new Duration(current, start);
        try {
            // Cast the minutes to a whole and set them as delay
            conference.setDelay(Math.toIntExact(duration.getStandardMinutes() * -1));
        } catch (ArithmeticException ignored) {
            // Do not set delay if an error occurs
        }

        // Get all users, which subscribed to this conference, and then filter for the client which send the instruction, so he doesn't get the update
        manager.getUserList().values().stream().filter(c -> c.getCode().equals(code)).forEach((client) -> {
            // Send user the updated conference object
            client.send("updateConference", new JSONObject().put("identifier", user.getIdentifier()).put("conference", new JSONObject(conference)));
        });
    }
}
