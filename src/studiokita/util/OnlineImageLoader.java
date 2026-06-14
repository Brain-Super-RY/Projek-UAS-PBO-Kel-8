package studiokita.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * OnlineImageLoader — Mengunduh dan melakukan caching gambar dari internet secara asynchronous.
 */
public class OnlineImageLoader {

    // Fitur Cache: Gambar yang sudah pernah di-download disimpan di memori agar hemat kuota dan cepat
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();

    /**
     * Memuat gambar dari URL internet ke dalam JLabel secara aman tanpa membuat UI Lag.
     * * @param labelTarget JLabel tempat menaruh gambar
     * @param urlString   Alamat URL gambar online
     * @param width       Lebar gambar yang diinginkan (pixel)
     * @param height      Tinggi gambar yang diinginkan (pixel)
     */
    public static void loadInto(JLabel labelTarget, String urlString, int width, int height) {
        // 1. Set teks loading sementara selagi proses download berjalan
        labelTarget.setText("Loading Image...");
        labelTarget.setIcon(null);
        labelTarget.setHorizontalAlignment(SwingConstants.CENTER);

        // 2. Cek apakah gambar sudah ada di memori cache
        if (imageCache.containsKey(urlString)) {
            labelTarget.setText("");
            labelTarget.setIcon(imageCache.get(urlString));
            return;
        }

        // 3. Jika belum ada di cache, jalankan proses download di latar belakang (Thread Baru)
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                // Mengambil aliran data gambar dari internet
                Image image = ImageIO.read(url);
                
                if (image != null) {
                    // Mengubah ukuran gambar (Resizing) agar pas dengan kotak di UI
                    Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    ImageIcon finalIcon = new ImageIcon(scaledImage);

                    // Simpan hasil ke cache memori
                    imageCache.put(urlString, finalIcon);

                    // Kembalikan ke UI Thread utama Java Swing secara aman (thread-safe)
                    SwingUtilities.invokeLater(() -> {
                        labelTarget.setText("");
                        labelTarget.setIcon(finalIcon);
                    });
                } else {
                    throw new Exception("Format gambar tidak dikenali.");
                }
            } catch (Exception e) {
                System.err.println("[OnlineImageLoader] Gagal memuat url: " + urlString + " -> " + e.getMessage());
                // Jika gagal (RTO/Salah URL), tampilkan teks error di komponen
                SwingUtilities.invokeLater(() -> {
                    labelTarget.setText("<html><center>No Preview<br>Available</center></html>");
                    labelTarget.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                });
            }
        }).start();
    }
}