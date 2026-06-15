-- =============================================================
-- DADOS DE EXEMPLO - ThomPharma
-- Execute este script para popular o banco com dados fictícios
-- Útil para testes, demonstrações e screenshots
-- =============================================================

-- limpa os dados existentes (mantém a estrutura)
TRUNCATE TABLE tb_sinonimos CASCADE;
TRUNCATE TABLE tb_lotes CASCADE;
TRUNCATE TABLE tb_materias_primas RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_clientes RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_prescritores RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_fornecedores RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_especialidades_medicas RESTART IDENTITY CASCADE;

-- =============================================================
-- ESPECIALIDADES MÉDICAS
-- =============================================================
INSERT INTO tb_especialidades_medicas (nome) VALUES
    ('Clínica Geral'),
    ('Dermatologia'),
    ('Pediatria'),
    ('Ginecologia'),
    ('Ortopedia'),
    ('Neurologia'),
    ('Psiquiatria'),
    ('Endocrinologia'),
    ('Cardiologia'),
    ('Medicina Esportiva');

-- =============================================================
-- FORNECEDORES
-- =============================================================
INSERT INTO tb_fornecedores (nome, cnpj_cpf, contato, telefone, email, cidade, uf) VALUES
    ('Fagron Brasil',           '60.665.981/0001-18', 'Comercial',      '(11) 3616-7000', 'comercial@fagron.com.br',       'São Paulo',      'SP'),
    ('Galena Química',          '49.503.786/0001-60', 'Vendas',         '(41) 3317-5100', 'vendas@galena.com.br',          'Pinhais',        'PR'),
    ('Delaware Ingredientes',   '03.560.473/0001-09', 'Atendimento',    '(11) 5694-3000', 'atendimento@delaware.com.br',   'São Paulo',      'SP'),
    ('Pharma Nostra',           '12.345.678/0001-90', 'Juliana Costa',  '(16) 3214-8800', 'juliana@pharmanostra.com.br',   'Ribeirão Preto', 'SP'),
    ('Henrifarma',              '23.456.789/0001-01', 'Ricardo Alves',  '(16) 3602-5500', 'ricardo@henrifarma.com.br',     'Ribeirão Preto', 'SP');

-- =============================================================
-- MATÉRIAS-PRIMAS
-- =============================================================
INSERT INTO tb_materias_primas
    (codigo, nome, unidade, tipo, dose_minima, dose_maxima, volume, volume_caps, peso_caps,
     saldo, rotulo, geladeira, controlado, controlada_tipo, classe_anvisa,
     observacoes, estoque_minimo, estoque_critico)
VALUES
    ('MP-0001', 'Cafeína',                 'mg', 'Sólido',    50,   200,  0, 0, 0,  250,  true,  false, false, 'Nenhuma', 'C1', 'Estimulante do SNC. Armazenar ao abrigo de umidade.',     50,  20),
    ('MP-0002', 'Vitamina C (Ácido Ascórbico)', 'mg', 'Sólido', 100, 2000, 0, 0, 0,  800,  true,  false, false, 'Nenhuma', 'C1', 'Antioxidante. Sensível à luz e calor.',                 100, 50),
    ('MP-0003', 'Cloreto de Magnésio P.A.','g',  'Sólido',    0.2,  1,    0, 0, 0,  350,  true,  false, false, 'Nenhuma', 'C1', 'Uso em reposição mineral. Controlar umidade.',           80,  30),
    ('MP-0004', 'Minoxidil',               'mg', 'Líquido',   5,    60,   0, 0, 0,  120,  true,  false, false, 'Nenhuma', 'C1', 'Para uso capilar. Fotossensível.',                        30,  10),
    ('MP-0005', 'Fluoxetina',              'mg', 'Sólido',    10,   80,   0, 0, 0,   15,  true,  false, true,  'ANVISA',  'C1', 'Antidepressivo. Controle ANVISA obrigatório.',            20,   5),
    ('MP-0006', 'Cápsula Gelatinosa 0',    'un', 'Cápsula',   0,    0,    0, 0.68, 0.1, 5000, false, false, false, 'Nenhuma', 'C1', 'Tamanho 0 para encapsulamento.',                     1000, 300),
    ('MP-0007', 'Cápsula Gelatinosa 1',    'un', 'Cápsula',   0,    0,    0, 0.5,  0.08, 4000, false, false, false, 'Nenhuma', 'C1', 'Tamanho 1 para encapsulamento.',                    1000, 300),
    ('MP-0008', 'Água Floral de Lavanda',  'ml', 'Floral',    0,    0,    0, 0, 0,   200, true,  false, false, 'Nenhuma', 'C1', 'Uso em florais de Bach e aromaterapia.',                  50,  20),
    ('MP-0009', 'Árnica Montana 6CH',      'ml', 'Homeopatia',0,    0,    0, 0, 0,   150, true,  false, false, 'Nenhuma', 'C1', 'Diluição 6CH. Armazenar longe de campos magnéticos.',    30,  10),
    ('MP-0010', 'Vidro Âmbar 30ml',        'un', 'Embalagem', 0,    0,   30, 0, 0,   300, false, false, false, 'Nenhuma', 'C1', 'Embalagem para xaropes e soluções.',                      50,  20),
    ('MP-0011', 'Melatonina',              'mg', 'Sólido',    0.5,  10,   0, 0, 0,   180, true,  false, false, 'Nenhuma', 'C1', 'Reguladora do sono. Sensível à luz.',                    40,  15),
    ('MP-0012', 'Progesterona Micronizada','mg', 'Sólido',    100,  400,  0, 0, 0,    8,  true,  true,  false, 'Nenhuma', 'C1', 'Manter refrigerado entre 2°C e 8°C.',                   20,   5),
    ('MP-0013', 'DHEA',                    'mg', 'Sólido',    25,   100,  0, 0, 0,   60,  true,  false, false, 'Nenhuma', 'C1', 'Hormônio adrenal. Uso em terapia de reposição.',          20,  10),
    ('MP-0014', 'Base Creme Hidratante',   'g',  'Excipiente',0,    0,    0, 0, 0,  1200, false, false, false, 'Nenhuma', 'C1', 'Base para cremes dermatológicos.',                       200, 100),
    ('MP-0015', 'Álcool Etílico 70%',      'ml', 'Líquido',   0,    0,    0, 0, 0,   500, false, false, false, 'Nenhuma', 'C1', 'Solvente e veículo para soluções.',                     100,  50);

