# Roadmap y flujo del proyecto

## Base elegida

- `Pagclientes/VIEW` queda como base visual por su estructura responsive, sidebar, dashboard, ofertas y perfil.
- `LOGR/esoterica` aporta la idea de servicio de autenticacion, registro completo y validaciones.
- `TODO` es la version organizada para continuar el desarrollo sin mezclar los dos prototipos.

## Flujo principal

1. El usuario entra por `/` y ve el login.
2. Puede iniciar sesion con `cliente / 12345`, usar una cuenta creada desde registro, o entrar como invitado.
3. Invitado:
   - Accede al dashboard y carta general.
   - Ve las ofertas VIP bloqueadas.
   - Tiene acceso directo a registrarse desde sidebar y panel VIP.
4. Cliente logueado:
   - Accede al dashboard.
   - Ve el resumen de ofertas VIP.
   - Puede entrar a `/offers` y `/profile`.
5. Al cerrar sesion se limpia la sesion activa y vuelve al login.

## Buenas practicas aplicadas

- Componentes standalone modernos, sin `standalone: true` explicito.
- `ChangeDetectionStrategy.OnPush` en componentes tocados.
- Servicio centralizado `AuthService` para evitar `localStorage` duplicado.
- Formularios reactivos para login y registro.
- Validaciones obligatorias y bloqueo de caracteres especiales en nombre, apellido y usuario.
- Control flow moderno de Angular con `@if` y `@for`.
- Datos repetidos de productos/ofertas movidos a `src/app/data/catalog.ts`.
- Guards para proteger rutas internas y perfil de cliente.

## Siguientes pasos sugeridos

1. Conectar `AuthService` con un backend real cuando tengan API.
2. Agregar mensajes visuales por campo en registro, no solo mensaje general.
3. Crear tests de comportamiento para invitado vs cliente en ofertas y perfil.
4. Extraer tarjetas de producto/oferta a componentes reutilizables si crecen mas pantallas.
5. Revisar accesibilidad con AXE antes de entregar.
