package cat.itacademy.s04.t02.n02.fruit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FruitApiMySqlApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Test
	void contextLoads() {
		assertThat(dataSource).isNotNull();
	}

	@Test
	void databaseConnectionTest() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			assertThat(connection).isNotNull();
			System.out.println("✅ Connexió a la BD correcta!");
		}
	}
}