-- =============================================================
-- LOTES
-- =============================================================
INSERT INTO tb_lotes
    (id_materia_prima, nome_lote, custo, fator, fator2, quantidade, saldo,
     densidade, validade, endereco_uso, endereco_estoque, id_fornecedor, data_cadastro)
VALUES
    (1,  'CAF-2024-001', 45.00,  1.0, 1.0,  500,  250,  1.0,  '2026-03-01', 'A1-P2', 'EST-01', 1, NOW()),
    (2,  'VTC-2024-015', 38.50,  1.0, 1.0, 1000,  800,  1.0,  '2026-06-15', 'A2-P1', 'EST-02', 3, NOW()),
    (3,  'MG-2024-008',  22.00,  1.0, 1.0,  500,  350,  1.0,  '2027-01-10', 'B1-P3', 'EST-03', 2, NOW()),
    (4,  'MNX-2024-003', 85.00,  1.0, 1.0,  200,  120,  1.04, '2025-11-20', 'A3-P1', 'EST-04', 4, NOW()),
    (5,  'FLX-2024-011', 95.00,  1.0, 1.0,   50,   15,  1.0,  '2026-04-30', 'C1-P1', 'EST-05', 1, NOW()),
    (6,  'CAP0-2024-02', 18.00,  1.0, 1.0, 5000, 5000,  1.0,  '2028-12-01', 'D1-P1', 'EST-06', 5, NOW()),
    (7,  'CAP1-2024-02', 16.50,  1.0, 1.0, 4000, 4000,  1.0,  '2028-12-01', 'D1-P2', 'EST-06', 5, NOW()),
    (8,  'AFL-2024-005', 32.00,  1.0, 1.0,  300,  200,  1.0,  '2025-09-15', 'B2-P2', 'EST-07', 3, NOW()),
    (9,  'ARM-2024-001', 28.00,  1.0, 1.0,  200,  150,  1.0,  '2026-08-20', 'B3-P1', 'EST-08', 2, NOW()),
    (10, 'VID30-2024-1', 95.00,  1.0, 1.0,  500,  300,  1.0,  '2030-01-01', 'E1-P1', 'EST-09', 5, NOW()),
    (11, 'MEL-2024-007', 55.00,  1.0, 1.0,  300,  180,  1.0,  '2026-05-10', 'A4-P1', 'EST-02', 1, NOW()),
    (12, 'PRG-2024-002', 120.00, 1.0, 1.0,   50,    8,  1.0,  '2025-08-01', 'C2-P1', 'EST-10', 4, NOW()),
    (13, 'DHE-2024-004', 78.00,  1.0, 1.0,  100,   60,  1.0,  '2026-02-28', 'A5-P1', 'EST-02', 3, NOW()),
    (14, 'BCH-2024-009', 42.00,  1.0, 1.0, 2000, 1200,  1.0,  '2027-03-15', 'F1-P1', 'EST-11', 2, NOW()),
    (15, 'ALC-2024-012', 18.00,  1.0, 1.0, 1000,  500,  0.87, '2026-01-01', 'F2-P1', 'EST-12', 5, NOW());

-- =============================================================
-- SINÔNIMOS DE MATÉRIAS-PRIMAS
-- =============================================================
INSERT INTO tb_sinonimos (id_materia_prima, sinonimo) VALUES
    (1,  '1,3,7-trimetilxantina'),
    (1,  'Cafeína anidra'),
    (2,  'Vitamina C'),
    (2,  'Ácido L-ascórbico'),
    (2,  'Ascorbato'),
    (3,  'MgCl2'),
    (3,  'Magnésio cloreto'),
    (4,  '2,4-diamino-6-piperidinopirimidinazina'),
    (11, 'N-acetil-5-metoxitriptamina'),
    (11, 'MLT');

