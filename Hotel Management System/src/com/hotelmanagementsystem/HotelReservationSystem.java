package com.hotelmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

import javax.management.RuntimeErrorException;

import com.mysql.cj.protocol.x.SyncFlushDeflaterOutputStream;

public class HotelReservationSystem {
	private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
	private static final String usrename = "root";
	private static final String password = "Sandip@123";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			Connection connection = DriverManager.getConnection(url, usrename, password);
			while (true) {
				System.out.println();
				System.out.println("HOTEL MANAGEMENT SYTSTEM");
				Scanner scanner = new Scanner(System.in);
				System.out.println("1. Reserve a Room");
				System.out.println("2. View Reservations");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update Reservations");
				System.out.println("5. Delete Reservations");
				System.out.println("0. Exit");
				System.out.println("Choose an Option:");
				int choice = scanner.nextInt();
				switch (choice) {
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
					System.out.println("Invalid choice. Try again.");

				}

			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	private static void reserveRoom(Connection connection, Scanner scanner) {
		System.out.println("Enter Guest Name: ");
		String guestName = scanner.next();
		scanner.nextLine();
		System.out.println("Enter Room Number: ");
		int roomNumber = scanner.nextInt();
		System.out.println("Enter Contact Number: ");
		String contactNumber = scanner.next();

		String sql = "insert into reservations(guest_name,room_number,contact_number)" + "values('" + guestName + "','"
				+ roomNumber + "','" + contactNumber + "')";

		try {
			Statement statement = connection.createStatement();
			int affectedRows = statement.executeUpdate(sql);

			if (affectedRows > 0) {
				System.out.println("********************* Reservation Successfull.....!");

			} else {
				System.out.println("Reservation failed.");
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	private static void viewReservation(Connection connection) {

		String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			System.out.println("Current Reservation:");
			System.out.println(
					"+----------------+-----------------+---------------+----------------------+-------------------------+");
			System.out.println(
					"| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date         |");
			System.out.println(
					"+----------------+-----------------+---------------+----------------------+-------------------------+");
			while (resultSet.next()) {
				int reservationId = resultSet.getInt("reservation_id");
				String guestName = resultSet.getString("guest_name");
				int roomNumber = resultSet.getInt("room_number");
				String contactNumber = resultSet.getString("contact_number");
				String reservationDate = resultSet.getTimestamp("reservation_date").toString();

				System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n", reservationId, guestName, roomNumber,
						contactNumber, reservationDate);

			}
			System.out.println(
					"+----------------+-----------------+---------------+----------------------+-------------------------+");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void getRoomNumber(Connection connection, Scanner scanner) {

		System.out.println("Enter reservation ID: ");
		int reservationId = scanner.nextInt();
		System.out.println("Enter Guest Name: ");
		String guestName = scanner.next();

		String sql = "select room_number from reservations " + "where reservation_id = " + reservationId + ""
				+ " AND guest_name ='" + guestName + "'";

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
			if (resultSet.next()) {
				int roomNumber = resultSet.getInt("room_number");
				System.out.println("Room number for Reservation ID  :" + reservationId + "  and guest Name" + guestName + " is:"
						+ roomNumber);
			} else {
				System.out.println("Reservation not found for the given Id and guesd name.");

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void updateReservation(Connection connection, Scanner scanner) {

		try {
			System.out.println("Enter reservation Id To Update:");
			int reservationId = scanner.nextInt();
			scanner.nextLine();

			if (!reservationExists(connection, reservationId)) {
				System.out.println("Reservation not found for the given ID.");
				return;
			}
			System.out.println("Enter new guest Name: ");
			String newGuestName = scanner.next();
			System.out.println("Enter New Room Number:  ");
			int newRoomNumber = scanner.nextInt();
			System.out.println("Enter New Contact Number: ");
			String newContactNumber = scanner.next();

			String sql = "update reservations set guest_name ='" + newGuestName + "'," + "room_number = "
					+ newRoomNumber + "," + "contact_number ='" + newContactNumber + "'" + "where reservation_id ="
					+ reservationId;

			try (Statement statement = connection.createStatement()) {
				int affectedRows = statement.executeUpdate(sql);

				if (affectedRows > 0) {
					System.out.println("Reservation Updated Successfullt!");
				} else {
					System.out.println("Reservation Update Failed.");
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static void deleteReservation(Connection connection, Scanner scanner) {
		try {
			System.out.println("Enter Reservario ID to Delete:n ");
			int reservation_Id = scanner.nextInt();
			if (!reservationExists(connection, reservation_Id)) {
				System.out.println("Reservation not found for given ID.");
				return;
			}
			String sql = "delete from reservations where reservation_id =" + reservation_Id;
			try (Statement statement = connection.createStatement()) {
				int affectedRows = statement.executeUpdate(sql);

				if (affectedRows > 0) {
					System.out.println("Reservation deleted Successfully!");

				} else {
					System.out.println("Reservation deletion failed.");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static boolean reservationExists(Connection connection, int reservationId) {
		try {
			String sql = " select reservation_id from reservations where reservation_id = " + reservationId;

			try (Statement statement = connection.createStatement();
					ResultSet resultSet = statement.executeQuery(sql)) {
				return resultSet.next();

			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void exit() throws InterruptedException {
		System.out.println("Exiting System");
		int i = 5;
		while (i != 0) {
			System.out.print(".");
			Thread.sleep(450);
			i--;
		}
		System.out.println();
		System.out.println("Thank For Using Hotel Reservation System!!!");
	}
//	private static void reserveRoom(Connection connection, Scanner scanner) {
//
//	}

}
