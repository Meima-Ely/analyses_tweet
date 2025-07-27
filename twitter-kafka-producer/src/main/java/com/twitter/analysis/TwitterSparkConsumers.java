package com.twitter.analysis;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;

// Importations HDFS nécessaires
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import java.util.*;

public class TwitterSparkConsumers {
    public static void main(String[] args) throws InterruptedException {
    	System.out.println("🚦 [Spark] En attente de tweets depuis Kafka...");

        // ✅ Configuration Spark pour le streaming local
        SparkConf conf = new SparkConf()
                .setMaster("local[*]")
                .setAppName("TwitterSparkConsumers");

        // ✅ Création du contexte de streaming (batch toutes les 5 secondes)
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(5));

        // ✅ Paramètres Kafka
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "localhost:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "tweet-consumer-group");
        kafkaParams.put("auto.offset.reset", "earliest"); // 🔁 Lire depuis le début
        kafkaParams.put("enable.auto.commit", false);

        // ✅ Nom du topic Kafka (doit correspondre au Producer)
        Collection<String> topics = Arrays.asList("tweets-topic");

        // ✅ Création du flux de données depuis Kafka
        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        streamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.Subscribe(topics, kafkaParams)
                );

        // ✅ Traitement et affichage des tweets reçus
        // Intégration HDFS dans foreachRDD
        stream.map(ConsumerRecord::value)
              .foreachRDD(rdd -> {
                  List<String> tweets = rdd.collect(); // Collecte tous les tweets du RDD en une liste
                  if (tweets.isEmpty()) {
                      System.out.println("Aucun tweet reçu dans ce batch.");
                  } else {
                      System.out.println(" [Spark] Traitement de " + tweets.size() + " tweets reçus.");

                      // --- Début de l'intégration HDFS ---
                      // Création de la configuration Hadoop
                      // Assurez-vous que le NameNode HDFS est accessible via localhost:9000
                      Configuration hadoopConf = new Configuration();
                      hadoopConf.set("fs.defaultFS", "hdfs://localhost:9000"); //

                      // Chemin de sortie HDFS. Utilisez un timestamp pour un nom de fichier unique par batch.
                      String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                      Path outputPath = new Path("/user/hp/tweets_raw_data/batch_" + timestamp + ".txt"); //

                      try (FileSystem hdfs = FileSystem.get(hadoopConf); // Obtient une instance du système de fichiers HDFS
                           BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(hdfs.create(outputPath, true)))) { // Crée le fichier dans HDFS, 'true' pour écraser s'il existe
                          
                          // Création des répertoires parents si ils n'existent pas
                          Path parentDir = outputPath.getParent();
                          if (!hdfs.exists(parentDir)) {
                              hdfs.mkdirs(parentDir); // Crée tous les répertoires parents nécessaires
                              System.out.println("Création du répertoire HDFS : " + parentDir);
                          }

                          for (String tweet : tweets) {
                              System.out.println(" Tweet reçu : " + tweet); // Affiche le tweet dans la console Spark
                              writer.write(tweet); // Écrit le tweet dans le fichier HDFS
                              writer.newLine(); // Ajoute une nouvelle ligne après chaque tweet
                          }
                          System.out.println("💾 [HDFS] " + tweets.size() + " tweets écrits dans HDFS : " + outputPath); //

                      } catch (Exception e) {
                          System.err.println(" Erreur lors de l'écriture dans HDFS : " + e.getMessage()); //
                          e.printStackTrace();
                      }
                      // --- Fin de l'intégration HDFS ---

                      System.out.println("✅ [Spark] Fin de traitement du batch.\n");
                  }
              });

        // ✅ Lancement du traitement en continu
        streamingContext.start();
        streamingContext.awaitTermination();
    }
}