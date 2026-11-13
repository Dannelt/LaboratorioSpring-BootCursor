# Prompts utilizados — Nexo, laboratorio bancario

**Proyecto:** LaboratorioSpring-BootCursor · Nexo
**Institución:** Tecnológico de Antioquia
**Herramientas:** ChatGPT (planeación y redacción de prompts) y Codex en VS Code (análisis, implementación y pruebas).

> **Trazabilidad:** Este documento es una recopilación organizada de las instrucciones utilizadas durante el desarrollo. Puede contener diferencias de formato respecto a los mensajes originales, como espaciado, listas o presentación de enlaces; no se presenta como una transcripción literal certificada. En esta revisión se conservan íntegramente los diez bloques de prompts recopilados, sin adaptar sus instrucciones técnicas al proyecto final.

**Resultados y evidencias:** Los resúmenes fuera de los bloques de prompts distinguen los resultados reportados por el estudiante de las ejecuciones realizadas desde Codex. Los archivos pendientes se registran en [el índice de evidencias](evidencias/INDICE.md); no se consideran adjuntos hasta incorporarlos.

| Etapa | Contenido |
|---|---|
| 1 | Validación de Spring Boot y reparación de Java/Maven |
| 2 | PostgreSQL y conexión con Spring Boot |
| 3 | Entidades y repositorios JPA |
| 4 | Servicios y lógica de transferencias |
| 5 | API REST y Swagger |
| 6 | Frontend React y TypeScript |
| 7 | Comprobantes y códigos QR |
| 8 | Documentación y entrega |

---

## Etapa 1 — Configuración inicial

### Prompt 1A — Diagnóstico

```text
Estamos desarrollando desde cero un laboratorio universitario FullStack de transferencias bancarias.

Ya generé el proyecto utilizando Spring Initializr con las siguientes tecnologías:

- Java 25
- Spring Boot 4.1.2
- Maven
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Validation
- Spring Boot DevTools

El proyecto está ubicado dentro de un repositorio Git clonado desde GitHub.

**PRIMERA ETAPA: ANÁLISIS Y VALIDACIÓN DEL ENTORNO**

Quiero que realices lo siguiente:

1. Analiza la estructura del proyecto Spring Boot.
2. Revisa el archivo pom.xml y verifica las dependencias y sus versiones.
3. Comprueba que Java 25 y Maven estén correctamente configurados.
4. Verifica que la estructura del proyecto siga las convenciones de Spring Boot.
5. Comprueba que no exista una carpeta adicional innecesaria que contenga otro proyecto anidado.
6. Ejecuta una compilación con Maven para verificar que el código inicial compile.
7. Identifica la configuración necesaria para conectar PostgreSQL.
8. Propón una estructura por capas sencilla para nuestro sistema bancario: controller, service, repository, entity, dto, exception y config.

**REGLAS DEL PROYECTO:**
- Utilizar nombres de clases, métodos y atributos en inglés.
- Utilizar Java Records para los DTO.
- Documentar métodos públicos con Javadoc.
- Respetar las buenas prácticas de Java 25 y Spring Boot 4.
- No implementar todavía las funcionalidades bancarias.
- No crear todavía las entidades ni las tablas.
- No modificar credenciales ni inventar contraseñas.
- No modificar el README existente.
- No realizar commits ni publicar cambios en GitHub.

Al finalizar, explícame en español:

1. Si el proyecto está correctamente configurado.
2. Si la compilación fue exitosa.
3. Qué estructura encontraste.
4. Qué necesitamos configurar para PostgreSQL.
5. Cuál debería ser nuestro siguiente paso.

No avances a otra etapa sin mi autorización.
```

**Resultado reportado:** Estructura correcta; Java 17 activo y fallo del Maven Wrapper impedían la compilación.

### Prompt 1B — Corrección del entorno

```text
Continuemos con la etapa 1 del laboratorio bancario.

Ya realizaste el diagnóstico inicial. Ahora necesito solucionar los problemas detectados para conseguir una compilación exitosa.

**TAREA 1: JAVA 25**

1. Busca si JDK 25 está instalado en Windows.
2. Si está instalado, identifica su ubicación exacta.
3. Configura JAVA_HOME y PATH para utilizar Java 25 en la terminal actual, sin modificar globalmente la configuración de otros proyectos.
4. Verifica con `java -version` y `javac -version`.
5. Si Java 25 no está instalado, indícame cómo instalarlo y detente antes de continuar.

**TAREA 2: MAVEN WRAPPER**

1. Analiza el error `No se puede indizar en una matriz nula` relacionado con `.Target[0]`.
2. Comprueba el estado de la carpeta `.m2` y del script `mvnw.cmd`.
3. Identifica la causa antes de realizar modificaciones.
4. Corrige el problema mediante el cambio mínimo y seguro, conservando el Maven Wrapper y la versión configurada cuando sea posible.
5. No elimines dependencias, archivos personales ni configuraciones de otros proyectos.

**TAREA 3: VALIDACIÓN**

Después de corregir los problemas:

1. Ejecuta `.\mvnw.cmd -version`.
2. Confirma que Maven utiliza Java 25.
3. Ejecuta `.\mvnw.cmd -B compile`.
4. Si compila correctamente, verifica la existencia de los archivos compilados.
5. Si falla, analiza y explica el error antes de aplicar otra solución.

**REGLAS:**

- No implementes todavía funcionalidades bancarias.
- No configures todavía PostgreSQL.
- No modifiques el README.
- No realices commits ni push.
- No cambies versiones innecesariamente.
- Explica brevemente cada modificación.

Al finalizar, proporciona un resumen de los problemas encontrados, las correcciones realizadas y los resultados de las verificaciones.

Nuestro objetivo es terminar correctamente la etapa 1 antes de comenzar la etapa 2.
```

