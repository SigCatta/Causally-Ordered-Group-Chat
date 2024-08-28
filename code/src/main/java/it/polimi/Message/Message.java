package it.polimi.Message;

import it.polimi.Entities.Participant;
import it.polimi.Message.Replication.RingUpdateMessage;
import it.polimi.States.RoomState;
import it.polimi.States.RoomStateManager;
import it.polimi.Storage.ReplicationManager;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.stream.IntStream;

public abstract class Message implements Serializable {
    protected String content;

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public abstract void process(RoomState state);

    public void sendMessage(Participant participant) {
        String[] parts = participant.ipAddress().split(":");
        int port = Integer.parseInt(parts[1]);
        try (Socket socket = new Socket(parts[0], port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(this);
            out.flush();
        } catch (Exception e) {
            System.out.println(participant.ipAddress());
            e.printStackTrace();
            handleException(participant);
        }
    }

    public void handleException(Participant participant) {
    }


    protected void substituteFailedUserNode(String failedNode) {
        List<String> nodes = ReplicationManager.getInstance().getUserNodes();
        substituteNode(failedNode, nodes);

        RingUpdateMessage.broadcast(new RingUpdateMessage(null, nodes));
    }

    protected void substituteFailedRoomNode(String failedNode) {
        List<String> nodes = ReplicationManager.getInstance().getRoomNodes();
        substituteNode(failedNode, nodes);

        RingUpdateMessage.broadcast(new RingUpdateMessage(nodes, null));
    }

    private void substituteNode(String failedNode, List<String> nodes) {
        int lastIndex = nodes.lastIndexOf(failedNode);

        if (lastIndex == nodes.size() - 1) {
            String myEndpoint = RoomStateManager.getInstance().getIp() + ':' + RoomStateManager.getInstance().getPort();
            if (nodes.contains(myEndpoint)) { // if I am in the list, replace failed node with the node before
                IntStream.range(0, nodes.size())
                        .forEach(i -> {
                            if (nodes.get(i).equals(failedNode))
                                nodes.set(i, nodes.get(nodes.indexOf(failedNode) - 1));
                        });
            } else { // If I am not in the last, I substitute the failed node
                IntStream.range(0, nodes.size())
                        .forEach(i -> {
                            if (nodes.get(i).equals(failedNode))
                                nodes.set(i, myEndpoint);
                        });
            }
        } else { // if the failed node is not the last one, replace it with the next one
            IntStream.range(0, nodes.size())
                    .forEach(i -> {
                        if (nodes.get(i).equals(failedNode))
                            nodes.set(i, nodes.get(lastIndex + 1));
                    });
        }
    }

}
