#!/bin/bash
set -euo pipefail
# Imagem gvenzl executa este script no primeiro provisionamento do volume.
# Credenciais fornecidas pelo ambiente; nao ha senha fixa dentro do SQL.
sqlplus -s /nolog <<SQL
WHENEVER OSERROR EXIT FAILURE
WHENEVER SQLERROR EXIT SQL.SQLCODE
CONNECT "${APP_USER}"/"${APP_USER_PASSWORD}"@//localhost:1521/FREEPDB1
SET ECHO OFF
SET VERIFY OFF
SET DEFINE OFF
SET SQLBLANKLINES ON
@/database/script_bd.sql
EXIT
SQL
