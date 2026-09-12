# Manara — CRM de Gestion des Activités Jeunesse

Application web full-stack de gestion des activités parascolaires, développée avec **Spring Boot** (backend) et **Angular** (frontend).

---

## Table des matières

- [Stack technique](#stack-technique)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Lancement](#lancement)
- [Structure du projet](#structure-du-projet)
- [Rôles et fonctionnalités](#rôles-et-fonctionnalités)
- [Notifications email](#notifications-email)
- [API REST](#api-rest)
- [Authentification](#authentification)
- [Comptes de test](#comptes-de-test)
- [Équipe](#équipe)

---

## Stack technique

| Couche          | Technologie                         |
|-----------------|-------------------------------------|
| Backend         | Spring Boot 3.x, Spring Security    |
| Base de données | MySQL 8 + Spring JDBC / JPA         |
| Frontend        | Angular 17+ (Standalone Components) |
| UI              | Bootstrap 5.3, Bootstrap Icons      |
| Email           | JavaMailSender (SMTP Gmail)         |
| Auth            | Session-based (cookie JSESSIONID)   |

---

## Architecture

```
manara/
├── backend/
│   └── src/main/java/com/manara/
│       ├── api/                  # REST Controllers
│       │   ├── AdminRestController
│       │   ├── AnimateurRestController
│       │   ├── ParentRestController
│       │   ├── CompteRestController
│       │   └── ContactRestController
│       ├── controller/           # Thymeleaf Controllers (legacy)
│       ├── model/                # Entités JPA
│       ├── repository/           # Spring Data JPA
│       ├── service/              # Logique métier
│       ├── dto/                  # Objets de transfert
│       └── security/             # SecurityConfig
│
└── frontend/
    └── src/app/
        ├── core/
        │   └── services/
        │       ├── auth.service.ts
        │       ├── admin.service.ts
        │       ├── animateur.service.ts
        │       ├── parent.service.ts
        │       └── compte.service.ts
        ├── features/
        │   ├── admin/
        │   ├── animateur/
        │   └── parent/
        └── shared/
            └── components/
                ├── sidebar-admin/
                ├── sidebar-animateur/
                └── sidebar-parent/
```

---

## Prérequis

- Java 21+
- Maven 3.9+
- Node.js 20+ et npm
- MySQL 8+
- Angular CLI : `npm install -g @angular/cli`

---

## Installation

### 1. Cloner le projet

```bash
git clone https://github.com/votre-org/manara.git
cd manara
```

### 2. Base de données

```sql
CREATE DATABASE manara_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Backend

```bash
cd backend
mvn clean install
```

### 4. Frontend

```bash
cd frontend
npm install
```

---

## Configuration

### `application.properties`

```properties
# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/manara_db
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Email (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre_email@gmail.com
spring.mail.password=votre_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Upload fichiers
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

Pour le mot de passe Gmail, utilisez un [App Password](https://myaccount.google.com/apppasswords) et non votre mot de passe principal.

---

## Lancement

### Backend

```bash
cd backend
mvn spring-boot:run
```

Serveur sur **http://localhost:8080**

### Frontend

```bash
cd frontend
ng serve
```

Application sur **http://localhost:4200**

---

## Structure du projet Angular

```
src/app/features/
├── admin/
│   ├── components/
│   │   ├── dashboard/
│   │   ├── sessions/
│   │   ├── activites/
│   │   ├── users/
│   │   ├── incidents/
│   │   ├── planning/
│   │   └── evenements/
│   └── admin.routes.ts
│
├── animateur/
│   ├── components/
│   │   ├── dashboard/
│   │   ├── activityAnimateur/
│   │   ├── presences/
│   │   │   ├── (appel du jour)
│   │   │   ├── historique/
│   │   │   └── zone-enfant/
│   │   ├── incidents/
│   │   │   ├── history/
│   │   │   └── report/
│   │   └── contact/
│   └── animateur.routes.ts
│
└── parent/
    ├── components/
    │   ├── dashboard/
    │   ├── enfants/
    │   │   └── list/
    │   ├── messages-enfant/
    │   ├── incidents/
    │   └── contact/
    └── parent.routes.ts
```

---

## Rôles et fonctionnalités

### ROLE_ADMIN

| Fonctionnalité | Description |
|---|---|
| Dashboard | Statistiques globales, sessions récentes |
| Sessions | CRUD complet, gestion inscrits / liste d'attente |
| Activites | CRUD des activités |
| Utilisateurs | Création, modification, suppression |
| Incidents | Consultation de tous les incidents |
| Planning | Vue hebdomadaire des sessions |
| Evenements | Gestion des événements |

### ROLE_ANIMATEUR

| Fonctionnalité | Description |
|---|---|
| Dashboard | Sessions actives, enfants inscrits, prochaine session |
| Activites | Historique des événements (incidents + sessions) groupé par jour |
| Presences | Appel du jour par session |
| Zone Enfant | Messages aux enfants/parents avec fichiers joints |
| Incidents | Signalement et historique |
| Contact Admin | Formulaire d'envoi d'email à l'administration |

### ROLE_PARENT

| Fonctionnalité | Description |
|---|---|
| Dashboard | Enfants inscrits, activités en cours |
| Enfants | Liste, ajout, modification des profils |
| Messages | Messages des animateurs par enfant + stats présences/absences |
| Incidents | Historique des incidents de ses enfants |
| Contact Admin | Formulaire d'envoi d'email à l'administration |

---

## Notifications email

L'application envoie automatiquement des emails dans les situations suivantes :

| Déclencheur | Destinataire | Contenu |
|---|---|---|
| Inscription d'un utilisateur | Parent | Email de bienvenue |
| Inscription d'un enfant à une activité | Parent | Confirmation avec nom de l'activité |
| Création d'une session | Animateur | Détails complets : activité, dates, horaires, lieu, capacité |
| Signalement d'un incident | Admin et/ou Parent | Gravité et description de l'incident |
| Message publié dans la zone enfant | Parent | Notification de nouveau message |
| Formulaire de contact | Admin | Message complet avec nom et email de l'expéditeur |

Tous les emails sont envoyés via `NotificationService` → `EmailService` → JavaMailSender (SMTP Gmail).

---

## API REST

Base URL : `http://localhost:8080`

### Auth

| Methode | Route | Description |
|---|---|---|
| POST | `/login` | Connexion (email + password) |
| POST | `/logout` | Déconnexion |
| GET | `/api/auth/me` | Utilisateur connecté |

### Admin — `/api/admin/**`

| Methode | Route | Description |
|---|---|---|
| GET | `/api/admin/dashboard` | Statistiques globales |
| GET/POST | `/api/admin/sessions` | Liste / création session |
| PUT/DELETE | `/api/admin/sessions/{id}` | Modification / suppression |
| GET/POST | `/api/admin/activites` | Liste / création activité |
| GET | `/api/admin/users/list` | Liste des utilisateurs |
| GET | `/api/admin/animateurs/list` | Liste des animateurs |
| GET | `/api/admin/incidents` | Tous les incidents |
| GET | `/api/admin/planning` | Planning hebdomadaire |

### Animateur — `/api/animateur/**`

| Methode | Route | Description |
|---|---|---|
| GET | `/api/animateur/dashboard` | Stats + prénom animateur |
| GET | `/api/animateur/activites` | Historique groupé par jour |
| GET | `/api/animateur/sessions` | Sessions de l'animateur |
| GET | `/api/animateur/presences/historique` | Historique présences |
| GET | `/api/animateur/sessions/{id}/participants` | Participants d'une session |
| POST | `/api/animateur/sessions/{id}/presences` | Enregistrer l'appel |
| GET | `/api/animateur/sessions/{id}/zone-enfant` | Détail zone enfant |
| POST | `/api/animateur/sessions/{id}/zone-enfant/poster` | Poster un message (multipart) |
| DELETE | `/api/animateur/messages/{id}` | Supprimer un message |
| GET | `/api/animateur/incidents/history` | Historique incidents |
| GET | `/api/animateur/incidents/form-data` | Données formulaire signalement |
| POST | `/api/animateur/incidents/report` | Soumettre un incident |

### Parent — `/api/parent/**`

| Methode | Route | Description |
|---|---|---|
| GET | `/api/parent/dashboard` | Stats parent |
| GET | `/api/parent/children` | Liste des enfants |
| POST | `/api/parent/children` | Ajouter un enfant |
| PUT | `/api/parent/children/{id}` | Modifier un enfant |
| GET | `/api/parent/activities` | Liste des activités |
| POST | `/api/parent/activities/register` | Inscrire un enfant à une session |
| GET | `/api/parent/planning` | Planning des inscriptions |
| GET | `/api/parent/enfants/{id}/messages` | Messages + stats enfant |
| GET | `/api/parent/incidents` | Incidents de ses enfants |

### Compte et Contact

| Methode | Route | Description |
|---|---|---|
| GET | `/api/compte` | Profil connecté |
| PUT | `/api/compte/update` | Mise à jour profil (inclut téléphone) |
| POST | `/api/contact` | Envoyer un message à l'admin |

---

## Authentification

L'application utilise l'authentification par session Spring Security avec cookie `JSESSIONID`.

Angular envoie le cookie automatiquement via l'intercepteur HTTP :

```typescript
// auth.interceptor.ts
const authReq = req.clone({ withCredentials: true });
```

Les routes sont protégées par rôle dans `SecurityConfig` :

```java
.requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
.requestMatchers("/api/animateur/**").hasAuthority("ROLE_ANIMATEUR")
.requestMatchers("/api/parent/**").hasAuthority("ROLE_PARENT")
.requestMatchers("/api/**").authenticated()
```

---

## Comptes de test

Pour tester l'application en local, créez les comptes suivants via `/api/auth/register` ou directement en base :

| Role | Email | Mot de passe |
|---|---|---|
| Admin | admin@manara.com | admin123 |
| Animateur | animateur1@manara.com | animateur123 |
| Parent | parent1@manara.com | parent123 |

Le compte admin doit avoir `role = ROLE_ADMIN` directement en base (l'inscription publique crée uniquement des parents).

---

## Equipe

Projet développé par **Yumnah,Reyes Hamphy, Cirius Rose Alexandra,Herve et Gabriel** — CRM Manara