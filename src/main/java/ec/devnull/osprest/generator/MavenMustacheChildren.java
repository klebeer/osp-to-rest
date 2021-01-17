package ec.devnull.osprest.generator;

import lombok.Data;

import java.util.List;

@Data
public class MavenMustacheChildren extends MavenMustache {

    private String artifactParentId;
    private List<MavenMustache> dependencies;

}
