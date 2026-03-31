#!/bin/bash
# setup-git-hooks.sh - Script para configurar Git Hooks

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

# Diretório de hooks
HOOKS_DIR=".git/hooks"
SCRIPTS_DIR="scripts"

# Verifica se está em um repositório Git
if [[ ! -d "$HOOKS_DIR" ]]; then
    print_color $RED "❌ Este não é um repositório Git!"
    print_color $YELLOW "Execute este script na raiz do repositório."
    exit 1
fi

# Verifica se o diretório de scripts existe
if [[ ! -d "$SCRIPTS_DIR" ]]; then
    print_color $RED "❌ Diretório 'scripts' não encontrado!"
    print_color $YELLOW "Certifique-se de que os scripts estão no diretório 'scripts/'."
    exit 1
fi

print_color $BLUE "🔧 Configurando Git Hooks..."

# Hook de commit-msg para validar mensagens de commit
print_color $BLUE "📝 Configurando hook commit-msg..."
cat > "$HOOKS_DIR/commit-msg" << 'EOF'
#!/bin/bash
# Hook para validar mensagens de commit usando Conventional Commits

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
VALIDATE_SCRIPT="$SCRIPT_DIR/scripts/validate-commit.sh"

if [[ -f "$VALIDATE_SCRIPT" ]]; then
    # Lê a mensagem de commit do arquivo temporário
    COMMIT_MSG=$(cat "$1")
    
    # Valida a mensagem
    if ! "$VALIDATE_SCRIPT" "$COMMIT_MSG"; then
        echo ""
        echo "❌ Mensagem de commit inválida!"
        echo "Por favor, siga o padrão Conventional Commits."
        echo "Use './scripts/validate-commit.sh --help' para mais informações."
        exit 1
    fi
else
    echo "⚠️  Script de validação não encontrado: $VALIDATE_SCRIPT"
    echo "Execute 'scripts/setup-git-hooks.sh' para configurar os hooks."
fi
EOF

chmod +x "$HOOKS_DIR/commit-msg"
print_color $GREEN "✅ Hook commit-msg configurado"

# Hook de pre-commit para verificar arquivos
print_color $BLUE "🔍 Configurando hook pre-commit..."
cat > "$HOOKS_DIR/pre-commit" << 'EOF'
#!/bin/bash
# Hook para verificações pré-commit

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

# Cores
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m'

print_color() {
    printf "${1}${2}${NC}\n"
}

# Verifica se há arquivos grandes
print_color "\${YELLOW}🔍 Verificando arquivos grandes..."
LARGE_FILES=$(git diff --cached --name-only | xargs -I {} find {} -size +5M 2>/dev/null || true)

if [[ -n "$LARGE_FILES" ]]; then
    print_color "\${RED}❌ Arquivos grandes detectados:"
    echo "$LARGE_FILES"
    print_color "\${YELLOW}⚠️  Considere usar Git LFS para arquivos grandes."
    exit 1
fi

# Verifica se há secrets/arquivos sensíveis
print_color "\${YELLOW}🔍 Verificando arquivos sensíveis..."
SENSITIVE_PATTERNS=(
    "password"
    "secret"
    "token"
    "api_key"
    "private_key"
    "credential"
)

SENSITIVE_FILES=false
for pattern in "\${SENSITIVE_PATTERNS[@]}"; do
    if git diff --cached --name-only | xargs grep -l -i "$pattern" 2>/dev/null; then
        SENSITIVE_FILES=true
        break
    fi
done

if [[ "$SENSITIVE_FILES" == true ]]; then
    print_color "\${RED}❌ Possíveis arquivos sensíveis detectados!"
    print_color "\${YELLOW}⚠️  Verifique se há senhas, tokens ou chaves privadas no commit."
    read -p "Deseja continuar mesmo assim? (s/N): " continue
    if [[ ! "\$continue" =~ ^[Ss]$ ]]; then
        exit 1
    fi
fi

# Verifica se há arquivos sem newline no final
print_color "\${YELLOW}🔍 Verificando arquivos sem newline..."
FILES_WITHOUT_NEWLINE=$(git diff --cached --name-only | xargs -I {} sh -c 'test "$(tail -c 1 "{}")" != "" && echo "{}"' 2>/dev/null || true)

if [[ -n "$FILES_WITHOUT_NEWLINE" ]]; then
    print_color "\${YELLOW}⚠️  Arquivos sem newline no final:"
    echo "$FILES_WITHOUT_NEWLINE"
    print_color "\${YELLOW}💡 Considere adicionar newline no final dos arquivos."
fi

# Verifica se há arquivos binários não documentados
print_color "\${YELLOW}🔍 Verificando arquivos binários..."
BINARY_FILES=$(git diff --cached --name-only | xargs -I {} file "{}" 2>/dev/null | grep -v "text" | cut -d: -f1 || true)

if [[ -n "$BINARY_FILES" ]]; then
    print_color "\${YELLOW}⚠️  Arquivos binários detectados:"
    echo "$BINARY_FILES"
    print_color "\${YELLOW}💡 Certifique-se de que arquivos binários estão em .gitignore"
