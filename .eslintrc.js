module.exports = {
  env: {
    es2021: true,
    node: true,
  },
  extends: [
    'airbnb-base',
    'plugin:import/typescript',
  ],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
  },
  plugins: [
    '@typescript-eslint',
  ],
  rules: {
    'no-shadow': 'off',
    '@typescript-eslint/no-shadow': ['error'],
    'import/extensions': 'off',
    'no-unused-vars': 'off',
    '@typescript-eslint/no-unused-vars': ['error'],
    'no-console': ['error', { allow: ['warn', 'error', 'info'] }],
    'import/no-cycle': 'off',
    'no-param-reassign': 'off',
    'import/prefer-default-export': 'off',
    'import/no-unresolved': 'off',
    'linebreak-style': 'off',
    'no-underscore-dangle': 'off',
    'no-plusplus': 'off',
    'no-restricted-syntax': 'off',
    'class-methods-use-this': 'off',
    'no-await-in-loop': 'off',
  },
};
