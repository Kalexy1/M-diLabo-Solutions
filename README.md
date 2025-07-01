# MédiLabo Solutions

## Description

Ce projet regroupe plusieurs microservices destinés à gérer une application de gestion de patients pour MédiLabo.  
Il comprend notamment un service d’interface utilisateur (patient-ui-service), des services métiers, ainsi que les bases de données associées.

## Architecture

- patient-ui-service : Interface utilisateur Spring Boot + Thymeleaf  
- patient-service : Service backend pour gestion des patients (MySQL)  
- note-service : Service pour la gestion des notes médicales (MongoDB)  
- risk-assessment-service : Calcul du risque de diabète  
- gateway-service : API Gateway  
- mysql-db : Base relationnelle MySQL  
- mongo-db : Base NoSQL MongoDB  

## Prérequis

- Docker et Docker Compose  
- Java 21  
- Maven (pour build local)  

## Installation

1. Cloner le repository :

```bash
git clone https://github.com/Kalexy1/M-diLabo-Solutions.git
cd projet-root
