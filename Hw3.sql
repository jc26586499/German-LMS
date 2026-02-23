CREATE DATABASE  IF NOT EXISTS `german_lms` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `german_lms`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: german_lms
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `study_logs`
--

DROP TABLE IF EXISTS `study_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `study_logs` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `study_date` date NOT NULL,
  PRIMARY KEY (`log_id`),
  UNIQUE KEY `user_id` (`user_id`,`study_date`),
  CONSTRAINT `study_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `study_logs`
--

LOCK TABLES `study_logs` WRITE;
/*!40000 ALTER TABLE `study_logs` DISABLE KEYS */;
INSERT INTO `study_logs` VALUES (5,1,'2026-02-19'),(6,1,'2026-02-20'),(7,1,'2026-02-21'),(8,1,'2026-02-22'),(3,1,'2026-02-23'),(1,2,'2026-02-23'),(2,6,'2026-02-23'),(4,9,'2026-02-23'),(9,10,'2026-02-23'),(10,11,'2026-02-23'),(11,12,'2026-02-23'),(12,13,'2026-02-23');
/*!40000 ALTER TABLE `study_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `t_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `amount` int DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `t_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`t_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,2,-200,'Purchase category: 家具','2026-02-22 18:41:47'),(2,2,500,'Top-up +500','2026-02-22 20:54:34'),(3,2,-200,'Purchase category: 天氣','2026-02-22 22:33:10'),(4,2,500,'Top-up +500','2026-02-22 22:37:52'),(5,2,-200,'Purchase category: 國家','2026-02-22 22:38:02'),(6,2,-200,'Purchase category: 親戚','2026-02-22 22:46:15'),(7,1,-200,'Purchase category: 國家','2026-02-23 00:06:24'),(8,6,1,'Daily Check-in 2026-02-23','2026-02-23 00:31:30'),(9,1,1,'Daily Check-in 2026-02-23','2026-02-23 00:45:53'),(10,9,1,'Daily Check-in 2026-02-23','2026-02-23 03:24:06'),(11,1,15,'Month Milestone 3 (2026-02)','2026-02-21 03:00:00'),(12,1,1,'Daily Check-in 2026-02-19','2026-02-19 01:00:00'),(13,1,1,'Daily Check-in 2026-02-20','2026-02-20 02:00:00'),(14,1,1,'Daily Check-in 2026-02-21','2026-02-21 03:00:00'),(15,1,1,'Daily Check-in 2026-02-22','2026-02-22 04:00:00'),(16,1,-200,'Purchase category: 天氣','2026-02-23 07:48:17'),(17,1,100,'Top-up +100','2026-02-23 07:49:54'),(18,1,-200,'Purchase category: 家具','2026-02-23 08:03:55'),(19,1,100,'Top-up +100','2026-02-23 08:30:13'),(20,10,1,'Daily Check-in 2026-02-23','2026-02-23 08:31:46'),(21,10,-200,'Purchase category: 親戚','2026-02-23 08:32:43'),(22,1,100,'Top-up +100','2026-02-23 08:40:11'),(23,11,1,'Daily Check-in 2026-02-23','2026-02-23 09:07:08'),(24,12,1,'Daily Check-in 2026-02-23','2026-02-23 09:09:44'),(25,12,100,'Top-up +100','2026-02-23 09:10:07'),(26,12,-200,'Purchase category: 家具','2026-02-23 09:10:18'),(27,13,1,'Daily Check-in 2026-02-23','2026-02-23 09:14:30'),(28,13,200,'Top-up +200','2026-02-23 09:14:42'),(29,13,-200,'Purchase category: 家具','2026-02-23 09:14:49');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_permissions`
--

DROP TABLE IF EXISTS `user_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_permissions` (
  `permission_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `category_purchased` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `purchase_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `user_id` (`user_id`,`category_purchased`),
  UNIQUE KEY `uq_user_category` (`user_id`,`category_purchased`),
  CONSTRAINT `user_permissions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_permissions`
--

LOCK TABLES `user_permissions` WRITE;
/*!40000 ALTER TABLE `user_permissions` DISABLE KEYS */;
INSERT INTO `user_permissions` VALUES (1,2,'水果','2026-02-22 10:22:55'),(2,3,'水果','2026-02-22 10:22:55'),(3,4,'水果','2026-02-22 10:22:55'),(4,2,'家具','2026-02-22 18:41:47'),(5,2,'天氣','2026-02-22 22:33:10'),(6,2,'國家','2026-02-22 22:38:02'),(7,2,'親戚','2026-02-22 22:46:15'),(8,1,'國家','2026-02-23 00:06:24'),(9,1,'天氣','2026-02-23 07:48:17'),(10,1,'家具','2026-02-23 08:03:55'),(11,10,'親戚','2026-02-23 08:32:43'),(12,12,'家具','2026-02-23 09:10:18'),(13,13,'家具','2026-02-23 09:14:49');
/*!40000 ALTER TABLE `user_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_word_stats`
--

DROP TABLE IF EXISTS `user_word_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_word_stats` (
  `stat_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `word_id` int NOT NULL,
  `correct_count` int NOT NULL DEFAULT '0',
  `wrong_count` int NOT NULL DEFAULT '0',
  `last_practiced` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`stat_id`),
  UNIQUE KEY `uq_user_word` (`user_id`,`word_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_word` (`word_id`),
  CONSTRAINT `fk_uws_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_uws_word` FOREIGN KEY (`word_id`) REFERENCES `words` (`word_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_word_stats`
--

LOCK TABLES `user_word_stats` WRITE;
/*!40000 ALTER TABLE `user_word_stats` DISABLE KEYS */;
INSERT INTO `user_word_stats` VALUES (1,2,17,1,0,'2026-02-22 20:01:35'),(2,2,32,0,1,'2026-02-22 20:15:38'),(3,2,1,1,2,'2026-02-22 20:40:30'),(4,2,38,1,0,'2026-02-22 20:16:12'),(5,2,18,0,2,'2026-02-22 23:27:40'),(6,2,24,0,1,'2026-02-22 20:17:26'),(7,2,28,0,1,'2026-02-22 20:17:48'),(8,2,5,1,3,'2026-02-23 09:15:27'),(9,2,8,0,2,'2026-02-22 20:25:15'),(10,2,13,1,2,'2026-02-22 20:40:26'),(11,2,4,0,3,'2026-02-22 23:08:49'),(12,2,2,2,3,'2026-02-23 09:15:22'),(13,2,14,0,5,'2026-02-22 20:41:42'),(14,2,16,0,2,'2026-02-22 20:41:19'),(15,2,12,0,1,'2026-02-22 20:40:40'),(16,2,20,1,0,'2026-02-22 23:27:24'),(17,1,2,0,2,'2026-02-23 00:07:51'),(18,2,3,0,1,'2026-02-23 00:08:33'),(19,1,1,1,1,'2026-02-23 07:52:02'),(20,1,8,1,1,'2026-02-23 07:51:54'),(21,1,4,1,0,'2026-02-23 08:30:53'),(22,10,7,0,1,'2026-02-23 08:32:30'),(23,1,38,1,0,'2026-02-23 08:40:38'),(24,1,40,0,1,'2026-02-23 08:40:43'),(25,12,10,1,0,'2026-02-23 09:10:38');
/*!40000 ALTER TABLE `user_word_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('student','admin') COLLATE utf8mb4_unicode_ci DEFAULT 'student',
  `balance` int DEFAULT '0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','1234','admin',9719),(2,'student1','1234','student',170),(3,'student2','1234','student',800),(4,'student3','1234','student',50),(5,'123','123','student',200),(6,'1','123','student',201),(7,'2','2','student',200),(8,'3','3','student',200),(9,'4','4','student',201),(10,'Qoo','1','student',1),(11,'Q','1','student',201),(12,'A','1','student',101),(13,'R','1','student',201);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `view_category_stats`
--

DROP TABLE IF EXISTS `view_category_stats`;
/*!50001 DROP VIEW IF EXISTS `view_category_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_category_stats` AS SELECT 
 1 AS `category`,
 1 AS `total_words`,
 1 AS `total_purchases`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `view_transaction_history`
--

DROP TABLE IF EXISTS `view_transaction_history`;
/*!50001 DROP VIEW IF EXISTS `view_transaction_history`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_transaction_history` AS SELECT 
 1 AS `t_id`,
 1 AS `username`,
 1 AS `amount`,
 1 AS `description`,
 1 AS `t_date`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `view_user_status`
--

DROP TABLE IF EXISTS `view_user_status`;
/*!50001 DROP VIEW IF EXISTS `view_user_status`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `view_user_status` AS SELECT 
 1 AS `user_id`,
 1 AS `username`,
 1 AS `balance`,
 1 AS `role`,
 1 AS `unlocked_category`,
 1 AS `purchase_date`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `words`
--

DROP TABLE IF EXISTS `words`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `words` (
  `word_id` int NOT NULL AUTO_INCREMENT,
  `vocabulary` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `meaning` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`word_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `words`
--

LOCK TABLES `words` WRITE;
/*!40000 ALTER TABLE `words` DISABLE KEYS */;
INSERT INTO `words` VALUES (1,'Apfel','蘋果','水果','fruit_01.jpg'),(2,'Banane','香蕉','水果','fruit_02.jpg'),(3,'Erdbeere','草莓','水果','fruit_03.jpg'),(4,'Zitrone','檸檬','水果','fruit_04.jpg'),(5,'Kirsche','櫻桃','水果','fruit_05.jpg'),(6,'Weintraube','葡萄','水果','fruit_06.jpg'),(7,'Wassermelone','西瓜','水果','fruit_07.jpg'),(8,'Pfirsich','水蜜桃','水果','fruit_08.jpg'),(9,'Stuhl','椅子','家具','furn_02.jpg'),(10,'Tisch','桌子','家具','furn_04.jpg'),(11,'Bett','床','家具','furn_03.jpg'),(12,'Schrank','衣櫃','家具','furn_06.jpg'),(13,'Schublade','抽屜','家具','furn_07.jpg'),(14,'Sofa','沙發','家具','furn_01.jpg'),(15,'Lampe','燈','家具','furn_05.jpg'),(16,'Fernsehen','電視','家具','furn_08.jpg'),(17,'Mutter','母親','親戚','rel_04.jpg'),(18,'Vater','父親','親戚','rel_03.jpg'),(19,'Bruder','兄弟','親戚','rel_05.jpg'),(20,'Schwester','姊妹','親戚','rel_06.jpg'),(21,'Großvater','祖父','親戚','rel_01.jpg'),(22,'Großmutter','祖母','親戚','rel_02.jpg'),(23,'Sohn','兒子','親戚','rel_07.jpg'),(24,'Tochter','女兒','親戚','rel_08.jpg'),(25,'Sonne','太陽','天氣','wea_01.jpg'),(26,'Regen','下雨','天氣','wea_02.jpg'),(27,'Schnee','下雪','天氣','wea_03.jpg'),(28,'Heiter bis wolkig','多雲時晴','天氣','wea_06.jpg'),(29,'Gewitter','暴風雨','天氣','wea_05.jpg'),(30,'Bewölkt','多雲','天氣','wea_04.jpg'),(31,'Heiß','炎熱','天氣','wea_07.jpg'),(32,'Kalt','寒冷','天氣','wea_08.jpg'),(33,'Deutschland','德國','國家','cnt_01.jpg'),(34,'Taiwan','台灣','國家','cnt_02.jpg'),(35,'Japan','日本','國家','cnt_03.jpg'),(36,'Österreich','奧地利','國家','cnt_04.jpg'),(37,'Frankreich','法國','國家','cnt_05.jpg'),(38,'Südkorea','南韓','國家','cnt_06.jpg'),(39,'Thailand','泰國','國家','cnt_07.jpg'),(40,'Vietnam','越南','國家','cnt_08.jpg');
/*!40000 ALTER TABLE `words` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `view_category_stats`
--

/*!50001 DROP VIEW IF EXISTS `view_category_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_category_stats` AS select `w`.`category` AS `category`,count(distinct `w`.`word_id`) AS `total_words`,(select count(0) from `user_permissions` `p` where (`p`.`category_purchased` = `w`.`category`)) AS `total_purchases` from `words` `w` group by `w`.`category` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_transaction_history`
--

/*!50001 DROP VIEW IF EXISTS `view_transaction_history`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_transaction_history` AS select `t`.`t_id` AS `t_id`,`u`.`username` AS `username`,`t`.`amount` AS `amount`,`t`.`description` AS `description`,`t`.`t_date` AS `t_date` from (`transactions` `t` join `users` `u` on((`t`.`user_id` = `u`.`user_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `view_user_status`
--

/*!50001 DROP VIEW IF EXISTS `view_user_status`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `view_user_status` AS select `u`.`user_id` AS `user_id`,`u`.`username` AS `username`,`u`.`balance` AS `balance`,`u`.`role` AS `role`,ifnull(`p`.`category_purchased`,'僅限免費主題') AS `unlocked_category`,`p`.`purchase_date` AS `purchase_date` from (`users` `u` left join `user_permissions` `p` on((`u`.`user_id` = `p`.`user_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-23 18:16:25
