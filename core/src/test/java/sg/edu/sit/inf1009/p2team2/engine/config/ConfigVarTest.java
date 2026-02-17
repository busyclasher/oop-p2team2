package sg.edu.sit.inf1009.p2team2.engine.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConfigVarTest {

    @Test
    void typedConversionsAndResetWork() {
        ConfigVar var = new ConfigVar(10, 5);

        assertEquals(10, var.asInt());
        assertEquals(10f, var.asFloat(), 0.0001f);
        assertTrue(var.asBool());

        var.setValue("2.5");
        assertEquals(2.5f, var.asFloat(), 0.0001f);

        var.setValue(false);
        assertFalse(var.asBool());

        var.reset();
        assertEquals(5, var.asInt());
    }
}
