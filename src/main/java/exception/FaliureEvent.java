package exception;

import models.Event;
import models.EventID;

import java.util.UUID;

public class FaliureEvent extends Event {
    private final Event event;
    private final Throwable error;
    private final long timestamp;
    public FaliureEvent(Event event, Throwable error, long timestamp) {
        super(new EventID(UUID.randomUUID().toString()),"failure-"+event.getTopic(),event.getFields(),timestamp);

        this.event = event;
        this.error = error;
        this.timestamp = timestamp;
    }
}
