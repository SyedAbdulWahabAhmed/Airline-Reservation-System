
This project is a console-based Airline Management System developed in Java using Object-Oriented Programming (OOP) principles. It simulates a real-world airline system where users can manage flights, passengers, reservations, and authentication.

The system supports both Admin and Customer roles with different functionalities and ensures data persistence using file handling.

🚀 Features
👨‍✈️ Admin Functionalities
Add, update, delete flights
Cancel flights
View all flights and passengers
Manage user accounts
View total revenue and flight-wise revenue

🧑‍💼 Customer Functionalities
Register & login
Search available flights
Book flight tickets
Cancel reservations
Check-in for flights
View booking history

🧱 Project Structure
📦 Model Classes
Flight → Handles flight details (schedule, seats, pricing)
Passenger → Stores passenger information
Reservation → Manages booking data
User → Handles authentication & roles (Admin/Customer)

⚙️ Service Layer
AuthService → Login, registration, password management
FlightService → Flight operations & seat management
PassengerService → Passenger CRUD operations
ReservationService → Booking, cancellation, check-in

💾 Storage Layer
DataStore (Singleton)
Handles file I/O
Stores data in CSV files
Ensures persistence

🛠️ Utilities
AppUtil
ID generation
Password hashing (SHA-256)
Input validation

❗ Custom Exceptions
AirlineException
FlightNotFoundException
PassengerNotFoundException
ReservationNotFoundException
NoSeatsAvailableException

💡 Key Concepts Used
Object-Oriented Programming (OOP)
Encapsulation, Abstraction, Polymorphism
File Handling (CSV-based persistence)
Singleton Design Pattern (DataStore)
Exception Handling
Data Validation & Hashing

💾 Data Storage
All data is stored in CSV files inside a data/ directory:
flights.csv
passengers.csv
reservations.csv
users.csv

🔐 Security
Passwords are stored using SHA-256 hashing
Basic authentication system implemented

▶️ How to Run
Clone the repository
Open in any Java IDE (IntelliJ / Eclipse / VS Code)
Compile and run the main class
Use console input to interact with the system

📈 Future Improvements
GUI (JavaFX / Swing)
Database integration (MySQL)
Online payment simulation
REST API version

👨‍💻 Author
Abdul Wahab
