
import java.io.Serializable;

public class Employee implements Serializable{
    private static int ID_Counter;
    
    private int id;
    private String firstName;
    private String lastName;
    private String position;
    private double salary;
    
    static{
        ID_Counter = 0;
    }

    public Employee(String firstName, String lastName, String position, double salary) {
        id = ID_Counter++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPosition() {
        return position;
    }

    public double getSalary() {
        return salary;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
}
