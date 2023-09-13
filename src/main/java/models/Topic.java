package models;

public class Topic {
    private final String topicName;

    public Topic(String topicName) {
        this.topicName = topicName;
    }
    public String getTopicName(){
        return this.topicName;
    }
}