**Resultado reportado:** JDK 25.0.5 y Maven Wrapper 3.3.4/Maven 3.10.0; `BUILD SUCCESS`.

---

## Etapa 2 — PostgreSQL

### Prompt 2A — Diagnóstico de PostgreSQL

```text
Continuemos con la ETAPA 2 del laboratorio universitario de transferencias bancarias.

La etapa 1 terminó correctamente:
- Java 25.0.5 funcionando.
- Maven 3.10.0 funcionando.
- Spring Boot 4.1.2.
- Compilación BUILD SUCCESS.

**OBJETIVO: Configurar PostgreSQL y verificar su conexión con Spring Boot.**

TAREA 1: VERIFICAR POSTGRESQL

1. Comprueba si PostgreSQL está instalado y disponible en Windows.
2. Identifica su versión, servicio y puerto.
3. Comprueba si el servidor está ejecutándose.
4. Si PostgreSQL no está instalado, explícame cómo instalarlo y detente.
5. No modifiques ni elimines bases de datos existentes.

TAREA 2: BASE DE DATOS

1. Comprueba si existe una base de datos llamada `laboratoriobanco`.
2. Si no existe, créala utilizando PostgreSQL o proporciona el procedimiento para crearla mediante pgAdmin.
3. Utiliza un usuario con permisos adecuados.
4. No inventes contraseñas ni muestres credenciales en la respuesta.
5. Si necesitas credenciales que no están disponibles, solicita que las configure localmente.

TAREA 3: CONFIGURACIÓN SPRING BOOT

Configura el archivo `application.properties` para:

- Establecer conexión con PostgreSQL.
- Utilizar variables de entorno para URL, usuario y contraseña.
- Mostrar las sentencias SQL de Hibernate en consola.
- Formatear las sentencias SQL.
- Evitar Open Session in View.
- Permitir actualización automática del esquema mediante Hibernate únicamente en el entorno local de desarrollo, siguiendo el laboratorio del profesor.

Utiliza configuraciones compatibles con Spring Boot 4.1.2.

No guardes contraseñas reales en archivos versionados por Git.

TAREA 4: VERIFICACIÓN

1. Confirma la conexión con PostgreSQL.
2. Verifica que Spring Boot pueda iniciar correctamente.
3. Comprueba si existen errores relacionados con JPA o Hibernate.
4. Ejecuta las pruebas automatizadas disponibles, si el entorno lo permite.
5. Si alguna prueba falla, explica su causa y corrige únicamente los problemas relacionados con esta etapa.

REGLAS:

- No crear todavía entidades bancarias.
- No implementar transferencias.
- No crear controladores ni endpoints nuevos.
- No desarrollar frontend.
- No modificar bases de datos ajenas al laboratorio.
- No modificar el README existente.
- No realizar commits ni push.
- Mantener la arquitectura y las dependencias actuales.
- Utilizar Java 25 al ejecutar Maven y Spring Boot.

Al finalizar, entrégame:

1. Estado de PostgreSQL.
2. Nombre de la base de datos creada o encontrada.
3. Configuración realizada en Spring Boot.
4. Resultado de la conexión.
5. Resultado de las pruebas.
6. Problemas detectados, si existen.
7. Confirmación de si podemos avanzar a la etapa 3.

Explícame los conceptos principales de manera breve y sencilla. No avances a la siguiente etapa sin autorización.
```

**Resultado reportado:** PostgreSQL no estaba instalado. Se instaló PostgreSQL 17 y se creó manualmente `laboratoriobanco` desde pgAdmin.

### Prompt 2B — Conexión de Spring Boot

```text
Continuemos con la ETAPA 2 de nuestro laboratorio universitario de transferencias bancarias.

Ya instalé PostgreSQL 17 y creé manualmente la base de datos `laboratoriobanco` mediante pgAdmin 4.

La base de datos está disponible en el servidor local y podemos administrarla correctamente.

**OBJETIVO: Configurar y comprobar la conexión entre Spring Boot 4.1.2 y PostgreSQL 17.**

1. Verifica que el servidor PostgreSQL esté funcionando y confirma su puerto, normalmente 5432.

2. Configura `application.properties` utilizando variables de entorno para:
   - URL: `jdbc:postgresql://localhost:5432/laboratoriobanco`
   - Usuario: `postgres`
   - Contraseña: mediante variable de entorno, sin escribirla directamente en archivos versionados.

3. Configura Hibernate/JPA para:
   - Actualizar automáticamente el esquema únicamente en desarrollo.
   - Mostrar las consultas SQL en consola.
   - Formatear las sentencias SQL.
   - Desactivar Open Session in View.

4. Verifica que el driver PostgreSQL esté correctamente declarado en Maven.

5. Utiliza Java 25 para compilar e iniciar la aplicación.

6. Ejecuta la aplicación y confirma que se establezca la conexión con PostgreSQL.

