CREATE DATABASE  IF NOT EXISTS `immo` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `immo`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: immo
-- ------------------------------------------------------
-- Server version	5.7.11-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `expose_base`
--

DROP TABLE IF EXISTS `expose_base`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `expose_base` (
  `expose_id` int(11) NOT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `html_page` longtext,
  PRIMARY KEY (`expose_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `query`
--

DROP TABLE IF EXISTS `query`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `query` (
  `query_id` int(11) NOT NULL AUTO_INCREMENT,
  `date_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `query` varchar(400) DEFAULT NULL,
  PRIMARY KEY (`query_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sub_query`
--

DROP TABLE IF EXISTS `sub_query`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sub_query` (
  `sub_query_id` int(11) NOT NULL AUTO_INCREMENT,
  `date_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sub_query` varchar(400) DEFAULT NULL,
  `fk_query_id` int(11) NOT NULL,
  PRIMARY KEY (`sub_query_id`),
  KEY `query_id_idx` (`fk_query_id`),
  CONSTRAINT `query_id` FOREIGN KEY (`fk_query_id`) REFERENCES `query` (`query_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=526 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subquery_to_expose`
--

DROP TABLE IF EXISTS `subquery_to_expose`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subquery_to_expose` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fk_expose_id` int(11) NOT NULL,
  `fk_subquery_id` int(11) NOT NULL,
  `status` varchar(45) NOT NULL,
  `date_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `expose_id_idx` (`fk_expose_id`),
  KEY `sub_query_id_idx` (`fk_subquery_id`),
  CONSTRAINT `expose_id` FOREIGN KEY (`fk_expose_id`) REFERENCES `expose_base` (`expose_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sub_query_id` FOREIGN KEY (`fk_subquery_id`) REFERENCES `sub_query` (`sub_query_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9821 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-02-20 22:43:18
