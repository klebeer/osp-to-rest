package ec.devnull.osprest.generator;


import ec.devnull.osprest.Configuration;
import ec.devnull.osprest.db.TypeDao;
import ec.devnull.osprest.generator.data.Pojo;
import ec.devnull.osprest.model.Type;
import ec.devnull.osprest.model.TypeAttribute;
import ec.devnull.osprest.util.CodeFormatter;
import ec.devnull.osprest.util.DataSourceProvider;
import ec.devnull.osprest.util.MustacheRunner;
import org.apache.commons.io.FileUtils;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class ConverterObjectGenerator {

    private ConverterObjectGenerator() {
    }

    public static void generate(Configuration configuration) {
        try {
            String packageName = configuration.getRootPackageName() + "." + configuration.getPackages().getConverterObjects();
            String objectPackage = configuration.getRootPackageName() + "." + configuration.getPackages().getEntityObjects();
            String outputDir = configuration.getSourceRoot() + "/" + configuration.getArtifactId() + "-" + Configuration.INTERFACES_MODULE + "/src/main/java/" + packageName.replace(".", "/") + "/";

            TypeDao typeDao = new TypeDao(DataSourceProvider.getDataSource(configuration));

            List<String> types = typeDao.getTypeList(configuration.getPackageName());
            typeDao.validateTypeList(types);
            for (String typeName : types) {
                generateType(packageName, objectPackage, outputDir, typeName, typeDao.getTypeAttributes(typeName));
            }

            if (Configuration.GENERATE_SOURCE_FOR_PLSQL_TYPES) {
                List<String> embeddedTypes = typeDao.getEmbeddedTypeList();
                for (String typeName : embeddedTypes) {
                    generateType(packageName, objectPackage, outputDir, typeName, typeDao.getEmbeddedTypeAttributes(typeName));
                }
            }

            generatePrimitiveTypeConverter(packageName, outputDir);

        } catch (PropertyVetoException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void generatePrimitiveTypeConverter(String packageName, String outputDir) throws IOException {
        Pojo pojo = new Pojo();
        pojo.setPackageName(packageName);
        String javaSource = MustacheRunner.build("PrimitiveTypeConverter.java.mustache", pojo);
        FileUtils.writeStringToFile(new File(outputDir + "PrimitiveTypeConverter.java"), CodeFormatter.format(javaSource), "utf-8");
    }

    private static void generateType(String packageName, String objectPackage, String outputDir, String typeName, List<TypeAttribute> typeAttributes) throws IOException {
        Type type = new Type();
        type.setTypeName(typeName);
        type.setAttributeList(typeAttributes);
        type.setConverterPackageName(packageName);
        type.setObjectPackage(objectPackage);
        String javaSource = MustacheRunner.build("converter.mustache", type);
        FileUtils.writeStringToFile(new File(outputDir + type.getJavaClassName() + "Converter.java"), CodeFormatter.format(javaSource), "utf-8");
    }


}
