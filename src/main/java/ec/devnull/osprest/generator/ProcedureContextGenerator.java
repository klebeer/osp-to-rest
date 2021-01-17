/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Ferenc Karsany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package ec.devnull.osprest.generator;


import ec.devnull.osprest.Configuration;
import ec.devnull.osprest.db.ProcedureDao;
import ec.devnull.osprest.generator.data.Pojo;
import ec.devnull.osprest.generator.data.PojoMapper;
import ec.devnull.osprest.model.Procedure;
import ec.devnull.osprest.model.ProcedureArgument;
import ec.devnull.osprest.util.CodeFormatter;
import ec.devnull.osprest.util.DataSourceProvider;
import ec.devnull.osprest.util.MustacheRunner;
import org.apache.commons.io.FileUtils;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fkarsany on 2015.01.28..
 */
public final class ProcedureContextGenerator {

    private ProcedureContextGenerator() {
    }

    public static void generate(Configuration configuration) {

        try {
            String packageName = configuration.getRootPackageName() + "." + configuration.getPackages().getProcedureContextObjects();
            String objectPackage = configuration.getRootPackageName() + "." + configuration.getPackages().getEntityObjects();
            String outputDir = configuration.getSourceRoot() + "/" + configuration.getArtifactId() + "-" + Configuration.INTERFACES_MODULE + "/src/main/java/" + packageName.replace(".", "/") + "/";
            ProcedureDao procedureDao = new ProcedureDao(DataSourceProvider.getDataSource(configuration));

            List<Procedure> allProcedures = procedureDao.getAllProcedure(configuration.getPackageName());

            if (configuration.getProcedureName() != null) {
                allProcedures = allProcedures.stream()
                        .filter(procedure ->
                                procedure.getProcedureName().equals(configuration.getProcedureName()))
                        .collect(Collectors.toList());
            }
            for (Procedure procedure : allProcedures) {
                generateProcedureContext(packageName, objectPackage, outputDir, procedure);
            }
        } catch (PropertyVetoException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void generateProcedureContext(String packageName, String objectPackage, String outputDir, Procedure procedure) throws IOException {
        List<ProcedureArgument> argumentList = procedure.getArgumentList();
        String pojoClassName = procedure.getStoredProcedureClassName();


        List<ProcedureArgument> argumentListIn = argumentList.stream().filter(ProcedureArgument::isInParam).collect(Collectors.toList());
        procedure.setArgumentList(argumentListIn);

        buildPojo(pojoClassName + "In", packageName + ".in", objectPackage, outputDir + "in/", procedure);

        List<ProcedureArgument> argumentListOut = argumentList.stream().filter(ProcedureArgument::isOutParam).collect(Collectors.toList());
        procedure.setArgumentList(argumentListOut);

        buildPojo(pojoClassName + "Out", packageName + ".output", objectPackage, outputDir + "output/", procedure);

    }

    private static void buildPojo(String pojoClassName, String packageName, String objectPackage, String outputDir, Procedure procedure) throws IOException {
        Pojo pojo = PojoMapper.procedureToPojo(procedure);
        pojo.setClassName(pojoClassName);
        pojo.setPackageName(packageName);
        pojo.setGeneratorName("ProcedureContextGenerator");
        pojo.getImports().add(objectPackage + ".*");

        String javaSource = MustacheRunner.build("pojo.mustache", pojo);
        FileUtils.writeStringToFile(new File(outputDir + pojo.getClassName() + ".java"), CodeFormatter.format(javaSource), "utf-8");
    }
}
