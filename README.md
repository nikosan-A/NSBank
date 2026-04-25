# NSBank – Neighborhood Savings Bank
## Java 8 Banking App (Java Swing + MySQL + JDBC + FlatLaf)

---

## 📋 Tools Used
| Tool | Purpose |
|---|---|
| **IntelliJ IDEA Community** | Write and manage Java code |
| **Java Swing (GUI)** | Graphical user interface |
| **XAMPP** | Run Apache + MySQL locally |
| **MySQL** | Store user and transaction data |
| **phpMyAdmin** | View/manage database visually |
| **FlatLaf 3.4.1** | Modern Look & Feel for Swing |
| **JDBC** | Connect Java to MySQL |
| **Maven** | Dependency and build management |

---

## 🗂 Project Structure
```
NSBank/
├── pom.xml                          ← Maven config (dependencies)
├── database/
│   └── nsbank.sql                   ← Run this first in phpMyAdmin
└── src/
    └── main/
        └── java/
            └── com/nsbank/
                ├── Main.java                ← Entry point
                ├── DBConnection.java        ← MySQL connection
                ├── User.java                ← User data model
                ├── UserAuthentication.java  ← Login, register, transactions
                ├── MainDashboard.java       ← Welcome screen
                ├── RegistrationForm.java    ← Sign up screen
                ├── LoginForm.java           ← Login screen
                ├── AccountDashboard.java    ← User hub (Cash In/Out/TXN/Settings)
                └── AdminDashboard.java      ← Admin panel
```

---

## 🚀 Setup Steps

### Step 1 – Start XAMPP
1. Open **XAMPP Control Panel**
2. Start **Apache** and **MySQL**

### Step 2 – Create the Database
1. Open your browser and go to `http://localhost/phpmyadmin`
2. Click **Import** (top navigation)
3. Choose the file: `database/nsbank.sql`
4. Click **Go**

This creates the `nsbankdb` database with the `users` and `transactions` tables plus seed data.

### Step 3 – Open in IntelliJ IDEA
1. Open IntelliJ IDEA
2. Click **File → Open** and select the `NSBank` folder
3. IntelliJ will detect `pom.xml` — click **Trust Project** if prompted
4. Wait for Maven to download dependencies (FlatLaf + MySQL connector)

### Step 4 – Run the App
- Open `src/main/java/com/nsbank/Main.java`
- Click the green ▶ Run button (or press `Shift + F10`)

---

## 🔐 Default Credentials

| Role  | Mobile Number | MPIN |
|-------|--------------|------|
| Admin | 09999999999  | 000000 |
| User  | 09111111111  | 111111 |
| User  | 09222222222  | 222222 |
| User  | 09333333333  | 333333 |
| User  | 09444444444  | 444444 |

---

## ✨ Features
- **Main Dashboard** – Welcome screen with Log In / Create Account
- **Registration Form** – Sign up with name, email, mobile number, and MPIN
- **Login Form** – Authenticate with mobile number + MPIN
- **Account Dashboard**
  - View real-time balance
  - View profile information
  - **Cash In** – add funds to your account
  - **Cash Out / Send Money** – transfer to another user
  - **Transaction History** – full scrollable history
  - **Settings** – change MPIN
  - Log Out
- **Admin Dashboard**
  - View all users
  - View all transactions
  - View transactions per user

---

## ⚠️ Notes
- Make sure XAMPP MySQL is running **before** launching the app.
- Default MySQL credentials in `DBConnection.java`: user = `root`, password = `` (empty).
  Change these if your XAMPP has a different password.
- This project is for **educational purposes only**.
