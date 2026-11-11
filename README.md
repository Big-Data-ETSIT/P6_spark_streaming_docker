<img  align="left" width="150" style="float: left;" src="https://www.upm.es/sfs/Rectorado/Gabinete%20del%20Rector/Logos/UPM/CEI/LOGOTIPO%20leyenda%20color%20JPG%20p.png">
<img  align="right" width="60" style="float: right;" src="https://www.dit.upm.es/images/dit08.gif">


<br/><br/>


# Práctica Spark Scala - Structured Streaming y Docker

## 1. Objetivo

- Afianzar los conceptos sobre Spark
- Desplegar en local un programa Spark Structured Streaming
- Desplegar Spark en contenedores Docker

## 2. Dependencias

Para realizar la práctica el alumno deberá tener instalado en su ordenador:
- Las mismas herramientas que en laboratorio anterior (Spark Batch)
- Docker y Docker Compose


## 3. Descripción de la práctica

La práctica plantea una serie de ejercicios para afianzar los conocimiento sobre el desarrollo y despliegue de aplicacines en entornos de Big Data, en este caso la herramienta que se utiliza es el motor de Big Data Apache Spark.

La práctica se desarrolla en tres bloques en donde se pretende ejemplificar como desplegar un job en Spark Structured Streaming y como desplegarlo con Docker.


## 4. Inicializar el entorno

Descargar las mismas herramientas y versiones que en el laboratorio anterior (Spark Batch)

Descargar el repositorio:
```
git clone https://github.com/Big-Data-ETSIT/P6_spark_streaming_docker
```

Descargar Docker y Docker Compose


## 5. Tareas a realizar.

### Bloque 1: Ejecutar el consultor de contador de palabras.

Usando como directorio de trabajo `P6_spark_streaming_docker/wordCountConsult`

**Pregunta 1: Explique qué hace el código WordConsultStreamingJob.scala**

**Pregunta 2: Explique la diferencia entre Spark y Spark Streaming**



En este apartado se va a desplegar en local utilizando el comando spark-submit. 

Para ello primero debemos compilar el código y empaquetarlo (generar el .jar). Utilizaremos sbt:

```
sbt compile
sbt package
```
Comprobar que se genera el .jar en la carpeta `P6_spark_streaming_docker/wordCountConsult/target/scala-2.12`

Arrancar un servidor con netcat al que se conectará el programa desarrollado en Spark. **Es necesario arrancar el servidor antes que el Job de Spark**

```
nc -kl 9090
```

Lanzar el programa con el comando spark-submit

```
spark-submit --class "es.upm.dit.WordConsultStreamingJob" target/scala-2.12/word-consult-streaming_2.12-1.0.jar
```

**Pregunta 3: Espere a que haya arrancado el programa. Introduzca en el terminal en el que inició netcat la palabra `quijote` e indique que se muestra en el terminal en el que arrancó el Job de Spark. Puede probar con más palabras. Deduzca qué hace el programa en relación con el laboratorio anterior.**

**Pregunta 4: ¿Cúando finaliza la ejecución del programa?**

Pare el servidor de netcat y el Job de Spark.


### Bloque 2: Desplegar el Job en Docker

En el fichero `docker-compose.yml` aparecen todos los contenedores necesarios para el despliegue:

- *proxy*: servidor proxy que permite comunicarse con Spark desde el ordenador del alumno (fuera de la red Docker). -> no importante para la práctica
- *spark-master*
- *spark-worker-1*
- *spark-worker-2*
- *spark-submit*

**Pregunta 5: indique para qué sirve cada servicio**

Para arrancar el escenario debe ejecutar el comando desde la raíz del proyecto. La primera vez puede tardar un par de minutos porque debe descargarse las imágenes desde DockerHub.

```
docker compose up -d
```

Además debe arrancar un cliente netcat desde un terminal del ordenador del alumno una vez que haya arrancado el escenario en Docker y que se haya iniciado el Job

