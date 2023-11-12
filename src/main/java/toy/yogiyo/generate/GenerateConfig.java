package toy.yogiyo.generate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;

/**
 * Generate 관련 클래스들을 Bean으로 등록하면 애플리케이션 로딩 시점에 자동으로 데이터 생성
 * <p>
 *     ! Commit, Push할 때 주석 처리 해야됨 !
 * </p>
 */

//@Configuration
public class GenerateConfig {

    // 데이터 생성 때문에 Owner에 setId() 추가함
    @Bean
    public GenerateDummyData generateDummyData(GenerateDummyData.Generator generator) {
        return new GenerateDummyData(generator);
    }

    @Bean
    public GenerateDummyData.Generator generator(EntityManager em, JdbcTemplate jdbcTemplate) {
        return new GenerateDummyData.Generator(em, jdbcTemplate);
    }
}
