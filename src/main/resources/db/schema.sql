CREATE DATABASE IF NOT EXISTS departamento;

USE departamento;

CREATE TABLE IF NOT EXISTS endereco (
	idendereco BIGINT PRIMARY KEY AUTO_INCREMENT,
    rua VARCHAR(30) NOT NULL,
    bairro VARCHAR(30) NOT NULL,
    cidade VARCHAR(30) NOT NULL,
    uf CHAR(2) NOT NULL
);

CREATE TABLE IF NOT EXISTS pessoa (
	idpessoa BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30) NOT NULL,
    sobrenome VARCHAR(30) NOT NULL,
    fk_endereco_pessoa BIGINT,
    FOREIGN KEY(fk_endereco_pessoa) REFERENCES endereco(idendereco)
);

CREATE TABLE IF NOT EXISTS telefone (
    idtelefone BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero VARCHAR(14) UNIQUE NOT NULL,
    tipo_telefone ENUM('residencial', 'celular') NOT NULL,
    fk_pessoa_telefone BIGINT,
    FOREIGN KEY(fk_pessoa_telefone) REFERENCES pessoa(idpessoa)
);

CREATE TABLE IF NOT EXISTS cliente (
    idcliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_pessoa_cliente BIGINT NOT NULL,
    email VARCHAR(45) UNIQUE NOT NULL,
    FOREIGN KEY(fk_pessoa_cliente) REFERENCES pessoa(idpessoa) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categoria (
	idcategoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30) NOT NULL,
    preco_hora DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS funcionario (
    idfuncionario BIGINT PRIMARY KEY AUTO_INCREMENT,
    fk_pessoa_funcionario BIGINT NOT NULL,
    fk_categoria_funcionario BIGINT,
    FOREIGN KEY(fk_categoria_funcionario) REFERENCES categoria(idcategoria) ON DELETE SET NULL,
    FOREIGN KEY(fk_pessoa_funcionario) REFERENCES pessoa(idpessoa) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS veiculo (
	idveiculo BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo_veiculo ENUM('carro', 'moto', 'caminhao') NOT NULL,
    marca VARCHAR(30) NOT NULL,
    modelo VARCHAR(30) NOT NULL,
    fk_cliente_veiculo BIGINT,
    FOREIGN KEY(fk_cliente_veiculo) REFERENCES cliente(idcliente)
);

CREATE TABLE IF NOT EXISTS peca (
	idpeca BIGINT PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(30) NOT NULL,
	preco_unitario DECIMAL(10, 2) NOT NULL,
    estoque INT NOT NULL
);

CREATE TABLE IF NOT EXISTS reparacao (
	idreparacao BIGINT PRIMARY KEY AUTO_INCREMENT,
	fk_cliente_reparacao BIGINT,
    fk_veiculo_reparacao BIGINT,
    valor_total DECIMAL(10, 2),
    data_finalizacao DATE,
    FOREIGN KEY(fk_cliente_reparacao) REFERENCES cliente(idcliente) ON DELETE SET NULL,
    FOREIGN KEY(fk_veiculo_reparacao) REFERENCES veiculo(idveiculo) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS reparacao_funcionario (
    idreparacao_funcionario BIGINT PRIMARY KEY AUTO_INCREMENT,
	reparacaoid BIGINT,
    funcionarioid BIGINT,
    horas_trabalhadas INT,
    valor_total DECIMAL(10, 2),
    FOREIGN KEY(reparacaoid) REFERENCES reparacao(idreparacao) ON DELETE CASCADE,
    FOREIGN KEY(funcionarioid) REFERENCES funcionario(idfuncionario) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS reparacao_peca (
    idreparacao_peca BIGINT PRIMARY KEY AUTO_INCREMENT,
	reparacaoid BIGINT,
    pecaid BIGINT,
    quantidade INT,
    valor_total DECIMAL(10, 2),
    FOREIGN KEY(reparacaoid) REFERENCES reparacao(idreparacao) ON DELETE CASCADE,
    FOREIGN KEY(pecaid) REFERENCES peca(idpeca) ON DELETE SET NULL
);

