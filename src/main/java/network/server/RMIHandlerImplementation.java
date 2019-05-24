package network.server;

import network.client.RMIClientConnection;
import network.message.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * This class is the implementation of the interface RMIHandler
 */
public class RMIHandlerImplementation extends UnicastRemoteObject implements RMIHandler {
    private final transient Server server;

    public RMIHandlerImplementation(Server server) throws RemoteException {
        this.server = server;
    }

    /**
     * Tries to execute the login with the server
     *
     * @param username username used for the login
     * @param client   client connection
     */
    @Override
    public void login(String username, RMIClientConnection client) {
        RMIConnection rmiSession = new RMIConnection(server, client);
        server.login(username, rmiSession);
    }

    /**
     * Sends a message to the server
     *
     * @param message message sent to server
     */
    @Override
    public void onMessage(Message message) {
        server.onMessage(message);
    }
}
