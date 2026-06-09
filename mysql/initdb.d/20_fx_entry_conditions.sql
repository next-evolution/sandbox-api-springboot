CREATE TABLE `fx_entry_conditions` (
  `code` varchar(16) NOT NULL COMMENT 'エントリーパターンコード',
  `is_match` bit(1) NOT NULL DEFAULT b'0' COMMENT '0:SKIP|1:MATCH',
  `conditions_number` tinyint(4) NOT NULL COMMENT '条件番号(連番)',
  `conditions_outline` varchar(255) NOT NULL COMMENT '条件',
  PRIMARY KEY (`code`, `is_match`, `conditions_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='エントリー条件';
