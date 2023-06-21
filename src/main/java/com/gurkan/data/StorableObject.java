package com.gurkan.data;

import java.util.HashMap;
import java.util.Map;

import com.gurkan.enums.ERenderKeys;
import com.gurkan.interfaces.ICustomSerializable;
import com.gurkan.interfaces.IStorableObject;

public class StorableObject implements ICustomSerializable, IStorableObject {
    private String fileName;

    public StorableObject(String fileName) {
        setFileName(fileName);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return this.fileName;
        // return this.fileName.substring(0, 20)
        // + this.fileName.substring(this.fileName.length() - 20,
        // this.fileName.length());
    }

    @Override
    public String render() {

        Map<String, String> map = new HashMap<>();

        map.put(ERenderKeys.FILENAME.toString(), this.getFileName());

        return IStorableObject.super.render(map);
    }

    @Override
    public String render(Map<String, String> map) {

        map.put(ERenderKeys.FILENAME.toString(), this.getFileName());

        return IStorableObject.super.render(map);
    }

    @Override
    public void importData(Map<String, String> map) {
        this.setFileName(map.get(ERenderKeys.FILENAME.toString()));
    }

}
