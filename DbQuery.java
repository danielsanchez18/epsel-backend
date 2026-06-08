import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbQuery {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bd_epsel", "root", "123456");
            Statement stmt = conn.createStatement();
            
            System.out.println("--- ZONAS ---");
            ResultSet rsZones = stmt.executeQuery("SELECT name FROM service_zones LIMIT 5");
            while (rsZones.next()) {
                System.out.println(rsZones.getString("name"));
            }
            
            System.out.println("--- CLIENTES ---");
            ResultSet rsCust = stmt.executeQuery("SELECT document_number, type FROM customers LIMIT 5");
            while (rsCust.next()) {
                System.out.println(rsCust.getString("document_number") + " | " + rsCust.getString("type"));
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
