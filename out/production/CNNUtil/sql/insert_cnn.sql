INSERT INTO cnn (id, name, layers, trainable_params, file_path)
VALUES (001, "MNIST_CNN_01", 7, 225034, "\\store\\saved_models\\MNIST_CNN_01.h5" );

#UPDATE cnn SET file_path="\\store\\saved_models\\MNIST_CNN_01.h5" WHERE id=001;

# these values are from model.summary() - or model.json()
INSERT INTO layer(cnn_id, depth, type, params, kernel_x, kernel_y, stride_x, stride_y)
VALUES (001, 1,  'CONV_2D', 320, 3,3, 1,1);
INSERT INTO layer(cnn_id, depth, type, params, kernel_x, kernel_y, stride_x, stride_y)
VALUES (001, 2,  'POOL', 0, 2,2, 1,1);
INSERT INTO layer(cnn_id, depth, type, params, kernel_x, kernel_y, stride_x, stride_y)
VALUES (001, 3,  'CONV_2D', 18496, 3,3, 1,1);
INSERT INTO layer(cnn_id, depth, type, params, kernel_x, kernel_y, stride_x, stride_y)
VALUES (001, 4,  'POOL', 0, 2,2, 1,1);
INSERT INTO layer(cnn_id, depth, type, params, kernel_x, kernel_y, stride_x, stride_y)
VALUES (001, 5,  'DENSE', 204928, null, null, null, null);
INSERT INTO layer(cnn_id, depth, type, params, kernel_x, kernel_y, stride_x, stride_y)
VALUES (001, 6,  'DENSE', 1290, null, null, null, null);

#UPDATE cnn SET name = "MNIST_CNN_01" where id=1;

SELECT * FROM cnn JOIN layer on cnn.id = layer.cnn_id;