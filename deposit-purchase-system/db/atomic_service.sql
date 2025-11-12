CREATE TABLE IF NOT EXISTS atomic_service (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_code VARCHAR(64) NOT NULL UNIQUE,
    service_name VARCHAR(128) NOT NULL,
    bean_name VARCHAR(128) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_service_code ON atomic_service(service_code);

INSERT INTO atomic_service (service_code, service_name, bean_name, description) VALUES
('S1', '证件审查接口', 'certificateAuditService', '验证客户身份证件有效性'),
('S2', '用户信息校验接口', 'userProfileValidateService', '校验用户基础信息'),
('S3', '风险等级匹配接口', 'riskMatchService', '匹配产品风险等级与客户等级'),
('S4', '限额校验接口', 'quotaValidateService', '校验单笔与单日限额'),
('S5', '库存锁定接口', 'inventoryLockService', '抢占产品库存'),
('S6', '预扣款接口', 'preDebitService', 'TCC 预扣客户资金'),
('S7', '产品购买接口', 'productPurchaseService', '落地购买流水'),
('S8', '短信验证码接口', 'smsOtpService', '发送与校验短信验证码'),
('S9', '活动资格校验接口', 'campaignEligibilityService', '验证活动资格'),
('S10', '反洗钱黑名单接口', 'amlBlacklistService', '核验AML黑名单');
