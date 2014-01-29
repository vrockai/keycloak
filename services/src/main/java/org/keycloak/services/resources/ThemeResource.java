package org.keycloak.services.resources;

import org.keycloak.freemarker.Theme;
import org.keycloak.freemarker.ThemeLoader;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/theme")
public class ThemeResource {

    private static FileTypeMap mimeTypes = MimetypesFileTypeMap.getDefaultFileTypeMap();

    @GET
    @Path("/{themType}/{themeName}/{path:.*}")
    public Response createQrCode(@PathParam("themType") String themType, @PathParam("themeName") String themeName, @PathParam("path") String path) {
        Theme theme = ThemeLoader.createTheme(themeName, Theme.Type.valueOf(themType.toUpperCase()));
        InputStream resource = theme.getResource(path);
        if (resource != null) {
            return Response.ok(resource).type(mimeTypes.getContentType(path)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }



}
