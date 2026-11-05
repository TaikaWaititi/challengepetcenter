# Verificação prática das instruções do README

Data: 04/11/2026, horário de Brasília. Escopo: inicialização pelo Docker Compose, testes Maven, acesso HTTP e roteiro de CRUD/SELECT documentados no README.

## Revisão de documentação

- README revisado em UTF-8, incluindo acentuação de Júlia, José e contêiner/contêineres.
- Comandos, identificadores, URLs e nomes técnicos mantidos.
- Links locais condicionados à inicialização da API; Azure identificada como destino da entrega.

## Comandos executados e resultados

| Instrução | Resultado |
|---|---|
| Reutilizar .env existente | Arquivo reutilizado sem revelar seus valores |
| Proteção do gerador new-local-env.ps1 | Recusou sobrescrever o .env; SHA-256 do arquivo permaneceu igual |
| docker compose up -d --build | Imagem compilada; Oracle e API iniciados com o volume existente |
| docker compose ps | Oracle saudável; API com porta 8080 publicada |
| docker compose logs --tail 100 app | Spring Boot iniciado com Tomcat na porta 8080 |
| http://localhost:8080/ | HTTP 200 |
| http://localhost:8080/swagger-ui.html | HTTP 200 após redirecionamento para Swagger UI |
| http://localhost:8080/api-docs | HTTP 200 |
| .\mvnw.cmd -B -ntp clean verify | BUILD SUCCESS: oito testes unitários, sem falhas |
| bash scripts/ci/test-oracle.sh | BUILD SUCCESS: oito testes unitários e um teste de integração Oracle; contêiner temporário removido pelo script |
| demo-crud-sprint4.ps1 com BaseUrl local | Cadastro, login e CRUD de pet concluídos; usuário de demonstração removido |
| SELECTs locais após cada operação | Inserção e consulta confirmadas; atualização do nome confirmada; exclusão retornou zero linhas |

Java do computador: 21.0.6. Os testes foram executados a partir da raiz do repositório. O teste de integração usa Oracle isolado e não altera o banco da entrega.

## Verificação na Azure

- [CI 12](https://dev.azure.com/Sprint4fiap/65b112f9-8fca-499e-93fc-86127fc2f2f6/_build/results?buildId=12) e [CD 13](https://dev.azure.com/Sprint4fiap/65b112f9-8fca-499e-93fc-86127fc2f2f6/_build/results?buildId=13) concluídos com Succeeded antes da demonstração completa.
- API, Swagger e OpenAPI responderam HTTP 200 tanto no HTTPS do Web App quanto na URL anterior do ACI. O OpenAPI gerou o endereço correto para cada acesso.
- O roteiro demo-crud-sprint4.ps1 foi executado em modo interativo contra https://app-petcenter-rm564939-sprint4.azurewebsites.net, usando o limite ajustado de 120 segundos.
- Cadastro e login foram concluídos. O pet exclusivo de demonstração teve ID 24; o usuário teve ID 26.
- select-oracle.ps1 foi executado após cada etapa, com SELECT no Oracle da VM Azure: nome original na inserção e consulta; novo nome na atualização; zero linhas após exclusão.
- O script concluiu com sucesso e removeu o usuário de demonstração. O registro do teste local (pet ID 4) e seu usuário também foram removidos.
- Não foi necessário alterar código de negócio, configuração de banco ou credenciais para executar o Compose e os testes. O ajuste ficou no tempo de espera e na identificação das chamadas do roteiro de demonstração.

## Ajuste encontrado durante a verificação

Uma requisição do roteiro de demonstração excedeu o limite anterior de 30 segundos logo após uma publicação na Azure. O script passou a aceitar o parâmetro TimeoutSec (1 a 600 segundos, padrão 120) e a mostrar o método/caminho da chamada em execução. O README orienta aguardar o CD concluir com Succeeded e a aplicação responder antes do CRUD.

Não foi adicionado retry automático de requisições de escrita. O usuário de teste deixado pela tentativa interrompida foi identificado e removido por ID, e-mail exclusivo, nome, horário recente e ausência de pets vinculados. A demonstração completa foi repetida para validar o roteiro ajustado.

## Estado e limites

- Nenhum volume existente foi apagado e nenhuma senha existente foi alterada.
- O ambiente local iniciado foi mantido disponível para acesso pelo localhost.
- Esta verificação cobre o fluxo descrito acima; o vídeo narrado pelo aluno e o PDF final continuam sendo entregas do grupo.
- A geração inicial de um novo .env não foi executada neste ambiente, pois o arquivo e o volume Oracle já existiam; foi seguido o caminho de reutilização previsto no README.
