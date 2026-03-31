#!/bin/bash
# validate-commit.sh - Script para validar Conventional Commits

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir colored output
print_color() {
    printf "${1}${2}${NC}\n"
}

# Padrão de Conventional Commits
PATTERN="^(feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)(\(.+\))?: .{1,50}"

# Tipos válidos de commits
VALID_TYPES=(
    "feat"     # Nova funcionalidade
    "fix"      # Correção de bug
    "docs"     # Documentação
    "style"    # Formatação, ponto-e-vírgula, etc.
    "refactor"  # Refatoração de código
    "perf"      # Melhoria de performance
    "test"      # Adição ou correção de testes
    "build"     # Mudanças no sistema de build
    "ci"        # Mudanças na configuração de CI
    "chore"     # Mudanças que não alteram src ou test files
    "revert"    # Revert de commits anteriores
)

# Função para validar mensagem de commit
validate_commit_message() {
    local message="$1"
    
    # Verifica se a mensagem corresponde ao padrão
    if [[ ! "$message" =~ $PATTERN ]]; then
        print_color $RED "❌ Mensagem de commit inválida!"
        print_color $YELLOW "Mensagem: $message"
        echo ""
        print_color $BLUE "📝 Padrão esperado:"
        echo "  <tipo>(<escopo>): <descrição>"
        echo ""
        print_color $BLUE "📋 Tipos válidos:"
        for type in "${VALID_TYPES[@]}"; do
            echo "  - $type"
        done
        echo ""
        print_color $BLUE "💡 Exemplos:"
        echo "  feat: adiciona sistema de autenticação"
        echo "  fix(auth): corrige erro de validação de token"
        echo "  docs(readme): atualiza instruções de instalação"
        echo "  refactor(user): otimiza consulta de usuários"
        echo ""
        print_color $YELLOW "📖 Mais informações: https://conventionalcommits.org/"
        return 1
    fi
    
    # Extrai o tipo do commit
    local type=$(echo "$message" | sed -E 's/^([a-z]+).*/\1/')
    
    # Verifica se o tipo é válido
    local valid_type=false
    for valid in "${VALID_TYPES[@]}"; do
        if [[ "$type" == "$valid" ]]; then
            valid_type=true
            break
        fi
    done
    
    if [[ "$valid_type" == false ]]; then
        print_color $RED "❌ Tipo de commit inválido: $type"
        print_color $YELLOW "Tipos válidos: ${VALID_TYPES[*]}"
        return 1
    fi
    
    # Verifica o comprimento da descrição
    local description=$(echo "$message" | sed -E 's/^[a-z]+(\(.+\))?: //')
    if [[ ${#description} -gt 50 ]]; then
        print_color $RED "❌ Descrição muito longa (${#description} caracteres)"
        print_color $YELLOW "Máximo recomendado: 50 caracteres"
        print_color $YELLOW "Descrição atual: $description"
        return 1
    fi
    
    # Verifica se não termina com ponto
    if [[ "$message" =~ \.$ ]]; then
        print_color $RED "❌ A descrição não deve terminar com ponto"
        print_color $YELLOW "Mensagem: $message"
        return 1
    fi
    
    return 0
}

# Função para mostrar ajuda
show_help() {
    echo "Uso: $0 [opções] [mensagem]"
    echo ""
    echo "Opções:"
    echo "  -h, --help     Mostra esta ajuda"
    echo "  -f, --file     Lê a mensagem de um arquivo"
    echo "  -c, --commit    Valida o último commit"
    echo ""
    echo "Exemplos:"
    echo "  $0 'feat: adiciona nova funcionalidade'"
    echo "  $0 -f commit-message.txt"
    echo "  $0 -c"
    echo ""
    echo "Padrão Conventional Commits:"
    echo "  <tipo>(<escopo>): <descrição>"
    echo ""
    echo "Tipos válidos:"
    for type in "${VALID_TYPES[@]}"; do
        echo "  - $type"
    done
}

# Função para validar último commit
validate_last_commit() {
    local last_commit=$(git log -1 --pretty=%B)
    print_color $BLUE "🔍 Validando último commit:"
    echo "$last_commit"
    echo ""
    
    if validate_commit_message "$last_commit"; then
        print_color $GREEN "✅ Mensagem do último commit está válida!"
        return 0
    else
        print_color $RED "❌ Mensagem do último commit é inválida!"
        return 1
    fi
}

# Função para validar mensagem de arquivo
validate_from_file() {
    local file="$1"
    
    if [[ ! -f "$file" ]]; then
        print_color $RED "❌ Arquivo não encontrado: $file"
        return 1
    fi
    
    local message=$(cat "$file")
    print_color $BLUE "📁 Validando mensagem do arquivo $file:"
    echo "$message"
    echo ""
    
    if validate_commit_message "$message"; then
        print_color $GREEN "✅ Mensagem do arquivo está válida!"
        return 0
    else
        print_color $RED "❌ Mensagem do arquivo é inválida!"
        return 1
    fi
}

# Main
case "${1:-}" in
    -h|--help)
        show_help
        exit 0
        ;;
    -f|--file)
        if [[ -z "${2:-}" ]]; then
            print_color $RED "❌ Erro: --file requer um nome de arquivo"
            show_help
            exit 1
        fi
        validate_from_file "$2"
        exit $?
        ;;
    -c|--commit)
        validate_last_commit
        exit $?
        ;;
    "")
        print_color $RED "❌ Erro: argumento obrigatório"
        show_help
        exit 1
        ;;
    *)
        # Valida a mensagem passada como argumento
        validate_commit_message "$1"
        exit $?
        ;;
esac
