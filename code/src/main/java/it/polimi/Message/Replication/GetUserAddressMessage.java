package it.polimi.Message.Replication;

import it.polimi.Message.Message;
import it.polimi.States.RoomState;
import it.polimi.Storage.ReplicationManager;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class GetUserAddressMessage extends Message implements Serializable {
    String name;
    String senderaddress;

    public GetUserAddressMessage(String name,String address){
        super("request of address");
        this.name=name;
        this.senderaddress=address;
    }

    public String getName() {
        return name;
    }

    public String getSenderaddress() {
        return senderaddress;
    }

    @Override
    public void process(RoomState state) {
        String participantAddress = ReplicationManager.getInstance().getIpAddress(name);
        try {
            String[] parts = senderaddress.split(":");
            String senderIP = parts[0];
            int senderPort = Integer.parseInt(parts[1]);

            try (Socket socket = new Socket(senderIP, senderPort);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                out.writeObject(participantAddress);
                out.flush();
            }

        } catch (Exception e) {
            System.err.println("Failed to send address response to " + senderaddress);
            e.printStackTrace();
        }
    }
}
