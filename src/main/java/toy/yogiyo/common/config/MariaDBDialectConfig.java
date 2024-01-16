package toy.yogiyo.common.config;

import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MariaDBDialectConfig extends MariaDBDialect {

    public MariaDBDialectConfig(){
        super();
        registerFunction(
                "st_distance_sphere",
                new StandardSQLFunction("st_distance_sphere", StandardBasicTypes.DOUBLE));
    }
}