7. Ejecuta las pruebas disponibles y verifica sus resultados.

**REGLAS:**
- No crear todavía entidades ni tablas bancarias.
- No implementar transferencias.
- No crear controladores ni frontend.
- No modificar bases de datos diferentes de `laboratoriobanco`.
- No mostrar ni almacenar contraseñas en Git.
- Si necesitas credenciales, solicita configurarlas localmente de forma segura.
- No modificar README.
- No realizar commits ni push.

Al finalizar, explícame:

1. Qué archivos modificaste.
2. Cómo funciona la conexión entre Spring Boot y PostgreSQL.
3. Cómo se protegieron las credenciales.
4. Si la aplicación inició correctamente.
5. Qué pruebas se ejecutaron y sus resultados.
6. Si podemos continuar con la etapa 3.

No avances de etapa sin autorización.
```

**Resultado reportado:** Conexión JPA con variables de entorno y perfil `dev`, `contextLoads()` aprobado e inicio de Spring Boot en 8080.

---
## Etapa 3 — Entidades y repositorios JPA

### Prompt 3 — Modelo de datos bancario

```text
Continuemos con la ETAPA 3 del laboratorio universitario de transferencias bancarias.

Las etapas anteriores terminaron correctamente:
- Java 25 y Spring Boot 4.1.2.
- PostgreSQL 17.12 funcionando.
- Base de datos `laboratoriobanco` conectada.
- Maven BUILD SUCCESS.
- Spring Boot funcionando en el puerto 8080.
- Prueba contextLoads exitosa.

OBJETIVO: Crear el modelo de datos y los repositorios del sistema bancario.

**TAREA 1: ESTRUCTURA**

Crea los paquetes necesarios dentro de `com.tdea.laboratoriobanco`:

- entity
- repository
- service
- controller
- dto
- exception
- config

No crees todavía servicios ni controladores funcionales.

**TAREA 2: ENTIDADES JPA**

Diseña e implementa tres entidades:

1. BankCustomer:
   - id
   - fullName
   - email

2. BankAccount:
   - id
   - accountNumber (único)
   - balance (BigDecimal)
   - customer (relación con BankCustomer)
   - version (para apoyar el control de concurrencia)

3. BankTransfer:
   - id
   - reference (UUID único para identificar la transferencia)
   - sourceAccount
   - destinationAccount
   - amount (BigDecimal)
   - createdAt (Instant)
   - status (enum apropiado)

Utiliza claves primarias autogeneradas y relaciones JPA correctamente definidas.

Consideraciones:
- Un cliente puede tener varias cuentas.
- Una cuenta puede participar en múltiples transferencias como origen o destino.
- Los importes monetarios deben utilizar BigDecimal, nunca double ni float.
- Define restricciones NOT NULL, UNIQUE y precisiones monetarias apropiadas.
- Evita relaciones bidireccionales innecesarias y problemas de serialización.
- Usa nombres en inglés.
- Implementa constructores, métodos y anotaciones necesarios para JPA.
- Documenta los métodos públicos mediante Javadoc.

**TAREA 3: REPOSITORIOS**

Crea interfaces Spring Data JPA que extiendan JpaRepository para las tres entidades.

Agrega métodos de consulta adecuados para:
- Buscar clientes por correo electrónico.
- Buscar cuentas por número de cuenta.
- Obtener las transferencias asociadas a una cuenta, tanto enviadas como recibidas, ordenadas cronológicamente.

No implementes todavía la lógica de transferencias.

**TAREA 4: VALIDACIONES**

1. Comprueba que el proyecto compile utilizando Java 25.
2. Ejecuta las pruebas existentes.
3. Inicia la aplicación con el perfil dev y las variables de entorno ya configuradas.
4. Comprueba que Hibernate cree las tablas correspondientes en `laboratoriobanco`.
5. Verifica las claves primarias, foráneas y restricciones.
6. Si puedes, incorpora pruebas de persistencia sencillas, sin modificar ni eliminar información ajena.

**REGLAS IMPORTANTES**

- No implementar transferencias monetarias todavía.
- No modificar el frontend.
- No implementar autenticación todavía.
- No crear datos bancarios ficticios automáticamente.
- No escribir contraseñas en el código.
- No modificar el README.
- No realizar commits ni push.
- No eliminar tablas o información existente.
- Mantener una arquitectura sencilla por capas.
- No agregar librerías innecesarias.

Al finalizar, explícame brevemente:

1. Las clases creadas.
2. La función de cada entidad.
3. Las relaciones entre las tablas.
4. Los repositorios implementados.
5. Los resultados de compilación y pruebas.
6. Las tablas generadas en PostgreSQL.
7. Qué debemos hacer en la etapa 4.

Detente después de terminar esta etapa.
```

**Resultado reportado:** Implementación de `BankCustomer`, `BankAccount`, `BankTransfer`, `TransferStatus` y tres repositorios. Hibernate creó las tres tablas y sus restricciones en la base local.

---

## Etapa 4 — Servicios y transferencias

### Prompt 4 — Reglas bancarias, transacciones y pruebas

```text
Continuemos con la ETAPA 4 del laboratorio universitario de transferencias bancarias.

