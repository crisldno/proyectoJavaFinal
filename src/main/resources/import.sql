/* Populate tables */
INSERT INTO estados (nombre) VALUES ('No Enviado');
INSERT INTO estados (nombre) VALUES ('Aprobado');
INSERT INTO estados (nombre) VALUES ('Rechazado');

INSERT INTO clientes (nombre, apellido, email, create_at, foto, monto_solicitar,plazo,numero_cuenta,estado_id,motivo_rechazo) VALUES('Juan', 'Velasquez', 'jgvelort1611@bgmail.com', '2023-08-01', '','100000','30','123456','1','');
