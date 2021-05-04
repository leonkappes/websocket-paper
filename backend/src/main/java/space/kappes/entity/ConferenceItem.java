package space.kappes.entity;

/**
 * Class representing a single item in a conference
 */
public class ConferenceItem {

    private final String description;
    private final String start;
    private final int duration;

    /**
     * @param description Description of the item, something like 9a,8c,Q1...
     * @param start Starting time of the item, something like 8:00, 8:55...
     * @param duration duration of that item, something like 30, 40. Duration is in minutes
     */
    public ConferenceItem(String description, String start, int duration) {
        this.description = description;
        this.start = start;
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public String getStart() {
        return start;
    }

    public int getDuration() {
        return duration;
    }
}
