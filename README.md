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
- Herramienta GIT para gestión de repositorios [Github](https://git-scm.com/downloads)
- Scala versión 2.12
- Spark versión 3.1.2
- SBT
- Java 8
- Máquina virtual con sistema operativo linux y distribución Ubuntu 22.04 (Disponible en el enlace compartido en moodle) 
- Docker y Docker Compose


## 3. Descripción de la práctica

La práctica plantea una serie de ejercicios para afianzar los conocimiento sobre el desarrollo y despliegue de aplicacines en entornos de Big Data, en este caso la herramienta que se utiliza es el motor de Big Data Apache Spark.

La práctica se desarrolla en tres bloques en donde se pretende ejemplificar como desplegar un job en Spark Structured Streaming y como desplegarlo con Docker.


## 4. Inicializar el entorno.

Para la realización de la práctica se ha provisionado una máquina virtual con sistema operativo linux y distribución Ubuntu. En los recursos de Moodle de la asignatura se puede acceder al enlace de descarga.

Descarge el fichero de VM con extensión .ova e importelo en virtualbox. 

Dentro de la máquina virtual abra un terminal y realice los siguientes pasos.

Instalar con sdkman la versión de Scala 2.12:
```
sdk install scala 2.12.15
```
Y configurarla como la versión por defecto:
```
sdk default scala 2.12.15
```

Instalar con sdkman Spark 3.1.2:

```
sdk install spark 3.1.2
```

Comprobar que la versión de Java instalada es la 8

```
java -version
```

Descargar el repositorio:
```
git clone https://github.com/Big-Data-ETSIT/P6_spark_streaming_docker
```

Descargar Docker y Docker Compose


## 5. Tareas a realizar.

### Bloque 1: Ejecutar el consultor de contador de palabras.

Usando como directorio de trabajo `P6_spark_batch/wordCountConsult`

**Pregunta 1: Explicar qué hace el código WordConsultStreamingJob.scala**
**Pregunta 2: Explicar la diferencia entre Spark y Spark Streaming**



En este apartado se va a desplegar en local utilizando el comando spark-submit. 

Para ello primero debemos compilar el código y empaquetarlo (generar el .jar). Utilizaremos sbt:

```
sbt compile
sbt package
```
Comprobar que se genera el .jar en la carpeta `P6_spark_batch/wordCountConsult/target/scala-2.12`

Arrancar un servidor con netcat al que se conectará el programa desarrollado en Spark. **Es necesario arrancar el servidor antes que el Job de Spark**

```
nc -kl 9090
```

Lanzar el programa con el comando spark-submit

```
spark-submit --class "es.upm.dit.WordConsultStreamingJob" target/scala-2.12/word-consult-streaming_2.12-1.0.jar
```

**Pregunta 3: Introduzca en el terminal en el que inició spark la palabra `quijote` e indique que se muestra en el terminal en el que arrancó el Job de Spark. Puede probar con más palabras. Deduzca qué hace el programa en relación con el laboratorio anterior.**

**Pregunta 4: ¿Cúando finaliza la ejecución del programa?**




### Bloque 2: Desplegar el Job en Docker

En el fichero `docker-compose.yml` aparecen todos los contenedores necesarios para el despliegue. Los cuales se van a explicar a continuación:

- *proxy*: servidor proxy que permite comunicarse con Spark desde el ordenador del alumno (fuera de la red Docker).
- *spark-master*: nodo master que gestiona el cluster.
- *spark-worker-1*: nodo worker que ejecutará los jobs.
- *spark-worker-2*: nodo worker que ejecutará los jobs.
- *spark-submit*: contenedor encargado de ejecutar spark-submit para iniciar el Job en Spark.

Para arrancar el escenario debe ejecutar el comando:

```
docker compose up -d
```

Además debe arrancar un cliente netcat desde un terminal del ordenador del alumno una vez que haya arrancado el escenario en Docker que se haya arrancado el Job

En un terminal ejecute el siguiente comando para ver los logs del Job de Spark:
```
docker logs spark-submit -f

```
En otro terminal ejecute el siguiente comando para arrancar el cliente netcat:

```
nc localhost 3000
```

Realice varias consultas al igual que se hizo en el apartado anterior a través del terminal en el que arrancó el cliente netcat.

**


Para parar el escenario debe  ejecutar el comando:
```
docker compose down
```


**Captura 1: caputra con todos los contenedores arrancados (para ello ejecute `docker ps`)**

**Captura 2: caputra con alguno de los resultados devueltos por Spark (terminal del contenedor spark-submit)**

**Pregunta 5: dibuje y explique la arquitectura del escenario. En el caso del proxy puede suponer que es un servidor al que realiza peticiones y las encamina a Spark**

Acceda a la UI de Spark y navegue por las diferentes pestañas:

- http://localhost:8080/ : información del cluster
- http://localhost:4040/ : informaicón de Spark Structured Streaming

**Pregunta 6: explique que se puede ver en esas pantallas. Puede incluír alguna captura que acompañe a su explicación**



## 7. Instrucciones para la Entrega y Evaluación.
El alumno debe subir un fichero pdf a moodle en donde se incluyan las respuestas a las preguntas planteadas a lo largo de la práctica y las capturas solicitadas.



