> Registro historico. O estado atual, acessos e pipelines estao em [Entrega 4 DevOps](ENTREGA4_DEVOPS.md). As pendencias abaixo retratam a data de cada etapa.

# Alteracoes e justificativas - DevOps Sprint 4

## Configuracao atual na organizacao Sprint4fiap

Esta secao substitui as pendencias das etapas historicas abaixo. Consulte [o guia ativo](ENTREGA4_DEVOPS.md) para acessos, evidencias e roteiro.

| Alteracao | Razao e requisito da Entrega 4 | Evidencia / limite |
|---|---|---|
| Projeto privado Sprint 4 – Azure DevOps, Git, Scrum, descricao com professor e equipe | Nome, descricao e configuracao exigidos na p.49, item 4 | Projeto em Sprint4fiap; projeto anterior preservado |
| Professor Basic e Project Administrators | Acesso do professor, p.50, item 5 | pf2128@fiap.com.br cadastrado e autorizado |
| `master`, PetCenter-CI, Java 21/Maven, testes JUnit/Mockito e Oracle isolado | Build e testes automaticos a cada mudanca, p.50, item 6.a e regras I/II/VI | Nove testes aprovados; resultados publicados na aba Tests |
| Pipeline Artifact `petcenter`, manifestos e SHA-256 | Artefato publicado no Azure DevOps, p.50, regra V | JAR do proprio CI; CD verifica o checksum |
| PetCenter-CD, `deploy-webapp.sh`, Azure Web App | CD apos CI/novo artefato, p.50, item 6.b e regras III/VII | Publica o JAR testado; verifica Build ID e OpenAPI |
| Identidade federada, Website Contributor so no Web App, variable group secreto | Credenciais protegidas, p.50, regra IV; evita penalidade p.54 | CI nao recebe segredos da aplicacao; CD nao pode recriar a VM |
| Oracle Free em VM Azure, disco persistente, VNet e NSG | Banco em nuvem, p.48-49, item 3; CRUD/SELECT p.52 | Data Pump com hash e contagens das 12 tabelas; Oracle independente do CD |
| Proxy Nginx no ACI e imagem no ACR | Compatibilidade com a URL anterior durante a adequacao | Decisao tecnica, sem novo requisito expresso no PDF; HTTPS ate o Web App |
| `HomeController.java`, APP_BUILD_ID e smoke HTTP | Evidenciar mudanca de codigo publicada, p.51-52 | Texto publico identifica o build implantado |
| `azure/agent`, `start-agent.ps1`, pool PetCenter-Linux | Disponibilizar agente Linux para executar as pipelines, p.50 | Escolha de implementacao; computador e Docker Desktop devem ficar ligados |
| `select-oracle.ps1` e roteiro CRUD | Exibir SELECT no banco em nuvem em cada operacao, p.52 | Run Command/SQLPlus sem abrir a porta Oracle para internet |
| README, guia e diagrama numerado | Solucao, stack, componentes, personas e CI/CD, p.49 itens 1/2; diagrama p.52 | Diagrama atualizado para infraestrutura efetiva |
| Roteiro de video e orientacao do PDF | Video p.51-52; PDF com equipe e links p.48 | Gravacao narrada pelo aluno e PDF final ainda dependem do grupo |

## Historico da preparacao inicial

Base: paginas fisicas 48-55 do PDF do Challenge 2026. Branch de publicacao: `prep/devops-sprint4`, a partir de `dc7c9c9`. Publicacao no GitHub autorizada pelo usuario em 04/11/2026. Os registros abaixo que mencionam ausencia de commit descrevem as etapas anteriores a essa autorizacao.

