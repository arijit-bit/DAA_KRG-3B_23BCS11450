import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class SortingVisualizer extends JPanel {
    private final int[] array;
    private final int barWidth = 5;
    private boolean isSorting = false;
    private boolean paused = false;
    private boolean stopRequested = false;
    private boolean fastMode = true;
    private String currentStatus = "Idle";

    private int highlightA = -1, highlightB = -1;
    private int comparisonCount = 0;

    private static JButton[] sortAlgorithmButtons;
    private static JButton pauseButton, resetButton, speedButton;
    private static JLabel statusLabel, infoLabel;

    public SortingVisualizer(int size) {
        this.array = new int[size];
        generateRandomArray();
    }

    public void generateRandomArray() {
        stopRequested = true;
        paused = false;
        isSorting = false;
        highlightA = highlightB = -1;
        comparisonCount = 0;
        Random rand = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(500);
        }
        currentStatus = "Idle";
        repaint();
        updateStatus();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int totalBarsWidth = array.length * barWidth;
        int offsetX = Math.max(0, (getWidth() - totalBarsWidth) / 2);

        for (int i = 0; i < array.length; i++) {
            g2.setColor((i == highlightA || i == highlightB) ? Color.RED : new Color(0, 200, 255));
            g2.fillRoundRect(offsetX + i * barWidth, getHeight() - array[i], barWidth - 1, array[i], 4, 4);
        }
    }

    private void updateStatus() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Status: " + currentStatus);
            infoLabel.setText("Time: " + getTimeComplexity(currentStatus) + " | Comparisons: " + comparisonCount);
        });
    }

    private String getTimeComplexity(String name) {
        if (name.contains("Bubble")) return "O(n²)";
        if (name.contains("Insertion")) return "O(n²)";
        if (name.contains("Binary Insertion")) return "O(n²)";
        if (name.contains("Merge")) return "O(n log n)";
        if (name.contains("Quick")) return "O(n log n)";
        if (name.contains("Radix")) return "O(nk)";
        if (name.contains("Bucket")) return "O(n + k)";
        if (name.contains("Counting")) return "O(n + k)";
        return "-";
    }


    private int getDelay() { return fastMode ? 5 : 30; }

    private void delay() {
        try {
            while (paused) Thread.sleep(100);
            Thread.sleep(getDelay());
        } catch (InterruptedException ignored) {}
    }

    private void swap(int i, int j) {
        comparisonCount++;
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        highlightA = i;
        highlightB = j;
        repaint();
        delay();
    }

    private void startSorting(String name, Runnable method) {
        if (isSorting) {
            stopRequested = true;
            return;
        }
        stopRequested = false;
        paused = false;
        isSorting = true;
        currentStatus = "Sorting: " + name;
        comparisonCount = 0;
        updateStatus();
        disableButtons();

        new Thread(() -> {
            try {
                method.run();
            } finally {
                highlightA = highlightB = -1;
                repaint();
                isSorting = false;
                currentStatus = stopRequested ? "Stopped" : "Done";
                updateStatus();
                enableButtons();
            }
        }).start();
    }

    public void bubbleSort() {
        startSorting("Bubble Sort", () -> {
            for (int i = 0; i < array.length - 1 && !stopRequested; i++) {
                for (int j = 0; j < array.length - i - 1 && !stopRequested; j++) {
                    comparisonCount++;
                    if (array[j] > array[j + 1]) {
                        swap(j, j + 1);
                    }
                    updateStatus();
                }
            }
        });
    }

