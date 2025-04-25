USE oficina;

CREATE TABLE IF NOT EXISTS endereco (
	idendereco BIGINT PRIMARY KEY AUTO_INCREMENT,
    rua VARCHAR(30),
    bairro VARCHAR(30), 
    cidade VARCHAR(30),
    uf CHAR(2)
);

CREATE TABLE IF NOT EXISTS cliente (
	idcliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30),
    sobrenome VARCHAR(30),
    fk_endereco_cliente BIGINT,
    FOREIGN KEY(fk_endereco_cliente) REFERENCES endereco(idendereco)
);

CREATE TABLE IF NOT EXISTS categoria (
	idcategoria BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30),
    preco_hora DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS funcionario (
	idfuncionario BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(30),
    sobrenome VARCHAR(30),
    fk_categoria_funcionario BIGINT,
    fk_endereco_funcionario BIGINT,
    FOREIGN KEY(fk_categoria_funcionario) REFERENCES categoria(idcategoria) ON DELETE SET NULL,
    FOREIGN KEY(fk_endereco_funcionario) REFERENCES endereco(idendereco)
);

CREATE TABLE IF NOT EXISTS telefone (
	idtelefone BIGINT PRIMARY KEY AUTO_INCREMENT,
    numero VARCHAR(14),
    tipo_telefone ENUM('residencial', 'celular'),
    fk_cliente_telefone BIGINT,
	fk_funcionario_telefone BIGINT,
    FOREIGN KEY(fk_cliente_telefone) REFERENCES cliente(idcliente),
    FOREIGN KEY(fk_funcionario_telefone) REFERENCES funcionario(idfuncionario)
);

CREATE TABLE IF NOT EXISTS veiculo (
	idveiculo BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo_veiculo ENUM('carro', 'moto', 'caminhao'),
    marca VARCHAR(30),
    modelo VARCHAR(30),
    fk_cliente_veiculo BIGINT,
    FOREIGN KEY(fk_cliente_veiculo) REFERENCES cliente(idcliente)
);

CREATE TABLE IF NOT EXISTS peca (
	idpeca BIGINT PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(30),
	preco_unitario DECIMAL(10, 2),
    estoque INT
);

CREATE TABLE IF NOT EXISTS reparacao (
	idreparacao BIGINT PRIMARY KEY AUTO_INCREMENT,
	fk_cliente_reparacao BIGINT,
    fk_veiculo_reparacao BIGINT,
    valor_total DECIMAL(10, 2),
    data_finalizacao DATE,
    FOREIGN KEY(fk_cliente_reparacao) REFERENCES cliente(idcliente) ON DELETE SET NULL,
    FOREIGN KEY(fk_veiculo_reparacao) REFERENCES veiculo(idveiculo)
);

CREATE TABLE IF NOT EXISTS reparacao_funcionario (
	reparacaoid BIGINT,
    funcionarioid BIGINT,
    horas_trabalhadas INT,
    valor_total DECIMAL(10, 2),
    PRIMARY KEY(reparacaoid, funcionarioid),
    FOREIGN KEY(reparacaoid) REFERENCES reparacao(idreparacao),
    FOREIGN KEY(funcionarioid) REFERENCES funcionario(idfuncionario)
);

CREATE TABLE IF NOT EXISTS reparacao_peca (
	reparacaoid BIGINT,
    pecaid BIGINT,
    quantidade INT,
    valor_total DECIMAL(10, 2),
    PRIMARY KEY(reparacaoid, pecaid),
    FOREIGN KEY(reparacaoid) REFERENCES reparacao(idreparacao),
    FOREIGN KEY(pecaid) REFERENCES peca(idpeca)
);

