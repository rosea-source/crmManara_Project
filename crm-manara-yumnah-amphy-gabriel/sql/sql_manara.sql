-- =========================
-- TABLE USERS
-- =========================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prenom VARCHAR(50) NOT NULL,
    nom VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    telephone VARCHAR(20)
);

-- =========================
-- TABLE ACTIVITES
-- =========================
CREATE TABLE activites (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    age_min INT NOT NULL,
    age_max INT NOT NULL
);

-- =========================
-- TABLE PARENTS
-- =========================
CREATE TABLE parents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    adresse VARCHAR(255),
    user_id INT,
    CONSTRAINT fk_parent_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =========================
-- TABLE ANIMATEURS
-- =========================
CREATE TABLE animateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    diplome VARCHAR(255),
    specialite VARCHAR(255),
    date_embauche DATE,
    CONSTRAINT fk_animateur_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =========================
-- TABLE ENFANTS
-- =========================
CREATE TABLE enfants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    date_naissance DATE NOT NULL,
    parent_id INT NOT NULL,
    allergies VARCHAR(255),
    notes_medicales TEXT,
    CONSTRAINT fk_enfant_parent FOREIGN KEY (parent_id) REFERENCES parents(id)
);

-- =========================
-- TABLE EVENTS
-- =========================
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(1000),
    date DATE,
    image_url VARCHAR(255)
);

-- =========================
-- TABLE SESSIONS
-- =========================
CREATE TABLE sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    activite_id INT NOT NULL,
    animateur_id INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    lieu VARCHAR(100),
    capacite_max INT DEFAULT 15,
    statut VARCHAR(20) DEFAULT 'Prévue',

    CONSTRAINT fk_session_activite FOREIGN KEY (activite_id) REFERENCES activites(id),
    CONSTRAINT fk_session_animateur FOREIGN KEY (animateur_id) REFERENCES animateurs(id)
);

-- =========================
-- TABLE INSCRIPTIONS
-- =========================
CREATE TABLE inscriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enfant_id INT,
    session_id INT,
    statut_paiement VARCHAR(50),

    CONSTRAINT fk_inscription_enfant FOREIGN KEY (enfant_id) REFERENCES enfants(id),
    CONSTRAINT fk_inscription_session FOREIGN KEY (session_id) REFERENCES sessions(id)
);

-- =========================
-- TABLE PRESENCES
-- =========================
CREATE TABLE presences (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inscription_id INT NOT NULL,
    statut VARCHAR(50) NOT NULL DEFAULT 'Présent',
    note_animateur VARCHAR(255),

    CONSTRAINT fk_presence_inscription FOREIGN KEY (inscription_id) REFERENCES inscriptions(id)
);

-- =========================
-- TABLE MESSAGES_ENFANT
-- =========================
CREATE TABLE messages_enfant (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contenu TEXT NOT NULL,
    fichier_nom VARCHAR(255),
    fichier_chemin VARCHAR(255),
    date_envoi DATETIME NOT NULL,
    session_id INT NOT NULL,
    enfant_id INT,
    animateur_id INT NOT NULL,
    lu BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_message_session FOREIGN KEY (session_id) REFERENCES sessions(id),
    CONSTRAINT fk_message_enfant FOREIGN KEY (enfant_id) REFERENCES enfants(id),
    CONSTRAINT fk_message_animateur FOREIGN KEY (animateur_id) REFERENCES animateurs(id)
);