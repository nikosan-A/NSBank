package com.nsbank;

/**
 * User – plain data model mirroring the `users` table.
 */
public class User {

    private int    id;
    private String name;
    private String email;
    private String number;
    private String pin;
    private double balance;
    private String role;   // "admin" or "user"

    public User() {}

    public User(int id, String name, String email, String number,
                String pin, double balance, String role) {
        this.id      = id;
        this.name    = name;
        this.email   = email;
        this.number  = number;
        this.pin     = pin;
        this.balance = balance;
        this.role    = role;
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getId()      { return id; }
    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public String getNumber()  { return number; }
    public String getPin()     { return pin; }
    public double getBalance() { return balance; }
    public String getRole()    { return role; }

    // ── Setters ──────────────────────────────────────────────
    public void setId(int id)           { this.id      = id; }
    public void setName(String name)    { this.name    = name; }
    public void setEmail(String email)  { this.email   = email; }
    public void setNumber(String num)   { this.number  = num; }
    public void setPin(String pin)      { this.pin     = pin; }
    public void setBalance(double bal)  { this.balance = bal; }
    public void setRole(String role)    { this.role    = role; }

    public boolean isAdmin() { return "admin".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', number='" + number + "', role='" + role + "'}";
    }
}
