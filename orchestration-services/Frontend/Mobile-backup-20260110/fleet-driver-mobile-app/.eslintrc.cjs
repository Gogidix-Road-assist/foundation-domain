module.exports = {
  root: true,
  env: {
    node: true,
    jest: true,
    es2022: true
  },
  extends: ['eslint:recommended', 'plugin:react/recommended'],
  plugins: ['react'],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    ecmaFeatures: {
      jsx: true
    }
  },
  settings: {
    react: {
      version: 'detect'
    }
  },
  rules: {
    'react/react-in-jsx-scope': 'off',
    'react/jsx-uses-vars': 'error',
    'no-unused-vars': ['error', { argsIgnorePattern: '^_' }]
  }
};
