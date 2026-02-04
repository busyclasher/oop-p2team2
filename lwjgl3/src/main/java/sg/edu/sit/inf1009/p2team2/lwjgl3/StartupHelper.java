/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Note: the above license and copyright applies to this file only.

package sg.edu.sit.inf1009.p2team2.lwjgl3;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import org.lwjgl.system.macosx.LibC;
import org.lwjgl.system.macosx.ObjCRuntime;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import static org.lwjgl.system.JNI.invokePPP;
import static org.lwjgl.system.JNI.invokePPZ;
import static org.lwjgl.system.macosx.ObjCRuntime.objc_getClass;
import static org.lwjgl.system.macosx.ObjCRuntime.sel_getUid;

/**
 * Adds some utilities to ensure that the JVM was started with the
 * {@code -XstartOnFirstThread} argument, which is required on macOS for LWJGL 3
 * to function. Also helps on Windows when users have names with characters from
 * outside the Latin alphabet, a common cause of startup crashes.
 */
public final class StartupHelper {

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    private StartupHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Starts a new JVM if required. Returns whether a new JVM was started and
     * thus no code should be executed in this one.
     *
     * @param redirectOutput whether the output of the new JVM should be rerouted
     *                       to the old JVM
     */
    public static boolean startNewJvmIfRequired(boolean redirectOutput) {
        String osName = System.getProperty("os.name").toLowerCase(java.util.Locale.ROOT);
        if (!osName.contains("mac")) {
            if (osName.contains("windows")) {
                // See original StartupHelper for why these properties are temporarily changed.
                String programData = System.getenv("ProgramData");
                if (programData == null) programData = "C:\\Temp\\";
                String prevTmpDir = System.getProperty("java.io.tmpdir", programData);
                String prevUser = System.getProperty("user.name", "libGDX_User");
                System.setProperty("java.io.tmpdir", programData + "/libGDX-temp");
                System.setProperty(
                    "user.name",
                    ("User_" + prevUser.hashCode() + "_GDX" + Version.VERSION).replace('.', '_')
                );
                Lwjgl3NativesLoader.load();
                System.setProperty("java.io.tmpdir", prevTmpDir);
                System.setProperty("user.name", prevUser);
            }
            return false;
        }

        // No need for -XstartOnFirstThread on Graal native image.
        if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) {
            return false;
        }

        // Checks if we are already on the main thread, such as from running via Construo.
        long objcMsgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
        long nsThread = objc_getClass("NSThread");
        long currentThread = invokePPP(nsThread, sel_getUid("currentThread"), objcMsgSend);
        boolean isMainThread = invokePPZ(currentThread, sel_getUid("isMainThread"), objcMsgSend);
        if (isMainThread) return false;

        long pid = LibC.getpid();

        // Check whether -XstartOnFirstThread is enabled.
        if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
            return false;
        }

        // Avoid looping restarts.
        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println("Unable to verify -XstartOnFirstThread; continuing without restart.");
            return false;
        }

        ArrayList<String> jvmArgs = new ArrayList<>();
        String separator = System.getProperty("file.separator", "/");
        String javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java";

        if (!(new File(javaExecPath)).exists()) {
            System.err.println("Java executable not found; cannot restart with -XstartOnFirstThread.");
            return false;
        }

        jvmArgs.add(javaExecPath);
        jvmArgs.add("-XstartOnFirstThread");
        jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        jvmArgs.add("-cp");
        jvmArgs.add(System.getProperty("java.class.path"));

        String mainClass = System.getenv("JAVA_MAIN_CLASS_" + pid);
        if (mainClass == null) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (trace.length > 0) {
                mainClass = trace[trace.length - 1].getClassName();
            } else {
                System.err.println("The main class could not be determined.");
                return false;
            }
        }
        jvmArgs.add(mainClass);

        try {
            if (!redirectOutput) {
                new ProcessBuilder(jvmArgs).start();
            } else {
                Process process = new ProcessBuilder(jvmArgs).redirectErrorStream(true).start();
                BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = processOutput.readLine()) != null) {
                    System.out.println(line);
                }
                process.waitFor();
            }
        } catch (Exception e) {
            System.err.println("There was a problem restarting the JVM");
            e.printStackTrace();
        }

        return true;
    }

    public static boolean startNewJvmIfRequired() {
        return startNewJvmIfRequired(true);
    }
}

