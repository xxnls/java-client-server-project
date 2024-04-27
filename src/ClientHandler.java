import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Server server;
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            String request;
            while((request = reader.readLine()) != null){
                
                if (request.equals("getEmployees")) {
                    server.sendEmployeesToClient(clientSocket);
                }

                if (request.equals("sendEmployees")) {
                    server.receiveEmployeesFromClient(clientSocket);
                    //System.out.println("REQUESTED");
                }
            }
            
        } catch (EOFException e) {
            System.out.println("Client disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // when connection closes, remove client from list
            server.removeClient(this);
            try {
                if (writer != null)
                    writer.close();
                if (reader != null)
                    reader.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}