--2 角色表 role
CREATE TABLE role(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色中文名称',
    role_key VARCHAR(50) NOT NULL COMMENT '角色英文标识 admin / user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

INSERT INTO role(role_name,role_key) VALUES
("超级管理员",'admin'),("普通用户",'user');

--3用户角色中间表 user_role
CREATE TABLE user_role(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户id',
    role_id BIGINT NOT NULL COMMENT '角色id',
    UNIQUE uk_user_role(user_id,role_id)
)ENGINE = InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '用户-角色表';
--给id=1的admin用户绑定admin角色
INSERT INTO user_role(user_id,role_id) VALUES(1,1);

---权限表
CREATE TABLE permission(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    perm_key VARCHAR(100) NOT NULL COMMENT '权限标识，如 user:add user:delete'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

INSERT INTO permission(perm_key) VALUES
('user:view'),
('user:add'),
('user:delete');

--5 角色权限中间表 role_permission【角色和权限多对多】
CREATE TABLE role_permission(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色id',
    perm_id BIGINT NOT NULL COMMENT '权限id',
    UNIQUE uk_role_perm (role_id,perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限中间表';

INSERT INTO role_permission(role_id,perm_id) VALUES
(1,1),(1,2),(1,3),
(2,1);