El modo de despliegue del Job se elige en el fichero `.env` (raíz del proyecto) con la variable `MODO_CLUSTER`:

```
MODO_CLUSTER=false   # modo client (por defecto): el driver corre en el contenedor spark-submit
MODO_CLUSTER=true    # modo cluster: el driver corre dentro de uno de los workers
```

Tras cambiarla hay que recrear el escenario (`docker compose down && docker compose up -d`).

**Ver los logs del driver**

- *Modo client* (`MODO_CLUSTER=false`): el driver corre en el contenedor `spark-submit`, por lo que sus logs se ven con:
  ```
  docker logs spark-submit -f
  ```
  La UI de la aplicación está en http://localhost:4040/
- *Modo cluster* (`MODO_CLUSTER=true`): el contenedor `spark-submit` solo envía el Job y termina (`docker logs spark-submit` muestra únicamente el estado del driver). El driver corre en un worker (1 o 2, según dónde lo asigne el master).
  - Por consola (sustituya `spark-worker-1` por `spark-worker-2` si el driver no está en el primero):
    ```
    docker exec spark-worker-1 sh -c 'tail -f /opt/spark/work/driver-*/stdout'
    ```
    Los errores están en `/opt/spark/work/driver-*/stderr` del mismo directorio.
  - Por la interfaz: abra http://localhost:8080/, en *Running Drivers* localice el driver y siga el enlace al worker que lo ejecuta (http://localhost:8081/ o http://localhost:8082/). En *Running Drivers* pulse `stdout` / `stderr` para ver sus logs.
  - La UI de la aplicación (puerto 4040) queda en el worker que ejecuta el driver y no está publicada en el host; desde el master (8080) se accede a ella con el enlace *Application Detail UI* de la aplicación.

En otro terminal ejecute el siguiente comando para arrancar el cliente netcat:

```
nc localhost 3000
```

Realice varias consultas al igual que se hizo en el apartado anterior a través del terminal en el que arrancó el cliente netcat.

Si necesita reiniciar el escenario puede ejecutar:
```
docker compose down
docker compose up -d
```



**Captura 1: captura con todos los contenedores arrancados (para ello ejecute `docker ps`)**

**Captura 2: captura con alguno de los resultados devueltos por Spark (logs del driver)**

**Pregunta 6: dibuje y explique la arquitectura del escenario. En el caso del proxy puede suponer que es un servidor al que realiza peticiones y las encamina a Spark**

Acceda a la UI de Spark y navegue por las diferentes pestañas:

- http://localhost:8080/ : información del cluster
- http://localhost:4040/ : informaicón de Spark Structured Streaming (solo en modo client; en modo cluster se accede desde la UI del master)

**Pregunta 7: explique qué se puede ver en esas pantallas. Puede incluír alguna captura que acompañe a su explicación**

### Bloque 3: Modo client vs modo cluster

Repita el despliegue del Bloque 2 con `MODO_CLUSTER=true` en el fichero `.env` (modo cluster) y después con `MODO_CLUSTER=false` (modo client), recreando el escenario cada vez con `docker compose down && docker compose up -d`. Consulte los logs del driver como se explica en el Bloque 2.

**Pregunta 8: explique la diferencia entre ejecutar el Job con `--deploy-mode client` y `--deploy-mode cluster`. ¿En qué contenedor se ejecuta el driver en cada caso? ¿Qué muestra `docker logs spark-submit` en cada modo?**

**Captura 3: captura de la UI del master (http://localhost:8080/) en modo cluster donde se vea el driver en ejecución y en qué worker corre**


Pare el escenario:
```
docker compose down
```


## 7. Instrucciones para la Entrega y Evaluación.
El alumno debe subir un fichero pdf a moodle en donde se incluyan las respuestas a las preguntas planteadas a lo largo de la práctica y las capturas solicitadas.