**Estado actual del proyecto:**
- Java 25.
- Spring Boot 4.1.2.
- Maven 3.10.0.
- PostgreSQL 17.
- Base de datos `laboratoriobanco`.
- Entidades BankCustomer, BankAccount y BankTransfer.
- Enum TransferStatus.
- Tres repositorios Spring Data JPA.
- Tres tablas generadas correctamente.
- Prueba contextLoads exitosa.
- Aplicación iniciada correctamente.

**OBJETIVO DE ESTA ETAPA:**

Implementar la lógica de negocio necesaria para realizar transferencias bancarias seguras y consistentes, sin desarrollar todavía los controladores REST ni el frontend.

### TAREA 1. SERVICIOS

Implementa los servicios necesarios para gestionar las cuentas bancarias y realizar transferencias.

Mantén una arquitectura por capas sencilla, sin crear clases innecesarias.

El servicio de transferencias debe:

1. Recibir la cuenta de origen, cuenta de destino e importe.
2. Verificar que ambas cuentas existan.
3. Impedir transferencias hacia la misma cuenta.
4. Comprobar que el importe sea mayor que cero.
5. Verificar que la cuenta de origen tenga saldo suficiente.
6. Descontar el importe de la cuenta de origen.
7. Incrementar el saldo de la cuenta de destino.
8. Registrar la transferencia con UUID, fecha, importe y estado COMPLETED.
9. Devolver los datos necesarios para generar posteriormente un comprobante.

### TAREA 2. INTEGRIDAD DE LAS OPERACIONES

Utiliza `@Transactional` correctamente para garantizar la atomicidad.

Si ocurre un error, no debe existir una operación parcialmente aplicada.

Implementa una estrategia de control de concurrencia apropiada para PostgreSQL y Spring Data JPA.

Evalúa el uso de bloqueos pesimistas para las cuentas involucradas, obteniéndolos en un orden consistente para reducir riesgos de deadlocks.

Ten en cuenta el campo `@Version` existente.

Asegura que dos transferencias simultáneas no puedan gastar el mismo saldo incorrectamente.

Evita registrar transferencias COMPLETED si la operación no se confirmó.

No implementes mecanismos complejos de compensación ni sistemas distribuidos; este laboratorio utiliza una única base de datos.

### TAREA 3. EXCEPCIONES PERSONALIZADAS

Implementa excepciones de negocio apropiadas para:

- Cuenta inexistente.
- Saldo insuficiente.
- Importe inválido.
- Transferencia entre la misma cuenta.
- Conflictos de concurrencia, cuando corresponda.

No desarrolles todavía el manejador HTTP global de excepciones. Eso se hará en la etapa de API REST.

### TAREA 4. DTO

Utiliza Java Records para los objetos de transferencia de datos.

Crea DTO apropiados para representar:
- Solicitud de transferencia.
- Resultado exitoso de transferencia.

No expongas entidades JPA directamente como contrato público.

Utiliza nombres en inglés y validaciones apropiadas.

### TAREA 5. PRUEBAS AUTOMATIZADAS

Implementa pruebas con JUnit 5 para validar:

1. Transferencia exitosa.
2. Saldo insuficiente.
3. Importe cero.
4. Importe negativo.
5. Cuenta de origen inexistente.
6. Cuenta de destino inexistente.
7. Transferencia a la misma cuenta.
8. Conservación del dinero entre ambas cuentas.
9. Rollback cuando ocurre una excepción.
10. Comportamiento ante transferencias concurrentes.

Incluye pruebas de integración contra PostgreSQL cuando sea posible.

Las pruebas deben crear y aislar sus propios datos, sin eliminar información ajena.

No utilices datos reales de clientes bancarios.

### TAREA 6. VALIDACIÓN FINAL

1. Compila con Java 25.
2. Ejecuta las pruebas.
3. Comprueba la persistencia correcta de los saldos.
4. Verifica que las operaciones fallidas no alteren los balances.
5. Confirma que el historial de transferencias se registra correctamente.
6. Identifica posibles errores de integridad o concurrencia.

**REGLAS GENERALES:**

- Respetar la arquitectura actual.
- Utilizar BigDecimal para dinero.
- No utilizar double ni float.
- Documentar métodos públicos con Javadoc.
- Mantener clases, atributos y métodos en inglés.
- No modificar innecesariamente las entidades existentes.
- No crear controladores REST todavía.
- No desarrollar frontend ni códigos QR todavía.
- No implementar autenticación en esta etapa.
- No insertar datos automáticamente al iniciar la aplicación.
- No modificar README.
- No realizar commits ni push.
- No almacenar credenciales en el código.
- No modificar bases de datos ajenas al laboratorio.

**AL FINALIZAR:**

Explícame en español:

1. Los servicios implementados y su responsabilidad.
2. Cómo funciona una transferencia paso a paso.
3. Cómo utilizaste `@Transactional`.
4. Cómo se controla la concurrencia.
5. Las excepciones creadas.
6. Las pruebas realizadas y sus resultados.
7. Los archivos modificados.
8. Los problemas pendientes para la etapa 5.

No avances a la siguiente etapa sin mi autorización.
```

**Resultado reportado:** Servicios `BankAccountService` y `BankTransferService`, reglas con `@Transactional`, bloqueos pesimistas, DTO y excepciones. El estudiante reportó 20 pruebas backend locales satisfactorias en esa etapa, incluidas 4 de integración; no corresponde a una ejecución completa verificada directamente por Codex.

---
## Etapa 5 — API REST y Swagger

### Prompt 5 — Endpoints, validaciones, documentación y pruebas

```text
Continuemos con la ETAPA 5 del laboratorio universitario de transferencias bancarias.

