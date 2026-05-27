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

