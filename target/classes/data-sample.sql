-- =========================================================
-- AssetFlow AI - Sample Data
-- Run manually after the app has created tables (ddl-auto=update)
-- Default password for all seeded users is: Password123!
-- BCrypt hash below corresponds to that password.
-- =========================================================

-- Departments
INSERT IGNORE INTO departments (name, code, description, active, created_at, updated_at)
VALUES
  ('Information Technology', 'IT', 'Handles all technology infrastructure', true, now(), now()),
  ('Human Resources', 'HR', 'Employee management and admin', true, now(), now()),
  ('Operations', 'OPS', 'Day to day operations', true, now(), now());

-- Asset Categories
INSERT IGNORE INTO asset_categories (name, description, prefix, active, created_at, updated_at)
VALUES
  ('Electronics', 'Laptops, monitors, peripherals', 'AF', true, now(), now()),
  ('Furniture', 'Desks, chairs, cabinets', 'FRN', true, now(), now()),
  ('Vehicles', 'Company cars and delivery vehicles', 'VEH', true, now(), now());

-- =========================================================
-- Users are intentionally NOT seeded here with a hardcoded password hash
-- (a made-up hash would just fail to authenticate). Instead, bootstrap
-- your first admin like this:
--
-- 1. Register a normal account through the API - it will be created as EMPLOYEE:
--      POST /api/auth/register
--      { "fullName": "System Admin", "email": "admin@assetflow.ai", "password": "Password123!" }
--
-- 2. Promote that one account to ADMIN directly in the database (one-time bootstrap
--    only - after this, use PATCH /api/users/{id}/role as an ADMIN for everyone else):
--      UPDATE users SET role = 'ADMIN' WHERE email = 'admin@assetflow.ai';
--
-- 3. Repeat step 1 (without step 2) to create additional EMPLOYEE demo accounts,
--    e.g. jane@assetflow.ai.
-- =========================================================

-- Sample assets (category_id references assumed to be 1, 2, 3 in insertion order)
INSERT IGNORE INTO assets (asset_code, name, category_id, serial_number, purchase_date, purchase_cost,
                     location, condition_rating, status, bookable, health_score, created_at, updated_at)
VALUES
  ('AF-0001', 'Dell Latitude 5440 Laptop', 1, 'SN-LT-0001', '2024-03-10', 950.00,
   'IT Department', 'GOOD', 'AVAILABLE', false, 92, now(), now()),
  ('FRN-0001', 'Ergonomic Office Chair', 2, 'SN-CH-0001', '2023-11-01', 220.00,
   'HR Office', 'NEW', 'AVAILABLE', false, 98, now(), now()),
  ('VEH-0001', 'Toyota Hiace Delivery Van', 3, 'SN-VH-0001', '2022-06-15', 28000.00,
   'Operations Yard', 'FAIR', 'AVAILABLE', true, 70, now(), now());
