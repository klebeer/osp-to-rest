package ec.devnull.osprest.generator;


import ec.devnull.osprest.Configuration;
import ec.devnull.osprest.db.ProcedureDao;
import ec.devnull.osprest.generator.data.PojoService;
import ec.devnull.osprest.model.OraclePackage;
import ec.devnull.osprest.model.Procedure;
import ec.devnull.osprest.util.CodeFormatter;
import ec.devnull.osprest.util.DataSourceProvider;
import ec.devnull.osprest.util.MustacheRunner;
import org.apache.commons.io.FileUtils;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public final class PackageObjectGenerator {

    private PackageObjectGenerator() {
    }

    public static void generate(Configuration configuration) {
        try {

            String implementationPackageName = configuration.getRootPackageName() + "." + configuration.getPackages().getPackageObjects();
            String contextPackage = configuration.getRootPackageName() + "." + configuration.getPackages().getProcedureContextObjects();
            String converterPackage = configuration.getRootPackageName() + "." + configuration.getPackages().getConverterObjects();
            String objectPackage = configuration.getRootPackageName() + "." + configuration.getPackages().getEntityObjects();
            String apiPackage = configuration.getRootPackageName() + "." + configuration.getPackages().getInterfacesObjects();
            String restPackage = configuration.getRootPackageName() + "." + configuration.getPackages().getRestObjects();
            String outputDir = configuration.getSourceRoot() + "/" + configuration.getArtifactId() + "-" + Configuration.IMPL_MODULE + "/src/main/java/" + implementationPackageName.replace(".", "/") + "/";
            String outputApiDir = configuration.getSourceRoot() + "/" + configuration.getArtifactId() + "-" + Configuration.INTERFACES_MODULE + "/src/main/java/" + apiPackage.replace(".", "/") + "/";
            String outputRestDir = configuration.getSourceRoot() + "/" + configuration.getArtifactId() + "-" + Configuration.REST_CONTROLLERS_MODULE + "/src/main/java/" + restPackage.replace(".", "/") + "/";

            String loggingClassInitializer = "";
            String loggingMethod = "";


            List<OraclePackage> allPackages = new ProcedureDao(DataSourceProvider.getDataSource(configuration)).getAllPackages(configuration.getPackageName());

            if (configuration.getPackageFunctions() != null) {
                allPackages.forEach(pkg -> {
                    List<Procedure> allProcedures = pkg.getProcedureList();
                    allProcedures = allProcedures.stream()
                            .filter(procedure -> configuration.getPackageFunctions().stream()
                                    .anyMatch(select -> select.getObjectName().equals(procedure.getProcedureName())))
                            .map(procedure -> {
                                configuration.getPackageFunctions().forEach(
                                        item -> {
                                            if (item.getObjectName().equals(procedure.getProcedureName())) {
                                                procedure.setProcedureDescription(item.getObjectDescription());
                                            }
                                        }
                                );
                                return procedure;
                            })
                            .collect(Collectors.toList());
                    pkg.setProcedureList(allProcedures);
                });
            }
            for (OraclePackage oraclePackage : allPackages) {
                oraclePackage.setJavaPackageName(implementationPackageName);
                oraclePackage.setContextPackage(contextPackage);
                oraclePackage.setConverterPackage(converterPackage);
                oraclePackage.setObjectPackage(objectPackage);
                oraclePackage.setBuilderPackage(apiPackage);
                oraclePackage.setRestPackage(restPackage);


                if (!loggingClassInitializer.isEmpty() && !loggingMethod.isEmpty()) {
                    oraclePackage.setLoggingInitializer(String.format(loggingClassInitializer, oraclePackage.getJavaClassName()));
                    oraclePackage.setLoggingMethod(loggingMethod);
                }

                generateInterfaces(outputApiDir, oraclePackage);
                generateRestLayer(outputRestDir, oraclePackage);
                generatePackageObject(outputDir, oraclePackage);

            }

            generateStoredProcedureCallExceptionClass(implementationPackageName, outputDir);

        } catch (PropertyVetoException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void generatePackageObject(String outputDir, OraclePackage oraclePackage) throws IOException {
        String javaSource = MustacheRunner.build("rest/serviceImplementation.mustache", oraclePackage);
        FileUtils.writeStringToFile(new File(outputDir + oraclePackage.getJavaClassName() + "Impl" + ".java"), CodeFormatter.format(javaSource), "utf-8");
    }

    private static void generateInterfaces(String outputApiDir, OraclePackage oraclePackage) throws IOException {
        String javaSource = MustacheRunner.build("rest/serviceInterface.mustache", oraclePackage);
        FileUtils.writeStringToFile(new File(outputApiDir + oraclePackage.getJavaClassName() + ".java"), CodeFormatter.format(javaSource), "utf-8");
    }

    private static void generateRestLayer(String outputDir, OraclePackage oraclePackage) throws IOException {
        String javaSource = MustacheRunner.build("rest/restController.mustache", oraclePackage);
        FileUtils.writeStringToFile(new File(outputDir + oraclePackage.getJavaClassName() + "RestController" + ".java"), CodeFormatter.format(javaSource), "utf-8");
    }

    private static void generateStoredProcedureCallExceptionClass(String packageName, String outputDir) throws IOException {
        OraclePackage op = new OraclePackage();
        op.setJavaPackageName(packageName);
        String javaSource = MustacheRunner.build("StoredProcedureCallException.java.mustache", op);
        FileUtils.writeStringToFile(new File(outputDir + "StoredProcedureCallException.java"), CodeFormatter.format(javaSource), "utf-8");
    }

    private static void generateServiceFile(String outputDir, OraclePackage oraclePackage) throws IOException {
        PojoService service = new PojoService();
        service.setName(oraclePackage.getJavaPackageName() + "." + oraclePackage.getJavaClassName());
        String serviceSource = MustacheRunner.build("rest/serviceFile.mustache", service);
        FileUtils.writeStringToFile(new File(outputDir + oraclePackage.getBuilderPackage() + "." + oraclePackage.getJavaClassName()), serviceSource, "utf-8");
    }
}