| Alteracao / arquivos | Razao | Etapa do PDF atendida | Limite da preparacao |
|---|---|---|---|
| `azure-pipelines.yml` | Disparar CI a cada mudanca em master, compilar Java 21, testar e publicar artefato | p.50, item 6.a, regras I, II, V e VI | YAML pronto; gatilho depende de publicar branch/configurar Azure DevOps |
| `azure-pipelines-cd.yml` | Disparar CD apos conclusao do CI que publicou o artefato; baixar o artefato do mesmo run | p.50, item 6.b, regras III e VII | Conexao, variable group, environment e execucao na nuvem pendentes |
| `scripts/ci/test-oracle.sh` | Testar contra Oracle isolado com schema real, sem depender de banco de producao ou H2 | p.50, item 6.a/VI; p.49, item 3 como alinhamento de tecnologia | Banco de testes descartavel nao substitui o banco em nuvem |
| `PetServiceTest.java`, `JwtServiceTest.java`; remocao do antigo `contextLoads` | Validar CRUD, isolamento de tutor, restricao de exclusao e assinatura JWT com assercoes de comportamento | p.50, item 6.a/VI; p.53, ausencia de testes | Testes unitarios locais |
| `OracleCrudIT.java` e perfil `oracle-it` no `pom.xml` | Subir aplicacao real, cadastrar, autenticar, executar CRUD HTTP e conferir SELECTs no Oracle | p.50, item 6.a/VI; apoia p.52, fluxo do video | Integracao local; nao substitui evidencias na nuvem |
| `azure/Dockerfile.runtime` | Implantar o JAR testado sem uma segunda compilacao; executar sem root | p.50, item 6.V/VII | Nao-root e melhoria reaproveitada da arquitetura anterior, nao nova obrigacao expressa da Sprint 4 |
| `scripts/ci/deploy-aci.sh` | Verificar checksum, usar tag do build, publicar no ACR e atualizar ACI automaticamente | p.50, item 6.b/III/VII; p.52, recursos atualizados | Deploy real nao executado |
| Bloqueio de grupo legado e exigencia de Oracle separado no deploy | Evitar reiniciar o Oracle junto com cada mudanca da aplicacao e comprometer a persistencia | Apoia p.49, item 3, e p.52, evidencias de persistencia | Decisao de implementacao; endereco e infraestrutura do Oracle pendentes |
| `scripts/ci/smoke.sh` | Falhar o CD se API/OpenAPI nao responderem apos deploy | p.52, acesso a aplicacao atualizada | Verificacao HTTP, nao prova completa de CRUD |
| `application.properties`, `.env.example`, Compose, `.gitignore`, `.dockerignore`, `new-local-env.ps1` | Remover defaults sensiveis e separar segredos de codigo, contexto Docker e versionamento | p.50, item 6.IV; p.54, variaveis desprotegidas | Historico nao reescrito; credenciais antigas precisam ser trocadas no ambiente |
| `database/init/01-schema.sh`, SQL canonico e `database/Dockerfile` | Inicializar schema sem CONNECT com senha fixa; manter terminadores PL/SQL corretos e uma fonte de SQL | p.50, item 6.IV; apoia testes e p.52, CRUD/SELECT | Inicializacao apenas de banco novo; sem migracao automatica de banco existente |
| `scripts/azure/prepare-sprint4.sh` | Preparar ACR e identidade para pull sem senha de admin no CD | Apoia p.50, item 6.IV/VII | Script manual nao executado; Azure CLI e escolha tecnica, nao exigencia expressa da Sprint 4 |
| `docs/arquitetura-sprint4.svg`, README | Expor stack, personas, conexoes e fluxo CI/CD numerado | p.49, itens 1 e 2; p.52, 20 pontos do diagrama | Arquitetura proposta; atualizar nome/destino real do banco antes de gravar |
| `scripts/demo-crud-sprint4.ps1` e wrappers dos scripts anteriores | Demonstrar CRUD com pausas e SELECT por ID para evidenciar cada operacao; evitar endpoint remoto antigo implicito | p.52, fluxo do video, CRUD e persistencia | SELECT manual precisa ser executado e filmado |
| README, `docs/SPRINT4.md`, documentos locais e aviso no guia historico Azure | Centralizar passos, configuracao do projeto, Basic do professor, gravacao e PDF de links | p.48, entrega obrigatoria; p.49, item 4; p.50, item 5; p.51-52, item 8 | Convites, turmas, professor, video e PDF final pendentes |
| `.gitattributes` | Preservar LF nos scripts usados pelo agente Linux e containers | Apoia execucao das pipelines p.50 | Ajuste tecnico de portabilidade |

## Decisoes que precisam ser mantidas claras

- O repositorio remoto esta em `main`. O YAML exige `master` como no PDF; nenhuma branch remota foi criada ou alterada.
- A arquitetura ACR + ACI da aplicacao foi preservada como ponto de partida. O banco precisa de ciclo de vida separado; o alvo exato aguarda definicao.
- A preparacao nao comprova CI/CD remoto funcional. Essa comprovacao so existe depois de configurar a conta, executar as pipelines e testar a aplicacao na Azure.
- O PDF final de submissao nao foi gerado: faltam turmas, professor/projeto e links finais. Nao confundir este relatorio de revisao com o PDF restrito de entrega.

## Validacao

Validacao local: 8 testes unitarios e 1 teste de integracao com Oracle real aprovados; JAR e imagens construidos; runtime executado como UID 1001 (spring); Bash, PowerShell, YAML, Compose e whitespace verificados. Banco descartavel removido automaticamente ao fim do teste. Execucao real das pipelines e deploy na nuvem ainda nao realizados. Nao considerar arquivo de pipeline ou diagrama, isoladamente, como evidencia de execucao na nuvem.

## Atualizacao apos teste de nuvem em 04/11/2026

A pedido do usuario, foi feita publicacao manual da API e do Oracle em um novo ACI descartavel. Cadastro, login e CRUD de pets e diarios passaram, com SELECTs no banco da Azure. O relato completo esta em `docs/TESTE_NUVEM.md`, relacionado as paginas 48, 49 (item 3) e 52 do PDF. O README e o guia da Sprint 4 foram atualizados para distinguir esse teste aprovado da validacao das pipelines, ainda pendente. Os scripts de CI/CD propostos nao foram executados nem modificados por este teste. Sem commit ou push.

## Nomes definitivos por solicitacao do usuario

O grupo passou a `rg-petcenter-rm564939-sprint4` e o ACI a `aci-petcenter-rm564939-sprint4`, com backup e restauracao do Oracle. O ACR foi movido para o novo grupo. O registro do processo esta em `docs/MIGRACAO_NOMES.md`; o README foi atualizado. Essa mudanca de nomenclatura atende a preferencia do usuario, nao representa um novo requisito do PDF. Sem commit/push.
