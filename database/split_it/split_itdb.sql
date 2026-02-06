-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 15, 2026 at 04:54 AM
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
-- Database: `split_itdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `bills`
--

CREATE TABLE `bills` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `payer_name` varchar(255) DEFAULT NULL,
  `due_date` varchar(50) DEFAULT NULL,
  `split_type` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bills`
--

INSERT INTO `bills` (`id`, `title`, `total_amount`, `payer_name`, `due_date`, `split_type`, `created_at`, `status`) VALUES
(4, 'fried chicker', 500.00, 'gab', '16/1/2026', 'Customize', '2026-01-13 22:31:43', 1),
(5, 'chicks', 325.00, 'tre', '16/1/2026', 'Customize', '2026-01-13 22:34:02', 1),
(7, 'dasdas', 240.00, 'fssfd', '17/1/2026', 'Equally', '2026-01-13 22:57:48', 1),
(8, 'Egg', 50.00, 'Alex', '15/1/2026', 'Customize', '2026-01-14 14:18:33', 1),
(9, 'Sinigang', 500.00, 'Albert', '16/1/2026', 'Equally', '2026-01-15 03:29:55', 1),
(10, 'Adobo', 350.00, 'Goten', '16/1/2026', 'Customize', '2026-01-15 03:31:34', 1);

-- --------------------------------------------------------

--
-- Table structure for table `bill_members`
--

CREATE TABLE `bill_members` (
  `id` int(11) NOT NULL,
  `bill_id` int(11) DEFAULT NULL,
  `person_name` varchar(255) NOT NULL,
  `individual_amount` decimal(10,2) DEFAULT 0.00,
  `is_paid` int(11) DEFAULT 0,
  `paid_amount` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bill_members`
--

INSERT INTO `bill_members` (`id`, `bill_id`, `person_name`, `individual_amount`, `is_paid`, `paid_amount`) VALUES
(3, 4, 'Lee', 350.00, 0, 0.00),
(4, 4, 'Oro', 150.00, 0, 0.00),
(5, 5, 'bi', 300.00, 0, 0.00),
(6, 5, 'ba', 25.00, 0, 0.00),
(11, 7, 'qweqwe', 120.00, 0, 120.00),
(12, 7, 'tretrt', 120.00, 0, 120.00),
(13, 8, 'Miy', 35.00, 0, 0.00),
(14, 8, 'Lee', 15.00, 0, 0.00),
(15, 9, 'Mati', 250.00, 0, 250.00),
(16, 9, 'Mate', 250.00, 0, 250.00),
(17, 10, 'Ket', 128.00, 0, 0.00),
(18, 10, 'Mea', 222.00, 0, 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `fullname` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `birthdate` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `fullname`, `email`, `password`, `birthdate`) VALUES
(3, 'John Carlo Dincos', 'jcdinco69@gmail.com', 'December62006', '6/12/2005'),
(5, 'Alexa Jane Solvidad', 'alexajane@gmail.com', 'Alexajane', '3/3/2006');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bills`
--
ALTER TABLE `bills`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `bill_members`
--
ALTER TABLE `bill_members`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bill_id` (`bill_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bills`
--
ALTER TABLE `bills`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `bill_members`
--
ALTER TABLE `bill_members`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bill_members`
--
ALTER TABLE `bill_members`
  ADD CONSTRAINT `bill_members_ibfk_1` FOREIGN KEY (`bill_id`) REFERENCES `bills` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
