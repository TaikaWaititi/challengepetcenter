# Sprint 4 - configuracao, operacao e entrega

Referencia: PDF do Challenge 2026, paginas fisicas 48 a 55, secao DEVOPS TOOLS & CLOUD COMPUTING, ENTREGA - 4o SPRINT. Os itens abaixo se referem a essa entrega. Criacao por CLI e CRUD de duas tabelas aparecem na Sprint 3 e nao foram tratados como novas obrigacoes textuais da Sprint 4.

## 1. Antes de ativar as pipelines

A publicacao desta versao no GitHub foi autorizada; os arquivos de CI/CD estao na branch `prep/devops-sprint4`. Houve uma publicacao manual em um ambiente ACI descartavel, com CRUD na nuvem aprovado; consulte [TESTE_NUVEM.md](TESTE_NUVEM.md). Nao houve criacao/configuracao do projeto Azure DevOps, convite ao professor ou execucao das pipelines. A configuracao das pipelines continua sendo uma etapa posterior.

A branch padrao do GitHub e `main`; esta versao foi preparada em `prep/devops-sprint4`. O PDF exige `master`: disponibilize os arquivos revisados nessa branch antes de ativar as duas pipelines. Nao renomear/excluir `main` automaticamente. Configure `Default branch for manual and scheduled builds` como `refs/heads/master` nas duas pipelines e confirme que nao ha override de gatilhos pela interface.

Defina o Oracle em nuvem separado do grupo ACI da API. Pode ser Oracle da FIAP ou um Oracle em container em infraestrutura persistente, conforme o PDF. Preencha URL JDBC, usuario e senha e valide conectividade a partir da Azure. `database/script_bd.sql` e o schema canonico; executar apenas em schema novo de testes/entrega, apos revisar objetos, triggers e massa de dados. O CD nao cria tabelas nem reinicializa banco. Para schema ja existente, verificar compatibilidade sem executar o DDL novamente. Backup e manutencao do banco ficam fora do deploy da API.

O grupo ACI antigo mistura app e Oracle sem volume persistente declarado. Atualizar esse grupo reinicia todos os containers e nao garante preservacao dos dados do Oracle. Por isso, o novo CD so aceita um grupo exclusivo da API e rejeita o grupo legado. O template `azure/container-group.template.yml` e historico e nao e consumido pelas novas pipelines.

## 2. Criar/configurar projeto Azure DevOps (PDF p.49, item 4; p.50, item 5)

- Nome: `Sprint 4 – Azure DevOps`.
- Descricao: `Projeto para entrega da Sprint 4 do professor <nome do professor>`, seguida dos integrantes com RM, nome e turma.
- Visibility: Private; Version control: Git; Work item process: Scrum ou Agile.
- Convidar o professor com nivel **Basic**, com permissoes para visualizar projeto, pipelines, execucoes e artefatos. A p.55 ilustra tambem Project administrators; validar o acesso efetivo conforme orientacao do professor.
- Autorizar a integracao do Azure Pipelines com este repositorio GitHub. O projeto Git do Azure DevOps nao exige duplicar o codigo em Azure Repos.

## 3. Infraestrutura Azure e autenticacao

Escolha nomes da Sprint 4 diferentes dos recursos antigos. Nao executar os exemplos com placeholders. Em Bash/Cloud Shell autenticado na assinatura correta:

```bash
export RESOURCE_GROUP='<grupo-sprint4>'
export AZURE_LOCATION='<regiao-com-disponibilidade-ACI>'
export ACR_NAME='<nome-globalmente-unico>'
export ACI_IDENTITY_NAME='<identidade-pull>'
bash scripts/azure/prepare-sprint4.sh
```

O script cria grupo, ACR Basic sem conta admin, identidade gerenciada e papel AcrPull no registro. Requer permissao para criar recursos e atribuir papeis, e providers Microsoft.ContainerRegistry, Microsoft.ContainerInstance e Microsoft.ManagedIdentity registrados. O comando cria recursos cobraveis; ainda nao foi executado nesta preparacao.

