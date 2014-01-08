package org.openskye.util;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.servlet.*;
import java.io.IOException;

@Singleton
@Slf4j
public final class PersistFilter implements Filter {
    private final UnitOfWork unitOfWork;
    private final PersistService persistService;
    private final Provider<EntityManager> emf;

    @Inject
    public PersistFilter(UnitOfWork unitOfWork, PersistService persistService, Provider<EntityManager> emf) {
        this.unitOfWork = unitOfWork;
        this.persistService = persistService;
        this.emf = emf;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            persistService.start();
        } catch (Exception e) {
            log.debug("Persistence service already started");
        }
    }

    public void destroy() {
        persistService.stop();
    }

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        log.debug("Starting persistence unit of work");
        unitOfWork.begin();
        log.debug("Entity manager is " + emf.get());
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            log.debug("Ending persistence unit of work");
            unitOfWork.end();
        }
    }
}

