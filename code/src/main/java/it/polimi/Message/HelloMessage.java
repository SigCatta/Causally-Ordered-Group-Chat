package it.polimi.Message;

import it.polimi.Controller.Controller;

import java.io.Serializable;

public class HelloMessage extends Message implements Serializable {

    public HelloMessage(String content) {
        super(content);
    }
    @Override
    public void process(Controller controller) {
        controller.handle(this);
    }
}
