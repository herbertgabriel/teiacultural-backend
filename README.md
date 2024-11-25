# Backend Teia Cultural

Este é o backend de uma rede social simplificada chamada **Teia Cultural**, onde os usuários podem criar perfis, fazer publicações, inserir imagens tanto nos perfis quanto nas publicações, gerenciar eventos, além de contar com funcionalidades de login, senha e gerenciamento de usuários com permissão para superadmin. O projeto é local e utiliza o **LocalStack** para simular o serviço de armazenamento S3 da AWS.

## Funcionalidades

- **Criação de Perfil:** Usuários podem criar seus perfis profissionais para divulgar seus serviços.
- **Publicações:** Usuários podem criar publicações e associar imagens.
- **Imagens:** As imagens associadas aos perfis e publicações são armazenadas no S3.
- **S3 com LocalStack:** A comunicação com o serviço S3 é simulada usando o LocalStack, permitindo testar funcionalidades sem necessidade de uma conta AWS real.
- **Login:** Sistema de autenticação básico com gerenciamento de login e senha.
- **Gestão de Eventos:** Permite a criação de eventos culturais.
- **Gestão de Usuários:** Sistema de gerenciamento de usuários, com um superadmin que pode gerenciar permissões.
  
## Tecnologias

- **Spring Boot:** Framework para desenvolvimento da aplicação backend.
- **MySQL:** Banco de dados relacional utilizado para armazenar dados dos usuários, publicações, eventos e outras informações.
- **AWS S3 (LocalStack):** Armazenamento de imagens nos perfis e publicações usando S3, simulando o serviço da AWS com LocalStack.
- **JWT (JSON Web Token):** Para autenticação de usuários e geração de tokens de acesso.
- **BCrypt:** Para criptografia de senhas.

## Requisitos

- **Java 21** ou superior
- **Maven** ou **Gradle** (para build)
- **Docker** (para rodar o LocalStack)
- **MySQL** (para banco de dados)

