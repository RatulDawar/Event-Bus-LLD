package models;

import java.util.function.Function;

public class Subscription<T> {
    private final String id;
    private final SubscriberID subscriberID;
    private final SubscriptionType type;
    private final Topic topic;
    final Function<T,Boolean> precondition;
    private final Function<Event,Void> handler;


    public Subscription(String id, SubscriberID subscriberID, SubscriptionType type, Topic topic, Function<T, Boolean> precondition, Function<Event, Void> handler) {
        this.id = id;
        this.subscriberID = subscriberID;
        this.type = type;
        this.topic = topic;
        this.precondition = precondition;
        this.handler = handler;
    }

    public SubscriberID getSubscriberID() {
        return subscriberID;
    }

    public SubscriptionType getType() {
        return type;
    }

    public Function<Event, Void> getHandler() {
        return handler;
    }

    public Topic getTopic() {
        return topic;
    }
}