Criar Service connection do tipo Azure Resource Manager chamada **`sc-petcenter-azure`**, preferencialmente por Workload Identity Federation. Escopo: grupo da Sprint 4. Para este roteiro, conceder a identidade da conexao Contributor no grupo e AcrPush no ACR, permitindo administrar ACI e atribuir a identidade ja criada. A atribuicao do AcrPull pelo bootstrap exige autorizacao adicional de RBAC na conta que o executa. Aguardar propagacao das permissoes e autorizar somente a pipeline CD a usar a conexao.

Nao habilitar conta admin do ACR para o novo CD. O agente faz push pela service connection; o ACI faz pull pela identidade gerenciada indicada em `ACI_IDENTITY_ID`. O roteiro pressupoe ACR no modo de permissoes RBAC convencional; se usar ABAC, revisar os papeis de repositorio antes de ativar.

## 4. Variable group `petcenter-sprint4`

Criar em Pipelines > Library e autorizar somente o CD. Marcar como secret os valores indicados. Nao salvar valores reais no Git, screenshots ou PDF.

| Variavel | Valor esperado | Secret |
|---|---|---|
| RESOURCE_GROUP | Grupo exclusivo da Sprint 4 | nao |
| AZURE_LOCATION | Regiao desse grupo/ACI | nao |
| ACR_NAME | Nome do registro, sem `.azurecr.io` | nao |
| ACI_NAME | Novo grupo exclusivo da API, por exemplo `aci-petcenter-sprint4-app` | nao |
| DNS_LABEL | Rotulo DNS unico na regiao | nao |
| ACI_IDENTITY_ID | Resource ID da identidade com AcrPull | nao |
| SPRING_DATASOURCE_URL | JDBC Oracle em nuvem acessivel pelo ACI | sim |
| SPRING_DATASOURCE_USERNAME | Usuario do schema da aplicacao | sim |
| SPRING_DATASOURCE_PASSWORD | Senha desse usuario | sim |
| JWT_SECRET | Chave Base64 aleatoria com ao menos 32 bytes decodificados | sim |

Para gerar uma chave no terminal Bash, sem adicionar ao codigo: `openssl rand -base64 48`. Guarde-a diretamente como segredo no Azure DevOps. Credenciais padrao presentes no historico do repositorio devem ser substituidas no ambiente real; remover valores do arquivo atual nao remove o historico.

## 5. Criar pipelines

1. Criar pipeline GitHub com `azure-pipelines.yml`; nomear exatamente **PetCenter-CI**.
2. Criar segunda pipeline com `azure-pipelines-cd.yml`; pode ser nomeada PetCenter-CD. O campo `resources.pipelines.source` referencia o nome PetCenter-CI, nao o nome do YAML.
3. Configurar `refs/heads/master` como branch padrao das duas. Autorizar o CD a baixar os artefatos do CI.
4. Criar Environment **petcenter-sprint4** antes da primeira execucao e autorizar o CD. Para a demonstracao automatica, nao adicionar aprovacao manual ao ambiente.
5. Disponibilizar agentes Linux com Java 21 e Docker; o YAML usa `ubuntu-24.04` e `JAVA_HOME_21_X64`. Confirmar capacidade/parallel jobs da organizacao.
6. Autorizar previamente service connection, variable group e environment. Fazer um ensaio; a gravacao precisa mostrar uma execucao iniciada automaticamente apos alteracao de codigo.

CI: checkout -> Java 21 -> Oracle descartavel -> Maven `clean verify -Poracle-it` -> resultados JUnit -> JAR e scripts -> PublishPipelineArtifact. Falha em teste impede publicacao do artefato e conclusao bem-sucedida do CI.

CD: gatilho por conclusao bem-sucedida do CI em `master` -> download do artefato daquele run -> checksum do JAR -> Docker build sem recompilar Java -> push com tag Build ID -> ACI exclusivo da API -> smoke HTTP. Nao usa `latest`. O arquivo `commit.txt` vincula o artefato ao codigo.

