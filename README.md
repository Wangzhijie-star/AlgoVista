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

## Windows Notes

If Node and Maven are not configured globally in `Path`, use a temporary PowerShell path:

```powershell
$env:PATH='D:\evm\node;D:\develop\apache-maven-3.9.11\bin;' + $env:PATH
```

PowerShell may block `npm.ps1`; use `npm.cmd` instead:

```powershell
cd C:\Users\Lenovo\Desktop\AlgoVista\代码库\frontend
npm.cmd install
npm.cmd run build
```

If Maven's global local repository is not writable, create a local settings file named `maven-settings.local.xml` and point `localRepository` to a writable directory such as `.m2/repository`. Then run:

```powershell
cd C:\Users\Lenovo\Desktop\AlgoVista\代码库\backend
mvn -s C:\Users\Lenovo\Desktop\AlgoVista\代码库\maven-settings.local.xml test
```
