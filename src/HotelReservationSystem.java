import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/Hotel_db";
    private static final String username = "gagan";
    private static final String password = "Gagan#123";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid Choice. Try again.");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    private static void reserveRoom(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter guest name : ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO Reservation (Guest_name, Room_number, Contact_number) " +
                    "VALUES ('"+ guestName +"'," + roomNumber + ",'" + contactNumber + "')";
            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows>0){
                    System.out.println("Reservation successful!");
                }else {
                    System.out.println("Reservation failed!");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    private static void viewReservation(Connection connection) throws SQLException{
        String sql = "SELECT Reservation_id, Guest_name, Room_number, Contact_number, Reservation_date FROM Reservation";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
            System.out.println("Current Reservation");
            System.out.println("+---------------+---------------+---------------+-----------------+-------------------+");
            System.out.println("|Reservation ID |Guest          | Room Number   | Contact Number  | Reservation Date   ");
            System.out.println("+---------------+---------------+---------------+-----------------+-------------------+");

            while (resultSet.next()){
                int reservationId = resultSet.getInt("Reservation_id");
                String guestName = resultSet.getString("Guest_name");
                int roomNumber = resultSet.getInt("Room_number");
                String contactNumber = resultSet.getString("Contact_number");
                String reservationDate = resultSet.getString("Reservation_date").toString();
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s  |\n",reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("+--------------+---------------+---------------+------------------+--------------------");
        }
    }


    private static void getRoomNumber(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.println("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT Room_number FROM Reservation "+"WHERE Reservation_id = " +reservationId +
                    "AND Guest_name = '"+ guestName +"'";
            try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("Room_Number");
                    System.out.println("Room number for Reservation ID " +reservationId + "and Guest " +guestName + "is: " +roomNumber);
                }else{
                    System.out.println("Reservation not found for given ID and guest name.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter the Reservation Id to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if(reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for given ID.");
                return;
            }
            System.out.println("Enter new Guest Name: ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE Reservation SET Guest_name ='" +newGuestName + "', " +
                    "Room_number = " + newRoomNumber + "," +
                    "Contact_number = '" + newContactNumber + "'" +
                    "WHERE Reservation_id = " + reservationId;

            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("Reservation updated sucessfully.");
                }else {
                    System.out.println("Reservation update fail.");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    private static void deleteReservation(Connection connection, Scanner scanner){
        try{
            System.out.print("Enter the reservation ID to delete: ");
            int reservationId = scanner.nextInt();
            if(reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            String sql = "DELETE FROM Reservation WHERE Reservation_id = " +reservationId;
            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if (affectedRows>0){
                    System.out.println("Reservation is sucessfully deleted.");
                }else{
                    System.out.println("Reservation delection failed.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static boolean reservationExists(Connection connection, int reservationId){
        try{
            String sql = "SELECT Reservation_id FROM reservation WHERE Reservation_id = "+reservationId;
            try(Statement statement = connection.createStatement()){
                ResultSet resultSet = statement.executeQuery(sql);
                return !resultSet.next();
            }
        }catch (SQLException e){
            e.printStackTrace();
            return true;
        }
    }

    public static void exit() throws InterruptedException{
        System.out.println("Existing System");
        int i =5;
        while (i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Management System!!!");
    }

}
