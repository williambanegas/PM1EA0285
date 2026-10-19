Aplicacion Agenda de Contactos

El objetivo de esta aplicacion es que se pueda registrar, eliminar, modificar, compartir y llamar 
a los contactos que se registren; cada contacto tendra su fotografia, pais de origen, numero de telefono 
y nota.

Se aplicara el patron MVC + DAO, para guardar los contactos con toda la informacion ingresada se hara usando SQLite.

La aplicacion estara compuesta por 3 pantallas que seran las siguientes:

1.  Pantalla de registro.
2.  Pantalla Lista de contactos.
3.  Pantalla llamar a contacto.

Cada pantalla se desglosara de la siguiente forma:

1. Pantalla de registro

- Habra un cuadro donde se muestre la foto del contacto que se esta registrando ( este debera abrir la camara del celular y capturar la foto del contacto; debera mostrar una vez tomada la fotografia si acepta o rechaza la imagen).
- Se pedira que seleccione el codigo de pais del contacto, (tendra las siguientes opciones; Honduras (+504), Costa Rica (+506), El Salvador (+503), Guatemala (+502) ).
- Se pedira el nombre del contacto.
- Se pedira el numero de telefono del contacto.
- Se pedira una nota para el contacto.
- Esta pantalla contendra 3 botones, guardar, limpiar y contactos salvados (que se dirigira a la pantalla 2).

* Alertas dentro de pantalla registro
- Alerta si no ingresa nombre del contacto.
- Alerta si no ingresa numero de telefono del contacto.
- Alerta si no selecciona pais de origen.
- Alerta debe ingresar una nota.
- Validar que el numero de telefono sea valido (8 digitos) , solo numeros.
- Validar que el nombre del contacto solo contenga letras.

2. Pantalla Lista de contactos
- Debe Desglosar en una lista todos los contactos guardados en el siguiente formato (Nombre y Numero).
- Debe haber un boton para regresar a la pantalla 1.
- Debe poder hacer una busqueda del contacto por nombre o numero.
- Debe haber un boton para eliminar un contacto seleccionado
- Debe haber un boton para modificar un contacto seleccionado
- Debe haber un boton para compartir un contacto seleccionado entre aplicaciones usando (Intent.Action_Send).
- Debe haber un boton para ver la imagen del contacto seleccionado.
- Debe haber un boton para llamar a un contacto.
- Cada accion debe mostrar un mensaje de confirmacion.

3. Pantalla llamar a contacto
- Se debe marcar al contacto seleccionado con Action_Call y solicitar el permiso de llamada con Call_Phone en tiempo de ejecucion.
- Confirmar la llamada con un AlertDialog.Builder(¿Desea llamar a "Nombre del contacto"?).

Verifica que la aplicacion funcione correctamente, y crea un Reporte.md con los cambios realizados.










