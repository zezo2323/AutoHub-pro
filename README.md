# 🚗 AutoHub — Car Sales & Rental Manager

---

## ✨ Project Overview

A complete JavaFX desktop application for managing car sales and rentals. The app handles car inventory, customer database, contracts, payments, and invoicing with a modern UI and solid business logic.

---

## 💡 Concept & Purpose

AutoHub organizes daily showroom and rental operations: adding cars and customers, handling contracts, and generating analytics. Supports multi-user access and tracks all actions using the MVC pattern for scalable code.

---

## 🚦 Key Features

- Multi-role login (Manager / Sales / Rental Agent)
- Modern dashboard with live stats and quick actions
- Car inventory management (add, edit, status, gallery, filters)
- Customer management (profiles, contracts, contact)
- Rental contracts: issue, renew, payment management
- Sales registration, print or export invoices
- Payments: record, schedule, reminders
- Analytics: charts, trends, top cars/rentals
- PDF invoices, integrated printing
- Fully customizable settings

---

## 🗄️ Database Structure

| Table     | Description                              |
|-----------|------------------------------------------|
| cars      | Car details, status, images              |
| customers | Customer info, contracts                 |
| rentals   | Rental contracts, dates, payments        |
| sales     | Sales contracts, billing                 |
| invoices  | Invoice numbers, amounts, payment status |

---

## 🔗 Key Relationships

- Car ↔ Rental / Sale (one-to-many)
- Customer ↔ contracts (one-to-many)
- Invoice ↔ Rental/Sale (one-to-one)

---

## 🛠️ Tech Stack

| Component   | Technology               |
|-------------|--------------------------|
| Language    | Java 17                  |
| UI          | JavaFX / FXML / CSS      |
| Database    | (optional) SQLite/MySQL  |
| IDE         | IntelliJ IDEA 2025       |
| Design Tool | Scene Builder, Icons8    |

---

## 🖼️ Example Features

- Add / Edit / Delete cars with images & status
- Register rental contracts, assign car & customer, manage payments
- Sales with instant invoices/receipts
- Statistics: top rentals & sales, monthly revenue, beautiful charts

---

## 📊 Example Dashboard (Inspiration)

![Dashboard](https://img.icons8.com/color/96/analytics.png)
![Car](https://img.icons8.com/color/96/car--v2.png)
![Customers](https://img.icons8.com/color/96/customer-insight.png)
![Agreement](https://img.icons8.com/color/96/agreement.png)
![Invoice](https://img.icons8.com/color/96/invoice.png)

---

## 👥 Developed By

- Zeyad Awad (zezo2323) — Project Owner
- Gamal   Leader Developer
- Team Members

---

## 🎨 Style & Inspirations

- Dark/modern UI inspired by Icons8 & Shields.io
- Animated SVG car icons, charts, badges
- MVC code structure for professional workflow
- Scene Builder for multi-page UI and design

---

## 📚 References

- JavaFX Docs ([openjfx.io](https://openjfx.io/))
- Scene Builder ([gluonhq.com/products/scene-builder/](https://gluonhq.com/products/scene-builder/))
- Icons from [icons8.com](https://icons8.com/) & badges from [shields.io](https://shields.io/)
- Official Git & GitHub Docs

---

> **Made with ❤️ by Netsoft Team**

