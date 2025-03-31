CREATE DATABASE oficina;

USE oficina;

CREATE TABLE endereco (
	idendereco BIGINT PRIMARY KEY AUTO_INCREMENT,
    rua VARCHAR(30),
    bairro VARCHAR(30), 
    cidade VARCHAR(30),
    uf CHAR(2)
);

CREATE TABLE telefone (
	idtelefone BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero VARCHAR(14),
    tipo_telefone ENUM('residencial', 'celular')
);

CREATE TABLE cliente (
	idcliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30),
    sobrenome VARCHAR(30),
    fk_endereco_cliente BIGINT,
    fk_telefone_cliente BIGINT
);

ALTER TABLE cliente 
ADD CONSTRAINT fk_endereco_cliente 
FOREIGN KEY(fk_endereco_cliente) REFERENCES endereco(idendereco);

ALTER TABLE cliente 
ADD CONSTRAINT fk_telefone_cliente 
FOREIGN KEY(fk_telefone_cliente) REFERENCES telefone(idtelefone);

CREATE TABLE categoria (
	idcategoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30),
    preco_hora DECIMAL(10, 2)
);

CREATE TABLE funcionario (
	idfuncionario BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30),
    sobrenome VARCHAR(30),
    fk_categoria_funcionario BIGINT,
    fk_endereco_funcionario BIGINT,
    fk_telefone_funcionario BIGINT
);

ALTER TABLE funcionario 
ADD CONSTRAINT fk_categoria_funcionario
FOREIGN KEY(fk_categoria_funcionario) REFERENCES categoria(idcategoria);

ALTER TABLE funcionario 
ADD CONSTRAINT fk_endereco_funcionario
FOREIGN KEY(fk_endereco_funcionario) REFERENCES endereco(idendereco);

ALTER TABLE funcionario 
ADD CONSTRAINT fk_telefone_funcionario 
FOREIGN KEY(fk_telefone_funcionario) REFERENCES telefone(idtelefone);

CREATE TABLE veiculo (
	idveiculo BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo_veiculo ENUM('carro', 'moto', 'caminhao'),
    marca VARCHAR(30),
    modelo VARCHAR(30),
    fk_cliente_veiculo BIGINT
);

ALTER TABLE veiculo 
ADD CONSTRAINT fk_cliente_veiculo
FOREIGN KEY(fk_cliente_veiculo) REFERENCES cliente(idcliente);

CREATE TABLE peca (
	idpeca BIGINT PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(30),
	preco_unitario DECIMAL(10, 2)
);

CREATE TABLE reparacao (
	idreparacao BIGINT PRIMARY KEY AUTO_INCREMENT,
	fk_cliente_reparacao BIGINT,
    fk_veiculo_reparacao BIGINT,
    valor_total DECIMAL(10, 2),
    data_finalizacao DATE
);

ALTER TABLE reparacao 
ADD CONSTRAINT fk_cliente_reparacao
FOREIGN KEY(fk_cliente_reparacao) REFERENCES cliente(idcliente);

ALTER TABLE reparacao 
ADD CONSTRAINT fk_veiculo_reparacao
FOREIGN KEY(fk_veiculo_reparacao) REFERENCES veiculo(idveiculo);

CREATE TABLE reparacao_funcionario (
	reparacaoid BIGINT,
    funcionarioid BIGINT,
    horas_trabalhadas INT,
    valor_total DECIMAL(10, 2),
    PRIMARY KEY(reparacaoid, funcionarioid)
);

ALTER TABLE reparacao_funcionario
ADD COLUMN horas_trabalhadas INT,
ADD COLUMN valor_total DECIMAL(10, 2);

ALTER TABLE reparacao_funcionario
ADD CONSTRAINT fk_reparacao_reparacao_funcionario
FOREIGN KEY(reparacaoid) REFERENCES reparacao(idreparacao);

ALTER TABLE reparacao_funcionario
ADD CONSTRAINT fk_funcionario_reparacao_funcionario
FOREIGN KEY(funcionarioid) REFERENCES funcionario(idfuncionario);

CREATE TABLE reparacao_peca (
	reparacaoid BIGINT,
    pecaid BIGINT,
    quantidade INT,
    valor_total DECIMAL(10, 2),
    PRIMARY KEY(reparacaoid, pecaid)
);

ALTER TABLE reparacao_peca
ADD CONSTRAINT fk_reparacao_reparacao_peca
FOREIGN KEY(reparacaoid) REFERENCES reparacao(idreparacao);

ALTER TABLE reparacao_peca
ADD CONSTRAINT fk_peca_reparacao_peca
FOREIGN KEY(pecaid) REFERENCES peca(idpeca);

