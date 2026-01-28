package com.antarctic;

import java.util.Scanner;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class App {
    public static void main(String[] args) {
        System.out.println("--- Starting Field Log ---");
        DatabaseHandler.initializeDatabase();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Antarctic Field Log ---");
            System.out.println("[1] Log New Penguin");
            System.out.println("[2] Exit");
            System.out.println("[3] View History");

            int choice = sc.nextInt();
            if (choice == 2) {
                System.out.println("--- Exiting Field Log ---");
                break;
            }
            if (choice == 3) {
                DatabaseHandler.printAllEncounters();
                continue;
            }

            System.out.println("Enter name of Island: ");
            String island = sc.next();

            System.out.println("Enter sex: ");
            String sex = sc.next();

            double billLength;
            while (true) {
                System.out.println("Enter Bill Length: ");
                billLength = sc.nextDouble();
                if (InputValidator.isValidMeasurement(billLength, 60)) {
                    break;
                }
            }

            double billDepth;
            while (true) {
                System.out.println("Enter Bill Depth: ");
                billDepth = sc.nextDouble();
                if (InputValidator.isValidMeasurement(billDepth, 60)) {
                    break;
                }
            }

            double flipperLength;
            while (true) {
                System.out.println("Enter Flipper Length: ");
                flipperLength = sc.nextDouble();
                if (InputValidator.isValidMeasurement(flipperLength, 230)) {
                    break;
                }
            }

            double bodyMass;
            while (true) {
                System.out.println("Enter Body Mass: ");
                bodyMass = sc.nextDouble();
                if (InputValidator.isValidMeasurement(bodyMass, 6500)) {
                    break;
                }
            }

            System.out.println("Contacting AI...");

            String json = String.format(
                    "{" +
                            "\"island\": \"%s\"," +
                            "\"bill_length_mm\": %.2f," +
                            "\"bill_depth_mm\": %.2f, " +
                            "\"flipper_length_mm\": %.2f, " +
                            "\"body_mass_g\": %.2f, " +
                            "\"sex\": \"%s\"" +
                            "}",
                    island, billLength, billDepth, flipperLength, bodyMass, sex);

            try {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://127.0.0.1:5000/predict"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String responseBody = response.body();

                String species = responseBody.replace("{\"species\": \"", "")
                        .replace("\"}", "")
                        .trim();

                System.out.println("\n>>> AI IDENTIFIED SPECIES: " + species);

                DatabaseHandler.saveEncounter(island, sex, species);
            } catch (Exception e) {
                System.out.println("Error: could not contact AI. is app.py running?");
                e.printStackTrace();
            }

        }
    }
}
