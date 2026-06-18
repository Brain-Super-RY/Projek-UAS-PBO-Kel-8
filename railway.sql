-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: thomas.proxy.rlwy.net    Database: railway
-- ------------------------------------------------------
-- Server version	9.7.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `layanan_jasa`
--

DROP TABLE IF EXISTS `layanan_jasa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `layanan_jasa` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_layanan` varchar(20) NOT NULL,
  `fotografer` varchar(100) NOT NULL,
  `paket` varchar(100) NOT NULL,
  `tgl_sesi` date NOT NULL,
  `durasi_jam` int NOT NULL,
  `jumlah_foto_edit` int NOT NULL DEFAULT '0',
  `harga_dasar` double NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_layanan` (`id_layanan`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `layanan_jasa`
--

LOCK TABLES `layanan_jasa` WRITE;
/*!40000 ALTER TABLE `layanan_jasa` DISABLE KEYS */;
INSERT INTO `layanan_jasa` VALUES (1,'JF001','Studio Kita','Paket Wisuda','2026-06-20',2,20,400000,'2026-06-14 12:46:41'),(2,'JF002','Studio Kita Team','Paket Portrait','2026-06-16',1,0,350000,'2026-06-14 13:50:42'),(3,'JF003','Andra Lesmana (Senior)','Paket Reguler','2026-01-01',3,10,300000,'2026-06-14 13:59:22'),(4,'JF004','Rian Hidayat (Pro)','Paket Produk','2026-06-17',2,20,500000,'2026-06-14 23:26:01'),(5,'JF005','Studio Kita Team','Paket Prewedding','2026-07-16',4,10,800000,'2026-06-15 00:58:11');
/*!40000 ALTER TABLE `layanan_jasa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `layanan_sewa`
--

DROP TABLE IF EXISTS `layanan_sewa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `layanan_sewa` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_layanan` varchar(20) NOT NULL,
  `jenis_alat` varchar(100) NOT NULL,
  `nama_kamera` varchar(100) NOT NULL,
  `tarif_per_hari` double NOT NULL,
  `tgl_mulai` date NOT NULL,
  `tgl_kembali` date NOT NULL,
  `tgl_dikembalikan` date DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `foto_url` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_layanan` (`id_layanan`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `layanan_sewa`
--

LOCK TABLES `layanan_sewa` WRITE;
/*!40000 ALTER TABLE `layanan_sewa` DISABLE KEYS */;
INSERT INTO `layanan_sewa` VALUES (1,'SW001','Kamera DSLR','Canon EOS R5',250000,'2026-06-14','2026-06-18','2026-06-20','2026-06-14 12:45:37','https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80'),(2,'SW002','Kamera DSLR','Sony Alpha A7 IV',250000,'2026-06-15','2026-06-16',NULL,'2026-06-14 13:50:11','https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80'),(3,'ALT-004','Kamera','Fujifilm X-T4 Black',200000,'2026-06-14','2026-06-15',NULL,'2026-06-14 16:36:49',NULL),(4,'ALT-005','Kamera','Nikon Z6 II Mirrorless',225000,'2026-06-14','2026-06-15',NULL,'2026-06-14 16:36:49',NULL),(9,'SW005','Kamera','DJI RS 3 Pro Gimbal Stabilizer',250000,'2026-06-15','2026-06-18',NULL,'2026-06-14 23:23:37','https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80'),(10,'SW006','Kamera','Sony FE 24-70mm f/2.8 GM',250000,'2026-06-15','2026-06-18',NULL,'2026-06-14 23:25:10','https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80'),(11,'SW007','Kamera','Nikon Z6 II Mirrorless',225000,'2026-06-15','2026-06-20',NULL,'2026-06-14 23:30:57','https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80'),(12,'SW008','Kamera','Panasonic Lumix GH6',275000,'2026-06-15','2026-06-20',NULL,'2026-06-15 00:56:29','https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80'),(13,'SW009','Kamera','Sony Alpha A7S III',400000,'2026-06-17','2026-06-21',NULL,'2026-06-15 02:30:37','https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80'),(14,'SW010','Kamera','Canon EOS R5',450000,'2026-06-16','2026-06-18',NULL,'2026-06-15 02:58:13','https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80');
/*!40000 ALTER TABLE `layanan_sewa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `master_alat`
--

