package space.kappes.entity;

/**
 * Class representing the selected settings of a user
 */
public class NotificationSettings {

    private final int eventIndex;
    private boolean nextUp;

    /**
     * @param eventIndex index of the event on which notifications should be sent
     * @param nextUp send notification if this item is the next up
     */
    public NotificationSettings(int eventIndex, boolean nextUp) {
        this.eventIndex = eventIndex;
        this.nextUp = nextUp;
    }

    public int getEventIndex() {
        return eventIndex;
    }

    public boolean isNextUp() {
        return nextUp;
    }

    public void setNextUp(boolean nextUp) {
        this.nextUp = nextUp;
    }

}
