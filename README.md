 

Documentación de la librería de conexiones a bases de datos desde java
Autores:
Jhashua Canton 
Joshua Barrantes
Librería con la capacidad de insertar objetos de java a una de las bases de datos que la misma librería permite utilizar. 
1 – Conexión con Oracle
1.1 - Constructores de la conexión con Oracle
-	Opción #1:  Crear objeto enviando los datos necesarios de la conexión.
-	Datos necesarios: (“usuario”, “contraseña”, “host”, “puerto”, “edición”).
Ejemplo: 
OracleConnection nueva_Conexion = new OracleConnection (“user”, “1234”, “localhost”, “1521”, “XE”);
-	Opción #2: Crear objeto enviando un objeto tipo Connection ya definida en la librería.
-	Datos necesarios: (“usuario”, “contraseña”, “host”, “puerto”, “edición”).
Ejemplo: 
Connection conn = new Connection (“user”, “1234”, “localhost”, “1521”, “XE”);
OracleConnection nueva_conexion = new OracleConnection (conn);
•	Nota: si la conexión tiene algún dato erróneo, ninguna de las funciones de la librería funcionara al momento de ser llamadas.
 
1.2 – Funciones de la conexión con Oracle.
1.2.1 – Enviar objetos con la función sendObject().
-	Datos necesarios: (objeto_cualquiera).
Ejemplo:
Persona nueva_Persona = new Persona (“123456789”, “José Gutiérrez”);
nueva_conexion.sendObject (nueva_Persona);
•	Nota: si al buscar la tabla del objeto, esta no existe, esta función creara la tabla, la función busca la primera variable que termine con "_id" para definirla como PRIMARY KEY en la tabla, de no cumplirse, se creara la tabla de igual forma, pero no creara ninguna variable en la tabla como PRIMARY KEY (puede afectar negativamente la experiencia de usuario).

1.2.2 – Actualizar objetos con la función updateObject(). 
-	Datos necesarios: (objeto_Desactualizado, objeto_Actualizado).
Ejemplo:
Persona nueva_Persona = new Persona (“123456789”, “José Gutiérrez”);
Persona otra_Persona = new Persona (“123456789”, “José Alberto”);
nueva_conexion.updateObject (nueva_Persona, otra_Persona);
•	Nota: Esto obliga al usuario a crear una copia del objeto previo a su modificación en el código, Ambos objetos deben ser del mismo tipo, de no cumplirse esto, la base de datos no actualizará ningún valor, además, si el objeto sin actualizar no se encuentra en la base de datos, no actualizará ni insertará ningún objeto.
 
1.2.3 – Obtener todos los objetos de una tabla con la función getAllObjects().
-	Datos necesarios: (clase_Del_Objeto.class).
Ejemplo: 
List<Persona> = getAllObjects(Persona.class);
•	Nota: la clase debe tener el constructor sin parámetros declarado de forma obligatoria, además, la función retornara un objeto de tipo List<clase_Del_Objeto> del mismo tipo del objeto que envió a buscar.

1.2.4 – Obtener un objeto especifico de una tabla con la función getObject().
-	Datos necesarios: (clase_Del_Objeto.class, “parametro_Para_Buscar”).
Ejemplo:
Persona traer = nueva_Conexion.getObject (Persona.class, “123456789”);
•	Nota: La función buscara un objeto en la tabla cuyo parámetro de algunos de los campos de la columna de la tabla establecida como llave primaria sea igual al parámetro ingresado en la función, si la tabla no tiene llave primaria, realizara la misma búsqueda, pero a partir del primer atributo declarado en la clase recibida como parámetro en esta función.

1.2.5 – Eliminar un objeto de una tabla con la función deleteObject().
-	Datos necesarios: (“objeto_A_Eliminar”).
Ejemplo:
Persona persona_Existente = new Persona (“123456789”, “José Alberto”);
nueva_Conexion.deleteObject (persona_Existente);
•	Nota: si el objeto no existe en la tabla, no se eliminará ningún objeto de la tabla.
2 – Conexión con MongoDB
2.1 – Constructores de la conexión con MongoDB
-	Opción #1: Crear objeto enviando los datos necesarios de la conexión
Ejemplo: 
MongoConnection nueva_Conexion = new MongoConnection (“localhost”, “1521”, “my_Database”);
•	Nota: si la conexión tiene algún dato erróneo, ninguna de las funciones de la librería funcionara al momento de ser llamadas.
 
2.2 – Funciones de la conexión con MongoDB
2.2.1 – Enviar objetos con la función sendObject().
-	Datos necesarios: (objeto_cualquiera).
Ejemplo:
Persona nueva_Persona = new Persona (“123456789”, “José Gutiérrez”);
nueva_conexion.sendObject (nueva_Persona);
•	Nota: La función busca la variable que posea el mismo nombre que la clase seguida del complemento “_id”, para convertirlo en el identificador único de la colección de la base de datos, si no hay una variable con esta característica, la base de datos de Mongo creara su propio identificador, el cual es complicado de manejar para el usuario (se recomienda de forma forzada que exista una variable con la característica mencionada previamente).

2.2.2 – Actualizar objetos con la función updateObject(). 
-	Datos necesarios: (objeto_Actualizado).
Ejemplo:
Persona nueva_Persona = new Persona (“123456789”, “José Gutiérrez”);
nueva_conexion.updateObject (nueva_Persona);
•	Nota: La función realiza la búsqueda del objeto por medio de la variable asignada como identificador del objeto (nombre de la clase + “_id”) para actualizar sus valores “a excepción del identificador”, si no existe una variable asignada como identificador, la actualización de los valores no surtirá efecto.
 
