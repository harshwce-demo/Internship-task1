# Internship-task1
Hotel System for reservatiom
# Hotel Reservation System

A simple **Java console-based Hotel Reservation System** that allows users to view rooms, search available rooms, book rooms, cancel reservations, and view reservation details.

## Features

* View all hotel rooms
* Search available rooms by category
* Book a hotel room
* Cancel a reservation
* View reservation details
* Simulate payment
* Save and load data using text files
* Uses Java OOP concepts

## Room Categories

| Category | Price per Night |
| -------- | --------------- |
| STANDARD | $80             |
| DELUXE   | $150            |
| SUITE    | $300            |

## Technologies Used

* Java
* Object-Oriented Programming (OOP)
* File Handling
* Collections
* Enums

## How to Run

1. Make sure Java is installed on your computer.
2. Save the file as:

```text
HotelSystem.java
```

3. Open the terminal in the project folder.
4. Compile the program:

```bash
javac HotelSystem.java
```

5. Run the program:

```bash
java HotelSystem
```

## Menu

```text
1. View all rooms
2. Search available rooms by category
3. Book a room
4. Cancel a reservation
5. View reservation details
6. Exit
```

## Data Storage

The program automatically stores data in:

```text
rooms.txt
reservations.txt
```

This allows room availability and reservations to remain saved between program runs.

## OOP Concepts Used

* **Classes and Objects** – Room, Reservation, and HotelService
* **Encapsulation** – Data and methods are organized inside classes
* **Enum** – Used for room categories
* **Methods** – Used for booking, cancellation, searching, and displaying information

## Author

**Harsh**
B.Tech – Artificial Intelligence & Machine Learning
Walchand College of Engineering, Sangli
