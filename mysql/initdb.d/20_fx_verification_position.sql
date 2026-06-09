CREATE TABLE `fx_verification_position` (
  `id` int(10) unsigned NOT NULL COMMENT 'ID',
  `position_number` tinyint(4) NOT NULL COMMENT 'オーダー番号(連番)',
  `settlement_price` decimal(10,5) NOT NULL COMMENT '決済価格',
  `settlement_pips` int(11) NOT NULL COMMENT '決済損益pips',
  `settlement_ratio` decimal(5,2) NOT NULL DEFAULT 0.00 COMMENT '決済損益比率(settlement_pips/loss_pips)',
  `lot` decimal(10,5) NOT NULL COMMENT 'LOT',
  `profit_amount` int(11) NOT NULL COMMENT '利益額',
  `loss_amount` int(11) NOT NULL COMMENT '損切り額',
  PRIMARY KEY (`id`,`position_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='検証データ注文情報テーブル';
