package sg.edu.sit.inf1009.p2team2.engine.io.storage;

/**
 * Factory helpers for opening storage providers.
 */
public final class StorageProviders {
    private StorageProviders() {
    }

    public static StorageProvider preferences(String preferencesName) {
        return new GdxStorageProvider(preferencesName);
    }
}