-- =============================================================
-- CLIENTES
-- =============================================================
INSERT INTO tb_clientes
    (nome, cpf, telefone, telefone2, email, cep, endereco, bairro, cidade, uf, desconto, observacoes)
VALUES
    ('Ana Beatriz Oliveira',    '234.567.890-12', '(16) 99812-3456', '',               'ana.oliveira@email.com',    '14020-010', 'Rua Lafaiete, 320',           'Centro',          'Ribeirão Preto', 'SP', 5,  'Cliente frequente. Alergia a corantes artificiais.'),
    ('Carlos Eduardo Mendes',   '345.678.901-23', '(16) 99723-4567', '(16) 3214-5678', 'carlosmendes@email.com',    '14025-050', 'Av. Presidente Vargas, 1100', 'Jardim Paulista',  'Ribeirão Preto', 'SP', 0,  ''),
    ('Mariana Souza Ferreira',  '456.789.012-34', '(16) 99634-5678', '',               'mariana.ferreira@email.com','14015-130', 'Rua Tibiriçá, 550',           'Higienópolis',    'Ribeirão Preto', 'SP', 10, 'Desconto especial autorizado pelo farmacêutico.'),
    ('Roberto Lima Costa',      '567.890.123-45', '(16) 99545-6789', '',               'robertocosta@email.com',    '14032-080', 'Rua Djalma Dutra, 88',        'Jardim Sumaré',   'Ribeirão Preto', 'SP', 0,  ''),
    ('Fernanda Alves Rodrigues', '678.901.234-56', '(16) 99456-7890', '(16) 3512-9900', 'fernanda.alves@email.com',  '14090-010', 'Av. das Esmeraldas, 775',     'Jardim América',  'Ribeirão Preto', 'SP', 5,  'Prefere contato por WhatsApp.'),
    ('Pedro Henrique Barros',   '789.012.345-67', '(16) 99367-8901', '',               'pedrobarros@email.com',     '14050-070', 'Rua Floriano Peixoto, 210',   'Centro',          'Ribeirão Preto', 'SP', 0,  ''),
    ('Luciana Castro Nunes',    '890.123.456-78', '(16) 99278-9012', '',               'luciananunes@email.com',    '14020-060', 'Rua General Osório, 95',      'Centro',          'Ribeirão Preto', 'SP', 0,  'Paciente crônica. Fórmulas mensais recorrentes.'),
    ('Thiago Martins Pereira',  '901.234.567-89', '(16) 99189-0123', '',               'thiagopereira@email.com',   '14060-120', 'Rua São Sebastião, 430',      'Vila Seixas',     'Ribeirão Preto', 'SP', 15, 'Desconto especial. Familiar de funcionário.');

-- =============================================================
-- PRESCRITORES
-- =============================================================
INSERT INTO tb_prescritores (nome, tipo_registro, numero_registro, telefone, email, observacoes)
VALUES
    ('Dra. Amanda Cristina Moura',   'CRM', 'CRM-SP 145230', '(16) 3602-4400', 'amanda.moura@clinica.com',    'Dermatologista. Prescreve muito minoxidil e fórmulas capilares.'),
    ('Dr. Bruno Figueiredo Leal',    'CRM', 'CRM-SP 98741',  '(16) 3512-7700', 'brunoleal@medico.com',        'Clínico geral. Atende no posto de saúde Quinta da Boa Vista.'),
    ('Dra. Carla Nakamura',          'CRM', 'CRM-SP 201345', '(16) 99330-1122','carla.nakamura@email.com',    'Endocrinologista. Especialista em reposição hormonal.'),
    ('Dr. Daniel Freitas Souza',     'CRM', 'CRM-SP 77821',  '(16) 3216-9900', 'danielfreitas@clinica.com',   'Psiquiatra. Prescreve manipulados psicotrópicos.'),
    ('Dra. Elisa Monteiro Borges',   'CRO', 'CRO-SP 54321',  '(16) 3411-5500', 'elisa.borges@odonto.com',     'Dentista. Fórmulas para dentística e periodontia.'),
    ('Dr. Fábio Augusto Ramos',      'CRV', 'CRMV-SP 32456', '(16) 3602-8800', 'fabioramos@vet.com',          'Veterinário. Atende cães e gatos de pequeno porte.'),
    ('Dra. Gabriela Pinto Vieira',   'CRP', 'CRP-06/78945',  '(16) 99441-2233','gabipinto@psi.com',           'Psicóloga. Indica florais e fitoterápicos.'),
    ('Dr. Henrique Takahashi',       'CRM', 'CRM-SP 312560', '(16) 3214-3300', 'henrique.t@hospital.com',     'Pediatra. Fórmulas pediátricas com sabor.');

-- =============================================================
-- FIM DO SCRIPT
-- (usuarios nao sao recriados pois ja existem no banco)
-- =============================================================
SELECT 'Dados de exemplo inseridos com sucesso!' AS resultado;