O Oracle de testes usa credenciais geradas em cada execucao e porta disponivel no host. Nao usa os segredos do variable group nem altera o banco de nuvem. O teste automatizado nao substitui o video e os SELECTs na nuvem.

## 6. Roteiro do video (PDF p.51-52, item 8)

1. Preparar IDE, GitHub, Azure DevOps, Portal Azure e cliente SQL. Imagem minima 720p, audio claro, explicacao pela voz do aluno e sem legendas.
2. Explicar aplicacao, stack, diagrama, configuracao do projeto e jobs/tasks das duas pipelines.
3. Alterar texto retornado por `src/main/java/com/fiap/challengepetcenter/controller/HomeController.java`, como um identificador da demonstracao. Alterar codigo, nao somente README.
4. Apos a aprovacao das alteracoes desta preparacao, demonstrar commit e push dessa alteracao para `master`.
5. Mostrar o disparo automatico do CI, abrir os detalhes e explicar build, testes e publicacao. Mostrar aba Tests e artefato petcenter com app.jar.
6. Mostrar o disparo automatico do CD e explicar download, checksum, imagem, push e deploy. Mostrar tag numerada no ACR e recurso ACI atualizado no Portal.
7. Abrir a URL publica e mostrar o novo texto. Abrir Swagger.
8. Rodar `scripts/demo-crud-sprint4.ps1 -BaseUrl 'http://<fqdn>:8080'`. A cada pausa, executar o SELECT exibido no cliente SQL conectado ao Oracle de nuvem. Mostrar insercao, consulta, atualizacao e ausencia da linha apos exclusao, explicando a persistencia.
9. Conferir acesso do professor, legibilidade e audio antes de entregar. Nao filmar valores de variaveis secretas, credenciais, tokens ou conteudo do `.env`.

O script de demonstracao cria um tutor e um pet temporarios; remove ambos ao terminar com sucesso. Se interrompido, remover manualmente apenas os registros identificados nessa demonstracao. Ele exibe o SELECT, mas quem grava deve executa-lo no cliente do banco. A opcao `-NonInteractive` serve a verificacao local; nao comprova os SELECTs no video.

## 7. Checklist de entrega

- [ ] Aplicacao Java com CRUD e Oracle em nuvem acessiveis.
- [ ] Projeto privado identificado conforme enunciado e professor com Basic/acesso efetivo.
- [ ] CI automatico em `master`, build e testes aprovados, resultados publicados.
- [ ] Novo artefato publicado no Azure DevOps, CD automatico e deploy concluido em ACI.
- [ ] Segredos protegidos e valores antigos substituidos no ambiente.
- [ ] Diagrama com personas, stack, conexoes e fluxo numerado revisado conforme destino real do banco.
- [ ] Video mostra codigo alterado, etapas, testes, artefato, Portal, versao atualizada, CRUD e SELECTs na nuvem.
- [ ] PDF somente com nome completo/RM/turma de todos e links GitHub, YouTube e Azure DevOps.
- [ ] Conferir prazo e horario oficiais da entrega; esta preparacao nao valida a data da turma.

Pontuacao: diagrama/fluxo ate 20, video ate 80. O PDF associa nota zero a localhost, atraso, falta de acesso ao Azure DevOps/video, projeto nao configurado e CI ou CD sem funcionar. Outras penalidades estao nas paginas 53-54.

## Referencias tecnicas oficiais

- [Pipeline completion triggers](https://learn.microsoft.com/en-us/azure/devops/pipelines/process/pipeline-triggers?view=azure-devops).
- [Pipeline artifacts](https://learn.microsoft.com/en-us/azure/devops/pipelines/artifacts/pipeline-artifacts?view=azure-devops).
- [Atualizacao de grupos ACI e reinicio de todos os containers](https://learn.microsoft.com/en-us/azure/container-instances/container-instances-update).
- [Pull no ACR com identidade gerenciada](https://learn.microsoft.com/en-us/azure/container-instances/using-azure-container-registry-mi).
