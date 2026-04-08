package ws.rest;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
public class MainClient {
 public static void main( String[] args )
 {
 System.out.println("Démarrage du Client....");
 // Objet de configuration
 ClientConfig config = new DefaultClientConfig();
 //objet client
 Client client = Client.create(config);
 //créer l'uri
 URI uri =
UriBuilder.fromUri("http://localhost:8080/produits").build();
 //obtenir une resource correspondante à l'uri du service web
 WebResource service = client.resource(uri);
 //Requête GET
System.out.println( "***********************************************" );
System.out.println("Méthode GET - Afficher tous les produits....");
