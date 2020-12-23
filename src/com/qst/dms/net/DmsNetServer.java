package com.qst.dms.net;

import com.qst.dms.entity.MatchedLogRec;
import com.qst.dms.entity.MatchedTransport;
import com.qst.dms.service.LogRecService;
import com.qst.dms.service.TransportService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class DmsNetServer {
    public DmsNetServer() {
        // log
        new AcceptLogThread(6666).start();
        // trans
        new AcceptTransThread(6668).start();
        System.out.println("Net Server is running on port 6666 and 6668");
    }

    private class AcceptLogThread extends Thread {
        private ServerSocket serverSocket;
        private Socket socket;
        private LogRecService logRecService;
        private ObjectInputStream ois;

        public AcceptLogThread(int port) {
            logRecService = new LogRecService();
            try {
                serverSocket = new ServerSocket(port);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (this.isAlive()) {
                try {
                    socket = serverSocket.accept();
                    if (socket != null) {
                        ois = new ObjectInputStream(socket.getInputStream());
                        ArrayList<MatchedLogRec> matchedLogs = (ArrayList<MatchedLogRec>) ois.readObject();
                        logRecService.saveMatchedLogToDB(matchedLogs);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                ois.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class AcceptTransThread extends Thread {
        private ServerSocket serverSocket;
        private Socket socket;
        private TransportService transportService;
        private ObjectInputStream ois;

        public AcceptTransThread(int port) {
            transportService = new TransportService();
            try {
                serverSocket = new ServerSocket(port);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (this.isAlive()) {
                try {
                    socket = serverSocket.accept();
                    if (socket != null) {
                        ois = new ObjectInputStream(socket.getInputStream());
                        ArrayList<MatchedTransport> matchedTransports = (ArrayList<MatchedTransport>) ois.readObject();
                        transportService.saveMatchTransportToDB(matchedTransports);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                ois.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        new DmsNetServer();
    }

}
