CREATE DATABASE IF NOT EXISTS rabc DEFAULT CHARACTER SET utf8mb4;
USE rabc;

CREATE TABLE `user` (
                        `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户id',
                        `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                        `password` VARCHAR(100) NOT NULL COMMENT '密码'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO `user` (`username`, `password`) VALUES ('admin', '123456');
ALTER TABLE `user` ADD COLUMN nickname VARCHAR(50) NULL COMMENT "用户昵称";

UPDATE user SET password='123456' WHERE username='admin';


