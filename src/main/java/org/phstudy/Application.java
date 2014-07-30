package org.phstudy;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.phstudy.model.TestJavaBean;

public class Application {
	static Log logger = LogFactory.getLog(Application.class);

	private static final String PERSISTENCE_ECLIPSELINK_UNIT_NAME = "pu-eclipselink";
	private static final String PERSISTENCE_HIBERNATE_UNIT_NAME = "pu-hibernate";

	private static final String DATABASE_JDBC_URL = "jdbc:postgresql:study";
	private static final String DATABASE_USERNAME = "study";
	private static final String DATABASE_PASSWORD = "";

	private static EntityManagerFactory factory;

	public static void main(String[] args) throws Exception {
		execute(PERSISTENCE_ECLIPSELINK_UNIT_NAME);
		execute(PERSISTENCE_HIBERNATE_UNIT_NAME);
	}
	
	private static void execute(String pu) {
		factory = Persistence.createEntityManagerFactory(
				PERSISTENCE_ECLIPSELINK_UNIT_NAME, getProperties());

		// persist
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		TestJavaBean testJavaBean = new TestJavaBean();
		testJavaBean.setName("study" + UUID.randomUUID().toString());
		testJavaBean.setSize("L");
		em.persist(testJavaBean);
		
		em.getTransaction().commit();
		em.close();

		// query
		em = factory.createEntityManager();
		em.getTransaction().begin();

		Query q = em.createNativeQuery(
				"select id, pname, psize from product",
				TestJavaBean.class);
		List<TestJavaBean> list = (List<TestJavaBean>) q.getResultList();

		for (TestJavaBean bean : list) {
			logger.info(bean.getName() + ": " + bean.getSize());
		}

		factory.close();
	}
	
	private static Properties getProperties() {
		Properties properties = new Properties();
		properties
				.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		properties.put("javax.persistence.database-major-version", "9");
		properties.put("javax.persistence.database-minor-version", "3");
		properties.put("javax.persistence.jdbc.url", DATABASE_JDBC_URL);
		properties.put("javax.persistence.jdbc.user", DATABASE_USERNAME);
		properties.put("javax.persistence.jdbc.password", DATABASE_PASSWORD);
		properties.put("javax.persistence.database-product-name", "PostgreSQL");
		properties.put("javax.persistence.schema-generation.database.action",
				"create");
		properties.put(
				"javax.persistence.schema-generation.create-database-schemas",
				"true");
		return properties;
	}
}
