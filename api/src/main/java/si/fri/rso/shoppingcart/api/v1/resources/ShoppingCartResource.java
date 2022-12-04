package si.fri.rso.shoppingcart.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.shoppingcart.lib.ShoppingCart;
import si.fri.rso.shoppingcart.services.beans.ShoppingCartBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;


@ApplicationScoped
@Path("/shopping-cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShoppingCartResource {

    private Logger log = Logger.getLogger(ShoppingCartResource.class.getName());

    @Inject
    private ShoppingCartBean shoppingCartBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all shopping carts.", summary = "Get all shopping carts")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of shopping carts.",
                    content = @Content(schema = @Schema(implementation = ShoppingCart.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getShoppingCarts() {

        List<ShoppingCart> shoppingCarts = shoppingCartBean.getShoppingCarts();

        return Response.status(Response.Status.OK).entity(shoppingCarts).build();
    }


    @Operation(description = "Get shopping cart by id.", summary = "Get shopping cart by id")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Shopping cart information.",
                    content = @Content(
                            schema = @Schema(implementation = ShoppingCart.class))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Shopping cart not found."
            )})
    @GET
    @Path("/{shoppingCartId}")
    public Response getShoppingCartById(@Parameter(description = "Shopping cart ID.", required = true)
                                     @PathParam("shoppingCartId") Integer shoppingCartId) {

        ShoppingCart shoppingCart = shoppingCartBean.getShoppingCartById(shoppingCartId);

        if (shoppingCart == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(shoppingCart).build();
    }

    @Operation(description = "Add shopping cart.", summary = "Add shopping cart")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Shopping cart successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error.")
    })
    @POST
    public Response createShoppingCart(@RequestBody(
            description = "DTO object with shopping cart data.",
            required = true, content = @Content(
            schema = @Schema(implementation = ShoppingCart.class))) ShoppingCart shoppingCart) {

        if (shoppingCart.getProducts() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            shoppingCart = shoppingCartBean.createShoppingCart(shoppingCart);
        }

        return Response.status(Response.Status.CONFLICT).entity(shoppingCart).build();
    }


    @Operation(description = "Update shopping cart data.", summary = "Update shoping cart")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Shopping cart successfully updated."
            )
    })
    @PUT
    @Path("{shoppingCartId}")
    public Response putShoppingCart(@Parameter(description = "Shopping cart ID.", required = true)
                                     @PathParam("shoppingCartId") Integer shoppingCartId,
                                     @RequestBody(
                                             description = "DTO object with shopping cart data.",
                                             required = true, content = @Content(
                                             schema = @Schema(implementation = ShoppingCart.class)))
                                             ShoppingCart shoppingCart) {

        shoppingCart = shoppingCartBean.putShoppingCart(shoppingCartId, shoppingCart);

        if (shoppingCart == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();
    }


    @Operation(description = "Delete shopping cart.", summary = "Delete shopping cart")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Shopping cart successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Shopping cart not found."
            )
    })
    @DELETE
    @Path("{shoppingCartId}")
    public Response deleteShoppingCart(@Parameter(description = "Shopping cart ID.", required = true)
                                        @PathParam("shoppingCartId") Integer shoppingCartId) {

        boolean deleted = shoppingCartBean.deleteShoppingCart(shoppingCartId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
