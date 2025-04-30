# Rapport Technique Complet - Projet 10 : Gestion des Stagiaires

## Introduction

Ce document présente un rapport technique détaillé sur l'application de gestion des stagiaires développée avec Spring Boot 3.4.5 et Java 21. L'application permet de gérer l'ensemble du processus de demande de stages, depuis la soumission des candidatures jusqu'au suivi des stagiaires.

## Démarrage rapide

### Prérequis
- Java 21 ou supérieur
- Maven 3.8.x ou supérieur

### Installation et lancement
1. Clonez le projet ou téléchargez les sources
2. Compilez le projet avec Maven :
   ```bash
   mvn clean package
   ```
3. Lancez l'application :
   ```bash
   java -jar target/gestion-stagiaires-0.0.1-SNAPSHOT.jar
   ```
4. Accédez à l'application dans votre navigateur : [http://localhost:8080](http://localhost:8080)

### Comptes utilisateurs par défaut
L'application crée automatiquement deux utilisateurs au premier démarrage :

1. **Administrateur** :
   - Email : admin@gestionstagiaires.com
   - Mot de passe : admin123
   - Rôle : ADMIN (tous les droits)

2. **Utilisateur standard** :
   - Email : user@gestionstagiaires.com
   - Mot de passe : user123
   - Rôle : USER (droits limités)

### Données de test
Au premier démarrage, l'application génère également deux stagiaires de test :

1. **Sophie Martin** :
   - Statut : En attente
   - Formation : Ingénierie informatique
   - École : École Polytechnique

2. **Thomas Dubois** :
   - Statut : Accepté
   - Formation : Master en Intelligence Artificielle
   - École : Université Paris-Saclay
   - Encadrant : Utilisateur standard

## Architecture du Projet

### Structure du Projet

Le projet suit une architecture en couches classique MVC (Modèle-Vue-Contrôleur) :

```
gestion-stagiaires/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── gestionstagiaires/
│   │   │           ├── config/          # Configuration de Spring
│   │   │           ├── controller/      # Contrôleurs REST
│   │   │           ├── dto/             # Objets de transfert de données
│   │   │           ├── model/           # Entités JPA
│   │   │           ├── repository/      # Interfaces d'accès aux données
│   │   │           ├── security/        # Configuration de sécurité
│   │   │           ├── service/         # Couche service métier
│   │   │           └── GestionStagiairesApplication.java
│   │   └── resources/
│   │       ├── static/                  # Ressources statiques (CSS, JS)
│   │       ├── templates/               # Templates Thymeleaf
│   │       └── application.properties   # Configuration de l'application
│   └── test/                            # Tests unitaires et d'intégration
└── pom.xml                              # Configuration Maven
```

### Modules Principaux

1. **Gestion des utilisateurs** : Inscription, authentification et gestion des profils
2. **Gestion des stagiaires** : Enregistrement et suivi des demandes de stages
3. **Traitement des stages** : Acceptation/refus des demandes, assignation des encadrants
4. **Notifications par email** : Envoi d'emails aux stagiaires (simulation)
5. **Tableau de bord** : Visualisation et statistiques des stages

## Technologies Utilisées

- **Backend** :
  - Spring Boot 3.4.5
  - Spring Security pour l'authentification
  - Spring Data JPA pour la persistance des données
  - Spring Mail pour l'envoi d'emails (simulé)
  - Base de données H2 (embarquée)
  - Lombok pour réduire le code boilerplate
  - Hibernate Validator pour la validation des données

- **Frontend** :
  - Thymeleaf pour les templates HTML
  - Bootstrap 5 pour le design responsive
  - Font Awesome pour les icônes
  - jQuery pour les interactions JavaScript
  - Police Proxima-Nova Regular

## Modèles de Données

### Entités Principales

1. **User** :
   - Attributs : id, email, password, nom, prenom, enabled, roles
   - Relations : ManyToMany avec Role, OneToMany avec Stagiaire

2. **Role** :
   - Attributs : id, name
   - Relations : ManyToMany avec User

3. **Stagiaire** :
   - Attributs : id, nom, prenom, email, telephone, ecole, formation, niveau, dateDebut, dateFin, description, statut
   - Relations : ManyToOne avec User (encadrant)

4. **StatutStage (Enum)** :
   - Valeurs : EN_ATTENTE, ACCEPTE, REFUSE, EN_COURS, TERMINE

## Fonctionnalités Détaillées

### 1. Authentification et Gestion des Utilisateurs

#### 1.1 Connexion
- Formulaire d'authentification sécurisé
- Validation des identifiants avec Spring Security
- Fonction "Se souvenir de moi"
- Protection contre les attaques CSRF

#### 1.2 Inscription
- Création de compte utilisateur
- Validation des données d'inscription
- Attribution automatique du rôle USER
- Encodage du mot de passe avec BCrypt

#### 1.3 Gestion des Profils
- Visualisation des informations personnelles
- Modification des données du profil
- Changement de mot de passe
- Affichage des rôles de l'utilisateur

#### 1.4 Administration des Utilisateurs (ADMIN)
- Liste de tous les utilisateurs
- Création de nouveaux utilisateurs
- Modification des utilisateurs existants
- Gestion des rôles (ADMIN, USER)
- Suppression d'utilisateurs

### 2. Gestion des Stagiaires

#### 2.1 Demande de Stage
- Formulaire de demande accessible sans authentification
- Collecte des informations personnelles et académiques
- Définition de la période de stage
- Soumission avec statut initial "EN_ATTENTE"

