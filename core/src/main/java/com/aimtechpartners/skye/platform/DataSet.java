package com.aimtechpartners.skye.platform;

/**
 * A <code>DataSet</code> is the metadata representing a set of structured data
 * <p/>
 * In this sense we are dealing with a structured data set that would form into columns and rows,
 * note that if you wish to handle other structures you may want to look at the {@link Document}
 */
public class DataSet extends SimpleObject {

    public DataSetType dataSetType;
    public long rowCount;

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
