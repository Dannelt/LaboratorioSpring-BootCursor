# Índice de evidencias — Nexo

Inventario de los archivos presentes en esta carpeta. Los enlaces apuntan a archivos existentes; los complementos pendientes se describen sin enlaces ficticios.

## Archivos adjuntos

| Identificador | Archivo | Contenido observado y referencia |
|---|---|---|
| Figura 1 | [postgresql.png](postgresql.png) | pgAdmin: esquema public con bank_accounts, bank_customers y bank_transfers. No se ven las restricciones ni el nombre de la base en el recorte. |
| Figura 2 | [swagger.png](swagger.png) | Cinco endpoints documentados y servidor localhost:8080. No muestra una petición ejecutada. |
| Figura 3 | [nexo-frontend.png](nexo-frontend.png) | Dos cuentas de demostración, formulario y tres movimientos completados; respalda interfaz e historial. |
| Figura 4 | [Comprobante SVG](nexo-comprobante-892808b5-d1d3-42b8-ba64-29f1d3e2edf6.svg) | Referencia 892808b5-d1d3-42b8-ba64-29f1d3e2edf6, importe 30,00, estado COMPLETED, cuentas enmascaradas y QR. |
| Documento complementario | [Nexo · Laboratorio bancario.pdf](Nexo%20%C2%B7%20Laboratorio%20bancario.pdf) | PDF de una página: texto extraído con la misma referencia, importe y cuentas enmascaradas del SVG. Sin autor/asunto personales en metadatos ni rutas personales detectadas en el texto. |
| Evidencia 5 | [maven-local-publico.txt](maven-local-publico.txt) | Versión pública depurada: 56 pruebas, 0 fallos, 0 errores, 0 omitidas y BUILD SUCCESS. Finalización: 2026-11-12T22:10:27-05:00; Java 25.0.5. |

Las figuras están incorporadas en la sección 13 del [informe](../INFORME_LABORATORIO.md). No se modificaron ni eliminaron los originales. maven-local.txt se conserva localmente, excluido de Git; se publica su copia depurada identificada, con resultados intactos.

## Alcance de los resultados

El registro Maven es evidencia de una ejecución local aportada por el estudiante y revisada documentalmente. No fue una ejecución nueva desde Codex. El resultado histórico de Codex continúa siendo 51 aprobadas, 4 omitidas y 1 error por falta de DB_*.

La captura de Nexo ya contiene el historial; no existe una imagen independiente del historial y no es necesario duplicarla para documentarlo. El SVG corresponde por referencia e importe al primer movimiento de esa captura. No se volvió a escanear el QR de este archivo durante esta revisión documental.

## Información y complementos pendientes

- Comando confirmado por el estudiante: `.\mvnw.cmd -B test 2>&1 | Tee-Object -FilePath docs/evidencias/maven-local.txt`, con perfil dev, DB_* y RUN_POSTGRES_TESTS=true. No es una ejecución nueva desde Codex.
- Incorporar fechas de captura cuando se conozcan. No deducirlas de la fecha de modificación del archivo.
- Revisión visual final del PDF recomendada; su texto y metadatos fueron inspeccionados sin detectar autor personal o secretos. No se modificó el PDF.
- Opcional: captura de una respuesta ejecutada en Swagger y detalle de claves/restricciones de PostgreSQL.
- Pendientes como evidencia complementaria: lectura del QR con teléfono, vista móvil y registro de consola de Vitest.
- La copia del resultado histórico de Codex no está adjunta en esta carpeta; no se enlaza un archivo inexistente.
- La copia pública sustituye las rutas personales y el usuario local por marcadores y declara esas redacciones; el original privado no debe agregarse forzadamente a Git.
- La búsqueda de patrones no encontró asignaciones de contraseña o tokens evidentes en el registro revisado; no sustituye una revisión manual de todos los documentos.

Referencias: [informe](../INFORME_LABORATORIO.md), [prompts](../PROMPTS_UTILIZADOS.md) y [README](../../README.md).
