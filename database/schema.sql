CREATE DATABASE IF NOT EXISTS chillywatts_db;

USE chillywatts_db;

CREATE TABLE analise_historico (
	id INT auto_increment PRIMARY KEY,
	cnpj VARCHAR(14) NOT NULL,
    nome VARCHAR(75) NOT NULL,
	consumoRealKwh DECIMAL(6, 2) NOT NULL,
    usoHorarioPico VARCHAR(50) NOT NULL, 
    epocaAno ENUM('Verão', 'Outono', 'Inverno', 'Primavera') NOT NULL,
    perfilEnergetico ENUM('Eficiente', 'Moderado', 'Ineficiente') NOT NULL,
    probabilidade DECIMAL(5, 2) NOT NULL CHECK (probabilidade BETWEEN 0 AND 100),
    consumoTeoricoEstimadoKwh DECIMAL (6, 2) NOT NULL,
    custoMensalAtual DECIMAL (8, 2) NOT NULL,
    economiaEstimadaPotencial DECIMAL (8, 2) NOT NULL
    
);

CREATE TABLE inventario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL,
    frezzersJson JSON,
    atualizadoEm DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

