🧬 MédiLabo Solutions — Plateforme de Gestion des Patients
📘 Description

MédiLabo Solutions est une application médicale complète organisée en microservices.
Elle permet la gestion des patients, des notes médicales, ainsi que le calcul automatisé du risque de diabète.
Une interface utilisateur en Spring Boot + Thymeleaf permet aux organisateurs et praticiens d’interagir avec le système.

Le projet respecte une architecture moderne : microservices, API Gateway, bases MySQL & MongoDB, conteneurisation Docker, sécurité JWT.

🏛 Architecture Globale
┌─────────────────────────┐
│     patient-ui-service   │  ← Interface utilisateur (Thymeleaf)
└───────────▲─────────────┘
            │ appels REST
┌───────────┴─────────────┐
│       gateway-service    │  ← API Gateway + Sécurité JWT
└───────────▲─────────────┘
            │ routage
┌───────────┼──────────────┬──────────────────────┐
│           │              │                      │
│  patient-service   note-service   risk-assessment-service
│   (MySQL)           (MongoDB)             (calcul du risque)
└───────────┼──────────────┴──────────────────────┘
            │
     Bases de données
         │
  ┌──────┴──────┐
  │   MySQL     │
  │ (patients)  │
  └─────────────┘
  ┌─────────────┐
  │   MongoDB    │
  │   (notes)    │
  └─────────────┘

🧩 Microservices
| Microservice                | Rôle                        | Technologie             | Base    |
| --------------------------- | --------------------------- | ----------------------- | ------- |
| **patient-ui-service**      | Interface web utilisateur   | Spring Boot + Thymeleaf | —       |
| **patient-service**         | Gestion CRUD patients       | Spring Boot + JPA       | MySQL   |
| **note-service**            | Gestion des notes médicales | Spring Boot             | MongoDB |
| **risk-assessment-service** | Calcul du risque de diabète | Spring Boot             | —       |
| **gateway-service**         | API Gateway + Sécurité JWT  | Spring Cloud Gateway    | —       |
| **mysql-db**                | Stockage des patients       | MySQL 8                 | —       |
| **mongo-db**                | Stockage des notes          | MongoDB 7               | —       |

✅ Prérequis
Java 21

Maven 3.9+

Docker & Docker Compose

Git

Un IDE (IntelliJ / Eclipse)

🏗 Installation & Lancement
1️⃣ Cloner le projet
git clone https://github.com/Kalexy1/M-diLabo-Solutions.git
cd projet-root
2️⃣ (Optionnel) Compiler les services
mvn clean package -DskipTests
3️⃣ Lancer toute la plateforme avec Docker
docker-compose up --build
4️⃣ URLs principales
| Service                       | URL                                            |
| ----------------------------- | ---------------------------------------------- |
| **Interface Patient (UI)**    | [http://localhost:8084](http://localhost:8084) |
| **Gateway API**               | [http://localhost:8080](http://localhost:8080) |
| **Patient-service**           | [http://localhost:8081](http://localhost:8081) |
| **Note-service**              | [http://localhost:8082](http://localhost:8082) |
| **Risk-service**              | [http://localhost:8083](http://localhost:8083) |
| (Optionnel) **phpMyAdmin**    | [http://localhost:8085](http://localhost:8085) |
| (Optionnel) **Mongo Express** | [http://localhost:8086](http://localhost:8086) |

🔐 Sécurité

Authentification JWT

Token stocké dans un cookie sécurisé HttpOnly

Rôles :

ORGANISATEUR → gestion des patients

PRATICIEN → consultation des dossiers, notes & rapports de risque

Sécurité centralisée dans le gateway-service

🩺 Fonctionnalités
👩‍💼 Organisateurs

Créer / modifier / supprimer un patient

Voir la liste complète

👨‍⚕️ Praticiens

Voir l’historique médical d’un patient

Ajouter une note médicale

Générer un rapport de risque de diabète

Analyse basée sur :

âge

genre

nombre de déclencheurs médicaux extraits des notes

🔎 Risk Assessment — Niveaux possibles

None

Borderline

In Danger

Early onset

🧪 Tests & Qualité

Chaque service inclut :

Tests unitaires JUnit 5

Tests de contrôleurs MockMvc

Tests d’intégration (profils test)

Objectif : ≥ 70% de couverture Jacoco

Aucune dépendance Spring inutile (clean Spring Boot 3.5)

🗂 Technologies utilisées

Spring Boot 3.5

Spring MVC / Web

Spring Data JPA

Spring Cloud Gateway

Spring Security 6

JWT Auth

Thymeleaf

MySQL

MongoDB

Docker & Docker Compose

JUnit 5

Maven

📄 Licence

Projet développé dans le cadre de la formation OpenClassrooms - Développeur d’Application Java.
Auteur : Kalexy1
