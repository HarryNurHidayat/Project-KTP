/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package unu.jogja.project.ktp.coba;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import unu.jogja.project.ktp.coba.exceptions.NonexistentEntityException;
import unu.jogja.project.ktp.coba.exceptions.PreexistingEntityException;

/**
 *
 * @author harry
 */
public class DummyJpaController implements Serializable {

    public DummyJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("unu.jogja_project.ktp_jar_0.0.1-SNAPSHOTPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public DummyJpaController() {
    }
    

    public void create(Dummy dummy) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dummy);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDummy(dummy.getId()) != null) {
                throw new PreexistingEntityException("Dummy " + dummy + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Dummy dummy) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            dummy = em.merge(dummy);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dummy.getId();
                if (findDummy(id) == null) {
                    throw new NonexistentEntityException("The dummy with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dummy dummy;
            try {
                dummy = em.getReference(Dummy.class, id);
                dummy.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dummy with id " + id + " no longer exists.", enfe);
            }
            em.remove(dummy);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Dummy> findDummyEntities() {
        return findDummyEntities(true, -1, -1);
    }

    public List<Dummy> findDummyEntities(int maxResults, int firstResult) {
        return findDummyEntities(false, maxResults, firstResult);
    }

    private List<Dummy> findDummyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Dummy.class));
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

    public Dummy findDummy(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Dummy.class, id);
        } finally {
            em.close();
        }
    }

    public int getDummyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Dummy> rt = cq.from(Dummy.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
