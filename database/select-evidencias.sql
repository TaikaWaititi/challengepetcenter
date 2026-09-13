SET PAGESIZE 100
SET LINESIZE 220
SET FEEDBACK ON

PROMPT USERS
SELECT id, nome, email, tipo_usuario, ativo
FROM users
ORDER BY id DESC
FETCH FIRST 10 ROWS ONLY;

PROMPT PETS
SELECT id, user_id, nome, especie, raca
FROM pets
ORDER BY id DESC
FETCH FIRST 10 ROWS ONLY;

PROMPT DIARIO_ENTRADAS
SELECT id, pet_id, data, humor_geral, status
FROM diario_entradas
ORDER BY id DESC
FETCH FIRST 10 ROWS ONLY;

PROMPT REGISTROS
SELECT id, entrada_id, tipo, subtipo, valor, unidade
FROM registros
ORDER BY id DESC
FETCH FIRST 10 ROWS ONLY;

PROMPT SOLICITACOES
SELECT id, pet_id, tutor_id, veterinario_id, status
FROM solicitacoes
ORDER BY id DESC
FETCH FIRST 10 ROWS ONLY;

PROMPT ALERTAS
SELECT id, pet_id, veterinario_id, tipo, titulo, ativo
FROM alertas
ORDER BY id DESC
FETCH FIRST 10 ROWS ONLY;

EXIT
