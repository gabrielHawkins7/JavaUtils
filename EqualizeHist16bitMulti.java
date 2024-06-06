//Multithreaded Histogram equzliaion of a 16bit Buffered Image
static BufferedImage equalizeHist16bit(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    int histR[] = new int[65536];
    int histG[] = new int[65536];
    int histB[] = new int[65536];

    // Step 1: Create histogram in parallel
    int numThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    int chunkSize = (height + numThreads - 1) / numThreads;
    for (int i = 0; i < numThreads; i++) {
        final int startRow = i * chunkSize;
        final int endRow = Math.min(startRow + chunkSize, height);
        executor.submit(() -> {
            int[] localHistR = new int[65536];
            int[] localHistG = new int[65536];
            int[] localHistB = new int[65536];
            for (int y = startRow; y < endRow; y++) {
                for (int x = 0; x < width; x++) {
                    int[] pixel = new int[3];
                    image.getRaster().getPixel(x, y, pixel);
                    localHistR[pixel[0]]++;
                    localHistG[pixel[1]]++;
                    localHistB[pixel[2]]++;
                }
            }
            synchronized (histR) {
                for (int j = 0; j < 65536; j++) {
                    histR[j] += localHistR[j];
                    histG[j] += localHistG[j];
                    histB[j] += localHistB[j];
                }
            }
        });
    }
    executor.shutdown();
    try {
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    // Step 2: Create CDF in place
    for (int i = 1; i < 65536; ++i) {
        histR[i] += histR[i - 1];
        histG[i] += histG[i - 1];
        histB[i] += histB[i - 1];
    }
    // Step 3: Scale histogram back to 16-bit
    int max = histR[65535];
    for (int i = 0; i < 65536; i++) {
        histR[i] = (int) Math.round(histR[i] * (65536.0) / max);
        histG[i] = (int) Math.round(histG[i] * (65536.0) / max);
        histB[i] = (int) Math.round(histB[i] * (65536.0) / max);
    }
    // Step 4: Apply histogram in parallel
    executor = Executors.newFixedThreadPool(numThreads);
    for (int i = 0; i < numThreads; i++) {
        final int startRow = i * chunkSize;
        final int endRow = Math.min(startRow + chunkSize, height);

        executor.submit(() -> {
            for (int y = startRow; y < endRow; y++) {
                for (int x = 0; x < width; x++) {
                    int[] oldPixel = new int[3];
                    image.getRaster().getPixel(x, y, oldPixel);
                    int[] newPixel = {
                        histR[oldPixel[0]],
                        histG[oldPixel[1]],
                        histB[oldPixel[2]]
                    };
                    image.getRaster().setPixel(x, y, newPixel);
                }
            }
        });
    }
    executor.shutdown();
    try {
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return image;
}
