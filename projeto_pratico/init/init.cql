CREATE KEYSPACE IF NOT EXISTS my_keyspace
WITH REPLICATION = {
  'class': 'NetworkTopologyStrategy',
  'datacenter1': 1
} AND tablets = {'enabled': false};

USE my_keyspace;

CREATE TABLE IF NOT EXISTS itens (
    name TEXT PRIMARY KEY,
    description TEXT,
    quantity_stock INT
);

CREATE TYPE IF NOT EXISTS item_udt (
    name TEXT,
    description TEXT,
    quantity_stock INT
);

CREATE TABLE IF NOT EXISTS responses (
    orderID UUID PRIMARY KEY,
    timestamp timestamp,
    itens_disponiveis list<frozen<item_udt>>,
    itens_indisponiveis list<frozen<item_udt>>,
    itens_inexistentes list<text>,
    order_status TEXT
);

INSERT INTO itens (name, description, quantity_stock) VALUES ('Teclado', 'Teclado mecânico com RGB', 150);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Mouse', 'Mouse gamer com DPI ajustável', 300);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Monitor', 'Monitor 24" Full HD', 75);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Notebook', 'Notebook com 16GB RAM e SSD 512GB', 40);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Headset', 'Headset com cancelamento de ruído', 120);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Webcam', 'Webcam 1080p com microfone', 80);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Microfone', 'Microfone condensador USB', 90);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Mousepad', 'Mousepad grande para gamer', 200);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Cadeira Gamer', 'Cadeira ergonômica preta e vermelha', 30);
INSERT INTO itens (name, description, quantity_stock) VALUES ('SSD', 'SSD 1TB NVMe', 60);
INSERT INTO itens (name, description, quantity_stock) VALUES ('HD', 'HD 2TB SATA III', 50);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Placa de Vídeo', 'RTX 4060 Ti 8GB', 25);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Processador', 'Ryzen 7 5800X', 35);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Placa-mãe', 'B550M com WiFi', 40);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Memória RAM', '2x8GB DDR4 3200MHz', 100);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Fonte', 'Fonte 650W 80 Plus Bronze', 70);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Gabinete', 'Gabinete com lateral em vidro', 45);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Cooler', 'Cooler RGB para CPU', 85);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Notebook Gamer', 'Notebook com RTX 3050 e i7', 20);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Carregador', 'Carregador universal USB-C', 300);
INSERT INTO itens (name, description, quantity_stock) VALUES ('HD Externo', 'HD externo 1TB USB 3.0', 60);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Pendrive', 'Pendrive 64GB USB 3.1', 400);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Adaptador HDMI', 'Adaptador HDMI para VGA', 100);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Adaptador USB', 'Adaptador USB para Ethernet', 120);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Suporte Monitor', 'Suporte articulado para monitor', 50);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Controle Xbox', 'Controle Xbox Series sem fio', 70);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Joystick', 'Joystick USB para PC', 40);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Projetor', 'Projetor LED 720p', 15);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Hub USB', 'Hub USB 4 portas 3.0', 100);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Tablet', 'Tablet Android 64GB', 22);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Celular', 'Smartphone com 128GB', 50);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Relógio Inteligente', 'Smartwatch com monitor cardíaco', 60);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Kindle', 'Leitor digital 8GB', 30);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Cabo HDMI', 'Cabo HDMI 2 metros', 150);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Cabo USB', 'Cabo USB-C 1 metro', 200);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Tripé', 'Tripé para celular e câmera', 40);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Estabilizador', 'Estabilizador de energia 500VA', 25);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Nobreak', 'Nobreak 1200VA com bateria', 15);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Scanner', 'Scanner de documentos A4', 20);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Impressora', 'Impressora multifuncional Wi-Fi', 35);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Câmera IP', 'Câmera de segurança Wi-Fi', 60);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Switch de Rede', 'Switch 8 portas Gigabit', 30);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Roteador', 'Roteador Dual Band 1200Mbps', 40);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Repetidor Wi-Fi', 'Repetidor de sinal Wi-Fi', 50);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Alexa', 'Assistente virtual Echo Dot', 70);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Lâmpada Smart', 'Lâmpada inteligente RGB', 150);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Tomada Inteligente', 'Tomada smart com Wi-Fi', 100);
INSERT INTO itens (name, description, quantity_stock) VALUES ('Controle Remoto IR', 'Controle universal Wi-Fi', 80);
