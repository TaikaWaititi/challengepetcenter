# Entrega 4 de DevOps - ambiente configurado

Referencia: paginas 48 a 55 do PDF do Challenge 2026. Esta e a documentacao ativa; os relatos de preparacao, teste manual e migracao anterior sao historicos.

## Projeto e acessos

- Organizacao: Sprint4fiap.
- Projeto: Sprint 4 – Azure DevOps, privado, Git, processo Scrum.
- Professor: Felipe Cavalcante Vilella de Oliveira, pf2128@fiap.com.br; acesso Basic e membro de Project Administrators.
- Integrantes: Arthur dos Santos Cabral (RM566515), Julia Tiziotto Buttler (RM564975), Mariana Xavier Quispe (RM566357), Bruno Martins Bettio (RM564939) e Jose Diogo Da Silva Neves (RM562341), turma 2TDSA.
- O projeto PetCenter na organizacao RM564975 e seus Boards foram preservados.

## Ferramentas e arquitetura

GitHub armazena o codigo. Azure DevOps executa CI/CD em YAML. O CI usa Java 21, Maven Wrapper, JUnit, Mockito e Oracle Free em container descartavel. O JAR testado e publicado como Pipeline Artifact. O CD entrega esse JAR em Azure Web App, uma das alternativas expressamente aceitas no PDF.

O banco da aplicacao e Oracle Free em Docker, em uma VM da Azure com disco de dados gerenciado. A porta 1521 e acessivel pela subnet privada do Web App; nao esta publicada para a internet. O volume `/opt/oracle/oradata` fica em `/srv/petcenter/oradata`, no disco montado em `/srv/petcenter`. Reiniciar a API ou publicar outro JAR nao recria o Oracle.

O ACI `aci-petcenter-rm564939-sprint4` preserva o endereco antigo como proxy Nginx para o Web App. A conexao do proxy ao Web App usa HTTPS com verificacao de certificado. O ACR armazena a imagem desse proxy; a API e publicada como JAR no runtime Java do Web App.

Recursos do grupo `rg-petcenter-rm564939-sprint4`:

| Recurso | Funcao |
|---|---|
| app-petcenter-rm564939-sprint4 | Azure Web App, destino do CD |
| asp-petcenter-sprint4 | App Service Plan Linux Basic B1 |
| vm-petcenter-oracle | Host exclusivo do Oracle, sem deploy da aplicacao |
| disk-petcenter-oracle-data | Disco de dados persistente de 32 GiB |
| vnet-petcenter-sprint4 | Subnets separadas para banco e integracao do Web App |
| nsg-petcenter-oracle | Permite Oracle somente a partir da subnet da aplicacao |
| pip-petcenter-oracle-outbound | Saida de rede da VM; portas de entrada bloqueadas pelo NSG |
| aci-petcenter-rm564939-sprint4 | Proxy para compatibilidade com a URL anterior |
| acrpetcenters4b5649391104 | Registro privado de imagens |
| stpetcenters4564939 | Backups privados de migracao |
| id-petcenter-azuredevops | Identidade federada do CD, limitada ao Web App |

## CI e CD

O fluxo numerado esta no [diagrama](arquitetura-sprint4.svg).

1. Desenvolvedor altera codigo e faz push em `master` no GitHub.
2. O webhook inicia PetCenter-CI automaticamente.
3. O agente Linux seleciona Java 21 e cria um Oracle de testes com senhas aleatorias.
4. Maven executa `clean verify -Poracle-it`: oito testes unitarios e um teste HTTP/SQL integrado.
5. Resultados JUnit aparecem na aba Tests. O CI publica `petcenter`, contendo `app.jar`, SHA256SUMS, build-id.txt, commit.txt e scripts de deploy.
6. PetCenter-CD e iniciado por conclusao bem-sucedida do CI em `master`.
7. O CD baixa o artefato daquele run e confere o SHA-256. Nao recompila Java.
8. A service connection federada autentica na Azure e publica o JAR no Web App. O script atua somente na aplicacao.
9. A pagina inicial deve mostrar o Build ID do artefato. O CD verifica esse identificador, a API e o OpenAPI antes de concluir.
10. Usuario acessa a API/Swagger. O CRUD e conferido com SELECT no Oracle em nuvem.

O gatilho exigido e `master`. A `main` e mantida como copia da versao entregue; alterar apenas main nao dispara o CI configurado. As pipelines sao YAML, conforme permitido pelo enunciado. PRs publicos nao executam no agente deste projeto.

## Execucoes verificadas

