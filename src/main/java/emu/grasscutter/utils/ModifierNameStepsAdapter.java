package emu.grasscutter.utils;

import com.google.gson.*;
import com.google.gson.stream.*;
import java.io.IOException;
import java.util.*;

public class ModifierNameStepsAdapter extends TypeAdapter<List<String>> {
    @Override
    public void write(JsonWriter out, List<String> value) throws IOException {
        out.beginArray();
        for (String s : value) {
            out.value(s);
        }
        out.endArray();
    }

    @Override
    public List<String> read(JsonReader in) throws IOException {
        List<String> result = new ArrayList<>();
        switch (in.peek()) {
            case STRING:
                result.add(in.nextString());
                break;
            case BEGIN_OBJECT:
                in.beginObject();
                while (in.hasNext()) {
                    String name = in.nextName();
                    if (name.equals("name")) {
                        result.add(in.nextString());
                    } else {
                        in.skipValue();
                    }
                }
                in.endObject();
                break;
            case BEGIN_ARRAY:
                in.beginArray();
                while (in.hasNext()) {
                    if (in.peek() == JsonToken.STRING) {
                        result.add(in.nextString());
                    } else if (in.peek() == JsonToken.BEGIN_OBJECT) {
                        in.beginObject();
                        while (in.hasNext()) {
                            String name = in.nextName();
                            if (name.equals("name")) {
                                result.add(in.nextString());
                            } else {
                                in.skipValue();
                            }
                        }
                        in.endObject();
                    } else {
                        in.skipValue();
                    }
                }
                in.endArray();
                break;
            default:
                in.skipValue();
        }
        return result;
    }
}
