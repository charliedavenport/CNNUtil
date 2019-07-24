CREATE TABLE `cnn`
(
  `id` int PRIMARY KEY,
  `name` varchar(150),
  `layers` int,
  `trainable_params` int,
  `hyper_params` int,
  `size_kb` double,
  `file_path` varchar(150)
);

CREATE TABLE `train`
(
  `cnn_id` int,
  `data_id` int,
  `epoch` int,
  `epochStart` timestamp,
  `loss` double,
  `acc` double,
  `val_loss` double,
  `val_acc` double
);

CREATE TABLE `test`
(
  `cnn_id` int,
  `data_id` int,
  `testStart` timestamp,
  `loss` double,
  `acc` double
);

CREATE TABLE `dataset`
(
  `id` int PRIMARY KEY,
  `name` varchar(150),
  `samples` int,
  `classes` int,
  `img_x` int,
  `img_y` int,
  `img_z` int
);

CREATE TABLE `data`
(
  `dataset` int,
  `file_path` varchar(150),
  `fold` int,
  `class` int
);

CREATE TABLE `hyperParam`
(
  `cnn_id` int,
  `layer` int,
  `name` varchar(150),
  `init_value` double,
  `current_value` double,
  `descr` varchar(300)
);

CREATE TABLE `layer`
(
  `cnn_id` int,
  `depth` int,
  `type` ENUM ('CONV_2D', 'DENSE', 'POOL'),
  `params` int,
  `kernel_x` int,
  `kernel_y` int,
  `stride_x` int,
  `stride_y` int
);

ALTER TABLE `train` ADD FOREIGN KEY (`cnn_id`) REFERENCES `cnn` (`id`);

ALTER TABLE `train` ADD FOREIGN KEY (`data_id`) REFERENCES `dataset` (`id`);

ALTER TABLE `test` ADD FOREIGN KEY (`cnn_id`) REFERENCES `cnn` (`id`);

ALTER TABLE `test` ADD FOREIGN KEY (`data_id`) REFERENCES `dataset` (`id`);

ALTER TABLE `data` ADD FOREIGN KEY (`dataset`) REFERENCES `dataset` (`id`);

ALTER TABLE `hyperParam` ADD FOREIGN KEY (`cnn_id`) REFERENCES `cnn` (`id`);

ALTER TABLE `hyperParam` ADD FOREIGN KEY (`layer`) REFERENCES `layer` (`depth`);

ALTER TABLE `layer` ADD FOREIGN KEY (`cnn_id`) REFERENCES `cnn` (`id`);

