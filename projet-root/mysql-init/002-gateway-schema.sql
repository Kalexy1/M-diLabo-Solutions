-- Schéma minimal pour la table users du gateway (si JPA = update, c'est optionnel)
CREATE TABLE IF NOT EXISTS medilabo_gateway.users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
