package emu.grasscutter.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class FloatAdapter extends TypeAdapter<Float> {
    @Override
    public void write(JsonWriter out, Float value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public Float read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case NUMBER:
                return (float) in.nextDouble();
            case STRING:
                String str = in.nextString();
                try {
                    return Float.parseFloat(str);
                } catch (NumberFormatException e) {
                    return 0f; // дефолт при ошибке
                }
            case NULL:
                in.nextNull();
                return null;
            default:
                in.skipValue();
                return 0f;
        }
    }
}
