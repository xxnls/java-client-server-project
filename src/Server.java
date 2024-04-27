
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private static List<ClientHandler> clients;
    private static ArrayList<Employee> employees = new ArrayList<>();

    static{

    }
    
    public Server() {
        clients = new ArrayList<>();
    }
    
    public List<Employee> getEmployees() {
        return employees;
    }

    public void start(int portNumber) {
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                //clienthandler creation, adding client to the list of clients
                ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                clients.add(clientHandler);

                //starting clienthandler
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readEmployeesFromFile(String filePath) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            
            ArrayList<Employee> employeesFromFile = (ArrayList<Employee>) objectInputStream.readObject();
            
            employees.addAll(employeesFromFile);
            System.out.println("Employees read from file: " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void saveEmployeesToFile(String filePath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {

            objectOutputStream.writeObject(employees);
            System.out.println("Employees saved to file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }
    
    public void sendEmployeesToClient(Socket clientSocket) {
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("Sending employees.");
            
            for (Employee employee : employees) {
                writer.flush();
                writer.println(employee.getId());
                writer.println(employee.getFirstName());
                writer.println(employee.getLastName());
                writer.println(employee.getPosition());
                writer.println(employee.getSalary());
            }

            writer.println("END");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void receiveEmployeesFromClient(Socket clientSocket) {
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            employees.clear();
            
            String line = "";
            while (!(line = reader.readLine()).equals("END")) {
                System.out.println(line);
                int id = Integer.parseInt(line);
                String firstName = reader.readLine();
                String lastName = reader.readLine();
                String position = reader.readLine();
                double salary = Double.parseDouble(reader.readLine());

                Employee employee = new Employee(firstName, lastName, position, salary);
                employee.setId(id);
                employees.add(employee);
            }
            
            for(Employee e : employees){
                System.out.println(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    public static void main(String[] args) {
        int portNumber = 12345;
       
        Server server = new Server();
        server.readEmployeesFromFile("employees.dat");
        server.saveEmployeesToFile("employees.dat");
        
        server.start(portNumber);
    }
}