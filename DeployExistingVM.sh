#!/usr/bin/env bash
set -euo pipefail

APP_DIR="/opt/challengepetcenter"
APP_USER="appuser"
GITHUB_REPO_URL="https://github.com/TaikaWaititi/challengepetcenter.git"

echo "==> Atualizando pacotes e instalando ferramentas"
export DEBIAN_FRONTEND=noninteractive
apt-get update
apt-get install -y ca-certificates curl gnupg git nano unzip

echo "==> Instalando Docker e Docker Compose Plugin"
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc

echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" > /etc/apt/sources.list.d/docker.list

apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

systemctl enable docker
systemctl start docker

echo "==> Criando usuario sem privilegios administrativos para executar o projeto"
id "$APP_USER" >/dev/null 2>&1 || useradd -m -s /bin/bash "$APP_USER"
usermod -aG docker "$APP_USER"

mkdir -p "$APP_DIR"
chown -R "$APP_USER:$APP_USER" "$APP_DIR"

echo "==> Clonando ou atualizando repositorio"
if [ -d "$APP_DIR/.git" ]; then
  sudo -u "$APP_USER" git -C "$APP_DIR" pull
else
  rm -rf "$APP_DIR"
  mkdir -p "$APP_DIR"
  chown -R "$APP_USER:$APP_USER" "$APP_DIR"
  sudo -u "$APP_USER" git clone "$GITHUB_REPO_URL" "$APP_DIR"
fi

echo "==> Subindo API Java e banco H2 com Docker Compose"
cd "$APP_DIR"
sudo -u "$APP_USER" docker compose down || true
sudo -u "$APP_USER" docker compose up -d --build

echo "==> Status dos containers"
sudo -u "$APP_USER" docker compose ps

echo "==> Usuario do container da API"
docker inspect petcenter-api --format "Container user: {{.Config.User}}"

echo "==> Volumes Docker"
docker volume ls

echo "==> Deploy concluido"
echo "API: http://74.163.80.209:8080"
echo "Swagger: http://74.163.80.209:8080/swagger-ui.html"
echo "H2 Console: http://74.163.80.209:81"
