# Reporte de Implementación y Mejoras - Aplicación Agenda de Contactos

Se ha desarrollado e implementado exitosamente la aplicación **Agenda de Contactos** siguiendo los lineamientos detallados en `Promp.md` y aplicando buenas prácticas de desarrollo Android (Arquitectura MVC + DAO, Material Design y APIs modernas).

---

## 1. Arquitectura y Componentes Implementados

- **Modelo (`Contact.java`)**: Entidad de datos que encapsula ID, Nombre, Código de País, Teléfono, Nota y Ruta de Fotografía.
- **DAO (`ContactDao.java` y `DatabaseHelper.java`)**: Patrón DAO para la gestión de la base de datos SQLite, implementando operaciones CRUD para contactos y gestión de **Países** (`countries` table) con precarga inicial (Honduras, Costa Rica, El Salvador, Guatemala).
- **Adaptador (`ContactAdapter.java`)**: Adaptador personalizado para `RecyclerView` que gestiona la selección visual de contactos con alto contraste de color.
- **Pantallas (Activities)**:
  1. **Pantalla de Registro (`MainActivity.java` / `activity_main.xml`)**:
     - Registro de nuevos contactos y edición de existentes, captura de fotografía con la cámara con diseño circular, selector dinámico de código de país.
     - **Imagen Predeterminada (`imagen_default`)**: Se muestra `imagen_default.jpg` como perfil inicial y se reemplaza al capturar una nueva foto.
     - **Gestión de Países**: Botón `+ País` para registrar nuevos países en SQLite y actualizar el `Spinner` en tiempo real.
     - **Estilo Visual Optimizado (Selector de País con Fondo Blanco y Texto Negro)**: Se configuró un adaptador personalizado para el `Spinner` de países asegurando que tanto las opciones desplegables como la selección actual tengan un **fondo blanco (`Color.WHITE`) y texto en color negro y negrita (`Color.BLACK`, `Typeface.BOLD`)**, evitando problemas de contraste o mezcla de colores.
  2. **Pantalla Lista de Contactos (`ContactListActivity.java` / `activity_contact_list.xml`)**:
     - Desglose de contactos guardados mostrando **Nombre y Número de Teléfono** en tarjetas (`CardView`) con alto contraste visual (`item_contact.xml`).
     - Buscador en tiempo real por nombre o número.
     - Panel de botones de action **inferiores organizados en 2 filas y posicionados ergonómicamente**.
     - Botón "← Registrar" optimizado para limpiar y reiniciar la Pantalla 1.
  3. **Pantalla Llamar a Contacto (`CallActivity.java` / `activity_call.xml`)**:
     - Vista centrada con foto circular de perfil (cargando `imagen_default` si el contacto no posee foto), nombre y teléfono destacados, solicitud de permisos en tiempo de ejecución (`CALL_PHONE`), diálogo de confirmación `AlertDialog.Builder` ("¿Desea llamar a [Nombre]?") e inicio de llamada mediante `Intent.ACTION_CALL`.

---

## 2. Fondo de Pantalla y Diseño Visual Profesional

- **Imagen de Fondo (`imagen_principal.jpg`)**: Configurada en las pantallas con contenedores translúcidos para garantizar legibilidad óptima.
- **Seguridad y Robustez**: Uso de `FileProvider`, `ActivityResultLauncher`, etiquetas `<queries>` para cámara, y manejo seguro de SQLite mediante DAO.
