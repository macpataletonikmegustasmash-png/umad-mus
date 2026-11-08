-- Script para crear la base de datos de UMAD Music.
-- Ojo: borra las tablas si ya existen, asi que se pierde todo lo guardado.

DROP TABLE IF EXISTS playlist_canciones;
DROP TABLE IF EXISTS playlists;
DROP TABLE IF EXISTS canciones;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id        SERIAL PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    creado_en TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE canciones (
    id                SERIAL PRIMARY KEY,
    titulo            VARCHAR(150) NOT NULL,
    artista           VARCHAR(150) NOT NULL,
    duracion_segundos INT          NOT NULL
);

CREATE TABLE playlists (
    id         SERIAL PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    usuario_id INT          NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE playlist_canciones (
    playlist_id INT NOT NULL REFERENCES playlists (id) ON DELETE CASCADE,
    cancion_id  INT NOT NULL REFERENCES canciones (id) ON DELETE CASCADE,
    posicion    INT NOT NULL,
    PRIMARY KEY (playlist_id, cancion_id)
);

INSERT INTO canciones (titulo, artista, duracion_segundos) VALUES
    ('Bohemian Rhapsody', 'Queen', 354),
    ('Hotel California', 'Eagles', 391),
    ('Billie Jean', 'Michael Jackson', 294),
    ('Smells Like Teen Spirit', 'Nirvana', 301),
    ('Clocks', 'Coldplay', 307),
    ('Viva la Vida', 'Coldplay', 242),
    ('Blinding Lights', 'The Weeknd', 200),
    ('De Música Ligera', 'Soda Stereo', 213),
    ('Lamento Boliviano', 'Enanitos Verdes', 222),
    ('Labios Compartidos', 'Maná', 287),
    ('Rosas', 'La Oreja de Van Gogh', 231),
    ('Querida', 'Juan Gabriel', 300),
    ('Tusa', 'Karol G, Nicki Minaj', 201),
    ('Ojitos Lindos', 'Bad Bunny, Bomba Estéreo', 258),
    ('Ella Baila Sola', 'Eslabon Armado, Peso Pluma', 165),
    ('La Bachata', 'Manuel Turizo', 162);
