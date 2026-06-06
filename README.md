# Esoterica

Backend Spring Boot para la plataforma de descuentos "Esoterica".

## Requisitos
- Java 17+
- PostgreSQL

## Configuracion rapida
1. Crear base de datos en PostgreSQL:
   - nombre: esoterica
2. Ajustar credenciales en `src/main/resources/application.properties`.

## Ejecutar
```powershell
./gradlew.bat bootRun
```

## Ejecutar con Docker
```powershell
docker compose up --build -d
```

Servicios:
- Frontend Angular: http://localhost:4200
- Backend Spring Boot: http://localhost:8080
- PostgreSQL: localhost:5432

Para detener todo:
```powershell
docker compose down
```

## Endpoints principales
- POST /api/clients/register
- GET /api/clients
- GET /api/clients/{id}
- PUT /api/clients/{id}
- POST /api/discounts
- GET /api/discounts
- PUT /api/discounts/{id}
- DELETE /api/discounts/{id}
- POST /api/codes/generate
- POST /api/codes/consume
- GET /api/menu
- POST /api/menu

## Migraciones
Flyway esta habilitado en `classpath:db/migration`.



Backend: 99999999 / Admin123 devuelve admin.
Backend: 70805544 / Secreto123 devuelve client.
