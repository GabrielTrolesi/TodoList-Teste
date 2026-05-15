# TodoList Full Stack

Aplicacao full stack simples para autenticacao de usuarios e gerenciamento de tarefas.

O projeto foi construido com:

- Backend em Kotlin com Spring Boot e Maven
- Frontend em Astro com componentes React
- Banco de dados SQLite
- Autenticacao com Spring Security, BCrypt e token Bearer opaco

## Visao Geral

O sistema permite:

- cadastro de usuarios
- login
- consulta do usuario autenticado
- criacao de tarefas
- edicao de tarefas
- conclusao de tarefas
- exclusao de tarefas

Cada tarefa pertence a um unico usuario autenticado.

## Como o Sistema Foi Construido

### Backend

O backend segue uma organizacao simples e separada por responsabilidade:

- `controllers` recebem a requisicao e delegam o trabalho
- `services` concentram a regra de negocio
- `repositories` acessam o banco
- `request/response models` isolam a API das entidades persistidas

Principais decisoes:

- senha armazenada com hash BCrypt
- token de autenticacao opaco
- apenas o hash SHA-256 do token e salvo no banco
- principal autenticado montado com dados simples do usuario, sem depender de entidade JPA lazy no contexto de seguranca
- regras de ownership de tarefas garantem que um usuario nao acesse tarefas de outro

### Frontend

O frontend foi estruturado para manter componentes pequenos e reaproveitaveis:

- paginas Astro para rotas
- componentes React para formularios e interacao
- `lib/api.ts` como cliente HTTP centralizado
- `lib/session.ts` para persistencia de sessao no `localStorage`

Principais decisoes:

- login e cadastro usam o mesmo fluxo visual
- sessao persistida em `localStorage`
- redirecionamento para `/todos` apos autenticacao bem-sucedida
- limpeza da sessao apenas quando a API responde `401` ou `403`

## Estrutura do Projeto

```text
.
├── AGENTS.md
├── README.md
├── docker-compose.yml
├── data/
├── frontend/
│   ├── src/
│   │   ├── components/ui/
│   │   ├── features/auth/
│   │   ├── features/todos/
│   │   ├── layouts/
│   │   ├── lib/
│   │   └── pages/
│   ├── package.json
│   └── astro.config.mjs
└── todolist/
    ├── src/main/kotlin/br/com/gabrieltrolesi/todolist/
    │   ├── auth/
    │   ├── config/
    │   ├── shared/
    │   ├── todos/
    │   └── users/
    ├── src/main/resources/
    │   ├── application.properties
    │   └── db/schema.sql
    └── pom.xml
```

## Banco de Dados

O projeto usa SQLite com arquivo local em:

```text
data/todolist.db
```

Tabelas principais:

- `users`
- `auth_tokens`
- `todos`

O schema inicial fica em:

- [schema.sql](/Users/akai/Documents/TodoList%20Teste/todolist/src/main/resources/db/schema.sql)

## Backend

### Stack

- Kotlin
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven
- SQLite

### Pacotes principais

- `auth`: cadastro, login, token e filtro Bearer
- `users`: usuario autenticado e endpoint `/me`
- `todos`: CRUD de tarefas
- `config`: seguranca e beans
- `shared`: excecoes e tratamento global

### Endpoints

Publicos:

- `POST /api/auth/register`
- `POST /api/auth/login`

Autenticados:

- `GET /api/users/me`
- `GET /api/todos`
- `GET /api/todos/{id}`
- `POST /api/todos`
- `PUT /api/todos/{id}`
- `PATCH /api/todos/{id}/complete`
- `DELETE /api/todos/{id}`

## Frontend

### Stack

- Astro
- React
- TypeScript
- Vitest
- Testing Library

### Rotas

- `/login`
- `/register`
- `/todos`

### Sessao

O frontend usa `localStorage` e nao usa cookies para autenticacao.

Chaves atuais:

- `todolist.accessToken`
- `todolist.user`

Importante:

- `localhost` e `127.0.0.1` nao compartilham `localStorage`
- durante os testes, mantenha sempre a mesma origem no navegador

## Como Rodar

### 1. Subir o SQLite com Docker

Na raiz do projeto:

```bash
docker compose up -d sqlite
```

### 2. Rodar o backend

```bash
cd todolist
./mvnw spring-boot:run
```

Backend esperado em:

- `http://127.0.0.1:8080`

Observacao:

- rode o backend a partir da pasta `todolist`
- em IDEs, configure o `working directory` para `todolist`

### 3. Rodar o frontend

```bash
cd frontend
npm install
ASTRO_TELEMETRY_DISABLED=1 npm run dev -- --host 127.0.0.1 --port 4321
```

Frontend esperado em:

- `http://127.0.0.1:4321/login`

Se a porta `4321` estiver ocupada, o Astro pode subir em outra, como `4322`.

## Como Testar

### Backend

```bash
cd todolist
./mvnw test
```

### Frontend

```bash
cd frontend
npm test
```

## Fluxo Recomendado de Validacao Manual

1. Subir o Docker
2. Subir o backend
3. Subir o frontend
4. Abrir `http://127.0.0.1:4321/login`
5. Criar uma conta em `/register`
6. Fazer login
7. Confirmar redirecionamento para `/todos`
8. Criar, editar, concluir e excluir tarefas

## Boas Praticas Adotadas

- classes com responsabilidade pequena
- controllers enxutos
- services com regra de negocio clara
- componentes React separados por feature
- cliente HTTP centralizado
- tratamento de sessao isolado
- testes para fluxos principais de autenticacao e tarefas

## Arquivos Importantes

- [AGENTS.md](/Users/akai/Documents/TodoList%20Teste/AGENTS.md)
- [docker-compose.yml](/Users/akai/Documents/TodoList%20Teste/docker-compose.yml)
- [application.properties](/Users/akai/Documents/TodoList%20Teste/todolist/src/main/resources/application.properties)
- [SecurityConfig.kt](/Users/akai/Documents/TodoList%20Teste/todolist/src/main/kotlin/br/com/gabrieltrolesi/todolist/config/SecurityConfig.kt)
- [AuthService.kt](/Users/akai/Documents/TodoList%20Teste/todolist/src/main/kotlin/br/com/gabrieltrolesi/todolist/auth/AuthService.kt)
- [TodoService.kt](/Users/akai/Documents/TodoList%20Teste/todolist/src/main/kotlin/br/com/gabrieltrolesi/todolist/todos/TodoService.kt)
- [AuthScreen.tsx](/Users/akai/Documents/TodoList%20Teste/frontend/src/features/auth/AuthScreen.tsx)
- [TodoBoard.tsx](/Users/akai/Documents/TodoList%20Teste/frontend/src/features/todos/TodoBoard.tsx)

