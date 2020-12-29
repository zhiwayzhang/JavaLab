package com.qst.dms.net;

import com.mysql.cj.xdevapi.Result;
import com.qst.dms.entity.MatchedLogRec;
import com.qst.dms.entity.MatchedTransport;
import com.qst.dms.service.LogRecService;
import com.qst.dms.service.TransportService;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class DmsNetServer {
    public DmsNetServer() {
        // log
        new AcceptLogThread(6666).start();
        // log query
        new GetLogThread(6667).start();
        // trans
        new AcceptTransThread(6668).start();
        new GetTransThread(6669).start();
        System.out.println("Net Server is running on port 6666 and 6668");
    }

    private class GetLogThread extends Thread {

        private ServerSocket serverSocket;
        private Socket socket;
        private LogRecService logRecService;
        private ObjectOutputStream oos;

        public GetLogThread(int port) {
            logRecService = new LogRecService();
            try {
                serverSocket = new ServerSocket(port);
            }catch (Exception e) {
                //e.printStackTrace();
            }
        }


        @Override
        public void run() {
            while (this.isAlive()) {
                try {
                    socket = serverSocket.accept();
                    if (socket != null) {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        ResultSet rt = logRecService.readLogResult();
                        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                        crs.populate(rt);
                        System.out.println(rt);
                        oos.writeObject(crs);
                        // getOriginalRow
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                oos.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetTransThread extends Thread {
        private ServerSocket serverSocket;
        private Socket socket;
        private TransportService transportService;
        private ObjectOutputStream oos;

        public GetTransThread(int port) {
            transportService = new TransportService();
            try {
                serverSocket = new ServerSocket(port);
            }catch (Exception e) {
                //e.printStackTrace();
            }
        }


        @Override
        public void run() {
            while (this.isAlive()) {
                try {
                    socket = serverSocket.accept();
                    if (socket != null) {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        ResultSet rt = transportService.readTransResult();
                        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
                        crs.populate(rt);
                        System.out.println(rt);
                        oos.writeObject(crs);
                        // getOriginalRow
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                oos.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                //e.printStackTrace();
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
                   // e.printStackTrace();
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
