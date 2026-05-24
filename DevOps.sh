#!/usr/bin/env bash
set -euo pipefail

# Challenge PetCenter - DevOps Tools & Cloud Computing
# Execute este script no Azure Cloud Shell ou em uma maquina com Azure CLI logado.
#
# Antes de executar, altere GITHUB_REPO_URL para o repositorio publico do grupo.
# O repositorio deve conter Dockerfile e docker-compose.yml na raiz do projeto Java.

RESOURCE_GROUP="${RESOURCE_GROUP:-rg-petcenter}"
LOCATION="${LOCATION:-brazilsouth}"
VM_NAME="${VM_NAME:-vm-petcenter}"
VM_SIZE="${VM_SIZE:-Standard_B2s}"
ADMIN_USER="${ADMIN_USER:-azureuser}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-2TDSA@2026devops}"
GITHUB_REPO_URL="${GITHUB_REPO_URL:-https://github.com/TaikaWaititi/challengepetcenter.git}"
APP_DIR="${APP_DIR:-/opt/challengepetcenter}"

if [[ "$GITHUB_REPO_URL" == *"SEU-USUARIO"* ]]; then
  echo "ERRO: altere a variavel GITHUB_REPO_URL no inicio do script antes de executar."
  exit 1
fi

echo "==> Criando Resource Group"
az group create \
  --name "$RESOURCE_GROUP" \
  --location "$LOCATION"

echo "==> Criando VM Linux Ubuntu"
az vm create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --image Ubuntu2404 \
  --location "$LOCATION" \
  --size "$VM_SIZE" \
  --authentication-type password \
  --admin-username "$ADMIN_USER" \
  --admin-password "$ADMIN_PASSWORD"

echo "==> Abrindo portas necessarias"
az vm open-port \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --port 22 \
  --priority 1000

az vm open-port \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --port 8080 \
  --priority 1001

az vm open-port \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --port 81 \
  --priority 1002

echo "==> Instalando Docker, Docker Compose, Git e ferramentas de apoio na VM"
az vm run-command invoke \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --command-id RunShellScript \
  --scripts '
set -e
export DEBIAN_FRONTEND=noninteractive

apt-get update
apt-get install -y ca-certificates curl gnupg git nano unzip

install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc

echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo ${UBUNTU_CODENAME:-$VERSION_CODENAME}) stable" > /etc/apt/sources.list.d/docker.list

apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

systemctl enable docker
systemctl start docker

id appuser >/dev/null 2>&1 || useradd -m -s /bin/bash appuser
usermod -aG docker appuser
usermod -aG docker '"$ADMIN_USER"'

mkdir -p '"$APP_DIR"'
chown -R appuser:appuser '"$APP_DIR"'
'

echo "==> Clonando projeto e subindo containers em background"
az vm run-command invoke \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --command-id RunShellScript \
  --scripts '
set -e

if [ -d '"$APP_DIR"'/.git ]; then
  sudo -u appuser git -C '"$APP_DIR"' pull
else
  rm -rf '"$APP_DIR"'
  mkdir -p '"$APP_DIR"'
  chown -R appuser:appuser '"$APP_DIR"'
  sudo -u appuser git clone '"$GITHUB_REPO_URL"' '"$APP_DIR"'
fi

if [ ! -f '"$APP_DIR"'/Dockerfile ] || [ ! -f '"$APP_DIR"'/docker-compose.yml ]; then
  echo "ERRO: o repositorio precisa ter Dockerfile e docker-compose.yml na raiz."
  exit 1
fi

cd '"$APP_DIR"'
sudo -u appuser docker compose down
sudo -u appuser docker compose up -d --build
sudo -u appuser docker compose ps
'

PUBLIC_IP="$(az vm list-ip-addresses \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --query "[0].virtualMachine.network.publicIpAddresses[0].ipAddress" \
  --output tsv)"

echo
echo "==> Deploy finalizado"
echo "API:        http://$PUBLIC_IP:8080"
echo "Swagger:    http://$PUBLIC_IP:8080/swagger-ui.html"
echo "H2 Console: http://$PUBLIC_IP:81"
echo
echo "Para acessar a VM:"
echo "ssh $ADMIN_USER@$PUBLIC_IP"
echo
echo "Para ver logs da aplicacao:"
echo "ssh $ADMIN_USER@$PUBLIC_IP 'cd $APP_DIR && docker compose logs -f app'"
echo
echo "IMPORTANTE: ao final da apresentacao, remova os recursos e tire print da evidencia:"
echo "az group delete --name $RESOURCE_GROUP --yes --no-wait"
