# Electronics Store Management Software

---
### USERNAMES & PASSWORDS

Below is the list of users with their roles and passwords. Use these to log in to the application:

Cashiers:
Username: cashier1, Password: 12345 (Manages Mobile Devices)
Username: cashier2, Password: 12345 (Manages Computers)
Username: cashier3, Password: 12345 (Manages Audio & Video)

Managers:
Username: manager1, Password: 54321 (Manages Computers)
Username: manager2, Password: 54321 (Manages Mobile Devices and Audio & Video)

Administrator:
Username: admin, Password: 123 (All, has full access to the system)

---
### HOW TO USE THE SOFTWARE

1. Login:
Use the provided usernames and passwords to log in. Each user sees a dashboard based on their role.

2. Cashier Dashboard:
Add items to a bill using their name and quantity.
Generate and save bills.
View today's bills and total sales.

3. Manager Dashboard:
Restock or add new items to inventory.
Monitor cashier performance and sales statistics.
Receive low-stock notifications.

4. Administrator Dashboard:
Manage employee profiles.
Grant or revoke employee access.
View detailed reports on sales and expenses.

5. Logout:
Click the red "Logout" button to return to the login screen.

---
### SOFTWARE DESIGN

DESIGN & ORGANIZATION
We organized it into models, controllers, and views for easy maintenance.

FILES
users.txt: Stores usernames, passwords, and roles
inventory.txt: Tracks inventory details (name, price, quantity, sector)
bills/: Directory which stores the bills in txt format


