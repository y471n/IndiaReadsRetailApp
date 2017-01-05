package com.indiareads.retailapp.utils;


import com.activeandroid.serializer.TypeSerializer;
import com.indiareads.retailapp.models.Rent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Created by vijay on 12/8/2015.
 */
public class UtilRentSerializer extends TypeSerializer {
    @Override
    public Class<?> getDeserializedType() {
        return Rent.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public String serialize(Object data) {
        if (data == null) {
            return null;
        }
        String serializedObject = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes=null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(data);
            yourBytes = bos.toByteArray();
            serializedObject =new String(yourBytes);
        } catch (Exception e) {
            System.out.println(e);
        }

        return serializedObject;
    }

    @Override
    public Rent deserialize(Object data) {
        if (data == null) {
            return null;
        }
        Rent rent=null;
        ByteArrayInputStream bis = new ByteArrayInputStream(((String)data).getBytes());
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            rent  = (Rent)in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }

        return rent;
    }
}