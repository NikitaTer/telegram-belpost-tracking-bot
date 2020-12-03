/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 8.0.20 : Database - telegram_belpost_bot
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`telegram_belpost_bot` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `telegram_belpost_bot`;

/*Table structure for table `state` */

DROP TABLE IF EXISTS `state`;

CREATE TABLE `state` (
  `id` int unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tracking` */

DROP TABLE IF EXISTS `tracking`;

CREATE TABLE `tracking` (
  `number` varchar(13) NOT NULL,
  `last_event` text,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `username` varchar(255) NOT NULL,
  `chat_id` bigint NOT NULL,
  `state_id` int unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`username`),
  KEY `state` (`state_id`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_tracking` */

DROP TABLE IF EXISTS `user_tracking`;

CREATE TABLE `user_tracking` (
  `user_username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `tracking_number` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `tracking_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'nameless',
  PRIMARY KEY (`user_username`,`tracking_number`),
  KEY `tracking` (`tracking_number`),
  CONSTRAINT `user_tracking_ibfk_1` FOREIGN KEY (`user_username`) REFERENCES `user` (`username`),
  CONSTRAINT `user_tracking_ibfk_2` FOREIGN KEY (`tracking_number`) REFERENCES `tracking` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
