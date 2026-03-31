# Scripts de Automação

Este diretório contém scripts para automatizar tarefas comuns do desenvolvimento e seguir boas práticas de Git.

## 📋 Scripts Disponíveis

### 🔧 [commit-and-push.sh](./commit-and-push.sh)
Script interativo para commit e push com validação de Conventional Commits.

**Uso:**
```bash
./scripts/commit-and-push.sh
```

**Funcionalidades:**
- ✅ Validação de Conventional Commits
- 🎨 Interface colorida e amigável
- 📝 Suporte para corpo do commit
- 🌳 Escolha de branch
- 📊 Resumo das operações

### 🏷️ [release.sh](./release.sh)
Script automatizado para criação de releases seguindo Semantic Versioning.

**Uso:**
```bash
./scripts/release.sh
```

**Funcionalidades:**
- 📦 Validação de versão (SemVer)
- 📝 Atualização automática do CHANGELOG.md
- 🏷️ Criação de tags Git
- 🚀 Push para repositório remoto
- ✅ Validações de segurança

### ✅ [validate-commit.sh](./validate-commit.sh)
Script para validar mensagens de commit seguindo Conventional Commits.

**Uso:**
```bash
# Validar mensagem direta
./scripts/validate-commit.sh "feat: adiciona nova funcionalidade"

# Validar último commit
./scripts/validate-commit.sh -c

# Validar de arquivo
./scripts/validate-commit.sh -f commit-message.txt

# Mostrar ajuda
./scripts/validate-commit.sh --help
```

**Funcionalidades:**
- 📝 Validação de padrão Conventional Commits
- 🎯 Verificação de tipos válidos
- 📏 Validação de comprimento da descrição
- 💡 Sugestões e exemplos

### 🔗 [setup-git-hooks.sh](./setup-git-hooks.sh)
Script para configurar Git Hooks automaticamente.

**Uso:**
```bash
./scripts/setup-git-hooks.sh
```

**Hooks Configurados:**
- **commit-msg**: Valida mensagens de commit
- **pre-commit**: Verifica arquivos antes do commit
- **post-commit**: Mostra informações após o commit
- **pre-push**: Verificações finais antes do push

## 🚀 Guia Rápido

### 1. Configuração Inicial
```bash
# Tornar scripts executáveis
chmod +x scripts/*.sh

# Configurar Git Hooks
./scripts/setup-git-hooks.sh
```

### 2. Fluxo de Desenvolvimento
```bash
# 1. Faça suas alterações
# ...

# 2. Commit com validação automática
./scripts/commit-and-push.sh

# 3. Quando pronto para release
./scripts/release.sh
```

### 3. Validação Manual
```bash
# Validar mensagem antes de commit
./scripts/validate-commit.sh "fix: corrige erro de validação"
```

## 📝 Padrão de Commits

### Formato
```
<tipo>(<escopo>): <descrição>

[corpo opcional]

[rodapé opcional]
```

### Tipos Válidos
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Documentação
- `style`: Formatação
- `refactor`: Refatoração
- `test`: Testes
- `chore`: Manutenção
- `perf`: Performance
- `build`: Build
- `ci`: CI/CD
- `revert`: Revert

### Exemplos
```bash
feat(auth): adiciona sistema de login com JWT
fix(order): corrige cálculo incorreto de total
docs(readme): atualiza instruções de instalação
refactor(user): otimiza consulta de usuários
```

## 🔧 Configuração

### Variáveis de Ambiente
Os scripts suportam as seguintes variáveis de ambiente:

```bash
# Editor padrão para edição de arquivos
export EDITOR=nano

# Desabilitar cores (para CI/CD)
export NO_COLOR=1
```

### Personalização
Você pode personalizar os scripts editando-os diretamente:

- **Cores**: Modifique as variáveis no início de cada script
- **Padrões**: Ajuste os padrões de validação
- **Integrações**: Adicione integrações com ferramentas externas

## 🐛 Troubleshooting

### Permissão Negada
```bash
chmod +x scripts/*.sh
```

### Git Hooks Não Funcionando
```bash
# Reconfigurar hooks
./scripts/setup-git-hooks.sh

# Verificar se hooks estão ativos
ls -la .git/hooks/
```

### Scripts Não Encontrados
```bash
# Verificar se está na raiz do projeto
pwd
ls scripts/

# Usar caminho relativo
./scripts/commit-and-push.sh
```

### Validação de Commit Falhando
```bash
# Verificar mensagem manualmente
./scripts/validate-commit.sh "sua mensagem"

# Desativar validação temporariamente
git commit --no-verify -m "mensagem"
```

## 🔄 Integração com IDEs

### VS Code
Adicione ao seu `.vscode/tasks.json`:
```json
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Commit with Validation",
            "type": "shell",
            "command": "./scripts/commit-and-push.sh",
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            }
        },
        {
            "label": "Create Release",
            "type": "shell",
            "command": "./scripts/release.sh",
            "group": "build"
        }
    ]
}
```

### IntelliJ IDEA
1. **File → Settings → Tools → External Tools**
2. **Add** com as seguintes configurações:
   - **Name**: Commit with Validation
   - **Program**: `$ProjectFileDir$/scripts/commit-and-push.sh`
   - **Working directory**: `$ProjectFileDir$`

## 📚 Recursos Adicionais

### Documentação
- [Conventional Commits](https://conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
- [Git Hooks Documentation](https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks)

### Ferramentas Relacionadas
- [Commitizen](https://commitizen-tools.github.io/commitizen/)
- [Husky](https://typicode.github.io/husky/)
- [lint-staged](https://github.com/okonet/lint-staged)

## 🤝 Contribuição

Para contribuir com os scripts:

1. **Teste em ambiente isolado**
2. **Mantenha compatibilidade com POSIX**
3. **Adicione documentação**
4. **Siga o padrão de código existente**

## 📄 Licença

Estes scripts seguem a mesma licença do projeto principal.
