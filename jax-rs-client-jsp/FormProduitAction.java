package ws.rest.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;
import iset.master.spring.model.Produit;

@WebServlet("/FormProduitAction")
public class FormProduitAction extends HttpServlet {

    private static final String BASE_URL = "http://localhost:8034/produits/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // a) Récupérer les paramètres du formulaire
        String nom = request.getParameter("nom");
        String description = request.getParameter("description");
        double prix = Double.parseDouble(request.getParameter("prix"));

        // b) Créer une instance de Produit
        Produit p = new Produit();
        p.setNom(nom);
        p.setDescription(description);
        p.setPrix(prix);

        // c) Convertir l'objet en JSON
        ObjectMapper mapper = new ObjectMapper();
        String jsonProduit = mapper.writeValueAsString(p);

        // d) Envoyer le POST pour ajouter le produit
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonProduit.getBytes());
            os.flush();
        }

        // Vérifier la réponse
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_CREATED) {
            throw new RuntimeException("Erreur HTTP : " + code);
        }

        // e) Récupérer tous les produits
        URL getUrl = new URL(BASE_URL);
        HttpURLConnection getConn = (HttpURLConnection) getUrl.openConnection();
        getConn.setRequestMethod("GET");
        getConn.setRequestProperty("Accept", "application/json");

        Scanner scanner = new Scanner(getConn.getInputStream());
        StringBuilder jsonResponse = new StringBuilder();
        while (scanner.hasNext()) {
            jsonResponse.append(scanner.nextLine());
        }
        scanner.close();

        // Afficher la liste des produits dans la réponse
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}