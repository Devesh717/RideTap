🚇 **RideTap – Smart Metro Ticket Booking Platform**



RideTap is a metro ticket booking and travel management system that allows users to search routes, book tickets, manage metro cards, and receive QR-based travel passes. The platform also provides AI-powered assistance for user support and travel-related queries.



✨ **Features**



👤 **User Management**



User Registration & Login



JWT Authentication & Authorization



Profile Management



Secure Password Encryption





🚇 **Metro Services**



Metro Station Information



Route Discovery



Fare Calculation



Travel History





🎫 **Ticket Booking**



Online Metro Ticket Booking



QR Code Ticket Generation



Booking Confirmation



Booking History Tracking





💳 **Metro Card Management**



Metro Card Registration



Balance Inquiry



Card Recharge



Transaction History





🤖 **AI Assistant**



View User Profile



Check Metro Card Balance



Travel Assistance



Natural Language Queries





📩 **Notifications**



Booking Confirmation Emails



Booking Cancellation Emails



Event-Based Notifications


🛡️ **Admin Panel**


Admin Authentication


Manage Users


Manage Metro Stations


View All Bookings


Monitor Metro Card Transactions




🛠️ Tech Stack



**Backend**



Java 21



Spring Boot



Spring Security



Spring Data JPA



Hibernate



Database



MySQL



Messaging



Apache Kafka



AI Integration



Gemini API



Spring AI



Security



JWT Authentication



BCrypt Password Encoding



**Tools**



Maven



Docker



Postman



Git & GitHub





📂 **Project Structure**



RideTap/



│



├── src/



│   ├── main/



│   │   ├── java/



│   │   ├── resources/



│   │   └── templates/



│   │



│   └── test/



│



├── pom.xml



├── docker-compose.yml



└── README.md





🚀 **Key Functionalities**



Secure user authentication using JWT



Metro ticket booking with QR code generation



Metro card balance management and recharge



Route and fare information



AI-powered passenger support



Email notifications for bookings



Kafka-based event processing





⚙️ **Setup**



Clone Repository



git clone https://github.com/your-username/RideTap.git





Configure application.properties



Update the following properties:



spring.datasource.url=jdbc:mysql://localhost:3306/ridetap_db



spring.datasource.username=root



spring.datasource.password=your_password



jwt.secret=your_jwt_secret



spring.ai.openai.api-key=your_api_key





Run Application



mvn spring-boot:run



A smart metro transit solution built using **Spring Boot, MySQL, Kafka, JWT Security, QR Ticketing, and AI-powered assistance.**



⭐ **Star the repository if you found it useful.**