- [CI 8](https://dev.azure.com/Sprint4fiap/65b112f9-8fca-499e-93fc-86127fc2f2f6/_build/results?buildId=8): **Succeeded**, motivo `individualCI`, nove testes aprovados e artefato `petcenter` publicado.
- [CD 9](https://dev.azure.com/Sprint4fiap/65b112f9-8fca-499e-93fc-86127fc2f2f6/_build/results?buildId=9): **Succeeded**, motivo `resourceTrigger`, iniciado automaticamente pelo CI 8 e publicacao do build 8 no Web App.
- CRUD da publicacao inicial: 14 verificacoes HTTP e sete SELECTs aprovados no Web App/Oracle da VM; registros de teste removidos.
- Apos CD 9: contagens das 12 tabelas preservadas e mesmo StartedAt do Oracle, sem reinicio do container do banco.

Validacao em 04/11/2026. O Build ID publico muda nas proximas publicacoes. O CD 7 foi cancelado durante a espera do comando de publicacao e pertence ao historico; a evidencia automatica concluida utilizada e o CD 9.

O deploy aguarda a conclusao do OneDeploy e a inicializacao do Web App antes de conferir Build ID e respostas HTTP. O proxy encaminha o cabecalho Forwarded para o Swagger preservar host, porta e protocolo do endereco acessado.

## Agente de execucao

O pool PetCenter-Linux usa o container `petcenter-azuredevops-agent`, no Docker Desktop deste computador. Foi necessario porque a organizacao nao tinha agente hospedado disponivel. O agente executa jobs do Azure DevOps; a aplicacao e o banco entregues continuam na Azure.

Para a execucao automatica e para gravar o video, manter computador e Docker Desktop ligados e o agente online. Se ele estiver parado:

```powershell
docker start petcenter-azuredevops-agent
```

Os arquivos em `azure/agent` e `scripts/azure/start-agent.ps1` permitem reconstruir/registrar o agente. O pool e sua autorizacao precisam existir no projeto. O registro usa um token temporario obtido pelo Azure CLI, salvo apenas em `tmp`, ignorado pelo Git. O container guarda sua configuracao no volume Docker `petcenter-ado-agent`. A rede Docker `petcenter-ci` conecta o agente somente ao Oracle descartavel do CI.

## Segredos e autorizacoes

- `sc-petcenter-github`: acesso ao repositorio, autorizado somente para CI e CD.
- `sc-petcenter-azure`: Workload Identity Federation com identidade gerenciada, sem senha permanente de service principal; autorizada somente para o CD.
- A identidade de deploy possui Website Contributor no Web App, sem permissoes para recriar a VM ou alterar o banco.
- Variable group `petcenter-sprint4`, autorizado somente para o CD. URL JDBC, usuario, senha e JWT marcados como secretos.
- Environment `petcenter-sprint4`, autorizado somente para o CD, sem aprovacao manual que interrompa a demonstracao automatica.
- CI usa credenciais descartaveis e nao recebe o variable group da aplicacao.
- `.env`, backups, tokens e arquivos renderizados com segredos ficam fora do Git.

## Evidencias de banco na nuvem

O roteiro `scripts/demo-crud-sprint4.ps1` faz cadastro, login e CRUD, pausando para exibir cada SELECT no video. Em outro terminal, executar as consultas com:

```powershell
.\scripts\azure\select-oracle.ps1 -Query 'SELECT id, nome FROM pets WHERE id = <ID_DO_PET>;'
```

Substituir `<ID_DO_PET>` pelo ID exibido pela API. O comando usa Azure VM Run Command e SQLPlus dentro do Oracle, sem expor a porta 1521 ou senha. Requer acesso administrativo adequado a VM na assinatura Azure. Para excluir, repetir a mesma consulta e mostrar zero linhas.

Tambem e possivel usar o Run Command no Portal da Azure. As consultas precisam ser realizadas no banco da nuvem, nao apenas observar as respostas HTTP.

## Migracao e preservacao das outras entregas

Antes da troca, a API antiga foi pausada e o schema PETCENTER foi exportado com Data Pump para armazenamento privado. O dump foi transferido para a VM com hash conferido e importado. As contagens das 12 tabelas foram comparadas; dados, relacionamentos e objetos de banco foram preservados. O ciclo de deploy da API agora e independente do Oracle.

O antigo endereco permanece atendido pelo proxy, preservando os clientes que o utilizavam. Mobile, DTOs, servicos e regras de negocio nao foram alterados por esta adequacao. O texto da pagina inicial foi atualizado para comprovar a versao publicada. Para a disciplina de Banco de Dados, ainda se deve demonstrar as procedures pela aplicacao conforme seu proprio enunciado; esta entrega nao reavalia esse requisito.

## Roteiro de gravacao (p.51-52)

1. Preparar IDE, GitHub, Azure DevOps, Portal Azure e terminal de consultas.
2. Apresentar solucao, stack e arquitetura com fluxo numerado.
3. Mostrar configuracao das pipelines, seus jobs/tasks, conexoes e nomes das variaveis sem revelar valores secretos.
4. Alterar texto em HomeController.java, fazer commit e push em master. Uma alteracao apenas no README nao atende.
5. Mostrar o CI iniciando automaticamente; explicar build, testes e publicacao, abrir a aba Tests e o artefato.
6. Mostrar o CD iniciando automaticamente apos o novo artefato; explicar download, checksum, autenticacao federada e deploy.
7. Mostrar o Web App atualizado no Portal e acessar o novo texto/Build ID pelo endereco publico.
8. Executar CRUD e exibir cada SELECT no Oracle da VM na Azure.
9. Conferir que o professor acessa o projeto, as execucoes e os artefatos.

Video minimo 720p, audio claro, explicacao por voz do aluno e sem legendas. Sem limite fixo de duracao. Nao mostrar senhas, tokens ou conteudo de .env.

## Entrega final (p.48)

PDF contendo somente nome completo, RM e turma dos integrantes e links de GitHub, YouTube e projeto Azure DevOps. A documentacao, descricao e diagrama ficam no README e nos arquivos vinculados.

A gravacao e o PDF com o link final do YouTube ainda precisam ser produzidos pelo grupo. As execucoes automatizadas documentadas aqui nao substituem a explicacao falada exigida do aluno.
