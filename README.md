# Analyse de Tweets avec Kafka, Spark et JavaFX

Ce projet est une application d'analyse de tweets en temps réel. Il simule un flux de tweets à partir d'un fichier CSV, les envoie vers un topic Kafka, les traite avec Spark Streaming, et visualise les résultats via une interface JavaFX.

## Architecture

Le projet est composé de trois modules principaux :

1.  **Kafka Producer (`TwitterKafkaProducer`)** : Lit les données du fichier `scrap-tweet2025.csv` et les publie sur le topic Kafka `tweets-topic`.
2.  **Spark Consumer (`TwitterSparkConsumers`)** : Consomme les messages du topic Kafka et effectue des analyses (ex: comptage de hashtags, sentiment simple, etc.).
3.  **Visualisation (`TweetAnalyzerAndGraph`)** : Une interface JavaFX qui affiche les statistiques et graphiques en temps réel.

## Prérequis

-   **Java 17** ou supérieur
-   **Apache Maven**
-   **Apache Kafka** (et Zookeeper) installés et configurés.

## Configuration Avant Lancement

### 1. Démarrer Kafka
Assurez-vous que Zookeeper et Kafka sont lancés localement.

```bash
# Exemple de commandes (chemins à adapter selon votre installation)
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

### 2. Créer le Topic Kafka
Créez le topic nécessaire au projet :

```bash
bin/kafka-topics.sh --create --topic tweets-topic --bootstrap-server localhost:9092
```

### 3. Vérifier le chemin du CSV
**IMPORTANT** : Le producteur lit le fichier CSV à un chemin spécifique.
Ouvrez `twitter-kafka-producer/src/main/java/com/twitter/analysis/TwitterKafkaProducer.java` et vérifiez la ligne 14 :

```java
String filePath = "C:/Users/hp/Desktop/analyses_tweet/scrap-tweet2025.csv";
```

Assurez-vous que ce chemin pointe bien vers votre fichier `scrap-tweet2025.csv`. Si vous êtes sur une autre partition ou dossier (ex: `d:\Bureau\analyses_tweet\...`), mettez à jour cette ligne.

## Installation et Exécution

Toutes les commandes doivent être exécutées depuis le dossier `twitter-kafka-producer` où se trouve le `pom.xml` principal.

Ouvrez un terminal et naviguez vers ce dossier :

```bash
cd twitter-kafka-producer
```

### Étape 1 : Nettoyer et Compiler

```bash
mvn clean compile
```

### Étape 2 : Lancer le Producteur (Producer)
Ce processus va lire le CSV et envoyer les tweets en boucle.

```bash
mvn exec:java@run-producer
```
*Laissez ce terminal ouvert.*

### Étape 3 : Lancer le Consommateur Spark (Consumer)
Ce processus va traiter les flux de données.

```bash
mvn exec:java@run-spark-consumer
```
*Ouvrez un nouveau terminal pour cette commande.*

### Étape 4 : Lancer l'Interface Graphique (Analyzer)
Pour voir les résultats.

```bash
mvn exec:java@run-analyzer
```
*Ouvrez un nouveau terminal pour cette commande.*
