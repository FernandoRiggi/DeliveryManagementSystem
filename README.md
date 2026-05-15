# Delivery Management System-

Projeto desenvolvido no contexto da disciplina de Verificação, Validação e Teste de Software (IFSP Câmpus São Carlos) ministrada pelo professor Lucas.

O sistema consiste em uma aplicação fullstack para gerenciamento de entregas urbanas, permitindo autenticação de usuários, criação de pedidos, despacho de entregadores e acompanhamento do ciclo de entrega
com foco no desenvolvimento de testes de software.
---

# Escopo do Dominio

O sistema consiste em um sistema de gerenciamento de entregas urbanas, com seu principal agregado sendo OrderDelivery.
ele tambem conta com com outras entidades como DeliveryMan e Customer.

## Value Objects
- Address
- Cep
- LogisticScore

---

# Tecnologias Utilizadas

## Backend
- Java 21
- Spring Boot 3.4.3
- Spring Security
- JWT Authentication
- Spring Data JPA
- PostgreSQL
- Maven

## Frontend
- React
- Vite
- Axios
- Bootstrap
- React Router DOM

## Infraestrutura
- Docker
- Docker Compose

---

# Rodando o projeto

## Pré-requisitos
- Java 21
- Maven
- Docker

## Rodando o projeto
- git clone https://github.com/FernandoRiggi/DeliveryManagementSystem.git
- cd DeliveryManagementSystem
- mvn clean install
- docker-compose up --build

## Finalizando o projeto
- docker-compose down