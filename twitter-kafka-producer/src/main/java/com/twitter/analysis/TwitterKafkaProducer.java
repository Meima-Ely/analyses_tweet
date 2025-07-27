package com.twitter.analysis;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class TwitterKafkaProducer {
    public static void main(String[] args) {
        String topicName = "tweets-topic";
        String filePath = "C:/Users/hp/Desktop/analyses_tweet/scrap-tweet2025.csv";

        // ✅ Configuration Kafka
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        try {
            int round = 1;
            while (true) {
                System.out.println("\n Envoi de tous les tweets - Round #" + round);

                BufferedReader reader = Files.newBufferedReader(Paths.get(filePath));
                String line;
                reader.readLine(); // ⏭️ Ignorer l'en-tête
                int counter = 1;

                while ((line = reader.readLine()) != null) {
                    ProducerRecord<String, String> record = new ProducerRecord<>(topicName, line);
                    producer.send(record);
                    System.out.println("Tweet #" + counter + " envoyé → " + line);
                    counter++;

                    TimeUnit.MILLISECONDS.sleep(300); // Simule un tweet toutes les 300ms
                }

                System.out.println("Fin du round #" + round);
                round++;

                reader.close();
                TimeUnit.SECONDS.sleep(15); // ⏸️ Pause entre chaque round
            }

        } catch (IOException | InterruptedException e) {
            System.err.println(" Erreur : " + e.getMessage());
        } finally {
            producer.close(); // 🔒 Fermeture propre
        }
    }
}
