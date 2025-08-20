-- -----------------------------------------------------
-- Database: onlinebillingsystem-pahanaedu
-- Description: SQL script for creating tables for the Online Billing System
-- Created by: Niwanthi Kaushalya
-- -----------------------------------------------------

-- Create Database
CREATE DATABASE IF NOT EXISTS onlinebillingsystem-pahanaedu;

-- Use Database
USE onlinebillingsystem-pahanaedu;

-- -----------------------------------------------------
-- Table: users
-- -----------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL
    );

-- -----------------------------------------------------
-- Table: customers
-- -----------------------------------------------------
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cus_Name VARCHAR(100) NOT NULL,
    cus_Address TEXT,
    cus_Mobile VARCHAR(20),
    cus_AccountNumber VARCHAR(20) UNIQUE NOT NULL,
    units_consumed INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
    );

-- -----------------------------------------------------
-- Table: items
-- -----------------------------------------------------
CREATE TABLE items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL UNIQUE,
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
    );

-- -----------------------------------------------------
-- Table: bills
-- -----------------------------------------------------
CREATE TABLE bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
    );

-- -----------------------------------------------------
-- Table: bill_details
-- -----------------------------------------------------
CREATE TABLE bill_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    item_id INT NOT NULL,
    item_name_at_sale VARCHAR(255) NOT NULL,
    unit_price_at_sale DECIMAL(10, 2) NOT NULL,
    units INT NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
    );
