package toy.yogiyo.common.config;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MySQLDialectConfig extends MySQLDialect {

    public MySQLDialectConfig(){
        super();
        registerFunction(
                "st_distance_sphere",
                new StandardSQLFunction("st_distance_sphere", StandardBasicTypes.DOUBLE));
    }
}
