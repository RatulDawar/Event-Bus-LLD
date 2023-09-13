package models;

import java.util.Map;

public class Event {
    private final EventID eventID;
    private final Topic topic;
    private final Map<String, Map<String,String>> fields;
    private final Long timestamp;

    public Event(EventID eventID, String topic, Map<String, Map<String, String>> fields, Long timestamp) {
        this.eventID = eventID;
        this.topic = new Topic(topic);
        this.fields = fields;
        this.timestamp = timestamp;
    }

    public EventID getEventID() {
        return this.eventID;
    }

    public Topic getTopic() {
        return topic;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Map<String, Map<String, String>> getFields() {
        return fields;
    }
}
