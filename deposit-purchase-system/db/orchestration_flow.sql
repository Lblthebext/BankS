CREATE TABLE IF NOT EXISTS orchestration_flow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    flow_code VARCHAR(64) NOT NULL UNIQUE,
    flow_name VARCHAR(128) NOT NULL,
    description VARCHAR(255),
    dag_json JSON NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_flow_code ON orchestration_flow(flow_code);

INSERT INTO orchestration_flow (flow_code, flow_name, description, dag_json, status, version) VALUES
('FLOW_A', '常规购买流程', '标准风控检查流程', JSON_OBJECT('nodes', JSON_ARRAY(
    JSON_OBJECT('id', 'S1', 'nextOnSuccess', 'S2', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', 'COMP_S1'),
    JSON_OBJECT('id', 'S2', 'nextOnSuccess', 'S3', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S3', 'nextOnSuccess', 'S4', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S4', 'nextOnSuccess', 'S5', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S5', 'nextOnSuccess', 'S6', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', 'COMP_S5'),
    JSON_OBJECT('id', 'S6', 'nextOnSuccess', 'S7', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', 'COMP_S6'),
    JSON_OBJECT('id', 'S7', 'nextOnSuccess', 'S8', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S8', 'nextOnSuccess', 'END_SUCCESS', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 1, 'timeout', 5000, 'compensation', NULL)
)), 1, 1),
('FLOW_B', '大额风控流程', '高风险客户双重验证', JSON_OBJECT('nodes', JSON_ARRAY(
    JSON_OBJECT('id', 'S10', 'nextOnSuccess', 'S1', 'nextOnFailure', 'END_FAIL', 'async', true, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S1', 'nextOnSuccess', 'S3', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', 'COMP_S1'),
    JSON_OBJECT('id', 'S3', 'nextOnSuccess', 'S4', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S4', 'nextOnSuccess', 'S9', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S9', 'nextOnSuccess', 'S5', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S5', 'nextOnSuccess', 'S6', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', 'COMP_S5'),
    JSON_OBJECT('id', 'S6', 'nextOnSuccess', 'S7', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', 'COMP_S6'),
    JSON_OBJECT('id', 'S7', 'nextOnSuccess', 'END_SUCCESS', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL)
)), 1, 1),
('FLOW_C', '活动秒杀流程', '活动库存优先抢占与短信校验', JSON_OBJECT('nodes', JSON_ARRAY(
    JSON_OBJECT('id', 'S9', 'nextOnSuccess', 'S8', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S8', 'nextOnSuccess', 'S1', 'nextOnFailure', 'END_FAIL', 'async', true, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S1', 'nextOnSuccess', 'S2', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', 'COMP_S1'),
    JSON_OBJECT('id', 'S2', 'nextOnSuccess', 'S5', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL),
    JSON_OBJECT('id', 'S5', 'nextOnSuccess', 'S6', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', 'COMP_S5'),
    JSON_OBJECT('id', 'S6', 'nextOnSuccess', 'S7', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', 'COMP_S6'),
    JSON_OBJECT('id', 'S7', 'nextOnSuccess', 'END_SUCCESS', 'nextOnFailure', 'END_FAIL', 'async', false, 'retry', 3, 'timeout', 5000, 'compensation', NULL)
)), 1, 1);
