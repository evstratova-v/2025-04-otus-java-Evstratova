package ru.otus.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectForMessage implements Copyable<ObjectForMessage> {
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public ObjectForMessage copy() {
        var copyObjectForMessage = new ObjectForMessage();
        if (data != null) {
            copyObjectForMessage.setData(new ArrayList<>(data));
        }
        return copyObjectForMessage;
    }

    @Override
    public String toString() {
        return "ObjectForMessage{" + "data=" + data + '}';
    }
}
