import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLJoinDemo {
    public static void main(String[] args) {
        // Database URL (you may need to change this)
        String url = "jdbc:sqlite:test.db";

        try (Connection connection = DriverManager.getConnection(url)) {
            // Create tables if they don't exist
            createTables(connection);

            // Insert sample data into users and orders tables
            insertSampleData(connection);

            // INNER JOIN: Retrieve user names and their associated orders
            System.out.println("INNER JOIN (Users and Orders):");
            String innerJoinSQL = "SELECT users.name, orders.order_id FROM users " +
                                   "INNER JOIN orders ON users.id = orders.user_id";
            printResultSet(connection, innerJoinSQL);

            // LEFT JOIN: Retrieve all users and their associated orders (if any)
            System.out.println("\nLEFT JOIN (Users and Orders):");
            String leftJoinSQL = "SELECT users.name, orders.order_id FROM users " +
                                  "LEFT JOIN orders ON users.id = orders.user_id";
            printResultSet(connection, leftJoinSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createUsersTableSQL);
        }

        String createOrdersTableSQL = "CREATE TABLE IF NOT EXISTS orders (order_id INTEGER PRIMARY KEY, user_id INTEGER)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createOrdersTableSQL);
        }
    }

    private static void insertSampleData(Connection connection) throws SQLException {
        // Insert users
        String insertUsersSQL = "INSERT INTO users (name) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertUsersSQL)) {
            preparedStatement.setString(1, "John");
            preparedStatement.executeUpdate();

            preparedStatement.setString(1, "Alice");
            preparedStatement.executeUpdate();
        }

        // Insert orders
        String insertOrdersSQL = "INSERT INTO orders (user_id) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrdersSQL)) {
            preparedStatement.setInt(1, 1); // John's order
            preparedStatement.executeUpdate();
        }
    }

    private static void printResultSet(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String userName = resultSet.getString("name");
                int orderId = resultSet.getInt("order_id");
                System.out.println("User: " + userName + ", Order ID: " + orderId);
            }
        }
    }
}