DROP TABLE IF EXISTS `master_alat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `master_alat` (
  `id_alat` varchar(20) NOT NULL,
  `jenis_alat` varchar(50) NOT NULL,
  `nama_kamera` varchar(100) NOT NULL,
  `tarif_per_hari` double NOT NULL,
  `foto_url` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id_alat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `master_alat`
--

LOCK TABLES `master_alat` WRITE;
/*!40000 ALTER TABLE `master_alat` DISABLE KEYS */;
INSERT INTO `master_alat` VALUES ('ALT-001','Kamera','Sony Alpha A7 IV',350000,'https://images.unsplash.com/photo-1607462109225-6b64ae2dd3cb?w=600&q=80'),('ALT-002','Kamera','Canon EOS R5',450000,'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80'),('ALT-003','Lensa','Sony FE 24-70mm f/2.8 GM',200000,'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?w=600&q=80'),('ALT-004','Kamera','Fujifilm X-T4 Black',200000,'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80'),('ALT-005','Kamera','Nikon Z6 II Mirrorless',225000,'https://images.unsplash.com/photo-1607462109225-6b64ae2dd3cb?w=600&q=80'),('ALT-006','Kamera','Panasonic Lumix GH6',275000,'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80'),('ALT-007','Kamera','Sony Alpha A7S III',400000,'https://images.unsplash.com/photo-1607462109225-6b64ae2dd3cb?w=600&q=80'),('ALT-008','Kamera','Canon EOS R6 Mark II',380000,'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600&q=80'),('ALT-009','Lensa','Canon RF 50mm f/1.2L USM',175000,'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?w=600&q=80'),('ALT-010','Lensa','Sigma 16mm f/1.4 DC DN',75000,'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?w=600&q=80'),('ALT-011','Lensa','Tamron 28-75mm f/2.8 Di III G2',100000,'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?w=600&q=80'),('ALT-012','Lensa','Sony FE 85mm f/1.4 GM',140000,'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?w=600&q=80'),('ALT-013','Lensa','Canon EF 70-200mm f/2.8L IS III',180000,'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?w=600&q=80'),('ALT-014','Lensa','Fujifilm XF 56mm f/1.2 R WR',95000,'https://images.unsplash.com/photo-1617005082133-548c4dd27f35?w=600&q=80'),('ALT-015','Lighting','Godox SK400II Studio Flash',80000,'https://images.unsplash.com/photo-1590004953392-5aba2e72269a?w=600&q=80'),('ALT-016','Lighting','Aputure Amaran 200d LED Video Light',150000,'https://images.unsplash.com/photo-1590004953392-5aba2e72269a?w=600&q=80'),('ALT-017','Lighting','Godox AD200 Pro Pocket Flash',120000,'https://images.unsplash.com/photo-1590004953392-5aba2e72269a?w=600&q=80'),('ALT-018','Lighting','Nanlite Pavotube 15C RGB 2 KIT',110000,'https://images.unsplash.com/photo-1590004953392-5aba2e72269a?w=600&q=80'),('ALT-019','Lighting','Ring Light Professional 18 Inch',40000,'https://images.unsplash.com/photo-1590004953392-5aba2e72269a?w=600&q=80'),('ALT-020','Stabilizer','DJI Ronin SC Gimbal Stabilizer',120000,'https://images.unsplash.com/photo-1584438784894-089d6a128f3e?w=600&q=80'),('ALT-021','Stabilizer','DJI RS 3 Pro Gimbal Stabilizer',250000,'https://cdn.shopify.com/s/files/1/0672/3806/8470/files/DJIRS3ProCombo18.jpg?v=1718333941'),('ALT-022','Stabilizer','Zhiyun Crane 2S Stabilizer',140000,'https://images.unsplash.com/photo-1584438784894-089d6a128f3e?w=600&q=80'),('ALT-023','Stabilizer','Tripod Manfrotto Professional 190X',50000,'https://images.unsplash.com/photo-1584438784894-089d6a128f3e?w=600&q=80'),('ALT-024','Audio','Rode Wireless Go II Dual Channel',100000,'https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=600&q=80'),('ALT-025','Audio','Shure SM7B Vocal Microphone',130000,'https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=600&q=80'),('ALT-026','Audio','Saramonic Blink 500 B2 Wireless',70000,'https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=600&q=80'),('ALT-027','Aksesoris','SanDisk Extreme Pro SDXC 128GB',30000,'https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=600&q=80'),('ALT-028','Aksesoris','Baterai Sony NP-FZ100 Original',25000,'https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=600&q=80');
/*!40000 ALTER TABLE `master_alat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quest`
--

DROP TABLE IF EXISTS `quest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quest` (
  `ques_idxxx` int NOT NULL,
  `ques_namex` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`ques_idxxx`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quest`
