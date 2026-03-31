#!/bin/bash
# commit-and-push.sh - Script automatizado para commit e push

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

# Verifica se há mudanças
if [[ -n $(git status --porcelain) ]]; then
    print_color $BLUE "🔍 Detectando mudanças..."
    
    # Mostra arquivos modificados
    print_color $YELLOW "📁 Arquivos modificados:"
    git status --porcelain
    
    # Pede o tipo de commit
    echo ""
    print_color $BLUE "Escolha o tipo do commit:"
    echo "1) feat:     Nova funcionalidade"
    echo "2) fix:      Correção de bug"
    echo "3) docs:     Documentação"
    echo "4) refactor: Refatoração"
    echo "5) test:     Testes"
    echo "6) chore:    Manutenção"
    echo "7) style:    Formatação/Estilo"
    
    read -p "Digite o número (1-7): " type_choice
    
    case $type_choice in
        1) type="feat" ;;
        2) type="fix" ;;
        3) type="docs" ;;
        4) type="refactor" ;;
        5) type="test" ;;
        6) type="chore" ;;
        7) type="style" ;;
        *) 
            print_color $RED "❌ Opção inválida. Usando 'feat' como padrão."
            type="feat" 
            ;;
    esac
    
    # Pede o escopo (opcional)
    echo ""
    read -p "Digite o escopo (opcional, ex: auth, customer, order): " scope
    
    # Pede a descrição
    echo ""
    read -p "Digite a descrição do commit: " description
    
    # Monta a mensagem de commit
    if [[ -n "$scope" ]]; then
        commit_message="$type($scope): $description"
    else
        commit_message="$type: $description"
    fi
    
    echo ""
    print_color $BLUE "📝 Commit message: $commit_message"
    
    # Pede se quer adicionar corpo ao commit
    echo ""
    read -p "Deseja adicionar descrição detalhada? (s/N): " add_body
    
    if [[ "$add_body" =~ ^[Ss]$ ]]; then
        echo ""
        print_color $YELLOW "📝 Digite a descrição detalhada (Ctrl+D para finalizar):"
        body=$(cat)
        
        # Cria arquivo temporário com a mensagem completa
        temp_file=$(mktemp)
        echo "$commit_message" > "$temp_file"
        echo "" >> "$temp_file"
        echo "$body" >> "$temp_file"
        
        # Faz o commit com o arquivo temporário
        git commit -F "$temp_file"
        rm "$temp_file"
    else
        # Faz o commit simples
        git add .
        git commit -m "$commit_message"
    fi
    
    # Pede o nome da branch
    echo ""
    read -p "Digite o nome da branch (ou 'main' para principal): " branch_name
    branch_name=${branch_name:-main}
    
    # Push
    print_color $BLUE "🚀 Fazendo push para a branch $branch_name..."
    git push origin $branch_name
    
    print_color $GREEN "✅ Mudanças commitadas e pushadas com sucesso!"
    
    # Mostra informações úteis
    echo ""
    print_color $YELLOW "📊 Resumo:"
    echo "- Commit: $(git rev-parse --short HEAD)"
    echo "- Branch: $branch_name"
    echo "- Mensagem: $commit_message"
    
    # Se for main/master, sugere criar PR
    if [[ "$branch_name" != "main" && "$branch_name" != "master" ]]; then
        echo ""
        print_color $BLUE "💡 Dica: Abra um Pull Request para mergear na branch principal"
    fi
    
else
    print_color $YELLOW "⚠️  Nenhuma mudança detectada no working directory."
    
    # Verifica se há commits não pushados
    if [[ -n $(git log origin/$branch_name..HEAD --oneline 2>/dev/null) ]]; then
        print_color $BLUE "📤 Detectados commits não pushados. Deseja fazer push agora? (s/N)"
        read -p "> " push_commits
        
        if [[ "$push_commits" =~ ^[Ss]$ ]]; then
            current_branch=$(git branch --show-current)
            git push origin $current_branch
            print_color $GREEN "✅ Commits não pushados foram enviados com sucesso!"
        fi
    fi
fi

echo ""
print_color $BLUE "🎉 Script concluído!"
