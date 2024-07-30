package it.polimi.Message;

public abstract class Message {
    protected String content;

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public abstract void process();
}