2.2.3 – Obtener todos los objetos de una tabla con la función getAllObjects().
-	Datos necesarios: (clase_Del_Objeto.class).
Ejemplo: 
List<Persona> = getAllObjects(Persona.class);
•	Nota: la clase debe tener el constructor sin parámetros declarado de forma obligatoria, además, la función retornara un objeto de tipo List<clase_Del_Objeto> del mismo tipo del objeto que envió a buscar.

2.2.4 – Obtener un objeto especifico de una tabla con la función getObject().
-	Datos necesarios: (clase_Del_Objeto.class, “parametro_Para_Buscar”).
Ejemplo:
Persona traer = nueva_Conexion.getObject (Persona.class, “123456789”);
•	Nota: La función buscara un objeto en la colección cuyo parámetro asignado como identificador (nombre de la clase + “_id”) sea igual al parámetro ingresado en la función, si la colección no tiene un identificador asignado, la búsqueda no surgirá efecto y retornara un objeto tipo NULL (revisar la nota del punto 2.2.1).

 
2.2.5 – Eliminar un objeto de una tabla con la función deleteObject().
-	Datos necesarios: (clase_Del_Objeto.class, “parametro_Para_Buscar”).
Ejemplo:
nueva_Conexion.deleteObject (Persona.class, “123456789”);
•	Nota: La función buscara un objeto en la colección cuyo parámetro asignado como identificador (nombre de la clase + “_id”) sea igual al parámetro ingresado en la función, si la colección no tiene un identificador asignado, la búsqueda no surgirá efecto y no eliminara ningún objeto (revisar la nota del punto 2.2.1).
3 – Conexión con MySQL
1.1 - Constructores de la conexión con MySQL
•	Opción #1: Crear objeto enviando los datos necesarios de la conexión.
o	Datos necesarios: ("usuario", "contraseña", "host", "puerto", "edición").
o	Ejemplo:
MySQLConnection nueva_Conexion = new MySQLConnection("user", "1234", "localhost", "3306", "miDB");
•	Opción #2: Crear objeto enviando un objeto tipo Connection ya definida en la librería.
o	Datos necesarios: (Connection).
o	Ejemplo:
Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/miDB", "user", "1234");
MySQLConnection nueva_Conexion = new MySQLConnection(conn);
•	Nota: si la conexión tiene algún dato erróneo, ninguna de las funciones de la librería funcionará al momento de ser llamadas.
1.2 – Funciones de la conexión con MySQL
1.2.1 – Enviar objetos con la función sendObject()
•	Datos necesarios: (objeto_cualquiera).
•	Ejemplo:
java
Copiar código
Persona nueva_Persona = new Persona("123456789", "José Gutiérrez");
nueva_Conexion.sendObject(nueva_Persona);
Nota: Si al buscar la tabla del objeto, esta no existe, la función creará la tabla. La función busca la primera variable que termine con "_id" para definirla como PRIMARY KEY en la tabla. De no cumplirse, se creará la tabla de igual forma, pero no se definirá ninguna variable como PRIMARY KEY (puede afectar negativamente la experiencia de usuario).
1.2.2 – Actualizar objetos con la función updateObject()
•	Datos necesarios: (objeto_Desactualizado, objeto_Actualizado).
•	Ejemplo:
Persona nueva_Persona = new Persona("123456789", "José Gutiérrez");
Persona otra_Persona = new Persona("123456789", "José Alberto");
nueva_Conexion.updateObject(nueva_Persona, otra_Persona);
Nota: Esto obliga al usuario a crear una copia del objeto previo a su modificación en el código. Ambos objetos deben ser del mismo tipo. Si el objeto sin actualizar no se encuentra en la base de datos, no se actualizará ni insertará ningún objeto.
1.2.3 – Obtener todos los objetos de una tabla con la función getAllObjects()
•	Datos necesarios: (clase_Del_Objeto.class).
•	Ejemplo:
List<Persona> personas = nueva_Conexion.getAllObjects(Persona.class);
Nota: La clase debe tener el constructor sin parámetros declarado de forma obligatoria. La función retornará un objeto de tipo List<clase_Del_Objeto> del mismo tipo del objeto que se envió a buscar.
1.2.4 – Obtener un objeto específico de una tabla con la función getObject()
•	Datos necesarios: (clase_Del_Objeto.class, "parametro_Para_Buscar").
•	Ejemplo:
Persona traer = (Persona) nueva_Conexion.getObject(Persona.class, "123456789");
Nota: La función buscará un objeto en la tabla cuyo parámetro de algunos de los campos de la columna de la tabla establecida como llave primaria sea igual al parámetro ingresado en la función. Si la tabla no tiene llave primaria, realizará la misma búsqueda, pero a partir del primer atributo declarado en la clase recibida como parámetro en esta función.
1.2.5 – Eliminar un objeto de una tabla con la función deleteObject()
•	Datos necesarios: (objeto_A_Eliminar).
•	Ejemplo:
Persona persona_Existente = new Persona("123456789", "José Alberto");
nueva_Conexion.deleteObject(persona_Existente);
Nota: Si el objeto no existe en la tabla, no se eliminará ningún objeto de la tabla.
