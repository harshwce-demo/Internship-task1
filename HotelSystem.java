import java.io.*;
import java.util.*;

/**
 * Hotel Reservation System
 * ----------------------------------------------------------
 * A simple console-based system to search, book, and manage
 * hotel rooms. Demonstrates core OOP concepts (classes,
 * encapsulation, enums) and file I/O for persisting
 * bookings and room availability between runs.
 * ----------------------------------------------------------
 */
public class HotelSystem {

    public static void main(String[] args) {
        HotelService service = new HotelService();
        service.loadData();

        Scanner sc = new Scanner(System.in);
        System.out.println("=== Hotel Reservation System ===");

        boolean running = true;
        while (running) {
            System.out.println("\n1. View all rooms");
            System.out.println("2. Search available rooms by category");
            System.out.println("3. Book a room");
            System.out.println("4. Cancel a reservation");
            System.out.println("5. View reservation details");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    service.displayAllRooms();
                    break;

                case "2":
                    System.out.print("Enter category (STANDARD/DELUXE/SUITE): ");
                    String catInput = sc.nextLine().trim().toUpperCase();
                    try {
                        RoomCategory category = RoomCategory.valueOf(catInput);
                        service.displayAvailableRoomsByCategory(category);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid category.");
                    }
                    break;

                case "3":
                    System.out.print("Enter Room ID to book: ");
                    int roomId = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Enter your name: ");
                    String guestName = sc.nextLine().trim();
                    System.out.print("Enter number of nights: ");
                    int nights = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Enter payment method (CARD/CASH): ");
                    String method = sc.nextLine().trim();
                    service.bookRoom(roomId, guestName, nights, method);
                    break;

                case "4":
                    System.out.print("Enter Reservation ID to cancel: ");
                    int resId = Integer.parseInt(sc.nextLine().trim());
                    service.cancelReservation(resId);
                    break;

                case "5":
                    System.out.print("Enter Reservation ID: ");
                    int viewId = Integer.parseInt(sc.nextLine().trim());
                    service.viewReservation(viewId);
                    break;

                case "6":
                    running = false;
                    service.saveData();
                    System.out.println("Data saved. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
        sc.close();
    }
}

/** Categories of rooms, each with its own nightly rate. */
enum RoomCategory {
    STANDARD(80.0),
    DELUXE(150.0),
    SUITE(300.0);

    final double ratePerNight;

    RoomCategory(double ratePerNight) {
        this.ratePerNight = ratePerNight;
    }
}

/** Represents a single hotel room. */
class Room {
    int roomId;
    RoomCategory category;
    boolean available;

    Room(int roomId, RoomCategory category, boolean available) {
        this.roomId = roomId;
        this.category = category;
        this.available = available;
    }

    @Override
    public String toString() {
        return "Room #" + roomId + " | " + category + " | $" + category.ratePerNight
                + "/night | " + (available ? "Available" : "Booked");
    }

    /** Converts this room to a single CSV line for file storage. */
    String toCsv() {
        return roomId + "," + category + "," + available;
    }

    /** Recreates a Room object from a CSV line. */
    static Room fromCsv(String line) {
        String[] parts = line.split(",");
        return new Room(Integer.parseInt(parts[0]), RoomCategory.valueOf(parts[1]),
                Boolean.parseBoolean(parts[2]));
    }
}

/** Represents a booking made by a guest. */
class Reservation {
    int reservationId;
    int roomId;
    String guestName;
    int nights;
    double totalPrice;
    boolean cancelled;

    Reservation(int reservationId, int roomId, String guestName, int nights,
                double totalPrice, boolean cancelled) {
        this.reservationId = reservationId;
        this.roomId = roomId;
        this.guestName = guestName;
        this.nights = nights;
        this.totalPrice = totalPrice;
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "Reservation #" + reservationId + " | Room #" + roomId + " | Guest: " + guestName
                + " | " + nights + " night(s) | Total: $" + String.format("%.2f", totalPrice)
                + " | " + (cancelled ? "CANCELLED" : "CONFIRMED");
    }

    String toCsv() {
        return reservationId + "," + roomId + "," + guestName + "," + nights + ","
                + totalPrice + "," + cancelled;
    }

    static Reservation fromCsv(String line) {
        String[] parts = line.split(",");
        return new Reservation(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2],
                Integer.parseInt(parts[3]), Double.parseDouble(parts[4]),
                Boolean.parseBoolean(parts[5]));
    }
}

/**
 * Core business logic: searching, booking, cancelling, and
 * persisting rooms/reservations using simple text files.
 */
class HotelService {

