package org.example;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Properties;

class Injector  {

    private Properties properties;

    Injector(String pathToPropertiesFile, String pathToPropertiesFile2) throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream(pathToPropertiesFile));
    }

    Injector() throws IOException {
        String pathToPropertiesFile = "src\\main\\java\\org\\example\\properties\\inj.properties";
       // String pathToPropertiesFile = "src\\main\\java\\org\\example\\properties\\inj2.properties";
        properties = new Properties();
        properties.load(new FileInputStream(pathToPropertiesFile));
    }

    <T> T inject(T obj) throws IOException, IllegalAccessException, InstantiationException {
        Class dependency;
        Class cl = obj.getClass();
        Field[] fields = cl.getDeclaredFields();
        for (Field field: fields) {
            Annotation a = field.getAnnotation(AutoInjectable.class);
            if (a == null) {
                return obj;
            }

            String[] fieldType = field.getType().toString().split(" ");
            String equalsClassName = properties.getProperty(fieldType[1], null);

            if (equalsClassName == null) {
                System.out.println("Error! Not found properties for field type " + fieldType[1]);
                return obj;
            }

            try {
                dependency = Class.forName(equalsClassName);
            }
            catch (ClassNotFoundException e) {
                System.out.println("Error! Not found class for " + equalsClassName);
                continue;
            }
            field.setAccessible(true);
            field.set(obj, dependency.newInstance());
        }
        return obj;
    }
}
