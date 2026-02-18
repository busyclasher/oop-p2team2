package sg.edu.sit.inf1009.p2team2.engine.scenes.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import sg.edu.sit.inf1009.p2team2.engine.scenes.MainScene;
import sg.edu.sit.inf1009.p2team2.engine.scenes.MenuScene;
import sg.edu.sit.inf1009.p2team2.engine.scenes.SettingsScene;

class SceneSmokeTest {

    @Test
    void menuSceneLoadCreatesMenuItems() throws Exception {
        MenuScene scene = new MenuScene(null);

        assertDoesNotThrow(scene::load);

        Field menuItemsField = MenuScene.class.getDeclaredField("menuItems");
        menuItemsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<?> menuItems = (List<?>) menuItemsField.get(scene);

        assertNotNull(menuItems);
        assertEquals(3, menuItems.size());
    }

    @Test
    void settingsSceneLoadCreatesControls() throws Exception {
        SettingsScene scene = new SettingsScene(null);

        assertDoesNotThrow(scene::load);

        assertNotNull(getFieldValue(scene, "volumeSlider"));
        assertNotNull(getFieldValue(scene, "fullscreenToggle"));
        assertNotNull(getFieldValue(scene, "frictionSlider"));
        assertNotNull(getFieldValue(scene, "gravitySlider"));
        assertNotNull(getFieldValue(scene, "speedSlider"));
        assertNotNull(getFieldValue(scene, "collisionsToggle"));
    }

    @Test
    void backgroundSpritesExist() throws Exception {
        assertTrue(spriteExists(getStaticString(MenuScene.class, "BACKGROUND_SPRITE")));
        assertTrue(spriteExists(getStaticString(SettingsScene.class, "BACKGROUND_SPRITE")));
        assertTrue(spriteExists(getStaticString(MainScene.class, "BACKGROUND_SPRITE")));
    }

    private static Object getFieldValue(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private static String getStaticString(Class<?> type, String fieldName) throws Exception {
        Field field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (String) field.get(null);
    }

    private static boolean spriteExists(String spriteName) {
        Path directPath = Paths.get("assets", spriteName);
        if (Files.exists(directPath)) {
            return true;
        }
        Path parentPath = Paths.get("..", "assets", spriteName);
        return Files.exists(parentPath);
    }
}
