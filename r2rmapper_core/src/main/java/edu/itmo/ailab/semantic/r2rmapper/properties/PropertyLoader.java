package edu.itmo.ailab.semantic.r2rmapper.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;

/**
 * R2R Mapper. It is a free software.
 *
 * Class for loading properties from Yaml file.
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 */
public class PropertyLoader {

    public static final Logger LOGGER = Logger.getLogger(PropertyLoader.class);

    public String filePath;

    public List<Object> properties = new ArrayList<>();

    public PropertyLoader(String filePath)
            throws FileNotFoundException {
        this.filePath = filePath;
        InputStream input = null;

        try {
            input = new FileInputStream(new File(filePath));
            Yaml yaml = new Yaml();
            for (Object data : yaml.loadAll(input)) {
                LOGGER.info("[ProperyLoader] Loading property: " + data);
                properties.add(data);
            }
        } catch (Throwable throwable) {
            LOGGER.error("Error reading file: "
                    + throwable.getClass().getName() +": "+ throwable.getMessage());
            System.exit(1);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Throwable throwable) {
                LOGGER.error("Error closing input file");
                throwable.printStackTrace();
                System.exit(1);
            }
        }

    }

    /**
     * Parse properties from Object.
     *
     * @param prop
     * @return
     * @throws R2RMapperException
     */
    @SuppressWarnings("rawtypes")
    public static Map<PropertyType, Object> parseProperty(Object prop)
            throws R2RMapperException {
        Map yamlProperty = (Map) prop;
        Map yamlDBProperty = (Map) yamlProperty.get("Database");
        Map<PropertyType, Object> property;
        property = new HashMap<>();

        if (yamlProperty.get("Name") != null) {
            property.put(PropertyType.NAME, yamlProperty.get("Name"));
        } else {
            throw new R2RMapperException("Property Name is missing");
        }

        if (yamlProperty.get("Url") != null) {
            property.put(PropertyType.URL, yamlProperty.get("Url"));
        } else {
            throw new R2RMapperException("Property Url is missing");
        }

        if (yamlProperty.get("Prefix") != null) {
            property.put(PropertyType.PREFIX, yamlProperty.get("Prefix"));
        } else {
            throw new R2RMapperException("Property Prefix is missing");
        }

        if (yamlDBProperty.get("JdbcUrl") != null) {
            property.put(PropertyType.JDBCURL, yamlDBProperty.get("JdbcUrl"));
        } else {
            throw new R2RMapperException("Property JdbcUrl is missing");
        }

        if (yamlDBProperty.get("Username") != null) {
            property.put(PropertyType.USERNAME, yamlDBProperty.get("Username"));
        } else {
            throw new R2RMapperException("Property Username is missing");
        }

        if (yamlDBProperty.get("Password") != null) {
            property.put(PropertyType.PASSWORD, yamlDBProperty.get("Password"));
        } else {
            property.put(PropertyType.PASSWORD, ""); // provide empty password
        }

        if (yamlDBProperty.get("Type") != null) {
            property.put(PropertyType.TYPE, yamlDBProperty.get("Type"));
        } else {
            throw new R2RMapperException("Property Type is missing");
        }

        if ( yamlDBProperty.get("Tables") != null) {
            List<String> tables = Lists.newArrayList(Splitter.on(",").trimResults().split(
                    (String) yamlDBProperty.get("Tables")));
            property.put(PropertyType.TABLES, (List<String>) tables);
        } else {
            throw new R2RMapperException("Property PrimaryKey is missing");
        }

        if ( yamlDBProperty.get("PrimaryKeys") != null) {
            List<String> primaryKeys = Lists.newArrayList(Splitter.on(",").trimResults().split(
                    (String) yamlDBProperty.get("PrimaryKeys")));
            property.put(PropertyType.PRIMARYKEYS, (List<String>) primaryKeys);
        } else {
            throw new R2RMapperException("Property PrimaryKeys is missing");
        }

        return property;

    }

}
