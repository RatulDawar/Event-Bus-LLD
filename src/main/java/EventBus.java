import exception.FaliureEvent;
import exception.RetryLimitExceededException;
import models.*;
import retry.RetryAlgorithm;
import utils.KeyedExecutor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class EventBus {
    private final KeyedExecutor executor;
    private final Map<Topic, Map<EventID,Event>> bus;
    private final Map<Topic, Set<Subscription>>  subscribers;

    private final Map<Topic,Map<SubscriberID,EventID>> subscriberIndexes;
    private final Map<Topic,ConcurrentSkipListMap<Long,EventID>> eventTimeStamps;
    private final EventBus deadLetterQueue;

    private final RetryAlgorithm<Event,Void> retryAlgorithm;
    public EventBus(EventBus deadLetterQueue) {
        this.deadLetterQueue = deadLetterQueue;
        this.retryAlgorithm = new RetryAlgorithm<>(5);
        this.eventTimeStamps = new ConcurrentHashMap<>();
        this.subscriberIndexes = new ConcurrentHashMap<>();
        this.subscribers = new ConcurrentHashMap<>();
        this.executor = new KeyedExecutor();
        this.bus = new ConcurrentHashMap<>();
    }

    public void publish(Topic topic, Event event){
        executor.submit(topic.toString(),()-> addEventToBus(topic,event));
    }
    public CompletableFuture<Void> addEventToBus(Topic topic, Event event){
        bus.get(topic).put(event.getEventID(),event);
        eventTimeStamps.get(topic).put(event.getTimestamp(),event.getEventID());
        subscribers.get(topic)
                .stream()
                .filter(subscription -> subscription.getType().equals(SubscriptionType.PUSH))
                .forEach(subscription -> push(event,subscription));
        return CompletableFuture.completedFuture(null);
    }
    public void subscribe(Topic topic,Subscription subscription){
        subscribers.putIfAbsent(topic,new CopyOnWriteArraySet<>());
        executor.submit(topic + subscription.getSubscriberID().toString(), () -> subscribers.get(topic).add(subscription));

    }

    public void setIndexAfterTimestamp(Topic topic, SubscriberID subscriberID,Long timestamp){
        subscriberIndexes.putIfAbsent(topic,new ConcurrentHashMap<>());
        subscriberIndexes.get(topic).put(subscriberID,eventTimeStamps.get(topic).higherEntry(timestamp).getValue());
    }
    public void setIndexAfterEvent(Topic topic, SubscriberID subscriberID,EventID eventID){
        subscriberIndexes.putIfAbsent(topic,new ConcurrentHashMap<>());
        subscriberIndexes.get(topic).put(subscriberID,eventID);
    }
    public CompletionStage<Event> poll(Topic topic,SubscriberID subscriberID){
        return executor.submit(topic + subscriberID.toString(), ()->{
            final EventID index = subscriberIndexes.get(topic).get(subscriberID);
            return getEvent(topic,index);
        });
    }
    public void push(Event event,Subscription subscription){
        try{
            retryAlgorithm.attempt((__)-> {
                return null;
            },event,retryAlgorithm.getMAX_ATTEMPTS(),subscription.getHandler());
        } catch (RetryLimitExceededException | InterruptedException e) {
            deadLetterQueue.publish(subscription.getTopic(),new FaliureEvent(event,e, System.currentTimeMillis()));
        }
    }

    public Event getEvent(Topic topic, EventID eventId){
        return bus.get(topic).get(eventId);


    }



}