**ESTADO ACTUAL**

- Java 25 y Spring Boot 4.1.2.
- PostgreSQL 17.12.
- Base de datos laboratoriobanco.
- Entidades BankCustomer, BankAccount y BankTransfer.
- Tres repositorios JPA.
- BankAccountService y BankTransferService implementados.
- Transferencias con @Transactional y control de concurrencia.
- 20 pruebas automatizadas exitosas contra el entorno correspondiente.

**OBJETIVO**

Implementar la API REST, documentarla con Swagger/OpenAPI y verificar las operaciones bancarias mediante peticiones HTTP.

### TAREA 1. CONTROLADORES REST

Implementa los controladores necesarios para exponer:

- GET /api/accounts: listar cuentas para seleccionar origen y destino.
- GET /api/accounts/{id}: consultar los datos y el saldo de una cuenta.
- POST /api/transfers: realizar una transferencia utilizando el servicio existente.
- GET /api/transfers/{reference}: recuperar una transferencia por su UUID.
- GET /api/accounts/{id}/transfers: consultar las transferencias enviadas y recibidas por una cuenta, ordenadas desde las más recientes.

Reutiliza los servicios existentes y amplíalos únicamente cuando sea necesario.

No dupliques la lógica de negocio en los controladores.

### TAREA 2. VALIDACIÓN Y RESPUESTAS HTTP

Utiliza DTO basados en Java Records.

Implementa validaciones mediante Jakarta Validation y @Valid.

Devuelve códigos HTTP adecuados:
- 200: consulta exitosa.
- 201: transferencia creada correctamente.
- 400: solicitud inválida.
- 404: cuenta o transferencia inexistente.
- 409: conflictos de negocio, como saldo insuficiente o concurrencia.
- 500: errores internos inesperados.

Implementa @RestControllerAdvice para centralizar las excepciones.

Evita mostrar trazas internas, credenciales o información sensible al cliente.

### TAREA 3. SWAGGER / OPENAPI

Agrega Springdoc OpenAPI compatible con Spring Boot 4.1.2.

Consulta la compatibilidad oficial y utiliza springdoc-openapi-starter-webmvc-ui versión 3.2.x estable, si continúa siendo compatible.

Documenta:
- Nombre y descripción de la API bancaria.
- Endpoints.
- Parámetros.
- DTO de solicitud y respuesta.
- Códigos de respuesta HTTP.

Verifica que funcionen:
- http://localhost:8080/swagger-ui.html
- http://localhost:8080/v3/api-docs

### TAREA 4. DATOS DE DEMOSTRACIÓN

Como la base de datos todavía no tiene clientes ni cuentas de ejemplo, prepara un script SQL opcional para el entorno de desarrollo que permita crear dos clientes ficticios y dos cuentas con saldos iniciales.

Requisitos:
- Guardar el script en una carpeta scripts.
- No ejecutarlo automáticamente.
- No borrar ni sobrescribir información existente.
- Evitar duplicados en ejecuciones posteriores.
- No utilizar información personal real.
- Explicar cómo ejecutarlo manualmente desde pgAdmin.

No agregues endpoints públicos que permitan modificar saldos arbitrariamente. Los saldos iniciales serán únicamente datos de demostración.

### TAREA 5. PRUEBAS

Crea pruebas automatizadas para comprobar:

1. Consulta de cuentas.
2. Consulta de cuenta inexistente.
3. Transferencia exitosa.
4. Solicitud con importe inválido.
5. Saldo insuficiente.
6. Transferencia entre la misma cuenta.
7. Consulta del comprobante.
8. Historial de transferencias enviadas y recibidas.
9. Respuestas HTTP y manejo de excepciones.
10. Funcionamiento de Swagger/OpenAPI.

Mantén las 20 pruebas existentes funcionando.

Compila con Java 25 y ejecuta las pruebas disponibles. Si no puedes acceder a las variables DB_*, no alteres las pruebas para ocultar errores: informa qué debemos ejecutar en nuestra terminal local.

**REGLAS**

- Mantener la arquitectura por capas.
- No reescribir los servicios que ya funcionan.
- Utilizar nombres en inglés.
- Documentar métodos públicos con Javadoc.
- No exponer entidades JPA directamente.
- No implementar frontend ni QR todavía.
- No implementar autenticación en esta etapa; indicar claramente que es una API académica que no debe exponerse públicamente sin controles de seguridad.
- No modificar README.
- No realizar commits ni push.
- No almacenar contraseñas.
- No alterar otras bases de datos.

**ENTREGA DE LA ETAPA**

Explícame en español:

1. Controladores y endpoints implementados.
2. Archivos creados o modificados.
3. DTO y validaciones utilizados.
4. Manejo de excepciones HTTP.
5. Configuración de Swagger.
6. Cómo probar cada endpoint.
7. Resultado de las pruebas.
8. Qué falta para la etapa 6.

