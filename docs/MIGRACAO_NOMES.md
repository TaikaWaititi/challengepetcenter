# Migracao para os nomes definitivos - 04/11/2026

A solicitacao foi retirar o nome de teste dos recursos usados pela versao candidata a entrega. O codigo Java permaneceu o mesmo; nao houve commit ou push.

## Identificacao atual

| Recurso | Nome |
|---|---|
| Grupo de recursos | `rg-petcenter-rm564939-sprint4` |
| Azure Container Instances | `aci-petcenter-rm564939-sprint4` |
| Azure Container Registry | `acrpetcenters4b5649391104` |
| Conta de armazenamento de backup privado | `stpetcenters4564939` |
| Imagem da API | `petcenter-api:sprint4-20261104` |
| Imagem base Oracle | `petcenter-oracle:base23-sprint4` |

- [API atual](http://petcenter-rm564939-sprint4.brazilsouth.azurecontainer.io:8080/)
- [Swagger atual](http://petcenter-rm564939-sprint4.brazilsouth.azurecontainer.io:8080/swagger-ui.html)

O grupo anterior `rg-petcenter-s4-test-20261104` e o ACI `aci-petcenter-s4-test` foram removidos somente depois da restauracao e da validacao do novo ambiente. `NetworkWatcherRG` foi preservado.

## Por que foi necessaria uma migracao

A Azure nao permite renomear resource groups, e o recurso ACI nao suporta movimentacao entre grupos. Por isso, foi preciso recriar o ACI no grupo definitivo e restaurar o Oracle. O ACR suporta movimentacao e foi mantido, com as imagens existentes.

Referencias oficiais: [renomeacao de grupos](https://learn.microsoft.com/en-us/azure/azure-resource-manager/management/frequently-asked-questions) e [suporte a movimentacao por recurso](https://learn.microsoft.com/en-us/azure/azure-resource-manager/management/move-support-resources).

## Passos realizados

1. Conferir os grupos e os recursos existentes.
2. Pausar o processo Java no ACI anterior para impedir gravacoes durante a copia do banco.
3. Exportar o schema `PETCENTER` com Oracle Data Pump, incluindo tabelas, dados e objetos de banco.
4. Criar um backup privado em Azure Blob Storage, com acesso temporario por SAS, e salvar tambem uma copia local no diretorio de trabalho. O acesso publico anonimo ao armazenamento esta desabilitado.
5. Conferir o SHA-256 do arquivo na origem, na copia local e no novo Oracle. Os hashes coincidiram. Backup: 1093632 bytes; SHA-256 `208f15a68548229b19d16d35486b61b250bb01b077c053c9a1adcfa2bd501728`.
6. Criar o grupo e o ACI com os nomes definitivos. A imagem da API e o mesmo artefato Java validado antes, com uma nova tag. O Oracle iniciou sem a carga automatica para receber a restauracao completa.
7. Importar o schema com Data Pump e comparar a contagem das 12 tabelas antes e depois.
8. Repetir cadastro, login JWT, CRUD de pets e diarios e SELECTs na Azure. A validacao passou novamente: 14 verificacoes HTTP e 7 consultas SQL.
9. Mover o ACR para o grupo definitivo e atualizar as tags para `projeto=petcenter`, `sprint=4` e `fase=release-candidate`.
10. Remover o grupo anterior e conferir que a API no novo endereco continua retornando HTTP 200.
11. Atualizar os documentos e o pacote local de revisao, sem commit ou push.

## Dados conferidos na restauracao

| Tabela | Antes | Depois |
|---|---:|---:|
| ALERTAS | 0 | 0 |
| AUDITORIA_REGISTROS | 3 | 3 |
| COMENTARIOS | 3 | 3 |
| DIARIO_ENTRADAS | 3 | 3 |
| INSIGHTS | 3 | 3 |
| LOGS_ERROS | 0 | 0 |
| PETS | 3 | 3 |
| PET_VETERINARIO | 3 | 3 |
| REGISTROS | 6 | 6 |
| SOLICITACOES | 3 | 3 |
| USERS | 4 | 4 |
| VETERINARIOS | 2 | 2 |

O backup contem os dados existentes antes da migracao. Ele fica fora do repositorio Git e do pacote de codigo. Os dados temporarios criados na revalidacao do CRUD foram removidos ao terminar.

## Limites que permanecem

A migracao muda a identificacao dos recursos e preserva o banco atual; ela nao implementa CI/CD no Azure DevOps nem adiciona persistencia duravel ao disco do Oracle em ACI. O schema agora foi restaurado por Data Pump na imagem base. Se o armazenamento efemero do container for perdido, sera necessario restaurar o backup; nao depender de reinicializacao automatica para recuperar esses dados. Antes de automatizar os redeploys, o banco deve ter persistencia e ciclo de vida separados da API, conforme a arquitetura proposta.

O ACR, ACI e armazenamento privado permanecem ativos e podem gerar custo. O grupo definitivo inclui o backup para recuperacao; a conta de armazenamento nao deve ser excluida antes de guardar outra copia verificada do banco.