--

LOCK TABLES `quest` WRITE;
/*!40000 ALTER TABLE `quest` DISABLE KEYS */;
INSERT INTO `quest` VALUES (1,'apa makanan favorit?'),(2,'apa minuman favorit?'),(3,'apa nama panggilan?');
/*!40000 ALTER TABLE `quest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaksi`
--

DROP TABLE IF EXISTS `transaksi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaksi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_transaksi` varchar(20) NOT NULL,
  `customer_username` varchar(50) NOT NULL,
  `id_layanan` varchar(20) NOT NULL,
  `jenis_layanan` enum('SEWA','JASA') NOT NULL,
  `total_biaya` double NOT NULL,
  `tgl_input` date NOT NULL DEFAULT (curdate()),
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) DEFAULT 'PENDING',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_transaksi` (`id_transaksi`),
  KEY `customer_username` (`customer_username`),
  CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`customer_username`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaksi`
--

LOCK TABLES `transaksi` WRITE;
/*!40000 ALTER TABLE `transaksi` DISABLE KEYS */;
INSERT INTO `transaksi` VALUES (1,'TRX0001','RY7','SW001','SEWA',1000000,'2026-06-14','2026-06-14 12:45:37','APPROVED'),(2,'TRX0002','RY7','JF001','JASA',900000,'2026-06-14','2026-06-14 12:46:41','APPROVED'),(3,'TRX0003','AsepGokil','SW002','SEWA',250000,'2026-06-14','2026-06-14 13:50:11','APPROVED'),(4,'TRX0004','AsepGokil','JF002','JASA',450000,'2026-06-14','2026-06-14 13:50:42','DECLINED'),(5,'TRX0005','AsepGokil','JF003','JASA',750000,'2026-06-14','2026-06-14 13:59:22','APPROVED'),(6,'TRX0006','RY7','SW005','SEWA',750000,'2026-06-15','2026-06-14 23:23:37','APPROVED'),(7,'TRX0007','RY7','SW006','SEWA',750000,'2026-06-15','2026-06-14 23:25:10','PENDING'),(8,'TRX0008','RY7','JF004','JASA',1000000,'2026-06-15','2026-06-14 23:26:01','DECLINED'),(9,'TRX0009','RY7','SW007','SEWA',1125000,'2026-06-15','2026-06-14 23:30:57','PENDING'),(10,'TRX0010','Amel','SW008','SEWA',1375000,'2026-06-15','2026-06-15 00:56:30','APPROVED'),(11,'TRX0011','Amel','JF005','JASA',1350000,'2026-06-15','2026-06-15 00:58:11','DECLINED'),(12,'TRX0012','Amel','SW009','SEWA',1600000,'2026-06-15','2026-06-15 02:30:37','APPROVED'),(13,'TRX0013','Amel','SW010','SEWA',900000,'2026-06-15','2026-06-15 02:58:13','APPROVED');
/*!40000 ALTER TABLE `transaksi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaksi_sewa`
--

DROP TABLE IF EXISTS `transaksi_sewa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaksi_sewa` (
  `id_transaksi` varchar(50) NOT NULL,
  `username_customer` varchar(50) NOT NULL,
  `id_alat` varchar(20) NOT NULL,
  `tgl_mulai` date NOT NULL,
  `tgl_kembali` date NOT NULL,
  `tgl_dikembalikan` date DEFAULT NULL,
  `status_pesanan` varchar(20) DEFAULT 'PENDING',
  PRIMARY KEY (`id_transaksi`),
  KEY `id_alat` (`id_alat`),
  CONSTRAINT `transaksi_sewa_ibfk_1` FOREIGN KEY (`id_alat`) REFERENCES `master_alat` (`id_alat`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaksi_sewa`
--

LOCK TABLES `transaksi_sewa` WRITE;
/*!40000 ALTER TABLE `transaksi_sewa` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaksi_sewa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `role` enum('ADMIN','CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
  `email` varchar(100) DEFAULT NULL,
  `no_telepon` varchar(20) DEFAULT NULL,
  `alamat` text,
  `admin_level` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin123','Administrator Utama','ADMIN','admin@studiokita.com','08123456789','Kantor StudioKita Karawang','Super Admin','2026-06-11 18:14:49'),(2,'customer','customer123','Budi Setiawan','CUSTOMER','budi@gmail.com','085712345678','Jl. Klari No. 12, Karawang',NULL,'2026-06-11 18:16:50'),(3,'RY7','RY7_706052','Rizky Yoga Salasa','CUSTOMER','Rizkyyogasalasa@gmail.com','083878435775','Jln Mawar no 2, Klari, Karawang',NULL,'2026-06-14 07:08:09'),(4,'AsepGokil','Asep12345','Asep Jaenudin','CUSTOMER','AsepGokil@gmail.com','086497315672','Jakarta',NULL,'2026-06-14 13:35:08'),(5,'Amel','Amel12345','Amel','CUSTOMER','Amel@gmail.com','081957436259','Subang',NULL,'2026-06-15 00:53:51'),(6,'azahra','aza123','aza','ADMIN',NULL,NULL,NULL,'Kasir / Frontdesk','2026-06-15 01:48:20'),(7,'JulianKe','Jul123','Julian','CUSTOMER','Jul@gmai.com','08469135715','Jakarta',NULL,'2026-06-15 02:54:22');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_rekap_transaksi`
--

DROP TABLE IF EXISTS `v_rekap_transaksi`;
/*!50001 DROP VIEW IF EXISTS `v_rekap_transaksi`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_rekap_transaksi` AS SELECT 
 1 AS `id_transaksi`,
 1 AS `nama_customer`,
 1 AS `no_telepon`,
 1 AS `jenis_layanan`,
 1 AS `detail_layanan`,
 1 AS `total_biaya`,
 1 AS `tgl_input`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping routines for database 'railway'
--

--
-- Final view structure for view `v_rekap_transaksi`
--

/*!50001 DROP VIEW IF EXISTS `v_rekap_transaksi`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_rekap_transaksi` AS select `t`.`id_transaksi` AS `id_transaksi`,`u`.`nama_lengkap` AS `nama_customer`,`u`.`no_telepon` AS `no_telepon`,`t`.`jenis_layanan` AS `jenis_layanan`,(case `t`.`jenis_layanan` when 'SEWA' then concat(`s`.`jenis_alat`,': ',`s`.`nama_kamera`) when 'JASA' then concat(`j`.`paket`,' (',`j`.`durasi_jam`,' jam)') end) AS `detail_layanan`,`t`.`total_biaya` AS `total_biaya`,`t`.`tgl_input` AS `tgl_input` from (((`transaksi` `t` join `users` `u` on((`t`.`customer_username` = `u`.`username`))) left join `layanan_sewa` `s` on(((`t`.`id_layanan` = `s`.`id_layanan`) and (`t`.`jenis_layanan` = 'SEWA')))) left join `layanan_jasa` `j` on(((`t`.`id_layanan` = `j`.`id_layanan`) and (`t`.`jenis_layanan` = 'JASA')))) order by `t`.`tgl_input` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-18 15:52:41
