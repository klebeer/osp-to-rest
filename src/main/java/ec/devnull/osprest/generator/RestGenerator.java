package ec.devnull.osprest.generator;


import ec.devnull.osprest.Configuration;
import ec.devnull.osprest.generator.data.Pojo;
import ec.devnull.osprest.util.CodeFormatter;
import ec.devnull.osprest.util.MustacheRunner;
import ec.devnull.osprest.util.StringHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


public class RestGenerator {


    private RestGenerator() {
    }

    public static void generate(Configuration configuration) {
        try {
            String packageName = configuration.getRootPackageName();
            String baseDir = configuration.getSourceRoot() + "/" + configuration.getArtifactId() + "-" + Configuration.REST_CONTROLLERS_MODULE;
            String outputDir = baseDir + "/src/main/java/" + packageName.replace(".", "/") + "/";
            String resourcesDir = baseDir + "/src/main/resources/";


            Pojo pojo = new Pojo();
            String className = StringHelper.toCamelCase(configuration.getArtifactId().replace("-", ""));
            pojo.setClassName(className);
            pojo.setPackageName(packageName);
            String javaSource = MustacheRunner.build("rest/restApp.mustache", pojo);
            FileUtils.writeStringToFile(new File(outputDir + className + "RestApp.java"), CodeFormatter.format(javaSource), "utf-8");

            //application yml
            String applicationSource = MustacheRunner.build("rest/application.yaml.mustache", configuration);
            FileUtils.writeStringToFile(new File(resourcesDir + "application.yaml"), applicationSource, "utf-8");

            String applicationProfiles = MustacheRunner.build("rest/application-profile.yaml.mustache", configuration);
            FileUtils.writeStringToFile(new File(resourcesDir + "application-dev.yaml"), applicationProfiles, "utf-8");
            FileUtils.writeStringToFile(new File(resourcesDir + "application-prod.yaml"), applicationProfiles, "utf-8");
            FileUtils.writeStringToFile(new File(resourcesDir + "application-test.yaml"), applicationProfiles, "utf-8");

            //Swagger
            String swaggerSource = MustacheRunner.build("rest/OpenApiConfig.mustache", configuration);
            FileUtils.writeStringToFile(new File(outputDir + "config/OpenApiConfig.java"), CodeFormatter.format(swaggerSource), "utf-8");


        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


}
