CREATE DATABASE  IF NOT EXISTS `sentiment` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `sentiment`;
-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: localhost    Database: sentiment
-- ------------------------------------------------------
-- Server version	5.6.26-log

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
-- Table structure for table `electronics_review`
--

DROP TABLE IF EXISTS `electronics_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `electronics_review` (
  `reviewerId` varchar(255) DEFAULT NULL,
  `prodId` varchar(255) DEFAULT NULL,
  `reviewerName` varchar(255) DEFAULT NULL,
  `phelp` int(11) DEFAULT NULL,
  `ohelp` int(11) DEFAULT NULL,
  `reviewText` text,
  `overall` float DEFAULT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `unixReviewTime` int(11) DEFAULT NULL,
  `reviewTime` varchar(11) DEFAULT NULL,
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86287 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `negateproximity`
--

DROP TABLE IF EXISTS `negateproximity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `negateproximity` (
  `sentenceId` int(11) NOT NULL,
  `relPos` bigint(11) NOT NULL,
  `isNegateNear` int(11) NOT NULL,
  PRIMARY KEY (`relPos`,`sentenceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `opinion_polarity`
--

DROP TABLE IF EXISTS `opinion_polarity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opinion_polarity` (
  `opinion` varchar(45) NOT NULL,
  `polarity` int(11) DEFAULT NULL,
  PRIMARY KEY (`opinion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `polarity`
--

DROP TABLE IF EXISTS `polarity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `polarity` (
  `word` varchar(100) NOT NULL,
  `Polarity` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `potentialfeature`
--

DROP TABLE IF EXISTS `potentialfeature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `potentialfeature` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `feature` varchar(255) DEFAULT NULL,
  `opinion` varchar(255) DEFAULT NULL,
  `prodId` varchar(255) DEFAULT NULL,
  `sentenceId` varchar(255) DEFAULT NULL,
  `mindistId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51311 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reviewsentence`
--

DROP TABLE IF EXISTS `reviewsentence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reviewsentence` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `reviewId` bigint(11) NOT NULL,
  `sentence` longtext,
  PRIMARY KEY (`id`),
  KEY `fk_reviewId_idx` (`reviewId`),
  CONSTRAINT `fk_reviewId` FOREIGN KEY (`reviewId`) REFERENCES `electronics_review` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=243814 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sentisentence`
--

DROP TABLE IF EXISTS `sentisentence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sentisentence` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `opinion` longtext,
  `feature` longtext,
  `sentenceId` int(11) DEFAULT NULL,
  `relPos` int(11) DEFAULT NULL,
  `polarity` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=133670 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tagwords`
--

DROP TABLE IF EXISTS `tagwords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tagwords` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `word` varchar(255) NOT NULL,
  `posTag` varchar(10) NOT NULL,
  `sentenceId` bigint(11) NOT NULL,
  `relPos` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_posId_idx` (`posTag`),
  KEY `fk_sentenceId_idx` (`sentenceId`),
  CONSTRAINT `fk_sentenceId` FOREIGN KEY (`sentenceId`) REFERENCES `reviewsentence` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=335005 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'sentiment'
--

--
-- Dumping routines for database 'sentiment'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-06  4:36:55
