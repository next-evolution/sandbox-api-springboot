CREATE TABLE `fx_verification_images` (
  `id` int(10) unsigned NOT NULL COMMENT 'ID',
  `image_type` varchar(16) NOT NULL COMMENT '種別(E|15m|1h|4h)',
  `file_name` varchar(64) NOT NULL COMMENT 'filename ${yyyyMMdd_HHmm}_${imageType}.png',
  PRIMARY KEY (`id`, `image_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='FXトレード画像ファイル';
