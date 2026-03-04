package ws.rest;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import iset.master.spring.model.Produit;

public class MainClient {
 public static void main( String[] args )
 {
 System.out.println("Démarrage du Client....");
 // Objet de configuration
 ClientConfig config = new DefaultClientConfig();
 config.getFeatures().put(com.sun.jersey.api.json.JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
 //objet client
 Client client = Client.create(config);
 //créer l'uri
 URI uri =
UriBuilder.fromUri("http://localhost:8034/produits").build();
 //obtenir une resource correspondante à l'uri du service web
 WebResource service = client.resource(uri);
 //Requête GET
System.out.println( "***********************************************" );

//référencer la méthode "getAllProduits"
WebResource resource= service.path("/");
//passer la méthode "get"
String reponseGetAllProduits= resource.accept(MediaType.APPLICATION_JSON).get(String.class);
System.out.println("Méthode GET - Afficher tous les produits....");
//Requête POST
System.out.println( "***********************************************" );
System.out.println( "Méthode POST - Ajouter un nouveau produit...." );
//référencer la méthode "saveProduit"
WebResource resourceSave= service.path("/") ;
Produit nouveauProduit =new Produit("Biscuit", 0.800, 15);
//passer la méthode « post »
Produit reponseSaveProduit=
 resourceSave
 .type(MediaType.APPLICATION_JSON)
 .post(Produit.class, nouveauProduit) ;
// Afficher la réponse textuelle de l'opération d'ajout


System.out.println(reponseSaveProduit) ;
//Afficher la réponse textuelle
System.out.println(reponseGetAllProduits);

// Récupérer des objets "Produit" en utilisant l'API Gson
Gson gson = new GsonBuilder()
    .setLenient()
    .registerTypeAdapter(java.time.LocalDate.class, new com.google.gson.JsonDeserializer<java.time.LocalDate>() {
        @Override
        public java.time.LocalDate deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
            if (json.getAsString().trim().isEmpty()) return null;
            return java.time.LocalDate.parse(json.getAsString());
        }
    })
    .create();

// Transformer la String JSON en JsonArray
JsonArray jo = JsonParser
        .parseString(reponseGetAllProduits)
        .getAsJsonArray();

// Convertir JsonArray -> tableau de Produit
Produit[] listeP = gson.fromJson(jo, Produit[].class);

System.out.println("***********************************************");
System.out.println("Liste des produits (API Gson)....");

for (Produit p : listeP) {
    System.out.println(p);
}
}
}