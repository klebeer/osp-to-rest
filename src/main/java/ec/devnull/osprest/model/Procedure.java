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

package ec.devnull.osprest.model;


import ec.devnull.osprest.builder.CallStringBuilder;
import ec.devnull.osprest.util.StringHelper;
import ec.devnull.osprest.util.TypeMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: fkarsany Date: 2013.11.18.
 */
public class Procedure {

    private String objectName;
    private String procedureName;
    private String procedureDescription;
    private String overload;
    private String methodType;
    private List<ec.devnull.osprest.model.ProcedureArgument> argumentList;
    private List<ec.devnull.osprest.model.BindParam> bindParams = null;
    private String callString;

    private Procedure() {
    }

    public List<ec.devnull.osprest.model.ProcedureArgument> getArgumentList() {
        return argumentList;
    }

    public void setArgumentList(List<ec.devnull.osprest.model.ProcedureArgument> argumentList) {
        this.argumentList = argumentList;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getOverload() {
        return overload;
    }

    public void setOverload(String overload) {
        this.overload = overload;
    }

    public String getProcedureDescription() {
        return procedureDescription;
    }

    public void setProcedureDescription(String procedureDescription) {
        this.procedureDescription = procedureDescription;
    }

    public String getJavaProcedureName() {
        String r = StringHelper.toCamelCaseSmallBegin(this.procedureName + "_" + this.overload);
        return StringHelper.unJavaKeyword(r);
    }

    public String getJavaProcedureDescription() {
        return this.procedureDescription;
    }


    public String getStoredProcedureClassName() {
        String className = "";
        if (procedureName.contains(objectName)) {
            String firstWord = procedureName.substring(procedureName.indexOf(objectName), objectName.length());
            String secondWord = procedureName.substring(firstWord.length());
            className = StringHelper.toCamelCase(firstWord + "_" + secondWord + "_" + this.getOverload());
        } else {
            className = StringHelper.toCamelCase(this.getObjectName() + "_" + this.getProcedureName() + "_" + this.getOverload());
        }
        return className;

    }

    public String getMethodArguments() {
        String arguments = "";
        if (argumentList != null) {
            List<ec.devnull.osprest.model.ProcedureArgument> inArguments = argumentList.stream().filter(ec.devnull.osprest.model.ProcedureArgument::isInParam).collect(Collectors.toList());
            for (ec.devnull.osprest.model.ProcedureArgument argument : inArguments) {
                arguments += argument.getJavaDataType() + " " + argument.getJavaPropertyName() + ",";
            }
            arguments = removeLastComa(arguments);
        }
        return arguments;
    }

    public String removeLastComa(String arguments) {
        if (arguments != null && arguments.length() > 0 && arguments.charAt(arguments.length() - 1) == ',') {
            arguments = arguments.substring(0, arguments.length() - 1);
        }
        return arguments;
    }

    public String getReturnJavaType() {
        return argumentList.get(0).getJavaDataType();
    }

    public String getCallString() {
        return this.callString;
    }


    public List<ec.devnull.osprest.model.BindParam> getBindParams() {
        return bindParams;
    }


    public boolean hasResultSetParam() {
        for (ec.devnull.osprest.model.ProcedureArgument pa : this.argumentList) {
            if (TypeMapper.JAVA_RESULTSET.equals(pa.getJavaDataType())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasInParameters() {
        return this.argumentList.stream().anyMatch(ProcedureArgument::isInParam);
    }

    public static class Builder {
        Procedure p = new Procedure();

        public Builder objectName(String objectName) {
            this.p.setObjectName(objectName);
            return this;
        }

        public Builder procedureName(String procedureName) {
            this.p.setProcedureName(procedureName);
            return this;
        }

        public Builder procedureDescription(String procedureDescription) {
            this.p.procedureDescription = procedureDescription;
            return this;
        }

        public Builder overload(String overload) {
            this.p.setOverload(overload);
            return this;
        }

        public Builder methodType(String methodType) {
            this.p.setMethodType(methodType);
            return this;
        }

        public Builder argumentList(List<ec.devnull.osprest.model.ProcedureArgument> argumentList) {
            this.p.setArgumentList(argumentList);
            return this;
        }

        public Procedure build() {
            initBindParams();
            return p;
        }

        private void initBindParams() {
            CallStringBuilder callStringBuilder = new CallStringBuilder(p);
            p.callString = callStringBuilder.build();
            p.bindParams = callStringBuilder.getBindParams();
        }

    }

}
