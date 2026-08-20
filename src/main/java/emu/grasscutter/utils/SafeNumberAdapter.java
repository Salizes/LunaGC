package emu.grasscutter.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class SafeNumberAdapter extends TypeAdapter<Number> {
    @Override
    public void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.STRING) {
            String str = in.nextString();
            try {
                // пробуем как double
                return Double.parseDouble(str);
            } catch (NumberFormatException e) {
                // если не число — возвращаем 0
                return 0;
            }
        } else if (in.peek() == JsonToken.NUMBER) {
            return in.nextDouble();
        } else {
            in.skipValue();
            return 0;
        }
    }
}
