package com.cs506.project;

import com.cs506.project.configs.ServerConfig;
import com.cs506.project.server.ProxyServer;
import com.cs506.project.server.ProxyServerListener;
import com.cs506.project.server.ProxyServerWorker;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class MockServer {
    public static void main(String[] args) {
        ServerConfig serverConfig = new ServerConfig(
            /* num_listeners */     4,
            /*   num_workers */     4,
            /*          port */ 18000,
            /*     port_list */  null,
            /*    chunk_size */  1024,
            /*   auto_append */ false,
            /*       timeout */  1000,
            /*      log_file */  null
        );

        ProxyServer server;
        try {
            server = new ProxyServer(serverConfig, new LinkedBlockingQueue<>());
        } catch (IOException e) {
            System.out.println("Failed to initialize the server."
                             + " See stderr for stack trace.");
            e.printStackTrace(System.err);
            return;
        }

        try {
            // Just a Simple Echo Server
            server.setup(x -> new String(x));
        } catch (IOException e) {
            System.out.println("Failed to start the server."
                             + " See stderr for stack trace.");
            e.printStackTrace(System.err);
            return;
        }

        server.run();

        try {
            server.waitForTermination();
        } catch (InterruptedException e) {
            System.out.println("Server did not close cleanly!"
                             + " See stderr for stack trace.");
            e.printStackTrace(System.err);
        }
    }
}
