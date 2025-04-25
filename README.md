Here’s a professional `README.md` file tailored for your **Banking System Backend Project**, using **Spring Boot 3**, **Spring Security 6**, **Swagger**, and **PostgreSQL**. I've also included the caution about setting permissions for the upload directories.

---

```markdown
# 🏦 National Bank of Rwanda - Backend Banking System

## 📌 Project Overview

This is a backend application developed using **Spring Boot 3**, **Spring Security 6**, and **PostgreSQL** to handle core banking operations for the National Bank of Rwanda. It supports:

- Customer registration and account management
- Saving, withdrawal, and money transfer operations
- Email notifications on every transaction
- Persistent message logs per transaction via database triggers
- File uploads for customer documents and profile images

> ⏱️ This project was built under a time constraint of **4 hours** as part of a practical assessment.

---

## 🚀 Features

- **Register Clients**: Store personal and account details.
- **Transactions**:
  - Saving
  - Withdrawal
  - Transfer between customers
- **Automatic Email Alerts**:
  - Message: `CUSTOMER NAMES - YOUR SAVING/WITHDRAW of <AMOUNT> on your account <ACCOUNT> has been Completed Successfully.`
- **Database Triggers**:
  - Automatically log messages in `Message` table on each transaction.
- **File Upload Support**:
  - Customer profile images
  - Document uploads (pdf/png/jpeg/jpg)

---

## 🛠️ Tech Stack

| Technology       | Version     |
|------------------|-------------|
| Spring Boot      | 3.x         |
| Spring Security  | 6.x         |
| PostgreSQL       | Latest      |
| Swagger UI       | Enabled     |

---

## 🔐 API Documentation

> Accessible via Swagger UI:

```
http://localhost:9090/Banking/swagger-ui/index.html
```

---

## 🧾 Database Configuration

In `application.properties` or `application.yml`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banking_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 📁 File Upload Configuration

Make sure the following directories **exist** and have proper **write permissions** before running the application:

```properties
# Allowed upload types
uploads.extensions=pdf,png,jpeg,jpg

# Base directories
uploads.directory=/home/uploads/ne-springboot/uploads
uploads.directory.customer_profiles=/home/uploads/ne-springboot/uploads/customer_profiles
uploads.directory.docs=/home/uploads/ne-springboot/uploads/docs
```

### ⚠️ Caution:
Before uploading any files, **ensure that the directories listed above are created and have appropriate read/write permissions**.  
Example:

```bash
sudo mkdir -p /home/uploads/ne-springboot/uploads/customer_profiles
sudo mkdir -p /home/uploads/ne-springboot/uploads/docs
sudo chmod -R 775 /home/uploads/ne-springboot/uploads
```

---

## 💻 Running the Project

1. Clone the repository
2. Set up PostgreSQL database
3. Configure your `application.properties`
4. Ensure directories for file uploads exist and are writable
5. Run the Spring Boot application
6. Test endpoints via Swagger or Postman

---

## 📬 Email Notifications

Ensure that your email service is configured correctly to send notifications on each transaction. This can be set in `application.properties`:

```properties
spring.mail.host=smtp.yourprovider.com
spring.mail.port=587
spring.mail.username=your_email
spring.mail.password=your_email_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🧪 Manual Testing

Records should be added manually using:

- Postman
- Swagger
- Application Runner (via `CommandLineRunner`)
- Directly via your preferred DBMS (pgAdmin, DBeaver, etc.)

---

## 📝 Tables Overview

- **Customer**: `id`, `firstName`, `lastName`, `email`, `mobile`, `dob`, `account`, `balance`, `lastUpdatedDateTime`
- **Banking**: `id`, `customer`, `account`, `amount`, `type`(saving/withdraw/transfer), `bankingDateTime`
- **Message**: `id`, `customer`, `message`, `dateTime`

---

## 📎 Notes

- All logic for sending emails and registering messages is implemented in the service layer.
- Triggers are defined in the PostgreSQL database to automatically insert messages into the `Message` table.

---

## 🧑‍💻 Author

Developed by Jean de Dieu .

---

## 📄 License

This project is for educational and evaluation purposes only.