fi

print_color "\${GREEN}✅ Verificações pré-commit concluídas!"
EOF

chmod +x "$HOOKS_DIR/pre-commit"
print_color $GREEN "✅ Hook pre-commit configurado"

# Hook de post-commit para notificações
print_color $BLUE "📬 Configurando hook post-commit..."
cat > "$HOOKS_DIR/post-commit" << 'EOF'
#!/bin/bash
# Hook para notificações pós-commit

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

print_color() {
    printf "\${1}\${2}\${NC}\n"
}

# Obtém informações do commit
COMMIT_HASH=$(git rev-parse --short HEAD)
COMMIT_MSG=$(git log -1 --pretty=%B)
BRANCH=$(git branch --show-current)

# Mostra informações do commit
print_color "\${GREEN}✅ Commit realizado com sucesso!"
echo ""
print_color "\${BLUE}📊 Informações:"
echo "  Hash: \$COMMIT_HASH"
echo "  Branch: \$BRANCH"
echo "  Mensagem: \$(echo "\$COMMIT_MSG" | head -1)"
echo ""

# Verifica se há commits não pushados
UNPUSHED=$(git log origin/\$BRANCH..\$BRANCH --oneline 2>/dev/null | wc -l)
if [[ \$UNPUSHED -gt 1 ]]; then
    print_color "\${BLUE}📤 Você tem \$((\$UNPUSHED - 1)) commits não pushados."
    print_color "\${BLUE}💡 Execute 'git push origin \$BRANCH' para enviar."
fi
EOF

chmod +x "$HOOKS_DIR/post-commit"
print_color $GREEN "✅ Hook post-commit configurado"

# Hook de pre-push para verificações finais
print_color $BLUE "🚀 Configurando hook pre-push..."
cat > "$HOOKS_DIR/pre-push" << 'EOF'
#!/bin/bash
# Hook para verificações pré-push

# Cores
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

print_color() {
    printf "\${1}\${2}\${NC}\n"
}

# Obtém a branch de destino
while read local_ref local_sha remote_ref remote_sha; do
    if [[ "\$remote_ref" =~ refs/heads/(.*) ]]; then
        REMOTE_BRANCH="\${BASH_REMATCH[1]}"
        break
    fi
done

# Verifica se está fazendo push para main/master sem proteção
if [[ "\$REMOTE_BRANCH" == "main" || "\$REMOTE_BRANCH" == "master" ]]; then
    print_color "\${YELLOW}⚠️  Você está fazendo push para a branch principal!"
    
    # Verifica se há muitos commits
    COMMITS=$(git log origin/\$REMOTE_BRANCH..\$BRANCH --oneline | wc -l)
    if [[ \$COMMITS -gt 5 ]]; then
        print_color "\${YELLOW}📊 Você está fazendo push de \$COMMITS commits."
        print_color "\${YELLOW}💡 Considere fazer um Pull Request para revisão."
        read -p "Deseja continuar? (s/N): " continue
        if [[ ! "\$continue" =~ ^[Ss]$ ]]; then
            print_color "\${RED}❌ Push cancelado."
            exit 1
        fi
    fi
fi

# Verifica se há testes (opcional)
if [[ -f "pom.xml" ]]; then
    print_color "\${BLUE}🧪 Verificando se há testes..."
    TEST_FILES=$(find src/test -name "*.java" 2>/dev/null | wc -l)
    if [[ \$TEST_FILES -eq 0 ]]; then
        print_color "\${YELLOW}⚠️  Nenhum teste encontrado!"
        print_color "\${YELLOW}💡 Considere adicionar testes antes do push."
    else
        print_color "\${GREEN}✅ \$TEST_FILES arquivos de teste encontrados."
    fi
fi

print_color "\${GREEN}✅ Verificações pré-push concluídas!"
EOF

chmod +x "$HOOKS_DIR/pre-push"
print_color $GREEN "✅ Hook pre-push configurado"

# Cria arquivo de configuração para Git
print_color $BLUE "⚙️  Configurando opções do Git..."
git config core.autocrlf input 2>/dev/null || true
git config core.eol lf 2>/dev/null || true
git config pull.rebase false 2>/dev/null || true
print_color $GREEN "✅ Configurações do Git atualizadas"

print_color $GREEN "🎉 Git Hooks configurados com sucesso!"
echo ""
print_color $BLUE "📋 Hooks configurados:"
echo "  - commit-msg: Valida mensagens de commit (Conventional Commits)"
echo "  - pre-commit: Verifica arquivos antes do commit"
echo "  - post-commit: Mostra informações após o commit"
echo "  - pre-push: Verificações finais antes do push"
echo ""
print_color $YELLOW "💡 Para desativar os hooks temporariamente:"
echo "  git commit --no-verify"
echo "  git push --no-verify"
echo ""
print_color $YELLOW "💡 Para remover os hooks:"
echo "  rm -rf .git/hooks/*"
echo "  scripts/setup-git-hooks.sh"
