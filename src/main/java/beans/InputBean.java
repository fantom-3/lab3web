package beans;

import util.AreaChecker;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class InputBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private ResultsBean resultsBean;

    private Integer x;
    private String y;
    private Integer r;

    private String clickX;
    private String clickY;

    public InputBean() {}

    public ResultsBean getResultsBean() {
        return resultsBean;
    }

    public void setResultsBean(ResultsBean resultsBean) {
        this.resultsBean = resultsBean;
    }

    public Integer getX() { return x; }
    public void setX(Integer x) { this.x = x; }

    public String getY() { return y; }
    public void setY(String y) { this.y = y; }

    public Integer getR() { return r; }
    public void setR(Integer r) { this.r = r; }

    public String getClickX() { return clickX; }
    public void setClickX(String clickX) { this.clickX = clickX; }

    public String getClickY() { return clickY; }
    public void setClickY(String clickY) { this.clickY = clickY; }

    public List<Integer> getXValues() {
        return Arrays.asList(-4, -3, -2, -1, 0, 1, 2, 3, 4);
    }

    public List<Integer> getRValues() {
        return Arrays.asList(1, 2, 3, 4, 5);
    }

    public void chooseR(int value) {
        this.r = value;
    }

    public void reset() {
        this.x = null;
        this.y = null;
        this.r = null;
        this.clickX = null;
        this.clickY = null;
    }

    public void initView() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null && !fc.isPostback()) {
            reset();
        }
    }

    public void check() {
        if (resultsBean == null) {
            addError("Внутренняя ошибка: resultsBean не внедрён. Проверь beans-config.xml.");
            return;
        }

        if (x == null) {
            addError("Выбери X.");
            return;
        }
        if (r == null) {
            addError("Выбери R.");
            return;
        }
        if (r < 1 || r > 5) {
            addError("R должен быть из {1,2,3,4,5}.");
            return;
        }

        Double yVal = parseNumber(y, "Y");
        if (yVal == null) return;

        if (yVal < -5.0 || yVal > 3.0) {
            addError("Y должен быть в диапазоне [-5; 3].");
            return;
        }

        long start = System.nanoTime();
        boolean hit = AreaChecker.isHit(x.doubleValue(), yVal, r.doubleValue());
        long execNs = System.nanoTime() - start;

        resultsBean.addResult(x.doubleValue(), yVal, r.doubleValue(), hit, execNs);
    }

    public void checkFromCanvas() {
        if (resultsBean == null) {
            addError("Внутренняя ошибка: resultsBean не внедрён. Проверь beans-config.xml.");
            return;
        }

        if (r == null) {
            addError("Сначала выбери R, затем кликай по графику.");
            return;
        }
        if (r < 1 || r > 5) {
            addError("R должен быть из {1,2,3,4,5}.");
            return;
        }

        Double xVal = parseNumber(clickX, "X");
        if (xVal == null) return;

        Double yVal = parseNumber(clickY, "Y");
        if (yVal == null) return;

        if (xVal < -4.0 || xVal > 4.0) {
            addError("Клик: X должен быть в диапазоне [-4; 4].");
            return;
        }
        if (yVal < -5.0 || yVal > 3.0) {
            addError("Клик: Y должен быть в диапазоне [-5; 3].");
            return;
        }

        long start = System.nanoTime();
        boolean hit = AreaChecker.isHit(xVal, yVal, r.doubleValue());
        long execNs = System.nanoTime() - start;

        resultsBean.addResult(xVal, yVal, r.doubleValue(), hit, execNs);
    }

    private Double parseNumber(String raw, String fieldName) {
        if (raw == null) {
            addError("Поле " + fieldName + " пустое.");
            return null;
        }

        String s = raw.trim();
        if (s.isEmpty()) {
            addError("Поле " + fieldName + " пустое.");
            return null;
        }

        s = s.replace(',', '.');

        try {
            double v = Double.parseDouble(s);
            if (Double.isNaN(v) || Double.isInfinite(v)) {
                addError(fieldName + " должен быть конечным числом.");
                return null;
            }
            return v;
        } catch (NumberFormatException e) {
            addError(fieldName + " должен быть числом.");
            return null;
        }
    }

    private void addError(String msg) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) return;
        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }
}