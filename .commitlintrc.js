module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [
      2,
      'always',
      [
        'feat',     // Nova funcionalidade
        'fix',      // Correção de bug
        'docs',     // Documentação
        'style',    // Formatação, ponto-e-vírgula, etc.
        'refactor',  // Refatoração de código
        'perf',      // Melhoria de performance
        'test',      // Adição ou correção de testes
        'build',     // Mudanças no sistema de build
        'ci',        // Mudanças na configuração de CI
        'chore',     // Mudanças que não alteram src ou test files
        'revert'     // Revert de commits anteriores
      ]
    ],
    'type-case': [2, 'always', 'lower-case'],
    'type-empty': [2, 'never'],
    'scope-case': [2, 'always', 'lower-case'],
    'scope-empty': [2, 'never'],
    'subject-case': [2, 'never', ['lower-case', 'upper-case', 'pascal-case', 'start-case']],
    'subject-empty': [2, 'never'],
    'subject-full-stop': [2, 'never', '.'],
    'subject-max-length': [2, 'always', 50],
    'header-max-length': [2, 'always', 72],
    'body-leading-blank': [1, 'always'],
    'body-max-line-length': [2, 'always', 72],
    'footer-leading-blank': [1, 'always'],
    'footer-max-line-length': [2, 'always', 72]
  }
};
