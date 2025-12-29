package beans;

import config.DbConfig;
import dao.ShotResultDao;
import model.ShotResult;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResultsBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private DbConfig dbConfig;
    private transient ShotResultDao dao;

    private List<ShotResult> results = new ArrayList<>();
    private String sessionId = "unknown";

    public ResultsBean() {}

    @PostConstruct
    private void init() {
        this.sessionId = resolveSessionId();

        if (dbConfig == null) {
            throw new IllegalStateException("DbConfig is null. Check beans-config.xml injection for resultsBean.dbConfig");
        }

        this.dao = new ShotResultDao(dbConfig);
        this.results = dao.findAllBySession(sessionId);
    }

    public List<ShotResult> getResults() {
        return results;
    }

    public String getSessionId() {
        return sessionId;
    }

    public DbConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public void addResult(double x, double y, double r, boolean hit, long execTimeNs) {
        ensureDao();

        ShotResult sr = new ShotResult();
        sr.setX(x);
        sr.setY(y);
        sr.setR(r);
        sr.setHit(hit);
        sr.setCheckTime(LocalDateTime.now());
        sr.setExecTimeNs(execTimeNs);
        sr.setSessionId(sessionId);

        dao.save(sr);
        results.add(0, sr);
    }

    public void reload() {
        ensureDao();
        this.results = dao.findAllBySession(sessionId);
    }

    public void clear() {
        ensureDao();
        dao.deleteBySession(sessionId);
        results.clear();
    }

    private void ensureDao() {
        if (dao == null) {
            if (dbConfig == null) {
                throw new IllegalStateException("DbConfig is null. Check beans-config.xml injection for resultsBean.dbConfig");
            }
            dao = new ShotResultDao(dbConfig);
        }
    }

    private String resolveSessionId() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) return "unknown";

        Object sessionObj = fc.getExternalContext().getSession(true);
        if (sessionObj instanceof HttpSession) {
            return ((HttpSession) sessionObj).getId();
        }
        return "unknown";
    }
}