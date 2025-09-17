CREATE DATABASE bankingdb;
USE bankingdb;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50),
    balance DOUBLE DEFAULT 0
);


import java.sql.*;
import java.util.Scanner;

public class OnlineBanking {
    static final String URL = "jdbc:mysql://localhost:3306/bankingdb";
    static final String USER = "root";
    static final String PASS = "yourpassword";

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        while (true) {
            System.out.println("1.Register 2.Login 3.Exit");
            int ch = sc.nextInt();
            if (ch == 1) register(conn, sc);
            else if (ch == 2) login(conn, sc);
            else break;
        }
        conn.close();
    }

    static void register(Connection conn, Scanner sc) throws Exception {
        System.out.print("Enter username: ");
        String u = sc.next();
        System.out.print("Enter password: ");
        String p = sc.next();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username,password) VALUES(?,?)");
        ps.setString(1, u);
        ps.setString(2, p);
        ps.executeUpdate();
        System.out.println("Registered successfully");
    }

    static void login(Connection conn, Scanner sc) throws Exception {
        System.out.print("Enter username: ");
        String u = sc.next();
        System.out.print("Enter password: ");
        String p = sc.next();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
        ps.setString(1, u);
        ps.setString(2, p);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("id");
            System.out.println("Login successful");
            dashboard(conn, sc, id);
        } else {
            System.out.println("Invalid credentials");
        }
    }

    static void dashboard(Connection conn, Scanner sc, int id) throws Exception {
        while (true) {
            System.out.println("1.Deposit 2.Withdraw 3.Check Balance 4.Logout");
            int ch = sc.nextInt();
            if (ch == 1) deposit(conn, sc, id);
            else if (ch == 2) withdraw(conn, sc, id);
            else if (ch == 3) balance(conn, id);
            else break;
        }
    }

    static void deposit(Connection conn, Scanner sc, int id) throws Exception {
        System.out.print("Enter amount: ");
        double amt = sc.nextDouble();
        PreparedStatement ps = conn.prepareStatement("UPDATE users SET balance=balance+? WHERE id=?");
        ps.setDouble(1, amt);
        ps.setInt(2, id);
        ps.executeUpdate();
        System.out.println("Deposited");
    }

    static void withdraw(Connection conn, Scanner sc, int id) throws Exception {
        System.out.print("Enter amount: ");
        double amt = sc.nextDouble();
        PreparedStatement ps1 = conn.prepareStatement("SELECT balance FROM users WHERE id=?");
        ps1.setInt(1, id);
        ResultSet rs = ps1.executeQuery();
        if (rs.next()) {
            double bal = rs.getDouble(1);
            if (bal >= amt) {
                PreparedStatement ps2 = conn.prepareStatement("UPDATE users SET balance=balance-? WHERE id=?");
                ps2.setDouble(1, amt);
                ps2.setInt(2, id);
                ps2.executeUpdate();
                System.out.println("Withdrawn");
            } else System.out.println("Insufficient funds");
        }
    }

    static void balance(Connection conn, int id) throws Exception {
        PreparedStatement ps = conn.prepareStatement("SELECT balance FROM users WHERE id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) System.out.println("Balance: " + rs.getDouble(1));
    }
}