    private static final String ROOMS_FILE = "rooms.txt";
    private static final String RESERVATIONS_FILE = "reservations.txt";

    private List<Room> rooms = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private int nextReservationId = 1;
    private Random random = new Random();

    /** Loads rooms and reservations from file, or creates default rooms if none exist. */
    void loadData() {
        File roomsFile = new File(ROOMS_FILE);
        if (roomsFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(roomsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.isBlank()) rooms.add(Room.fromCsv(line));
                }
            } catch (IOException e) {
                System.out.println("Could not load rooms: " + e.getMessage());
            }
        } else {
            // Seed default rooms on first run
            rooms.add(new Room(101, RoomCategory.STANDARD, true));
            rooms.add(new Room(102, RoomCategory.STANDARD, true));
            rooms.add(new Room(201, RoomCategory.DELUXE, true));
            rooms.add(new Room(202, RoomCategory.DELUXE, true));
            rooms.add(new Room(301, RoomCategory.SUITE, true));
        }

        File resFile = new File(RESERVATIONS_FILE);
        if (resFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(resFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.isBlank()) {
                        Reservation r = Reservation.fromCsv(line);
                        reservations.add(r);
                        if (r.reservationId >= nextReservationId) {
                            nextReservationId = r.reservationId + 1;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not load reservations: " + e.getMessage());
            }
        }
    }

    /** Writes the current rooms and reservations to file. */
    void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            for (Room room : rooms) pw.println(room.toCsv());
        } catch (IOException e) {
            System.out.println("Could not save rooms: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(RESERVATIONS_FILE))) {
            for (Reservation r : reservations) pw.println(r.toCsv());
        } catch (IOException e) {
            System.out.println("Could not save reservations: " + e.getMessage());
        }
    }

    void displayAllRooms() {
        System.out.println("\n--- All Rooms ---");
        for (Room room : rooms) {
            System.out.println(room);
        }
    }

    void displayAvailableRoomsByCategory(RoomCategory category) {
        System.out.println("\n--- Available " + category + " Rooms ---");
        boolean found = false;
        for (Room room : rooms) {
            if (room.category == category && room.available) {
                System.out.println(room);
                found = true;
            }
        }
        if (!found) System.out.println("No available rooms in this category.");
    }

    void bookRoom(int roomId, String guestName, int nights, String paymentMethod) {
        Room room = findRoom(roomId);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        if (!room.available) {
            System.out.println("Sorry, that room is already booked.");
            return;
        }
        if (nights <= 0) {
            System.out.println("Number of nights must be positive.");
            return;
        }

        double totalPrice = room.category.ratePerNight * nights;

        boolean paymentSuccess = simulatePayment(totalPrice, paymentMethod);
        if (!paymentSuccess) {
            System.out.println("Payment failed. Booking not completed. Please try again.");
            return;
        }

        room.available = false;
        Reservation reservation = new Reservation(nextReservationId++, roomId, guestName,
                nights, totalPrice, false);
        reservations.add(reservation);
        saveData();

        System.out.println("Booking confirmed!");
        System.out.println(reservation);
    }

    void cancelReservation(int reservationId) {
        Reservation reservation = findReservation(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }
        if (reservation.cancelled) {
            System.out.println("This reservation is already cancelled.");
            return;
        }

        reservation.cancelled = true;
        Room room = findRoom(reservation.roomId);
        if (room != null) room.available = true;

        saveData();
        System.out.println("Reservation #" + reservationId + " cancelled. Refund of $"
                + String.format("%.2f", reservation.totalPrice) + " simulated.");
    }

    void viewReservation(int reservationId) {
        Reservation reservation = findReservation(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }
        System.out.println(reservation);
    }

    /** Simulates a payment gateway call (90% success rate). */
    private boolean simulatePayment(double amount, String method) {
        System.out.println("Processing payment of $" + String.format("%.2f", amount)
                + " via " + method + "...");
        boolean success = random.nextDouble() < 0.9;
        System.out.println(success ? "Payment successful." : "Payment declined.");
        return success;
    }

    private Room findRoom(int roomId) {
        for (Room room : rooms) {
            if (room.roomId == roomId) return room;
        }
        return null;
    }

    private Reservation findReservation(int reservationId) {
        for (Reservation r : reservations) {
            if (r.reservationId == reservationId) return r;
        }
        return null;
    }
}
