-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 25, 2026 at 12:59 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `nsbankdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `id` int(11) NOT NULL,
  `user_number` varchar(20) NOT NULL,
  `type` varchar(100) NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `date` datetime NOT NULL DEFAULT current_timestamp(),
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`id`, `user_number`, `type`, `amount`, `date`, `user_id`) VALUES
(1, '09111111111', 'Cash In', 5000.00, '2025-08-01 09:00:00', 2),
(2, '09111111111', 'Transfer to 09222222222', 1000.00, '2025-08-02 10:30:00', 2),
(3, '09222222222', 'Received from 09111111111', 1000.00, '2025-08-02 10:30:00', 3),
(4, '09333333333', 'Cash In', 10000.00, '2025-08-03 14:00:00', 4),
(5, '09333333333', 'Transfer to 09444444444', 2000.00, '2025-08-04 08:15:00', 4),
(6, '09444444444', 'Received from 09333333333', 2000.00, '2025-08-04 08:15:00', 5),
(7, '09937152840', 'Cash In', 454542.00, '2026-04-25 15:52:36', 6),
(8, '09999999990', 'Cash In', 453405034053.00, '2026-04-25 15:55:57', 7),
(9, '09999999990', 'Transfer to 09937152840', 987726326.00, '2026-04-25 15:56:12', 7),
(10, '09937152840', 'Received from 09999999990', 987726326.00, '2026-04-25 15:56:12', 6),
(11, '09123456782', 'Cash In', 35000.00, '2026-04-25 16:22:55', 9),
(12, '09123456782', 'Transfer to 09937152840', 15000.00, '2026-04-25 16:23:27', 9),
(13, '09937152840', 'Received from 09123456782', 15000.00, '2026-04-25 16:23:27', 6),
(14, '09937152840', 'Cash In', 1000000.00, '2026-04-25 16:24:45', 6),
(15, '09937152840', 'Transfer to 09123456782', 3000000.00, '2026-04-25 16:25:05', 6),
(16, '09123456782', 'Received from 09937152840', 3000000.00, '2026-04-25 16:25:05', 9);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `number` varchar(20) NOT NULL,
  `pin` varchar(10) NOT NULL,
  `balance` decimal(15,2) NOT NULL DEFAULT 0.00,
  `role` enum('admin','user') NOT NULL DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `number`, `pin`, `balance`, `role`) VALUES
(1, 'Admin', 'admin@nsbank.com', '09999999999', '000000', 0.00, 'admin'),
(2, 'Juan', 'juan@nsbank.com', '09111111111', '111111', 15000.00, 'user'),
(3, 'Maria', 'maria@nsbank.com', '09222222222', '222222', 8500.00, 'user'),
(4, 'Pedro', 'pedro@nsbank.com', '09333333333', '333333', 22000.00, 'user'),
(5, 'Ana', 'ana@nsbank.com', '09444444444', '444444', 5300.00, 'user'),
(6, 'Francis Nikko Altares', 'francis.nikko.28@gmail.com', '09937152840', '123456', 986195868.00, 'user'),
(7, 'Kate Altares', 'kate@email.com', '09999999990', '123456', 452417307727.00, 'user'),
(8, 'Test', 'test@mail.com', '09123456789', '123456', 0.00, 'user'),
(9, 'Aurora San', 'auroraaurora@gmail.com', '09123456782', '123456', 3020000.00, 'user');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_user` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `number` (`number`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
