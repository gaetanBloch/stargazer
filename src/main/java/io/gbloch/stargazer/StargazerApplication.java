package io.gbloch.stargazer;

import javax.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    tags = {@Tag(name = "Starneighbours", description = "Starneighbours APIs")},
    info =
        @Info(
            title = "Stargazer API",
            version = "1.0.0",
            contact =
                @Contact(
                    name = "Gaëtan Bloch",
                    url = "https://github.com/gaetanBloch",
                    email = "gaetan.bloch@gmail.com"),
            license = @License(name = "Apache-2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")))
public final class StargazerApplication extends Application {}
