package ec.devnull.osprest.generator;

import ec.devnull.osprest.Configuration;
import ec.devnull.osprest.util.MustacheRunner;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class DevOpsGenerator {

    private DevOpsGenerator() {
    }

    public static void generate(Configuration configuration) {
        try {
            String baseDir = configuration.getSourceRoot() + "/" + configuration.getArtifactId() + "-" + Configuration.REST_CONTROLLERS_MODULE;

            String dockerSource = MustacheRunner.build("devOps/Dockerfile.mustache", configuration);
            FileUtils.writeStringToFile(new File(baseDir + "/Dockerfile"), dockerSource, "utf-8");
            String dockerCompose = MustacheRunner.build("devOps/docker-compose.yml.mustache", configuration);
            FileUtils.writeStringToFile(new File(baseDir + "/docker-compose.yml"), dockerCompose, "utf-8");

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