Detente al terminar y espera mi autorización.
```

**Resultado reportado:** Cinco endpoints, Swagger/OpenAPI, manejador global de errores y script SQL opcional de dos cuentas ficticias. Se probaron las operaciones y el historial en Swagger.

---

## Etapa 6 — React y TypeScript

### Prompt 6 — Interfaz bancaria de Nexo

```text
Continuemos con la ETAPA 6 del laboratorio bancario FullStack.

El backend está implementado con Java 25, Spring Boot 4.1.2 y PostgreSQL 17. Tenemos API REST, Swagger, cuentas bancarias, transferencias e historial.

**OBJETIVO: Desarrollar el frontend con React y TypeScript.**

1. Verifica que Node.js 24 o superior y npm estén disponibles.
2. Crea el frontend en una carpeta `frontend` independiente dentro del repositorio existente, utilizando React, TypeScript y Vite.
3. Configura Axios para consumir nuestra API REST desde una variable de entorno `VITE_API_BASE_URL`.
4. Utiliza el puerto predeterminado de Vite, 5173, y configura CORS en Spring Boot exclusivamente para el origen local necesario durante el desarrollo.
5. Consulta los DTO y endpoints reales del backend antes de implementar el cliente HTTP. No inventes campos.

**INTERFAZ PRINCIPAL**

Desarrolla una interfaz bancaria moderna, profesional y responsive que incluya:

- Encabezado con nombre del sistema.
- Tarjetas para visualizar las cuentas y sus saldos.
- Formulario para transferir dinero entre cuentas.
- Selección de cuenta de origen y destino.
- Validación de importes positivos y cuentas diferentes.
- Historial de transferencias, mostrando origen, destino, importe, fecha y estado.
- Mensajes de confirmación y errores mediante Toast.
- Indicadores de carga y estados vacíos.

**FUNCIONAMIENTO**

- Obtener las cuentas directamente desde PostgreSQL mediante la API.
- Ejecutar las transferencias mediante POST /api/transfers.
- Actualizar los saldos e historial después de una transferencia exitosa.
- Permitir seleccionar una cuenta para consultar su historial.
- Presentar los datos reales devueltos por el backend.
- Manejar correctamente errores HTTP 400, 404 y 409.
- Evitar envíos duplicados mientras se procesa una transferencia.
- Utilizar los endpoints existentes sin modificar innecesariamente la lógica bancaria.

**ORGANIZACIÓN**

Organiza los componentes, servicios HTTP, tipos TypeScript y estilos de forma sencilla y mantenible.

Utiliza componentes reutilizables cuando aporten valor, pero evita dividir excesivamente el proyecto.

**PRUEBAS Y VERIFICACIÓN**

- Instala las dependencias necesarias.
- Comprueba la compilación del frontend.
- Ejecuta las verificaciones disponibles.
- Verifica la comunicación con el backend.
- Explica cómo ejecutar backend y frontend simultáneamente.

**REGLAS**

- No modificar las reglas de transferencias existentes.
- No implementar autenticación todavía.
- No generar el código QR en esta etapa; lo haremos en la etapa 7.
- No insertar datos ficticios automáticamente.
- No almacenar contraseñas ni secretos en el frontend.
- No modificar el README todavía.
- No realizar commits ni push.
- Mantener el diseño y la arquitectura sencillos para un laboratorio universitario.

Al finalizar, explícame en español:

1. Qué estructura creaste.
2. Qué componentes implementaste.
3. Cómo funciona la comunicación con el backend.
4. Qué dependencias utilizaste.
5. Qué pruebas ejecutaste.
6. Cómo iniciar el frontend.
7. Qué queda pendiente.

Detente después de esta etapa.
```

**Resultado reportado:** Vite + React/TypeScript, Axios y CORS para desarrollo, componentes de cuentas, transferencias e historial. 20 pruebas de frontend aprobadas inicialmente y transferencias manuales exitosas en navegador.

---
## Etapa 7 — Comprobantes con código QR

### Prompt 7 — QR, descarga e impresión

```text
Continuemos con la ETAPA 7 de nuestro laboratorio bancario FullStack.

**ESTADO ACTUAL**

Tenemos un sistema funcional con:

- Java 25, Spring Boot 4.1.2 y PostgreSQL 17.
- API REST documentada mediante Swagger.
- Transferencias bancarias con control de concurrencia.
- Historial de transacciones.
- Frontend desarrollado con React, TypeScript y Vite.
- Comunicación frontend-backend verificada.
- Transferencias reales de prueba ejecutadas exitosamente.
- Interfaz visual denominada Nexo.

**OBJETIVO DE LA ETAPA 7**

Implementar comprobantes bancarios con códigos QR que contengan los datos básicos de cada transferencia.

### TAREA 1. COMPROBANTE BANCARIO

Crea un componente React llamado TransferReceipt que permita visualizar el comprobante de una transferencia.

Debe mostrar:

- Nombre de la aplicación Nexo.
- Estado de la transferencia.
- Referencia UUID.
- Cuenta de origen.
- Cuenta de destino.
- Importe transferido.
- Fecha y hora.
- Código QR.

Mantén el diseño visual actual, con su paleta de colores, tipografía y estilo de tarjetas.

El comprobante debe ser responsive.

### TAREA 2. GENERACIÓN DEL QR

Utiliza una biblioteca estable y compatible con React y TypeScript para generar códigos QR reales.

El código QR debe codificar información estructurada de la transferencia:

