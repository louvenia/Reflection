package edu.school21.manager;

import edu.school21.annotations.OrmColumn;
import edu.school21.annotations.OrmColumnId;
import edu.school21.annotations.OrmEntity;
import edu.school21.exception.DataCannotBeFound;
import org.reflections.Reflections;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OrmManager {
    private final DataSource ds;
    private final String DROP_TABLE = "drop table if exists %s cascade;\n";
    private final String CREATE_TABLE = "create table if not exists %s (\n";
    private final String INSERT_INTO = "insert into %s(";
    private final String UPDATE_TABLE = "update %s set ";

    public OrmManager(DataSource ds) {
        this.ds = ds;
    }

    public void createTable() {
        Reflections reflections = new Reflections("edu.school21.classes");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(OrmEntity.class);

        for (Class<?> cl : classes) {
            try {
                OrmEntity entity = cl.getAnnotation(OrmEntity.class);

                Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement();

                stmt.execute(String.format(DROP_TABLE, entity.table()));
                System.out.printf(DROP_TABLE, entity.table());

                StringBuilder sb = new StringBuilder();
                sb.append(String.format(CREATE_TABLE, entity.table()));

                Field[] fields = cl.getDeclaredFields();
                for(int i = 0; i < fields.length; i++) {
                    String type = fields[i].getType().getSimpleName();
                    if(fields[i].isAnnotationPresent(OrmColumnId.class)) {
                        String id = getIdData(type);
                        if(!id.isEmpty()) {
                            sb.append(id);
                        }
                    } else if(fields[i].isAnnotationPresent(OrmColumn.class)) {
                        String dataColumn = getColumnData(fields[i].getAnnotation(OrmColumn.class), type);
                        if(!dataColumn.isEmpty()) {
                            sb.append(dataColumn);
                        }
                    }
                    if (i != fields.length - 1) {
                        sb.append(",\n");
                    } else {
                        sb.append("\n);\n");
                    }
                }

                stmt.executeUpdate(sb.toString());
                System.out.println(sb);

                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(Object entity) {
        try {
            Class<?> cl = entity.getClass();
            OrmEntity en = cl.getAnnotation(OrmEntity.class);
            StringBuilder sb = new StringBuilder();

            sb.append(String.format(INSERT_INTO, en.table()));

            Field[] fields = cl.getDeclaredFields();
            List<Object> listObj = new ArrayList<>();

            for(int i = 0; i < fields.length; ++i) {
                if(fields[i].isAnnotationPresent(OrmColumn.class)) {
                    fields[i].setAccessible(true);
                    OrmColumn cm = fields[i].getAnnotation(OrmColumn.class);
                    sb.append(cm.name());

                    if(i != fields.length - 1) {
                        sb.append(", ");
                    } else {
                        sb.append(") values\n");
                    }


                    Object obj = fields[i].get(entity);
                    listObj.add(obj);
                }
            }

            sb.append("\t(");
            for(int i = 0; i < listObj.size(); ++i) {
                if(listObj.get(i) != null) {
                    if(listObj.get(i).getClass().getSimpleName().equals("String")) {
                        sb.append("'").append(listObj.get(i)).append("'");
                    } else {
                        sb.append(listObj.get(i));
                    }
                } else {
                    sb.append(listObj.get(i));
                }

                if(i != listObj.size() - 1) {
                    sb.append(", ");
                } else {
                    sb.append(");\n");
                }
            }

            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(sb.toString());
            System.out.println(sb);

            stmt.close();
            conn.close();
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void update(Object entity) {
        Class<?> cl = entity.getClass();
        OrmEntity ormEn = cl.getAnnotation(OrmEntity.class);
        StringBuilder sb = new StringBuilder();

        sb.append(String.format(UPDATE_TABLE, ormEn.table()));

        Field[] fields = cl.getDeclaredFields();
        Object id = null;
        try {
            for(int i = 0; i < fields.length; ++i) {
                fields[i].setAccessible(true);
                if(fields[i].isAnnotationPresent(OrmColumn.class)) {
                    OrmColumn cm = fields[i].getAnnotation(OrmColumn.class);
                    sb.append(cm.name());

                    Object obj = fields[i].get(entity);
                    if(obj != null) {
                        if(obj.getClass().getSimpleName().equals("String")) {
                            sb.append(" = ").append("'").append(obj).append("'");
                        } else {
                            sb.append(" = ").append(obj);
                        }
                    } else {
                        sb.append(" = ").append(obj);
                    }


                    if(i != fields.length - 1) {
                        sb.append(", ");
                    } else {
                        sb.append(" where id = ");
                    }

                } else if(fields[i].isAnnotationPresent(OrmColumnId.class)) {
                    id = fields[i].get(entity);
                }
            }
            sb.append(id).append(";");

            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(sb.toString());
            System.out.println(sb);

            stmt.close();
            conn.close();
        } catch (SQLException | IllegalAccessException error) {
            error.printStackTrace();
        }
    }

    public <T> T findById(Long id, Class<T> aClass) {
        OrmEntity entity = aClass.getAnnotation(OrmEntity.class);
        String query = "select * from " + entity.table() + " where id = " + id;
        T object = null;

        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            if(!rs.next()) {
                throw new DataCannotBeFound("No data available for this identifier and class");
            }
            object = aClass.newInstance();

            for (Field field : aClass.getDeclaredFields()) {
                field.setAccessible(true);
                if(field.isAnnotationPresent(OrmColumnId.class)) {
                    Type fieldType = field.getType();
                    if (fieldType.equals(Integer.class) || fieldType.equals(Long.class)) {
                        field.set(object, rs.getObject("id"));
                    } else {
                        errorMessage("This type of parameter for ID is not supported");
                    }
                } else if (field.isAnnotationPresent(OrmColumn.class)) {
                    String columnLabel = field.getAnnotation(OrmColumn.class).name();
                    Type typeField = field.getType();

                    if (typeField.equals(Integer.class) || typeField.equals(Long.class) ||
                        typeField.equals(Boolean.class) || typeField.equals(String.class)) {
                        field.set(object, rs.getObject(columnLabel));
                    } else if (typeField.equals(Double.class)) {
                        field.set(object, rs.getDouble(columnLabel));
                    } else {
                        errorMessage("This type of parameter is not supported " + typeField);
                    }
                }
            }

            stmt.close();
            conn.close();
        } catch (SQLException | InstantiationException | IllegalAccessException error) {
            error.printStackTrace();
        }

        return object;
    }

    private String getIdData(String type) {
        if(type.equalsIgnoreCase("long")) {
            return "\tid bigserial primary key";
        } else if(type.equalsIgnoreCase("Integer") || type.equals("int")) {
            return "\tid serial primary key";
        } else {
            return "";
        }
    }

    private String getColumnData(OrmColumn elem, String type) {
        if(type.equals("String")) {
            if(elem.length() > 0) {
                return String.format("\t%s varchar(%d)", elem.name(), elem.length());
            }
            return String.format("\t%s varchar", elem.name());
        } else if(type.equalsIgnoreCase("Integer") || type.equals("int")) {
            return String.format("\t%s int", elem.name());
        } else if(type.equalsIgnoreCase("double")) {
            return String.format("\t%s numeric", elem.name());
        } else if(type.equalsIgnoreCase("boolean")) {
            return String.format("\t%s boolean", elem.name());
        } else if(type.equalsIgnoreCase("long")) {
            return String.format("\t%s bigint", elem.name());
        } else {
           return "";
        }
    }

    private void errorMessage(String msg) {
        System.err.println(msg);
        System.exit(-1);
    }
}
