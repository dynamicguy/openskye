package com.infobelt.skye.platform;

import java.util.ArrayList;
import java.util.List;

/**
 * A <code>DataSet</code> is the metadata representing a set of structured data
 * <p/>
 * In this sense we are dealing with a structured data set that would form into columns and rows,
 * note that if you wish to handle other structures you may want to look at the {@link Document}
 */
public class DataSet extends SimpleObject {

    private DataSetType dataSetType;
    private long rowCount;
    private List<Column> columns = new ArrayList<>();

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public long getRowCount() {
        return rowCount;
    }

    public void setRowCount(long rowCount) {
        this.rowCount = rowCount;
    }

    public DataSetType getDataSetType() {
        return dataSetType;
    }

    public void setDataSetType(DataSetType dataSetType) {
        this.dataSetType = dataSetType;
    }

}
