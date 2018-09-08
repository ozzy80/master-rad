-- MySQL Script generated by MySQL Workbench
-- Fri Sep  7 10:33:00 2018
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema p2p_tracker
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `p2p_tracker` ;

-- -----------------------------------------------------
-- Schema p2p_tracker
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `p2p_tracker` DEFAULT CHARACTER SET utf8 ;
USE `p2p_tracker` ;

-- -----------------------------------------------------
-- Table `p2p_tracker`.`channel`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `p2p_tracker`.`channel` ;

CREATE TABLE IF NOT EXISTS `p2p_tracker`.`channel` (
  `channel_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `chunk_size` SMALLINT UNSIGNED NOT NULL,
  `bitrate` INT UNSIGNED NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(512) NULL,
  PRIMARY KEY (`channel_id`),
  UNIQUE INDEX `channel_id_UNIQUE` (`channel_id` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `p2p_tracker`.`peer_information`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `p2p_tracker`.`peer_information` ;

CREATE TABLE IF NOT EXISTS `p2p_tracker`.`peer_information` (
  `ip_address` VARBINARY(16) NOT NULL,
  `port_number` SMALLINT UNSIGNED NOT NULL,
  `club_number` TINYINT UNSIGNED NOT NULL,
  `last_active_message` DATETIME NOT NULL,
  `channel_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`ip_address`),
  UNIQUE INDEX `ip_address_UNIQUE` (`ip_address` ASC) VISIBLE,
  INDEX `fk_peer_information_channel_idx` (`channel_id` ASC) VISIBLE,
  CONSTRAINT `fk_peer_information_channel`
    FOREIGN KEY (`channel_id`)
    REFERENCES `p2p_tracker`.`channel` (`channel_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `p2p_tracker`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `p2p_tracker`.`user` ;

CREATE TABLE IF NOT EXISTS `p2p_tracker`.`user` (
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `enabled` TINYINT(1) NOT NULL,
  PRIMARY KEY (`username`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `p2p_tracker`.`authorities`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `p2p_tracker`.`authorities` ;

CREATE TABLE IF NOT EXISTS `p2p_tracker`.`authorities` (
  `authorityId` INT NOT NULL AUTO_INCREMENT,
  `authority` VARCHAR(45) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`authorityId`),
  INDEX `fk_authorities_user1_idx` (`username` ASC) VISIBLE,
  CONSTRAINT `fk_authorities_user1`
    FOREIGN KEY (`username`)
    REFERENCES `p2p_tracker`.`user` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `p2p_tracker`.`top_channel`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `p2p_tracker`.`top_channel` ;

CREATE TABLE IF NOT EXISTS `p2p_tracker`.`top_channel` (
  `views_number` INT NOT NULL,
  `top_channel_id` INT NOT NULL AUTO_INCREMENT,
  `channel_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`top_channel_id`),
  INDEX `fk_top_channel_channel1_idx` (`channel_id` ASC) VISIBLE,
  CONSTRAINT `fk_top_channel_channel1`
    FOREIGN KEY (`channel_id`)
    REFERENCES `p2p_tracker`.`channel` (`channel_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `p2p_tracker`.`peer_token`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `p2p_tracker`.`peer_token` ;

CREATE TABLE IF NOT EXISTS `p2p_tracker`.`peer_token` (
  `ip_address` VARBINARY(16) NOT NULL,
  `token` SMALLINT NOT NULL,
  PRIMARY KEY (`ip_address`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
