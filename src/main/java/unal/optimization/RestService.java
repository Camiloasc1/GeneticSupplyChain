package unal.optimization;
import unal.optimization.Entity.Graph;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by root on 11/20/16.
 */
// The Java class will be hosted at the URI path "/helloworld"
@Path("/Service")
public class RestService {
    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces("text/plain")
    public String getClichedMessage() {
        // Return some cliched textual content
        return "Test";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClichedMessage(Graph g) {
        // Return some cliched textual content
        System.out.println(g.toString());
        return Response.ok(g).build();
    }

}
