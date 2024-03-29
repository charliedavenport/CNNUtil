INSERT INTO cnn (id, name, layers, trainable_params, file_path)
VALUES ("MNIST_CNN_01", 7, 225034, "\\store\\saved_models\\MNIST_CNN_01.h5" );

#UPDATE cnn SET file_path="\\store\\saved_models\\MNIST_CNN_01.h5" WHERE id=001;

# these values are from model.summary() - or model.json()
INSERT INTO layer(cnn_id, depth, type, params, filters, kernel_x, kernel_y, stride_x, stride_y, input_x, input_y, input_z, output_x, output_y, output_z)
VALUES (3, 1,  'CONV_2D', 320, 32, 3,3, 1,1, 28,28,1, 26,26,32 );
INSERT INTO layer(cnn_id, depth, type, params, filters, kernel_x, kernel_y, stride_x, stride_y, input_x, input_y, input_z, output_x, output_y, output_z)
VALUES (3, 2,  'POOL', 0, null, 2,2, 1,1, 26,26,32, 13,13,32);
INSERT INTO layer(cnn_id, depth, type, params, filters, kernel_x, kernel_y, stride_x, stride_y, input_x, input_y, input_z, output_x, output_y, output_z)
VALUES (3, 3,  'CONV_2D', 18496, 64, 3,3, 1,1, 13,13,32, 11,11,64);
INSERT INTO layer(cnn_id, depth, type, params, filters, kernel_x, kernel_y, stride_x, stride_y, input_x, input_y, input_z, output_x, output_y, output_z)
VALUES (3, 4,  'POOL', 0, null, 2,2, 1,1, 11,11,64, 5,5,64);
INSERT INTO layer(cnn_id, depth, type, params, filters, kernel_x, kernel_y, stride_x, stride_y, input_x, input_y, input_z, output_x, output_y, output_z)
VALUES (3, 5,  'FLATTEN', 0, null, null, null, null, null, 5,5,64, 1600,null,null);
INSERT INTO layer(cnn_id, depth, type, params, filters, kernel_x, kernel_y, stride_x, stride_y, input_x, input_y, input_z, output_x, output_y, output_z)
VALUES (3, 6,  'DENSE', 204928, null, null, null, null, null, 1600,null,null, 128,null,null);
INSERT INTO layer(cnn_id, depth, type, params, filters, kernel_x, kernel_y, stride_x, stride_y, input_x, input_y, input_z, output_x, output_y, output_z)
VALUES (3, 7,  'DENSE', 1290, null, null, null, null, null, 128,null,null, 10,null,null);

#UPDATE cnn SET name = "MNIST_CNN_01" where id=1;

SELECT * FROM layer;
select * from cnn;

update cnn set layers=7 WHERE id in (1, 2,3);