/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50610
Source Host           : localhost:3306
Source Database       : practice

Target Server Type    : MYSQL
Target Server Version : 50610
File Encoding         : 65001

Date: 2013-04-11 11:25:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` varchar(32) NOT NULL COMMENT 'Primary Key',
  `role_id` varchar(32) NOT NULL,
  `uri` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_role_id` (`role_id`),
  CONSTRAINT `FK_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('0', '0', '');
INSERT INTO `t_role` VALUES ('1', '0', '');
INSERT INTO `t_role` VALUES ('10', '0', '');
INSERT INTO `t_role` VALUES ('11', '0', '');
INSERT INTO `t_role` VALUES ('12', '0', '');
INSERT INTO `t_role` VALUES ('13', '0', '');
INSERT INTO `t_role` VALUES ('14', '0', '');
INSERT INTO `t_role` VALUES ('15', '0', '');
INSERT INTO `t_role` VALUES ('16', '0', '');
INSERT INTO `t_role` VALUES ('17', '0', '');
INSERT INTO `t_role` VALUES ('18', '0', '');
INSERT INTO `t_role` VALUES ('19', '0', '');
INSERT INTO `t_role` VALUES ('2', '0', '');
INSERT INTO `t_role` VALUES ('20', '0', '');
INSERT INTO `t_role` VALUES ('3', '0', '');
INSERT INTO `t_role` VALUES ('4', '0', '');
INSERT INTO `t_role` VALUES ('5', '0', '');
INSERT INTO `t_role` VALUES ('6', '0', '');
INSERT INTO `t_role` VALUES ('7', '0', '');
INSERT INTO `t_role` VALUES ('8', '0', '');
INSERT INTO `t_role` VALUES ('9', '0', '');

-- ----------------------------
-- Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` varchar(32) NOT NULL DEFAULT '' COMMENT 'Primary Key',
  `name` varchar(64) NOT NULL DEFAULT '''''' COMMENT 'Login Name',
  `password` varchar(32) NOT NULL DEFAULT '''''' COMMENT 'Login Password',
  `email` varchar(128) NOT NULL DEFAULT '''''' COMMENT 'User Email',
  `role` int(11) NOT NULL DEFAULT '0' COMMENT 'User Role',
  `status` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000' COMMENT 'Account Status',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('0', 'Admin', '21232f297a57a5a743894a0e4a801fc3', 'admin@example.com', '1', '0000000000');
INSERT INTO `t_user` VALUES ('1', 'Guest', '084e0343a0486ff05530df6c705c8bb4', 'guest@example.com', '0', '0000000000');