- Referencia UUID.
- Cuenta de origen y destino (preferiblemente parcialmente enmascaradas).
- Importe.
- Fecha y hora.
- Estado de la operación.

Utiliza los datos reales devueltos por el backend.

No introduzcas información ficticia ni generes un QR decorativo.

No incluyas contraseñas, tokens, saldos ni información personal sensible.

El QR debe poder escanearse mediante una aplicación lectora y permitir consultar sus datos básicos.

Aclara que el QR es un comprobante académico y no constituye por sí mismo una firma digital ni una garantía criptográfica de autenticidad.

### TAREA 3. INTEGRACIÓN CON LAS TRANSFERENCIAS

Cuando una transferencia termine exitosamente:

1. Mostrar el mensaje de confirmación.
2. Presentar un comprobante visual.
3. Generar automáticamente su código QR.
4. Permitir cerrar el comprobante y volver a la interfaz.

No modificar los saldos ni repetir la transferencia al generar el QR.

### TAREA 4. COMPROBANTES HISTÓRICOS

Permite seleccionar una transferencia desde el historial.

Al seleccionar una operación:

1. Consultar sus datos utilizando GET /api/transfers/{reference}.
2. Mostrar el comprobante correspondiente.
3. Generar el mismo contenido QR a partir de los mismos datos.
4. Permitir regresar al historial.

No dupliques la lógica de generación del QR.

### TAREA 5. DESCARGA DEL COMPROBANTE

Permite al usuario descargar o imprimir su comprobante en un formato visual adecuado.

El comprobante descargado debe incluir el QR, la referencia y los datos básicos de la transferencia.

Evita incorporar dependencias pesadas innecesariamente.

### TAREA 6. PRUEBAS

Implementa pruebas para verificar:

1. Visualización del comprobante.
2. Generación de un QR válido con datos reales.
3. Correspondencia entre los datos del QR y la transferencia.
4. Visualización después de una transferencia exitosa.
5. Consulta de comprobantes históricos.
6. Manejo de referencias inexistentes.
7. Descarga o impresión del comprobante.
8. Funcionamiento responsive.

Ejecuta las pruebas de frontend y backend disponibles.

Verifica que la compilación de producción de React termine correctamente.

**REGLAS DEL PROYECTO**

- Mantener la arquitectura existente.
- Conservar el diseño actual de Nexo.
- No modificar la lógica bancaria ya validada.
- No reestructurar innecesariamente el frontend.
- No introducir autenticación ni funcionalidades ajenas al laboratorio.
- No almacenar credenciales ni datos sensibles en el QR.
- No realizar commits ni push.
- No modificar todavía el README.
- No inventar resultados de pruebas.

**AL FINALIZAR**

Explícame en español:

1. Qué archivos creaste o modificaste.
2. Cómo funciona el código QR.
3. Qué información contiene.
4. Cómo se visualiza el comprobante.
5. Cómo consultar transferencias anteriores.
6. Cómo descargar o imprimir el comprobante.
7. Qué pruebas realizaste y sus resultados.
8. Qué falta para la etapa 8.

Detente al finalizar esta etapa y espera mi autorización.
```

**Resultado reportado:** Componente `TransferReceipt`, generación del QR con `qrcode.react`, descarga SVG, impresión/PDF y consulta de comprobantes históricos; 29 pruebas de frontend aprobadas. Los comprobantes PDF y SVG aportados ya están incorporados y enlazados en el [índice de evidencias](evidencias/INDICE.md).

---

## Etapa 8 — Documentación y preparación de entrega

### Prompt 8 — README, informe, registro de prompts y seguridad Git

```text
Continuemos con la ETAPA 8 del laboratorio universitario Nexo.

El proyecto FullStack está desarrollado y sus funcionalidades principales fueron verificadas.

**Tecnologías:**
- Java 25 y Spring Boot 4.1.2
- Spring Data JPA y PostgreSQL 17
- React, TypeScript y Vite
- Axios y Swagger/OpenAPI
- JUnit, Vitest y Testing Library
- qrcode.react

**REQUISITOS DEL PROFESOR**

Debemos entregar:
1. Código fuente publicado en GitHub.
2. Informe técnico del laboratorio.
3. Registro de los prompts utilizados.

**TAREA 1. REVISIÓN FINAL**

Analiza el proyecto completo y verifica:
- Estructura del backend y frontend.
- Entidades, repositorios, servicios y controladores.
- Transferencias, historial y comprobantes QR.
- Configuración de PostgreSQL y CORS.
- Pruebas automatizadas.
- Archivos que deben excluirse de Git.

No alteres la lógica bancaria que ya funciona.

**TAREA 2. README.md**

Actualiza el README principal con:

1. Descripción y objetivo de Nexo.
2. Requisitos funcionales.
3. Tecnologías y versiones verificadas.
4. Arquitectura por capas.
5. Estructura de directorios.
6. Modelo de datos y relaciones.
7. API REST y sus endpoints.
8. Explicación del funcionamiento de las transferencias.
9. Atomicidad, rollback y concurrencia.
10. Comprobantes QR.
11. Requisitos previos de instalación.
12. Configuración segura de PostgreSQL.
13. Variables de entorno requeridas.
14. Instrucciones para ejecutar backend y frontend.
15. Instrucciones para ejecutar las pruebas.
16. Uso de Swagger.
17. Limitaciones académicas del proyecto.

