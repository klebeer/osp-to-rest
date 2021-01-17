package ec.devnull.osprest.generator;

import ec.devnull.osprest.Configuration;
import ec.devnull.osprest.util.MustacheRunner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class MavenPomGenerator {


    private MavenPomGenerator() {
    }

    public static void generate(Configuration configuration) {
        try {
            String artifactGroupId = configuration.getArtifactGroupId();
            String artifactId = configuration.getArtifactId();
            String artifactVersion = configuration.getArtifactVersion();
            String outputDir = configuration.getSourceRoot() + "/" + artifactId + "/";

            configuration.setSourceRoot(outputDir);

            String artifactIdInterfaces = artifactId.concat("-").concat(Configuration.INTERFACES_MODULE);
            String artifactIdImpls = artifactId.concat("-").concat(Configuration.IMPL_MODULE);
            String artifactIdControllers = artifactId.concat("-").concat(Configuration.REST_CONTROLLERS_MODULE);


            generateMvnMainObject(artifactGroupId, artifactId, artifactVersion, outputDir);


            List<MavenMustache> dependencies = new ArrayList<>();
            MavenMustache dependencyConfiguration = new MavenMustache();
            dependencyConfiguration.setArtifactGroupId(artifactGroupId);

            dependencyConfiguration.setArtifactVersion(artifactVersion);

            dependencies.add(dependencyConfiguration);

            generateMvnInterfacesObject(artifactGroupId,
                    artifactId,
                    artifactIdInterfaces,
                    artifactVersion,
                    dependencies,
                    outputDir.concat(artifactIdInterfaces).concat("/"));


            dependencies.clear();
            MavenMustache dependencyInterface = new MavenMustache();
            dependencyInterface.setArtifactGroupId(artifactGroupId);
            dependencyInterface.setArtifactId(artifactIdInterfaces);
            dependencyInterface.setArtifactVersion(artifactVersion);

            dependencies.add(dependencyConfiguration);
            dependencies.add(dependencyInterface);

            generateMvnImplsObject(artifactGroupId,
                    artifactId,
                    artifactIdImpls,
                    artifactVersion,
                    dependencies,
                    outputDir.concat(artifactIdImpls).concat("/"));

            dependencies.clear();
            MavenMustache dependencyImpl = new MavenMustache();
            dependencyImpl.setArtifactGroupId(artifactGroupId);
            dependencyImpl.setArtifactId(artifactIdImpls);
            dependencyImpl.setArtifactVersion(artifactVersion);

            dependencies.add(dependencyConfiguration);
            dependencies.add(dependencyInterface);
            dependencies.add(dependencyImpl);

            generateMvnControllersObject(artifactGroupId,
                    artifactId,
                    artifactIdControllers,
                    artifactVersion,
                    dependencies,
                    outputDir.concat(artifactIdControllers).concat("/"));

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void generateMvnMainObject(String artifactGroupId, String artifactId, String artifactVersion, String outputDir) throws IOException {
        MavenMustache pojo = new MavenMustache();
        pojo.setArtifactGroupId(artifactGroupId);
        pojo.setArtifactId(artifactId);
        pojo.setArtifactVersion(artifactVersion);

        String xmlSource = MustacheRunner.build("rest/mvn.mustache", pojo);
        FileUtils.writeStringToFile(new File(outputDir + "pom.xml"), xmlSource, "utf-8");
    }

    private static void generateMvnConfigurationObject(String artifactGroupId, String artifactIdParent, String artifactId, String artifactVersion, String outputDir) throws IOException {
        MavenMustacheChildren pojo = new MavenMustacheChildren();
        pojo.setArtifactGroupId(artifactGroupId);
        pojo.setArtifactParentId(artifactIdParent);
        pojo.setArtifactVersion(artifactVersion);
        pojo.setArtifactId(artifactId);


        Path path = Paths.get(outputDir + "/src/main/java/../../test/java/");
        Files.createDirectories(path);

        String xmlSource = MustacheRunner.build("rest/mvnConfiguration.mustache", pojo);
        FileUtils.writeStringToFile(new File(outputDir + "pom.xml"), xmlSource, "utf-8");
    }

    private static void generateMvnInterfacesObject(String artifactGroupId, String artifactIdParent, String artifactId, String artifactVersion, List<MavenMustache> dependencies, String outputDir) throws IOException {
        MavenMustacheChildren pojo = new MavenMustacheChildren();
        pojo.setArtifactGroupId(artifactGroupId);
        pojo.setArtifactParentId(artifactIdParent);
        pojo.setArtifactVersion(artifactVersion);
        pojo.setArtifactId(artifactId);
        pojo.setDependencies(dependencies);


        Path path = Paths.get(outputDir + "/src/main/java/../../test/java/");
        Files.createDirectories(path);

        String xmlSource = MustacheRunner.build("rest/mvnInterface.mustache", pojo);
        FileUtils.writeStringToFile(new File(outputDir + "pom.xml"), xmlSource, "utf-8");
    }

    private static void generateMvnImplsObject(String artifactGroupId, String artifactIdParent, String artifactId, String artifactVersion, List<MavenMustache> dependencies, String outputDir) throws IOException {
        MavenMustacheChildren pojo = new MavenMustacheChildren();
        pojo.setArtifactGroupId(artifactGroupId);
        pojo.setArtifactParentId(artifactIdParent);
        pojo.setArtifactVersion(artifactVersion);
        pojo.setArtifactId(artifactId);
        pojo.setDependencies(dependencies);


        Path path = Paths.get(outputDir + "/src/main/java/../../test/java/");
        Files.createDirectories(path);

        String xmlSource = MustacheRunner.build("rest/mvnImpl.mustache", pojo);
        FileUtils.writeStringToFile(new File(outputDir + "pom.xml"), xmlSource, "utf-8");
    }

    private static void generateMvnControllersObject(String artifactGroupId, String artifactIdParent, String artifactId, String artifactVersion, List<MavenMustache> dependencies, String outputDir) throws IOException {
        MavenMustacheChildren pojo = new MavenMustacheChildren();
        pojo.setArtifactGroupId(artifactGroupId);
        pojo.setArtifactParentId(artifactIdParent);
        pojo.setArtifactVersion(artifactVersion);
        pojo.setArtifactId(artifactId);
        pojo.setDependencies(dependencies);

        Files.createDirectories(Paths.get(outputDir + "/src/main/java/"));
        Files.createDirectories(Paths.get(outputDir + "/src/main/java/"));

        String xmlSource = MustacheRunner.build("rest/mvnController.mustache", pojo);
        FileUtils.writeStringToFile(new File(outputDir + "pom.xml"), xmlSource, "utf-8");
    }

}
