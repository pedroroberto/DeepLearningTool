package deeplearning.models;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * Created by Pedro on 6/28/17.
 */
public class HistoryRow {
    public BooleanProperty failCC = new SimpleBooleanProperty(false);
    public BooleanProperty failDL = new SimpleBooleanProperty(false);
    public BooleanProperty endedCC = new SimpleBooleanProperty(false);
    public BooleanProperty endedDL = new SimpleBooleanProperty(false);

    public Evaluation evalDL;
    public Evaluation evalCC;

    public boolean enabledDL;
    public boolean enabledCC;

    public Classifier classifierCC;
    public Classifier classifierDL;

    public CCType classicalType;

    public Date startTime;
    public long elapsedTimeCC;
    public long elapsedTimeDL;
    public String baseInfoCC;
    public String baseInfoDL;

    public Exception exceptionCC;
    public Exception exceptionDL;

    public HistoryRow(boolean enabledDL, boolean enabledCC) {
        this.enabledDL = enabledDL;
        this.enabledCC = enabledCC;
        this.startTime = new Date();
    }

    public static Callback<HistoryRow, Observable[]> extractor() {
        return new Callback<HistoryRow, Observable[]>()  {
            @Override
            public Observable[] call(HistoryRow param) {
                return new Observable[]{param.endedCC, param.endedDL, param.failCC, param.failDL};
            }
        };
    }

    public String getResultsTextCC() {
        if(failCC.getValue()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exceptionCC.printStackTrace(pw);
            return sw.toString();
        }

        StringBuilder str = new StringBuilder("=== Base Information ===\n\n" + baseInfoCC + "\n");

        str.append(this.classifierCC.toString());
        str.append("\n=== Classifier Statistics Summary ===\n" +evalCC.toSummaryString() + "\n");

        try {
            str.append(evalCC.toClassDetailsString());
            str.append("\n");
        } catch (Exception excp) {
            excp.printStackTrace();
        }

        try {
            str.append(evalCC.toMatrixString());
        } catch (Exception excp) {
            excp.printStackTrace();
        }

        str.append("\n\nModel build time: " + elapsedTimeCC+ " ms");

        return str.toString();
    }

    public String getResultsTextDL() {
        if(failDL.getValue()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exceptionDL.printStackTrace(pw);
            return sw.toString();
        }

        StringBuilder str = new StringBuilder("=== Base Information ===\n\n" + baseInfoDL + "\n");

        str.append("\n=== Network Information ===\n\n" + this.classifierDL.toString());
        str.append("\n=== Classifier Statistics Summary ===\n" + evalDL.toSummaryString() + "\n");

        try {
            str.append(evalDL.toClassDetailsString());
            str.append("\n");
        } catch (Exception excp) {
            excp.printStackTrace();
        }

        try {
            str.append(evalDL.toMatrixString());
        } catch (Exception excp) {
            excp.printStackTrace();
        }

        str.append("\n\nModel build time: " + elapsedTimeDL + " ms");

        return str.toString();
    }


    public enum CCType {
        C45(0), NN(1), RB(2), NB(3);

        public int type;
        CCType(int t) {
            this.type = t;
        }
    }
}