La documentación debe permitir que otra persona pueda descargar el proyecto y ejecutarlo.

**TAREA 3. INFORME TÉCNICO**

Crea `docs/INFORME_LABORATORIO.md` con una estructura académica:

- Portada con campos editables para estudiante, docente, asignatura e institución.
- Introducción.
- Justificación.
- Objetivo general.
- Objetivos específicos.
- Tecnologías utilizadas.
- Descripción de la arquitectura.
- Modelo de base de datos.
- Desarrollo del backend.
- Desarrollo del frontend.
- Funcionamiento de transferencias.
- Manejo de errores y concurrencia.
- Generación de comprobantes QR.
- Pruebas realizadas y resultados.
- Evidencias del funcionamiento.
- Conclusiones.
- Referencias técnicas.

Utiliza información real del código. No inventes capturas, resultados o evidencias.

Deja identificados los espacios donde agregaré capturas de Swagger, PostgreSQL, React y comprobantes.

**TAREA 4. REGISTRO DE PROMPTS**

Crea `docs/PROMPTS_UTILIZADOS.md`.

Organízalo según nuestras ocho etapas:
1. Configuración inicial.
2. PostgreSQL.
3. Entidades y repositorios.
4. Lógica de negocio.
5. API REST y Swagger.
6. Frontend React.
7. Códigos QR.
8. Documentación y entrega.

IMPORTANTE: No inventes ni presentes como textuales prompts que no estén disponibles en los archivos del proyecto. Prepara la estructura para que pueda incorporar los prompts originales que utilicé con Codex.

**TAREA 5. SEGURIDAD Y GITHUB**

Revisa:
- .gitignore
- Variables de entorno.
- Contraseñas y credenciales.
- Archivos temporales.
- target/
- node_modules/
- dist/
- Archivos generados.
- Datos privados.

Comprueba que los archivos necesarios para ejecutar el proyecto estén incluidos y que los secretos estén excluidos.

Verifica el estado de Git y la rama actual.

**TAREA 6. VALIDACIÓN**

Ejecuta las verificaciones y pruebas que permita el entorno.

No afirmes haber ejecutado pruebas que no se hayan ejecutado realmente.

Resume los resultados existentes y distingue las pruebas nuevas de los resultados históricos verificados.

**REGLAS FINALES**

- No realizar commits todavía.
- No ejecutar git push.
- No crear otro repositorio.
- No modificar la lógica bancaria salvo que encuentres un error crítico y lo justifiques.
- No inventar información académica.
- No colocar contraseñas reales.
- Mantener una documentación profesional, clara y en español.

**AL FINALIZAR**

Entrégame:

1. Resumen de los archivos documentados.
2. Estado general del proyecto.
3. Resultados de las verificaciones.
4. Problemas o riesgos pendientes.
5. Archivos que debo revisar antes de publicar.
6. Comandos recomendados para el commit y push final, sin ejecutarlos.

Detente y espera autorización antes de publicar en GitHub.
```

**Resultado reportado:** Codex actualizó `README.md`, preparó `docs/INFORME_LABORATORIO.md`, una plantilla para este registro de prompts y `.gitignore`; revisó Git pero no hizo commit ni push.

---

## Validaciones y notas para la sustentación

- El trabajo se organizó en etapas separadas y Codex se detenía para permitir una revisión antes de continuar.
- Java 25.0.5, Spring Boot 4.1.2 y PostgreSQL 17.12 se validaron mediante compilación, pruebas e inicio de la aplicación.
- Las pruebas de integración con PostgreSQL requerían variables de entorno configuradas manualmente en PowerShell, porque no se heredaban automáticamente en la sesión de Codex.
- En la etapa 4 el estudiante reportó 20 pruebas backend aprobadas. Posteriormente aportó una **ejecución local de 56 pruebas backend aprobadas, sin errores ni omisiones**, respaldada por el [registro público depurado](evidencias/maven-local-publico.txt). Codex revisó el registro, pero no ejecutó esa corrida. El estudiante confirmó el comando Maven, el perfil dev, DB_* y RUN_POSTGRES_TESTS=true.
- La **ejecución desde Codex de la etapa 8** registró **51 aprobadas, 4 omitidas y 1 error de configuración** por falta de variables DB_* (DB_URL ausente). No contradice por sí misma el reporte local: corresponde a otro entorno. El índice reserva `maven-codex.txt` para una copia revisada de esa salida.
- En la etapa 7 se reportaron **29 pruebas frontend aprobadas** y compilación de producción exitosa.
- El estudiante reportó verificaciones manuales de Swagger, historial, transferencias sobre cuentas de demostración y comprobantes QR; las capturas y comprobantes disponibles están enlazados en el índice. La lectura con teléfono y la vista móvil siguen sin evidencia separada.
- El QR contiene datos básicos de la transferencia; no es una firma digital ni prueba criptográfica de autenticidad.
- La aplicación es académica y no tiene autenticación para exposición pública.

**Antes de la entrega:** conserva la fuente original y señala las diferencias de formato cuando corresponda; no cambies el contenido de los prompts para ajustarlo al resultado final. Adjunta las evidencias pendientes y completa fecha, entorno y comando de las pruebas locales. Este registro conserva los diez prompts recopilados; no incluye las revisiones documentales posteriores como si fueran parte de esos bloques.
