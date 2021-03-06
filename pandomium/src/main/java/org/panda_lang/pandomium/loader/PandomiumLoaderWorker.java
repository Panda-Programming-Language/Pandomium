package org.panda_lang.pandomium.loader;

import org.panda_lang.pandomium.Pandomium;
import org.panda_lang.pandomium.loader.os.PandomiumLinuxNativesLoader;
import org.panda_lang.pandomium.settings.PandomiumSettings;
import org.panda_lang.pandomium.settings.categories.NativesSettings;
import org.panda_lang.pandomium.util.SystemUtils;
import org.panda_lang.pandomium.util.os.PandomiumOS;

public class PandomiumLoaderWorker implements Runnable {

    private final PandomiumLoader loader;

    public PandomiumLoaderWorker(PandomiumLoader loader) {
        this.loader = loader;
    }

    @Override
    public void run() {
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() throws Exception {
        loader.updateProgress(0);

        PandomiumNativesLoader nativeLoader = new PandomiumNativesLoader();
        nativeLoader.loadNatives(loader);

        Pandomium pandomium = loader.getPandomium();
        PandomiumSettings settings = pandomium.getSettings();

        NativesSettings nativesSettings = settings.getNatives();
        String nativePath = nativesSettings.getNativeDirectory();
        SystemUtils.injectLibraryPath(nativePath);

        if (PandomiumOS.isLinux()) {
            PandomiumLinuxNativesLoader linuxNativesLoader = new PandomiumLinuxNativesLoader();
            linuxNativesLoader.loadLinuxNatives(nativePath);
        }

        loader.updateProgress(100);
        loader.callListeners(PandomiumProgressListener.State.DONE);
    }

}
