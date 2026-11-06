# VaiPraQuem

Esse é um projeto desenvolvido pelos alunos do 2º semestre de 
Análise e Desenvolvimento de Sistemas para a disciplina de UPX da UniFacens.
O intuito dessa disciplina é aplicar os conceitos aprendidos durante o semestre
em um projeto prático.

-- Resumo do projeto

O intuito inicial do projeto é criar uma plataforma que conecta pessoas e empresas 
interessadas em descartar produtos reutilizáveis a pessoas interessadas em adquiri-los 
ou reaproveitá-los, promovendo a reutilização de matérias e a redução do desperdício.

## 🚀 Tecnologias
- Linguagem: Java
- Banco de Dados: MySQL
- Prototipação: Figma

## 📦 Como rodar o projeto
1. Clone este repositório:
   `git clone https://github.com/viniciniwnl/VaiPraQuem`
   
2. Instale as dependências:
   Para fazer a integração com o banco de dados e o programa rodar de forma correta
   é necessário adicionar o arquivo .jar do Conector que você consegue direto do
   Maven Repository através do link: `https://mvnrepository.com/artifact/com.mysql/mysql-connector-j`
   
3. Execute a aplicação:
   Caso queira conferir os dados sendo gravados no banco, é necessário ter o MySQL Workbench 8.0 ou +
   e rodar o script. É necessário também trocar o último parâmetro do método getConnection, dentro
   da classe ConnectionFactory em `vaiPraquem/src/factory/ConnectionFactory.java` e colocar a senha
   que você utiliza para conectar no Workbench.

- IMPORTANTE
  Caso os passos anteriores não sejam seguidos, pode ocorrer algum erro durante a execução do código!
