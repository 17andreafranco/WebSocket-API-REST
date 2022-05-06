package aar;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.logging.Logger;

/**
 * This class performs the insert, read, delete and search operations
 *
 *
 */
public class DatabaseService {

	Logger log = Logger.getLogger(DatabaseService.class.getName());

	public int insertCurrencies(Currencies currencies) {
		EntityManager entityManager = EntityManagerListener.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(currencies);
			entityManager.flush();
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw e;
		} finally {
			entityManager.close();
		}
		return currencies.getId();
	}

	public int insertExchange(Exchange exchange) {
		EntityManager entityManager = EntityManagerListener.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(exchange);
			entityManager.flush();
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw e;
		} finally {
			entityManager.close();
		}
		return exchange.getId();
	}

	public Currencies readCurrencies(int id) {
		EntityManager entityManager = EntityManagerListener.createEntityManager();
		Currencies currency = null;
		try {
			currency = entityManager.find(Currencies.class, id);
		} finally {
			entityManager.close();

			if (currency == null)
				log.warning("No currencies were found with given id value");
		}
		return currency;
	}

	public Exchange readExchange(int id) {
		EntityManager entityManager = EntityManagerListener.createEntityManager();
		Exchange exchange = null;
		try {
			exchange = entityManager.find(Exchange.class, id);
		} finally {
			entityManager.close();

			if (exchange == null)
				log.warning("No records records were found with given id value");
		}
		return exchange;
	}

	public boolean deleteCurrencies(Integer id) {
		EntityManager entityManager = EntityManagerListener.createEntityManager();
		boolean result = false;
		try {
			entityManager.getTransaction().begin();
			Currencies entity = null;

			entity = entityManager.find(Currencies.class, id);
			if (entity != null) {
				entityManager.remove(entity);
				entityManager.getTransaction().commit();
				result = true;
			} else {
				log.warning("No currencies were found with given id value !!");
				result = false;
			}

		} catch (Exception e) {
			result = false;
			entityManager.getTransaction().rollback();
			throw e;
		} finally {
			entityManager.close();
		}
		return result;
	}

	public boolean deleteExchange(int id) {
		EntityManager entityManager = EntityManagerListener.createEntityManager();
		boolean result = false;
		try {
			entityManager.getTransaction().begin();
			Exchange entity = null;

			entity = entityManager.find(Exchange.class, id);
			if (entity != null) {
				entityManager.remove(entity);
				entityManager.getTransaction().commit();
				result = true;
			} else {
				log.warning("No records records were found with given id value !!");
				result = false;
			}

		} catch (Exception e) {
			result = false;
			entityManager.getTransaction().rollback();
			throw e;
		} finally {
			entityManager.close();
		}
		return result;
	}

	public List<Exchange> search(String key, String value) {
		EntityManager entityManager = EntityManagerListener.createEntityManager();
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Exchange> criteriaQuery = criteriaBuilder.createQuery(Exchange.class);
			Root<Exchange> root = criteriaQuery.from(Exchange.class);
			criteriaQuery.select(root);
			Predicate where = criteriaBuilder.equal(root.get(key), value);
			criteriaQuery.where(where);
			return entityManager.createQuery(criteriaQuery).getResultList();
		} finally {
			entityManager.close();

		}
	}

	public List<Currencies> findAllCurrencies() {
		EntityManager entityManager = EntityManagerListener.createEntityManager();
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Currencies> criteriaQuery = criteriaBuilder.createQuery(Currencies.class);

			Root<Currencies> rootEntry = criteriaQuery.from(Currencies.class);
			CriteriaQuery<Currencies> all = criteriaQuery.select(rootEntry);
			TypedQuery<Currencies> allQuery = entityManager.createQuery(all);
			return allQuery.getResultList();
		} finally {
			entityManager.close();

		}
	}

	public List<Exchange> findAllExchange() {
		EntityManager entityManager = EntityManagerListener.createEntityManager();
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Exchange> criteriaQuery = criteriaBuilder.createQuery(Exchange.class);

			Root<Exchange> rootEntry = criteriaQuery.from(Exchange.class);
			CriteriaQuery<Exchange> all = criteriaQuery.select(rootEntry);
			TypedQuery<Exchange> allQuery = entityManager.createQuery(all);
			return allQuery.getResultList();
		} finally {
			entityManager.close();

		}
	}

	public void updatePrice(Integer id, float priceCryptonator, String time) {

		EntityManager entityManager = EntityManagerListener.createEntityManager();

		Exchange exchange = readExchange(id);

		entityManager.getTransaction().begin();
		exchange.setPrice(priceCryptonator);
		exchange.setTimeStamp(time);

		entityManager.merge(exchange);
		entityManager.flush();
		entityManager.getTransaction().commit();
	}

	public void changePriceMax(Integer id, float priceCryptonator, String time) {

		EntityManager entityManager = EntityManagerListener.createEntityManager();

		Exchange exchange = readExchange(id);
		float priceMax = exchange.getPriceMax();

		if (priceMax < priceCryptonator) {
			entityManager.getTransaction().begin();
			exchange.setPriceMax(priceCryptonator);
			exchange.setTimePriceMax(time);

			entityManager.merge(exchange);
			entityManager.flush();
			entityManager.getTransaction().commit();
		}
	}
}