public void countingSort() {
    startSorting("Counting Sort", () -> {
        int max = Arrays.stream(array).max().orElse(0);
        int[] count = new int[max + 1];
        int[] output = new int[array.length];

        for (int num : array) {
            count[num]++;
            comparisonCount++;
            highlightA = Arrays.asList(array).indexOf(num);
            repaint();
            delay();
            updateStatus();
        }

        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        for (int i = array.length - 1; i >= 0; i--) {
            int val = array[i];
            output[count[val] - 1] = val;
            count[val]--;
            highlightA = i;
            repaint();
            delay();
            updateStatus();
        }

        for (int i = 0; i < array.length; i++) {
            array[i] = output[i];
            highlightA = i;
            repaint();
            delay();
        }
    });
}

    public void insertionSort() {
        startSorting("Insertion Sort", () -> {
            for (int i = 1; i < array.length && !stopRequested; i++) {
                int key = array[i];
                int j = i - 1;
                while (j >= 0 && array[j] > key && !stopRequested) {
                    comparisonCount++;
                    array[j + 1] = array[j];
                    highlightA = j;
                    repaint();
                    delay();
                    j--;
                    updateStatus();
                }
                comparisonCount++;
                array[j + 1] = key;
                repaint();
                delay();
                updateStatus();
            }
        });
    }

    public void binaryInsertionSort() {
        startSorting("Binary Insertion Sort", () -> {
            for (int i = 1; i < array.length && !stopRequested; i++) {
                int key = array[i];
                int low = 0, high = i - 1;
                while (low <= high && !stopRequested) {
                    comparisonCount++;
                    int mid = low + (high - low) / 2;
                    if (array[mid] > key) high = mid - 1;
                    else low = mid + 1;
                }
                for (int j = i - 1; j >= low && !stopRequested; j--) {
                    array[j + 1] = array[j];
                    highlightA = j;
                    repaint();
                    delay();
                }
                array[low] = key;
                repaint();
                delay();
                updateStatus();
            }
        });
    }

    public void mergeSort() {
        startSorting("Merge Sort", () -> mergeSort(array, 0, array.length - 1));
    }

    private void mergeSort(int[] arr, int l, int r) {
        if (l >= r || stopRequested) return;
        int m = (l + r) / 2;
        mergeSort(arr, l, m);
        mergeSort(arr, m + 1, r);
        merge(arr, l, m, r);
        updateStatus();
    }

    private void merge(int[] arr, int l, int m, int r) {
        int[] temp = new int[r - l + 1];
        int i = l, j = m + 1, k = 0;
        while (i <= m && j <= r && !stopRequested) {
            comparisonCount++;
            if (arr[i] <= arr[j]) temp[k++] = arr[i++];
            else temp[k++] = arr[j++];
            updateStatus();
        }
        while (i <= m && !stopRequested) temp[k++] = arr[i++];
        while (j <= r && !stopRequested) temp[k++] = arr[j++];
        for (k = 0; k < temp.length && !stopRequested; k++) {
            arr[l + k] = temp[k];
            highlightA = l + k;
            repaint();
            delay();
        }
    }

    public void quickSort() {
        startSorting("Quick Sort", () -> quickSort(array, 0, array.length - 1));
    }

    private void quickSort(int[] arr, int low, int high) {
        if (low < high && !stopRequested) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high && !stopRequested; j++) {
            comparisonCount++;
            if (arr[j] < pivot) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }

    public void radixSort() {
        startSorting("Radix Sort", () -> {
            int max = Arrays.stream(array).max().orElse(0);
            for (int exp = 1; max / exp > 0 && !stopRequested; exp *= 10) {
                countSortByDigit(exp);
                updateStatus();
            }
        });
    }

    private void countSortByDigit(int exp) {
        int n = array.length;
        int[] output = new int[n];
        int[] count = new int[10];
        for (int i = 0; i < n; i++) {
            count[(array[i] / exp) % 10]++;
            comparisonCount++;
        }
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            int digit = (array[i] / exp) % 10;
            output[--count[digit]] = array[i];
        }
        for (int i = 0; i < n; i++) {
            array[i] = output[i];
            highlightA = i;
            repaint();
            delay();
        }
    }

    public void bucketSort() {
        startSorting("Bucket Sort", () -> {
            int bucketCount = 10;
            List<List<Integer>> buckets = new ArrayList<>();
            for (int i = 0; i < bucketCount; i++) buckets.add(new ArrayList<>());
            int max = Arrays.stream(array).max().orElse(1);
            for (int value : array) {
                comparisonCount++;
                int idx = (value * bucketCount) / (max + 1);
                buckets.get(idx).add(value);
            }
            int index = 0;
            for (List<Integer> bucket : buckets) {
                Collections.sort(bucket);
                for (int value : bucket) {
                    array[index++] = value;
                    highlightA = index - 1;
                    repaint();
                    delay();
                }
            }
            updateStatus();
        });
    }

    private void disableButtons() {
        for (JButton btn : sortAlgorithmButtons) btn.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    private void enableButtons() {
        for (JButton btn : sortAlgorithmButtons) btn.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    private static JButton createStyledButton(String text, String tooltip, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.setToolTipText(tooltip);
        btn.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(90, 90, 90)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(new Color(60, 60, 60)); }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sorting Visualizer");
            SortingVisualizer panel = new SortingVisualizer(150);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(new Color(20, 20, 20));

            statusLabel = new JLabel("Status: Idle");
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            topPanel.add(statusLabel, BorderLayout.WEST);

            infoLabel = new JLabel("Time: - | Comparisons: 0");
            infoLabel.setForeground(Color.WHITE);
            infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            topPanel.add(infoLabel, BorderLayout.CENTER);

            speedButton = createStyledButton("Speed: Fast", "Toggle speed", 13);
            speedButton.addActionListener(e -> {
                panel.fastMode = !panel.fastMode;
                speedButton.setText(panel.fastMode ? "Speed: Fast" : "Speed: Slow");
            });
            topPanel.add(speedButton, BorderLayout.EAST);

            JPanel utilityPanel = new JPanel(new FlowLayout());
            utilityPanel.setBackground(new Color(20, 20, 20));

            pauseButton = createStyledButton("⏸ Pause", "Pause/resume", 20);
            pauseButton.addActionListener(e -> {
                panel.paused = !panel.paused;
                pauseButton.setText(panel.paused ? "▶ Resume" : "⏸ Pause");
                panel.updateStatus();
            });
            pauseButton.setEnabled(false);
            utilityPanel.add(pauseButton);

            resetButton = createStyledButton("⟳ Reset", "New array", 16);
            resetButton.addActionListener(e -> panel.generateRandomArray());
            utilityPanel.add(resetButton);

            JPanel sortButtonsPanel = new JPanel(new FlowLayout());
            sortButtonsPanel.setBackground(new Color(20, 20, 20));
            String[] names = {
    "Bubble", "Insertion", "Binary Insertion", "Merge", "Quick", "Radix", "Bucket", "Counting"
};

            sortAlgorithmButtons = new JButton[names.length];
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                JButton btn = createStyledButton(name, "Run " + name, 16);
                btn.addActionListener(e -> {
                    panel.generateRandomArray();
                    switch (name) {
                        case "Bubble" -> panel.bubbleSort();
                        case "Insertion" -> panel.insertionSort();
                        case "Binary Insertion" -> panel.binaryInsertionSort();
                        case "Merge" -> panel.mergeSort();
                        case "Quick" -> panel.quickSort();
                        case "Radix" -> panel.radixSort();
                        case "Bucket" -> panel.bucketSort();
                        case "Counting" -> panel.countingSort();
                    }
                });
                sortAlgorithmButtons[i] = btn;
                sortButtonsPanel.add(btn);
            }

            JPanel bottom = new JPanel();
            bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
            bottom.setBackground(new Color(20, 20, 20));
            bottom.add(utilityPanel);
            bottom.add(sortButtonsPanel);

            frame.setLayout(new BorderLayout());
            frame.add(topPanel, BorderLayout.NORTH);
            frame.add(panel, BorderLayout.CENTER);
            frame.add(bottom, BorderLayout.SOUTH);
            frame.setSize(950, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
