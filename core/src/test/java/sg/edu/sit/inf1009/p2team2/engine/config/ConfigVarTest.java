package sg.edu.sit.inf1009.p2team2.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConfigVarTest {

    @Test
    void resetRestoresDefaultValue() {
        ConfigVar<Integer> var = new ConfigVar<>(42, 10);

        var.reset();

        assertEquals(10, var.getValue());
    }

    @Test
    void numericAndStringConversionsWork() {
        ConfigVar<Object> var = new ConfigVar<>("15", 0);
        assertEquals(15, var.asInt());

        var.setValue(Float.valueOf(3.5f));
        assertEquals(3.5f, var.asFloat(), 0.0001f);

        var.setValue(Boolean.TRUE);
        assertEquals(1, var.asInt());
        assertEquals(1f, var.asFloat(), 0.0001f);
        assertTrue(var.asBool());

        var.setValue("false");
        assertFalse(var.asBool());
    }

    @Test
    void asStringUsesResolvedValue() {
        ConfigVar<Object> var = new ConfigVar<>(null, "default-text");

        assertEquals("default-text", var.asString());

        var.setValue(99);
        assertEquals("99", var.asString());
    }
}
