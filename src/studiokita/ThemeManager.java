package studiokita;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/** ThemeManager — Manages theme switching safely */
public class ThemeManager {
    public enum Theme { DARK, LIGHT }
    private static Theme current = Theme.DARK;
    private static List<Runnable> listeners = new ArrayList<>();

    public static Theme getCurrent() { return current; }
    public static boolean isDark()   { return current==Theme.DARK; }

    public static void toggle() { 
        current = (current == Theme.DARK) ? Theme.LIGHT : Theme.DARK; 
        notifyAll_(); 
    }

    public static void addListener(Runnable r) { listeners.add(r); }
    public static void removeListener(Runnable r) { listeners.remove(r); }
    private static void notifyAll_() { new ArrayList<>(listeners).forEach(Runnable::run); }
}
