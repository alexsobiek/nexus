package com.alexsobiek.nexus.netty.test;

import com.alexsobiek.nexus.Nexus;
import com.alexsobiek.nexus.netty.AbstractSocket;
import com.alexsobiek.nexus.netty.tcp.TCPClient;
import com.alexsobiek.nexus.netty.tcp.TCPServer;
import com.alexsobiek.nexus.netty.test.impl.ChannelHandler;
import com.alexsobiek.nexus.netty.test.impl.Connection;
import com.alexsobiek.nexus.netty.test.impl.ConnectionAddedEvent;
import com.alexsobiek.nexus.netty.test.impl.Pipeline;
import io.netty.channel.ChannelFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestServerClient {
    public static final Nexus nexus = Nexus.builder().build();

    final InetSocketAddress address = new InetSocketAddress(3000); // address to bind & connect to

    final ChannelHandler serverHandler = new ChannelHandler(ChannelHandler.Type.SERVER); // handle incoming server connections
    final Pipeline serverPipeline = new Pipeline(serverHandler);

    final ChannelHandler clientHandler = new ChannelHandler(ChannelHandler.Type.CLIENT); // handle outgoing client connections
    final Pipeline clientPipeline = new Pipeline(clientHandler);

    // Construct server & client
    final TCPServer server = nexus.library(TCPServer.builder().pipeline(serverPipeline).address(address).build());
    final TCPClient client = nexus.library(TCPClient.builder().pipeline(clientPipeline).address(address).build());

    volatile boolean serverConnected = false;
    volatile boolean clientConnected = false;
    SocketAddress clientAddress;
    SocketAddress serverAddress;


    @Test
    @DisplayName("Test TCP Client/Server")
    void testTCPServerClient() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        nexus.eventBus().listen(ConnectionAddedEvent.class, this::onConnectionActive);

        assertTrue(isServer(server));

        ChannelFuture serverFuture = server.start(); // Start the server
        assertTrue(serverFuture.isSuccess());

        ChannelFuture clientFuture = client.start(); // Connect to the server
        assertTrue(clientFuture.isSuccess());

        // keep thread alive until connection is established
        while (!serverConnected || !clientConnected) {
        }

        // Assert both the client and server are connected and aware of each other
        assertTrue(serverHandler.getConnection(clientAddress).isPresent());
        assertTrue(clientHandler.getConnection(serverAddress).isPresent());

        // TODO: test packet sending
    }

    void onConnectionActive(ConnectionAddedEvent event) {
        Connection conn = event.getConnection();
        if (conn.getType() == ChannelHandler.Type.SERVER) { // connection C->S
            serverAddress = conn.getLocalAddress();
            serverConnected = true;
        } else { // connection S->C
            clientAddress = conn.getLocalAddress();
            clientConnected = true;
        }
    }

    boolean isServer(AbstractSocket<?, ?> socket) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method isServerMethod = AbstractSocket.class.getDeclaredMethod("isServer");
        isServerMethod.setAccessible(true);
        return (boolean) isServerMethod.invoke(socket);
    }
}