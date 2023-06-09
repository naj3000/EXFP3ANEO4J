
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Values;

import java.util.List;
import java.util.Scanner;

public class Conexion {
 private static final String URI = "bolt://localhost:7687";  // URI de la base de datos Neo4j
private static final String USER = "neo4j";  // Nombre de usuario para autenticación
private static final String PASSWORD = "12345678";  // Contraseña para autenticación

public static void main(String[] args) {
    // Establecer una conexión con el servidor Neo4j
    try (Driver driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD))) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Mostrar opciones de menú
            System.out.println("Opciones:");
            System.out.println("1. Crear nodo raíz");
            System.out.println("2. Crear playlist");
            System.out.println("3. Agregar elemento a playlist");
            System.out.println("4. Eliminar playlist");
            System.out.println("5. Mostrar playlists");
            System.out.println("6. Salir");
            System.out.print("Ingrese una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer de entrada

            switch (opcion) {
                case 1:
                    crearNodoRaiz(driver);
                    break;
                case 2:
                    crearPlaylist(driver, scanner);
                    break;
                case 3:
                    agregarElementoAPlaylist(driver, scanner);
                    break;
                case 4:
                    eliminarPlaylist(driver, scanner);
                    break;
                case 5:
                    mostrarPlaylists(driver);
                    break;
                case 6:
                    return; // Salir del programa
                default:
                    System.out.println("Opción inválida");
                    break;
            }
        }
    }
}

// Crea un nodo raíz en la base de datos
private static void crearNodoRaiz(Driver driver) {
    try (Session session = driver.session()) {
        session.writeTransaction(new TransactionWork<Void>() {
            @Override
            public Void execute(Transaction tx) {
                // Ejecuta una transacción en la base de datos para crear un nodo raíz
                tx.run("CREATE (:Celular {nombre: 'BRANDON'})");
                return null;
            }
        });
        System.out.println("Nodo raíz creado");
    }
}

// Crea una playlist en la base de datos
private static void crearPlaylist(Driver driver, Scanner scanner) {
    System.out.print("Ingrese el nombre de la playlist: ");
    String nombre = scanner.nextLine();

    try (Session session = driver.session()) {
        session.writeTransaction(new TransactionWork<Void>() {
            @Override
            public Void execute(Transaction tx) {
                // Ejecuta una transacción en la base de datos para crear una playlist
                tx.run("MATCH (celular:Celular {nombre: 'BRANDON'}) " +
                        "CREATE (celular)-[:ES_PARTE_DE]->(:Playlist {nombre: $nombre})",
                        Values.parameters("nombre", nombre));
                return null;
            }
        });
        System.out.println("Playlist creada");
    }
}

// Agrega un elemento a una playlist en la base de datos
private static void agregarElementoAPlaylist(Driver driver, Scanner scanner) {
    System.out.print("Ingrese el nombre de la playlist: ");
    String playlist = scanner.nextLine();
    System.out.print("Ingrese el nombre del elemento: ");
    String elemento = scanner.nextLine();

    try (Session session = driver.session()) {
        session.writeTransaction(new TransactionWork<Void>() {
            @Override
            public Void execute(Transaction tx) {
                // Ejecuta una transacción en la base de datos para agregar un elemento a la playlist
                tx.run("MATCH (p:Playlist {nombre: $playlist}) " +
                        "CREATE (p)-[:CONTIENE]->(:Elemento {nombre: $elemento})",
                        Values.parameters("playlist", playlist, "elemento", elemento));
                return null;
            }
        });
        System.out.println("Elemento agregado a la playlist");
    }
}

// Elimina una playlist de la base de datos
private static void eliminarPlaylist(Driver driver, Scanner scanner) {
    System.out.print("Ingrese el nombre de la playlist a eliminar: ");
    String playlist = scanner.nextLine();

    try (Session session = driver.session()) {
        session.writeTransaction(new TransactionWork<Void>() {
            @Override
            public Void execute(Transaction tx) {
                // Ejecuta una transacción en la base de datos para eliminar una playlist
                tx.run("MATCH (celular:Celular {nombre: 'BRANDON'})-[:ES_PARTE_DE]->(p:Playlist {nombre: $playlist}) " +
                        "DETACH DELETE p",
                        Values.parameters("playlist", playlist));
                return null;
            }
        });
        System.out.println("Playlist eliminada");
    }
}

// Muestra todas las playlists en la base de datos
private static void mostrarPlaylists(Driver driver) {
    try (Session session = driver.session()) {
        List<Record> playlists = session.readTransaction(new TransactionWork<List<Record>>() {
            @Override
            public List<Record> execute(Transaction tx) {
                // Ejecuta una transacción en la base de datos para obtener todas las playlists
                Result result = tx.run("MATCH (celular:Celular {nombre: 'BRANDON'})-[:ES_PARTE_DE]->(p:Playlist) " +
                        "RETURN p.nombre");
                return result.list();
            }
        });

        System.out.println("Playlists:");

        for (Record record : playlists) {
            // Imprime el nombre de cada playlist
            System.out.println(record.get("p.nombre").asString());
        }
    }
}
}
