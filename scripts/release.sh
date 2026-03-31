#!/bin/bash
# release.sh - Script automatizado para criação de releases

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

# Verifica se está na branch main/master
current_branch=$(git branch --show-current)
if [[ "$current_branch" != "main" && "$current_branch" != "master" ]]; then
    print_color $RED "❌ Execute este script na branch main ou master"
    print_color $YELLOW "Branch atual: $current_branch"
    exit 1
fi

# Verifica se não há mudanças pendentes
if [[ -n $(git status --porcelain) ]]; then
    print_color $RED "❌ Há mudanças não commitadas. Faça commit primeiro."
    print_color $YELLOW "Use 'git add .' e 'git commit' ou execute o script commit-and-push.sh"
    exit 1
fi

# Verifica se está atualizado com o remoto
print_color $BLUE "🔄 Verificando atualizações remotas..."
git fetch origin
if [[ $(git rev-parse HEAD) != $(git rev-parse origin/$current_branch) ]]; then
    print_color $RED "❌ Sua branch local não está atualizada com o remoto."
    print_color $YELLOW "Execute 'git pull origin $current_branch' primeiro."
    exit 1
fi

# Mostra última versão
print_color $BLUE "📦 Última versão:"
git tag --sort=-version:refname | head -5 | while read -r tag; do
    if [[ $tag == v* ]]; then
        echo "  - $tag"
        break
    fi
done

echo ""
print_color $BLUE "📝 Padrão de versionamento (SemVer):"
echo "  MAJOR.MINOR.PATCH"
echo "  Ex: 1.2.0"
echo ""

# Pede a versão
read -p "Digite a nova versão (ex: 1.2.0): " VERSION

# Valida formato da versão
if [[ ! $VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    print_color $RED "❌ Formato de versão inválido. Use MAJOR.MINOR.PATCH"
    print_color $YELLOW "Exemplos válidos: 1.0.0, 1.2.3, 2.0.1"
    exit 1
fi

# Verifica se a tag já existe
if git rev-parse "v$VERSION" >/dev/null 2>&1; then
    print_color $RED "❌ A tag v$VERSION já existe!"
    print_color $YELLOW "Use outra versão ou delete a tag existente com 'git tag -d v$VERSION'"
    exit 1
fi

# Pede o tipo de release
echo ""
print_color $BLUE "🏷️  Tipo de release:"
echo "1) Major - Mudanças que quebram compatibilidade"
echo "2) Minor - Novas funcionalidades (backward compatible)"
echo "3) Patch - Correções de bugs"

read -p "Digite o número (1-3): " release_type

case $release_type in
    1) type_name="MAJOR" ;;
    2) type_name="MINOR" ;;
    3) type_name="PATCH" ;;
    *) 
        print_color $RED "❌ Opção inválida."
        exit 1
        ;;
esac

# Confirmação
echo ""
print_color $YELLOW "⚠️  Confirmação de Release:"
echo "  Versão: v$VERSION"
echo "  Tipo: $type_name"
echo "  Branch: $current_branch"
echo ""
read -p "Confirma a criação da release? (s/N): " confirm

if [[ ! "$confirm" =~ ^[Ss]$ ]]; then
    print_color $YELLOW "❌ Release cancelada."
    exit 0
fi

# Atualiza CHANGELOG
print_color $BLUE "📝 Atualizando CHANGELOG.md..."
current_date=$(date +%Y-%m-%d)

# Backup do CHANGELOG atual
cp CHANGELOG.md CHANGELOG.md.backup

# Adiciona nova seção de versão
sed -i "s/\[Unreleased\]/[Unreleased]\n\n## [$VERSION] - $current_date/" CHANGELOG.md

print_color $GREEN "✅ CHANGELOG.md atualizado"

# Pede para editar o CHANGELOG
echo ""
print_color $YELLOW "✏️  Edite o CHANGELOG.md para adicionar detalhes da release"
print_color $BLUE "Pressione Enter para abrir o editor..."
read -r

# Abre o editor (nano ou vim)
if command -v nano >/dev/null 2>&1; then
    nano CHANGELOG.md
elif command -v vim >/dev/null 2>&1; then
    vim CHANGELOG.md
else
    print_color $RED "❌ Nenhum editor encontrado. Edite CHANGELOG.md manualmente."
    read -p "Pressione Enter quando terminar de editar..."
fi

# Verifica se o CHANGELOG foi modificado
if ! cmp -s CHANGELOG.md CHANGELOG.md.backup; then
    print_color $GREEN "✅ CHANGELOG.md modificado"
    rm CHANGELOG.md.backup
else
    print_color $YELLOW "⚠️  CHANGELOG.md não foi modificado"
    read -p "Deseja continuar mesmo assim? (s/N): " continue_anyway
    if [[ ! "$continue_anyway" =~ ^[Ss]$ ]]; then
        print_color $YELLOW "❌ Release cancelada."
        exit 0
    fi
    rm CHANGELOG.md.backup
fi

# Commit das mudanças de documentação
print_color $BLUE "💾 Fazendo commit das mudanças de documentação..."
git add CHANGELOG.md README.md DOCUMENTACAO.md 2>/dev/null || true
git commit -m "chore: prepara release v$VERSION

- Atualiza CHANGELOG.md com v$VERSION
- Atualiza documentação para nova versão
- Prepara release notes"

# Cria a tag
print_color $BLUE "🏷️  Criando tag v$VERSION..."
git tag -a "v$VERSION" -m "Release v$VERSION

Tipo: $type_name
Data: $current_date

$(git log --oneline $(git describe --tags --abbrev=0 HEAD^)..HEAD)"

# Push
print_color $BLUE "🚀 Enviando mudanças para o repositório remoto..."
git push origin $current_branch
git push origin --tags

print_color $GREEN "✅ Release v$VERSION criada com sucesso!"

# Informações úteis
echo ""
print_color $BLUE "📊 Resumo da Release:"
echo "- Versão: v$VERSION"
echo "- Tipo: $type_name"
echo "- Data: $current_date"
echo "- Tag: v$VERSION"
echo "- Commit: $(git rev-parse --short HEAD)"

echo ""
print_color $YELLOW "🔗 Próximos passos:"
echo "1. GitHub Actions irá automaticamente:"
echo "   - Criar a release no GitHub"
echo "   - Publicar no registry (se configurado)"
echo "   - Notificar stakeholders (se configurado)"
echo ""
echo "2. Verifique a release em:"
echo "   https://github.com/dtarcisiorbm/ecommerce-spring/releases"
echo ""
echo "3. Compartilhe as novidades com a equipe!"

# Limpeza
echo ""
print_color $BLUE "🧹 Limpando arquivos temporários..."
rm -f CHANGELOG.md.backup 2>/dev/null || true

print_color $GREEN "🎉 Processo de release concluído com sucesso!"
