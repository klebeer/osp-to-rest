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

package ec.devnull.osprest.db;


import ec.devnull.osprest.model.TypeAttribute;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * User: fkarsany
 * Date: 2013.11.14.
 */
public class TypeDao {

    private static final String GET_TYPE_ATTRIBUTES = "Select attr_name,\n" +
            "       attr_type_name,\n" +
            "       attr_no,\n" +
            "       nvl(nvl(scale, (Select scale From user_coll_types t Where t.type_name = aa.attr_type_name)), -1) data_scale,\n" +
            "       Case\n" +
            "         When attr_type_owner Is Not Null Then\n" +
            "          1\n" +
            "         Else\n" +
            "          0\n" +
            "       End multi_type,\n" +
            "       bb.typecode,\n" +
            "       (Select elem_type_name From user_coll_types t Where t.type_name = aa.attr_type_name) collection_base_type\n" +
            "  From user_type_attrs aa, user_types bb\n" +
            " Where upper(aa.type_name) = ?\n" +
            "   And aa.attr_type_name = bb.type_name(+)\n" +
            " Order By attr_no Asc";

    private static final String GET_EMBEDDED_TYPE_ATTRIBUTES =
            "Select *\n" +
                    "  From (Select Distinct d.argument_name attr_name,\n" +
                    "                        d.data_type attr_type_name,\n" +
                    "                        d.position attr_no,\n" +
                    "                        nvl(d.data_scale, -1) data_scale,\n" +
                    "                        0 multi_type,\n" +
                    "                        bb.typecode typecode,\n" +
                    "                        Null collection_base_type\n" +
                    "          From (Select t.*\n" +
                    "                  From user_arguments t\n" +
                    "                 Where t.type_name || '_' || t.type_subname = ?\n" +
                    "                   And rownum < 2) m\n" +
                    "          Left Join (Select * From user_arguments t Where data_level = 1) d\n" +
                    "            On m.object_name = d.object_name\n" +
                    "           And m.package_name = d.package_name\n" +
                    "           And nvl(m.overload, -1) = nvl(d.overload, -1)\n" +
                    "          Left Join user_types bb\n" +
                    "            On d.data_type = bb.type_name)\n" +
                    " Order By attr_no";


    private JdbcTemplate jdbcTemplate;

    public TypeDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<String> getTypeList() {
        return jdbcTemplate.queryForList("SELECT type_name FROM user_types WHERE typecode = 'OBJECT'");
    }

    public List<String> getTypeList(String typeName) {
        return jdbcTemplate.queryForList("SELECT distinct n.type_name\n" +
                "  FROM user_types n,\n" +
                "       (select distinct type_name\n" +
                "          from user_arguments\n" +
                "         where (package_name like ? or\n" +
                "               (package_name is null and\n" +
                "               object_name like ?))) l\n" +
                " where n.type_name = l.type_name\n" +
                "   and n.typecode = 'OBJECT'\n" +
                "union\n" +
                "select distinct elem_type_name\n" +
                "  from user_coll_types u,\n" +
                "       (SELECT distinct n.type_name\n" +
                "          FROM user_types n,\n" +
                "               (select distinct type_name\n" +
                "                  from user_arguments\n" +
                "                 where (package_name like ? or\n" +
                "                       (package_name is null and\n" +
                "                       object_name like ?))) l\n" +
                "         where n.type_name = l.type_name\n" +
                "           and n.typecode = 'COLLECTION') h\n" +
                " where u.type_name = h.type_name", typeName, typeName, typeName, typeName);

    }

    public List<TypeAttribute> getTypeAttributes(String typeName) {

        return jdbcTemplate.query(
                GET_TYPE_ATTRIBUTES,
                new Object[]{typeName.toUpperCase()}, (resultSet, i) -> new TypeAttribute(
                        resultSet.getString("attr_name"),
                        resultSet.getString("attr_type_name"),
                        resultSet.getInt("attr_no"),
                        resultSet.getInt("data_scale"),
                        resultSet.getInt("multi_type"),
                        resultSet.getString("typecode"),
                        resultSet.getString("collection_base_type")
                )
        );
    }

    public List<String> getEmbeddedTypeList() {
        return jdbcTemplate.queryForList("Select Distinct type_name || '_' || type_subname\n" +
                "  From user_arguments t\n" +
                " Where type_subname Is Not Null");
    }

    public List<TypeAttribute> getEmbeddedTypeAttributes(String typeName) {

        return jdbcTemplate.query(GET_EMBEDDED_TYPE_ATTRIBUTES,
                new Object[]{typeName.toUpperCase()}, (resultSet, i) -> new TypeAttribute(
                        resultSet.getString("attr_name"),
                        resultSet.getString("attr_type_name"),
                        resultSet.getInt("attr_no"),
                        resultSet.getInt("data_scale"),
                        resultSet.getInt("multi_type"),
                        resultSet.getString("typecode"),
                        resultSet.getString("collection_base_type")
                ));
    }

    public List<String> validateTypeList(List<String> types) {

        List<String> typesIterator = new ArrayList<>(types);
        typesIterator.forEach(type -> getSubTypesList(types, type));

        return types;
    }

    private List<String> getSubTypesList(List<String> types, String typeName) {
        List<TypeAttribute> typeAttributes = getTypeAttributes(typeName);

        typeAttributes.stream().
                filter(entry -> entry.getCollectionBaseType() != null && !entry.getCollectionBaseType().isEmpty()).
                forEach(type -> {
                    types.add(type.getCollectionBaseType());
                    getSubTypesList(types, type.getCollectionBaseType());
                });
        return types;
    }
}