#### 2.2 Liste des Stagiaires
- Affichage paginé de tous les stagiaires
- Filtrage par statut (En attente, Accepté, Refusé, etc.)
- Recherche par nom, prénom ou email
- Actions rapides (voir, modifier, supprimer)

#### 2.3 Détails d'un Stagiaire
- Vue complète des informations d'un stagiaire
- Historique du traitement de la demande
- Coordonnées et détails de formation
- Informations sur l'encadrant assigné

#### 2.4 Traitement des Demandes
- Formulaire de traitement intégré
- Changement de statut (Accepté, Refusé, En cours, Terminé)
- Assignation d'un encadrant parmi les utilisateurs
- Envoi d'email au stagiaire (simulé)

### 3. Tableau de Bord

#### 3.1 Statistiques Globales
- Nombre de stagiaires par statut
- Graphique de répartition des stages
- Tendances et évolution des demandes

#### 3.2 Suivi Personnel
- Liste des stagiaires assignés à l'utilisateur connecté
- Statut et progression des stages encadrés
- Accès rapide aux actions de traitement

#### 3.3 Actions Rapides
- Liens directs vers les fonctionnalités principales
- Recherche rapide de stagiaires
- Création de nouvelles demandes

### 4. Emails et Notifications

#### 4.1 Notifications Email
- Envoi d'emails lors du traitement des demandes (simulation)
- Contenu personnalisé selon la décision
- Possibilité d'ajouter un message personnalisé

## Sécurité

### Authentification
- Utilisation de Spring Security
- Stockage sécurisé des mots de passe avec BCrypt
- Session sécurisée avec timeout configurable
- Protection CSRF sur tous les formulaires

### Autorisation
- Contrôle d'accès basé sur les rôles (RBAC)
- Sécurisation des URLs et des méthodes
- Annotations @PreAuthorize pour les contrôleurs
- Filtrage des contenus basé sur l'utilisateur connecté

### Validation des Données
- Validation côté serveur avec Hibernate Validator
- Validation côté client avec JavaScript
- Protection contre les injections SQL (via JPA)
- Échappement des données dans les vues Thymeleaf

## Interface Utilisateur

### Design Responsive
- Adaptation à tous les types d'appareils
- Bootstrap 5 pour la mise en page
- Police Proxima-Nova Regular pour une meilleure lisibilité
- Icônes Font Awesome pour une interface moderne

### Composants UI
- Formulaires avec validation en temps réel
- Tableaux interactifs avec tri et pagination
- Cards pour l'affichage des informations structurées
- Badges colorés pour indiquer les statuts
- Tooltips et popovers pour l'aide contextuelle

## Guide d'Utilisation par Profil

### Visiteur (non authentifié)
1. Consulter la page d'accueil pour comprendre l'application
2. Soumettre une demande de stage via le formulaire dédié
3. S'inscrire pour créer un compte utilisateur
4. Se connecter avec ses identifiants

### Utilisateur Standard (ROLE_USER)
1. Consulter le tableau de bord avec les statistiques
2. Voir la liste des stagiaires et leurs détails
3. Traiter les demandes de stage (accepter, refuser)
4. Être assigné comme encadrant de stagiaires
5. Suivre les stagiaires dont il est l'encadrant
6. Mettre à jour son profil personnel

### Administrateur (ROLE_ADMIN)
1. Accès à toutes les fonctionnalités de l'utilisateur standard
2. Gestion complète des utilisateurs (création, modification, suppression)
3. Attribution des rôles aux utilisateurs
4. Suppression des stagiaires
5. Configuration des paramètres de l'application

## Configuration de l'Application

### Paramètres Principaux (application.properties)
```properties
# Base de données
spring.datasource.url=jdbc:h2:file:./data/gestion-stagiaires-db
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

# Serveur
server.port=8080
server.servlet.session.timeout=30m

# Thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.encoding=UTF-8

# Email (configuration de test)
spring.mail.host=smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=your-username
spring.mail.password=your-password
```

### Personnalisation
- Modification du logo et des couleurs dans les fichiers CSS
- Adaptation des templates Thymeleaf selon les besoins
- Ajout de nouveaux statuts de stage dans l'enum StatutStage
- Configuration des paramètres d'email pour un environnement de production

## Maintenance et Améliorations Possibles

### Améliorations Techniques
1. Migration vers une base de données PostgreSQL ou MySQL
2. Mise en place d'un système de logging plus avancé
3. Ajout de tests automatisés (unitaires et d'intégration)
4. Optimisation des requêtes de base de données pour les grands volumes

### Nouvelles Fonctionnalités
1. Système de documents (upload de CV, conventions, rapports)
2. Calendrier des stages avec événements et rappels
3. Évaluation des stagiaires par les encadrants
4. Statistiques avancées et export de données
5. Notifications par SMS ou push (en plus des emails)

## Conclusion

L'application "Gestion des Stagiaires" offre une solution complète et facile à utiliser pour gérer l'ensemble du processus de stages, de la demande initiale jusqu'à la clôture. Sa conception modulaire et son interface intuitive en font un outil adapté aux établissements d'enseignement, entreprises ou organisations accueillant des stagiaires.

Son architecture basée sur Spring Boot garantit robustesse et évolutivité, tandis que l'interface responsive permet une utilisation sur tous types d'appareils. Les fonctionnalités de sécurité intégrées assurent la protection des données sensibles des stagiaires et utilisateurs.

Cette application représente une base solide qui peut être facilement étendue pour répondre à des besoins spécifiques ou intégrée dans un système d'information plus large.
