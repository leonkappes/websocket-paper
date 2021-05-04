package space.kappes.entity;

import java.util.ArrayList;

/**
 * Class representing a conference
 */
public class Conference {

    private final ArrayList<ConferenceItem> conferenceItems; // Ordered list of all items in the conference
    private int delay; // total delay, negative or positive
    private ConferenceItem currentItem;

    public Conference() {
        this.conferenceItems = new ArrayList<>();
        this.delay = 0;
        this.currentItem = null;
    }

    public ArrayList<ConferenceItem> getConferenceItems() {
        return conferenceItems;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public ConferenceItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(ConferenceItem currentItem) {
        this.currentItem = currentItem;
    }
}
