package models;

public class Entity {
    private final SubscriberID subscriberID;
    private final String name;
    private final String ipAddress;

    public Entity(SubscriberID subscriberID, String name, String ipAddress) {
        this.subscriberID = subscriberID;
        this.name = name;
        this.ipAddress = ipAddress;
    }
}
