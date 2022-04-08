/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package unu.jogja.project.ktp;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import unu.jogja.project.ktp.exceptions.NonexistentEntityException;
import unu.jogja.project.ktp.exceptions.PreexistingEntityException;

/**
 *
 * @author harry
 */
public class DataJpaController implements Serializable {

    public DataJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("unu.jogja_project.ktp_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public DataJpaController() {
    }
    
    

    public void create(Data data) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(data);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findData(data.getId()) != null) {
                throw new PreexistingEntityException("Data " + data + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Data data) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            data = em.merge(data);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = data.getId();
                if (findData(id) == null) {
                    throw new NonexistentEntityException("The data with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Data data;
            try {
                data = em.getReference(Data.class, id);
                data.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The data with id " + id + " no longer exists.", enfe);
            }
            em.remove(data);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Data> findDataEntities() {
        return findDataEntities(true, -1, -1);
    }

    public List<Data> findDataEntities(int maxResults, int firstResult) {
        return findDataEntities(false, maxResults, firstResult);
    }

    private List<Data> findDataEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Data.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Data findData(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Data.class, id);
        } finally {
            em.close();
        }
    }

    public int getDataCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Data> rt = cq.from(Data.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
