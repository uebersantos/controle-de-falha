/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub.web;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import dev.rpmhub.client.CatalogRC;
import dev.rpmhub.model.Book;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/bookManagement")
public class ManagementWS {

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(ManagementWS.class.getName());

    @RestClient
    @Inject
    CatalogRC catalog;

    /**
     * Retrieves a list of available books.
     *
     * @return a list of available books
     */
    @GET
    @Path("/listBooks")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    @CircuitBreaker(
            requestVolumeThreshold = 4, // Número de falhas antes de abrir o circuito
            failureRatio = 0.75, // Proporção de falhas para abrir o circuito
            delay = 10000, // Tempo em milissegundos para o circuito permanecer aberto antes de tentar fechar
            successThreshold = 2 // Número de sucessos antes de fechar o circuito
    )
    @Timeout(500) // Tempo máximo de espera em milissegundos antes de lançar uma exceção
    public List<Book> listBooksAvailable() {
        LOGGER.info("ManagementWS listBooksAvailable executed");
        return catalog.listBooksAvailable();
    }

    /**
     * Marks a book as not available based on the provided ISBN.
     *
     * @param isbn the ISBN of the book to mark as not available
     * @return the updated list of books
     */
    @POST
    @Path("/markNotAvailable")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("User")
    public List<Book> markNotAvailable(String json) {
        return catalog.markNotAvailable(json);
    }
}
