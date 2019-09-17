package revolut;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 *  jetty server
 */
public class Server {

    public static void main (String[] args) {

        ResourceConfig config = new ResourceConfig() {
            {
                register(Resource.class);   // register the resource
                register(new Binder()); // registers the dependency injections
            }
        };
        config.packages("revolut");
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(2222);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/*");

        // start a server
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }

    }
}
