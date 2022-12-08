package com.hwacom.modbussim;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPoint {

    int id;
    int min;
    int max;
    int start;
    int end;
    boolean gen;
    boolean acc;
    boolean accTo;
    int[] accToId;
    boolean specify;
    @JsonProperty("specifyTime")
    SpecifyTime[] specifyTimes;
    boolean resetValue;
    String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean isGen() {
        return gen;
    }

    public void setGen(boolean gen) {
        this.gen = gen;
    }

    public boolean isAcc() {
        return acc;
    }

    public void setAcc(boolean acc) {
        this.acc = acc;
    }

    public boolean isAccTo() {
        return accTo;
    }

    public void setAccTo(boolean accTo) {
        this.accTo = accTo;
    }

    public int[] getAccToId() {
        return accToId;
    }

    public void setAccToId(int[] accToId) {
        this.accToId = accToId;
    }

    public boolean isSpecify() {
        return specify;
    }

    public void setSpecify(boolean specify) {
        this.specify = specify;
    }

    public SpecifyTime[] getSpecifyTimes() {
        return specifyTimes;
    }

    public void setSpecifyTimes(SpecifyTime[] specifyTimes) {
        this.specifyTimes = specifyTimes;
    }

    public boolean isResetValue() {
        return resetValue;
    }

    public void setResetValue(boolean resetValue) {
        this.resetValue = resetValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
