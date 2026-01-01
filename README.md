Here’s the complete, ready-to-copy English README for your **Real-Time Tweet Analysis** project.  
Just select all the text below (from the first `<p align="center">` to the very end), copy it, and paste it directly into your GitHub repository’s `README.md` file.

```markdown
<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=12,32,28&height=300&section=header&text=Real-Time%20Tweet%20Analysis&fontSize=80&fontColor=white&animation=fadeIn" />
</p>

# 🚀 Real-Time Tweet Analysis with Kafka, Spark & JavaFX

A powerful **real-time tweet analysis application** that simulates a live Twitter stream from a CSV file, processes it using Apache Kafka and Spark Streaming, and visualizes insights through a modern **JavaFX dashboard**.

Live data flow → Streaming processing → Interactive visualizations!

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white" />
  <img src="https://img.shields.io/badge/Apache_Spark-E25A1C?style=for-the-badge&logo=apache-spark&logoColor=white" />
  <img src="https://img.shields.io/badge/JavaFX-Oracle-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" />
</p>

## 🏗 Project Architecture

The application is built with **three independent modules** working together seamlessly:

1. **Kafka Producer (`TwitterKafkaProducer`)**  
   → Reads tweets from `scrap-tweet2025.csv` and publishes them to the Kafka topic `tweets-topic`.

2. **Spark Streaming Consumer (`TwitterSparkConsumers`)**  
   → Consumes live messages from Kafka  
   → Performs real-time analytics (hashtag counting, basic sentiment analysis, trends, etc.)

3. **JavaFX Dashboard (`TweetAnalyzerAndGraph`)**  
   → Beautiful, responsive GUI  
   → Displays live statistics, charts, and word clouds in real time

<p align="center">
  <img width="80%" src="https://via.placeholder.com/800x400?text=Architecture+Diagram+(CSV+%E2%86%92+Kafka+%E2%86%92+Spark+%E2%86%92+JavaFX+Dashboard)" alt="Architecture Overview" />
  <br><em>Data flows from CSV → Kafka → Spark Processing → Live JavaFX Visualization</em>
</p>

## ⚙️ Prerequisites

- **Java 17** or higher
- **Apache Maven**
- **Apache Kafka** (with Zookeeper) installed and running locally

## 🛠 Setup Before Running

### 1. Start Kafka & Zookeeper

Open a terminal and run (adjust paths to your Kafka installation):

```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka Broker (in a new terminal)
bin/kafka-server-start.sh config/server.properties
```

### 2. Create the Kafka Topic

```bash
bin/kafka-topics.sh --create --topic tweets-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### 3. Update CSV File Path (Important!)

The producer reads the tweet data from a CSV file.  
Open the file:  
`twitter-kafka-producer/src/main/java/com/twitter/analysis/TwitterKafkaProducer.java`

Update line ~14 to match your actual file location:

```java
String filePath = "C:/Users/hp/Desktop/analyses_tweet/scrap-tweet2025.csv"; // ← Change this path if needed
```

## 🚀 Running the Application

All commands should be executed from the root folder `twitter-kafka-producer` (where the main `pom.xml` is located).

```bash
cd twitter-kafka-producer
```

### Step 1: Clean & Compile

```bash
mvn clean compile
```

### Step 2: Launch the Producer (Sends Tweets to Kafka)

This will read the CSV and simulate a continuous tweet stream.

```bash
mvn exec:java@run-producer
```

**Keep this terminal open.**

### Step 3: Launch the Spark Consumer (Processes Data)

Open a **new terminal** and run:

```bash
mvn exec:java@run-spark-consumer
```

**Keep this terminal open.**

### Step 4: Launch the JavaFX Dashboard (Visualize Results)

Open a **third terminal** and run:

```bash
mvn exec:java@run-analyzer
```

Enjoy watching real-time analytics come to life! 📊✨

## 🎨 Features Highlight

- Live hashtag trends
- Basic sentiment distribution
- Top mentioned users/entities
- Interactive charts & graphs
- Smooth, responsive JavaFX interface

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=12,32,28&height=200&section=footer" />
</p>

<p align="center">
  Made with ❤️ for big data streaming enthusiasts | Feel free to star ⭐ and fork!
</p>
```

