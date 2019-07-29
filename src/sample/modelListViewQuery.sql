SELECT DISTINCT C.name
FROM cnn C,
	(SELECT T.cnn_id
     FROM train T, 
		(SELECT D.id
         FROM dataset D
         WHERE D.name = ?) E
	 WHERE T.data_id = E.id) U
WHERE C.id = U.cnn_id;