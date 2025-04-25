## API Oficina

>  Api desenvolvida a fim de pôr em prática meus conhecimentos em Java e Spring Boot

Este repositório contém somente o **Backend** do projeto.

---

## 🎯 Funcionalidades

* ✅ **Cadastro de Clientes**  
  Permite o cadastro, consulta e exclusão de clientes. Também possibilita visualizar os veículos e reparos associados a um cliente.

* ✅ **Cadastro de Veículos**  
  Permite o cadastro, consulta e exclusão de veículos. Os veículos são associados aos clientes, permitindo a consulta de todos os veículos registrados para um determinado cliente.

* ✅ **Cadastro de Funcionários**  
  Permite o cadastro, consulta e exclusão de funcionários, além de associá-los a reparos específicos.

* ✅ **Cadastro de Reparos**  
  Cadastro de reparos realizados, incluindo informações sobre os funcionários e peças associadas. Permite adicionar ou remover funcionários e peças de reparos existentes.

* ✅ **Manipulação de Funcionários e Peças em Reparos**  
  Permite associar ou desassociar funcionários e peças a reparos específicos.
  
* ✅ **Controle do estoque de Peças**  
  Permite controlar o estoque de peças.


* ✅ **Consultas Avançadas**
    - **Filtrar Clientes por Nome**: Permite buscar clientes por nome.
    - **Filtrar Reparos por Data**: Possibilita filtrar os reparos realizados por data.
    - **Visualizar Funcionários e Peças Associados a um Reparo**: Para cada reparo, é possível consultar os funcionários e as peças que foram utilizados.
    - **Visualizar Veículos de um Cliente**: Permite listar os veículos registrados para um cliente específico.

## 🛠️ Tecnologias Utilizadas

- ![Java](https://img.shields.io/badge/Java-17-blue?logo=java) - Linguagem de programação utilizada.
- ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-brightgreen?logo=spring) - Framework para criação de APIs REST em Java.
- ![JPA](https://img.shields.io/badge/JPA-blue?logo=eclipselink) - Framework para mapeamento objeto-relacional.
- ![Hibernate](https://img.shields.io/badge/Hibernate-blue?logo=hibernate) - Framework ORM para persistência de dados.
- ![Docker](https://img.shields.io/badge/Docker-blue?logo=docker) - Conteinerização da aplicação.
- ![MySQL](https://img.shields.io/badge/MySQL-black?logo=mysql) - Banco de dados utilizado para persistência de dados.
- ![Maven](https://img.shields.io/badge/Maven-Build-blue?logo=apachemaven) - Gerencia dependências e automação de builds para projetos Java.

---

## 💾 Estrutura do Banco de Dados

<div style="text-align: center;">
  <img src="src/main/resources/db/model-oficina.png" width="700"/>
</div>

## 🚀 Como Rodar a API

### 1. Clone o Repositório
- Primeiro, clone o repositório:
    ```
        git clone https://github.com/mbranches/api-oficina.git
        cd api-oficina
    ```
  
### 2. Popule as variáveis de ambiente.

#### a. **Variáveis Docker**
- Renomeie o arquivo da raíz do repositório `.envTemplate` para `.env` e preencha as variáveis com os valores apropriados para o seu ambiente de desenvolvimento.

#### b. **Variáveis Spring Boot (`src/main/resources`)**
- Obs: Caso queira utilizar as credenciais predefinidas no application, você pode pular essa etapa
- Navegue até o diretório `src/main/resources`
- Renomeie o arquivo `.envTemplate` para `.env` e configure as variáveis conforme necessário para o Spring Boot.

### 3. Rode o docker
- A partir da raíz do repositório rodar o seguinte comando:
    ```
      docker-compose up -d
    ```

### 4. Instalação do Maven
- Certifique-se de ter o Maven instalado no seu computador. Caso não tenha, clique [aqui](https://dicasdeprogramacao.com.br/como-instalar-o-maven-no-windows/) para ter acesso ao tutorial.

### 5. Inicialize o Spring Boot
- Com o docker rodando é só inicializar a API com os seguinte comandos:
    ```
      mvn clean install
      mvn spring-boot:run
    ```

## 🚀 Como Consumir a API com Postman

### O que é o Postman?

O **Postman** é uma ferramenta popular para testar e consumir APIs. Ele permite que você faça requisições HTTP de forma simples e intuitiva, além de visualizar respostas, testar diferentes cenários e automatizar testes de APIs. Usar o Postman é uma forma prática de interagir com a **API Oficina**, permitindo que você envie dados, faça consultas e visualize as respostas da API sem precisar escrever código.

### Por que Usar o Postman nesta Aplicação?

Usar o **Postman** para consumir a API facilita o processo de desenvolvimento e testes. Com ele, você pode:

- Testar as rotas da API de forma rápida e fácil.
- Interagir com a API.
- Explorar todas as funcionalidades disponíveis (CRUD de Reparos, Clientes, Funcionários etc.).
- Organizar e salvar requisições para reutilização posterior.

### Como Importar a Collection para o Postman

1. **Baixar o Arquivo da Collection**:  
   Na pasta `data` do repositório, você encontrará um arquivo chamado `Oficina.postman_collection.json`. Esse arquivo contém todas as rotas da API, prontas para serem usadas no **Postman**.

2. **Abrir o Postman**:  
   Caso não tenha o Postman instalado, você pode baixá-lo gratuitamente no [site oficial](https://www.postman.com/downloads/), caso já tenha o **Postman** mas não sabe usar, clique [aqui](https://www.youtube.com/watch?v=64-O-dDR7ic-) para assistir um tutorial introdutório.


3. **Importar a Collection**:
  - Abra o **Postman**.
  - Clique em **Import** no canto superior esquerdo da tela.
  - Selecione o arquivo `Oficina.postman_collection.json` que você baixou da pasta `data`.
  - Após a importação, todas as rotas estarão disponíveis no **Postman**.

4. **Configuração da URL**:  
   Ao importar a collection, as rotas da API estarão configuradas para o ambiente de produção. Caso estiver rodando localmente e não alterou a porta, a URL da API é ``http://localhost:8080``.

5. **Consumindo a API**:  
   Agora, você pode começar a testar a API, realizando operações como:
  - **Cadastrar um Cliente** (POST)
  - **Consultar Reparos que um Cliente fez** (GET)
  - **Filtrar Reparos por data** (GET)
  - **Excluir um Cliente** (DELETE)

   Agora, basta explorar, testar e interagir com a API de forma prática e eficiente utilizando o **Postman**. 🚀
