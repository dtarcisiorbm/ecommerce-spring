#!/bin/bash

# Script de setup para configuração de ambiente
# Uso: ./scripts/setup-env.sh [dev|test|prod]

set -e

ENVIRONMENT=${1:-dev}
ENV_FILE=".env.${ENVIRONMENT}"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Configurando ambiente: ${ENVIRONMENT}${NC}"

# Verifica se o arquivo de ambiente existe
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}Erro: Arquivo $ENV_FILE não encontrado!${NC}"
    echo -e "${YELLOW}Ambientes disponíveis: dev, test, prod${NC}"
    exit 1
fi

# Verifica se já existe um .env ativo
if [ -f ".env" ]; then
    echo -e "${YELLOW}Aviso: Arquivo .env existente será sobrescrito${NC}"
    read -p "Deseja continuar? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Operação cancelada."
        exit 1
    fi
fi

# Copia o arquivo de ambiente para .env
cp "$ENV_FILE" .env

# Carrega as variáveis de ambiente
set -a
source .env
set +a

echo -e "${GREEN}Ambiente ${ENVIRONMENT} configurado com sucesso!${NC}"
echo -e "${YELLOW}Variáveis carregadas:${NC}"
echo "  - JWT_SECRET: ${JWT_SECRET:0:10}..."
echo "  - DB_HOST: $DB_HOST"
echo "  - DB_NAME: $DB_NAME"
echo "  - SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"

# Verifica se as variáveis obrigatórias estão definidas
REQUIRED_VARS=("JWT_SECRET" "DB_HOST" "DB_NAME" "DB_USERNAME" "DB_PASSWORD")
MISSING_VARS=()

for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
        MISSING_VARS+=("$var")
    fi
done

if [ ${#MISSING_VARS[@]} -ne 0 ]; then
    echo -e "${RED}Erro: Variáveis obrigatórias não definidas: ${MISSING_VARS[*]}${NC}"
    exit 1
fi

echo -e "${GREEN}Próximos passos:${NC}"
echo "1. Suba os serviços com: docker-compose up -d"
echo "2. Execute a aplicação: ./mvnw spring-boot:run"
echo "3. Ou execute com profile: ./mvnw spring-boot:run -Dspring-boot.run.profiles=${ENVIRONMENT}"

echo -e "${GREEN}Setup concluído!${NC}"
