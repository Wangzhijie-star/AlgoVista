# AlgoVista

AlgoVista is an algorithm learning platform for visualizing algorithm execution steps.

## Stack

- Frontend: Vue 3, Vite, Vue Router, Pinia, Axios
- Backend: Spring Boot 3, MyBatis-Plus, MySQL
- Auth: server-side Session

## Local Setup

1. Create a MySQL database named `algovista`.
2. Copy `backend/src/main/resources/application-local.example.yml` to `application-local.yml`.
3. Fill local database credentials if they differ from the default template.
4. Start backend from `backend` with `mvn spring-boot:run`.
5. Start frontend from `frontend` with `npm install` and `npm run dev`.

The local config file is ignored by Git.
