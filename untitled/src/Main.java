import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    static Map<String, Integer> klasy = new HashMap<>();
    static List<String[]> wektory;
    static List<String[]> wektoryTreningowe;
    static List<String[]> odleglosci;
    static List<String[]> klasyfikowane;
    static String[] wektorX;
    static String[] klasyfikowanyX;
    static String klasa;
    static int decyzja = 0;
    static int zgodnosc;
    static int k;

    public static void main(String[] args) {


        System.out.println("Podaj sciezke do pliku treningowego: ");

        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        wektoryTreningowe = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = bf.readLine()) != null) {
                String[] wektor = line.split(",");
                if (!klasy.containsKey(wektor[wektor.length - 1])) {
                    klasy.put(wektor[wektor.length - 1], 0);
                }
                wektoryTreningowe.add(wektor);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            System.out.println("Wybierz opcję: ");
            System.out.print("1.Plik testowy\n2.Własny wektor\n3.Zmien k\n4.Wyjdź\n ");
            int wybor = Integer.parseInt(scanner.nextLine());
            if (wybor == 1) {
                decyzja = 1;
                System.out.println("Podaj sciezke do pliku: ");
                wektory = new ArrayList<>();
                String plikTestowy = scanner.nextLine();
                System.out.println("Podaj k: ");
                k = Integer.parseInt(scanner.nextLine());
                try (BufferedReader bf = new BufferedReader(new FileReader(plikTestowy))) {
                    String line;
                    while ((line = bf.readLine()) != null) {
                        String[] wektor = line.split(",");
                        wektory.add(wektor);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                przydzielKlasy();
                sprawdzZgodnoscWektorow();

                int[] ints = new int[]{1,2,3,4,5};


            } else if (wybor == 2) {
                decyzja = 2;
                System.out.println("Podaj dane wektoru: ");
                wektorX = scanner.nextLine().split(",");
                System.out.println("Podaj k: ");
                k = Integer.parseInt(scanner.nextLine());
                przydzielKlase();

            } else if(wybor == 3){
                System.out.println("Podaj k: ");
                k = Integer.parseInt(scanner.nextLine());
                if(decyzja == 1){
                    przydzielKlasy();
                    sprawdzZgodnoscWektorow();
                }else if(decyzja == 2){
                    przydzielKlase();
                }
            }else if(wybor == 4){
                System.exit(0);
            }
        }


    }

    public static void sprawdzZgodnoscWektorow() {
        zgodnosc = 0;
        for (int i = 0; i < klasyfikowane.size(); i++) {
            String[] klasyfikowany = klasyfikowane.get(i);
            String[] wektorDoSprawdzenia = wektory.get(i);
            if (klasyfikowany[klasyfikowany.length - 1].equals(wektorDoSprawdzenia[wektorDoSprawdzenia.length - 1])) {
                zgodnosc++;
            }

        }
        double procentZgodnosci = (double) zgodnosc / klasyfikowane.size() * 100;
        String wyswietlZgodnosc = String.format("%.2f", procentZgodnosci);
        System.out.println("Zgodnosc wynosi: " + wyswietlZgodnosc + "%");
    }

    public static void przydzielKlase() {
        String[] klasyfikowany = Arrays.copyOf(wektorX, wektorX.length + 1);
        odleglosci = odlegloscWektorow(wektoryTreningowe, wektorX);
        klasa = klasyfikuj(odleglosci, klasy, k);
        klasyfikowanyX = klasyfikowany;
        klasyfikowany[klasyfikowany.length - 1] = klasa;
        System.out.println(Arrays.toString(klasyfikowanyX));
    }

    public static void przydzielKlasy() {
        klasyfikowane = new ArrayList<>();
        for (String[] wektor : wektory) {
            String[] klasyfikowany = Arrays.copyOf(wektor, wektor.length);
            odleglosci = odlegloscWektorow(wektoryTreningowe, wektor);
            klasa = klasyfikuj(odleglosci, klasy, k);
            klasyfikowany[klasyfikowany.length - 1] = klasa;
            klasyfikowane.add(klasyfikowany);

        }
        for (String[] wektor : klasyfikowane) {
            System.out.println(Arrays.toString(wektor));
        }
    }

    public static List<String[]> odlegloscWektorow(List<String[]> wektoryDoObliczenia, String[] wektorX) {
        List<String[]> result = new ArrayList<>();
        for (String[] strings : wektoryDoObliczenia) {
            Double dlugosc = 0.0;
            for (int i = 0; i < wektorX.length - 1; i++) {
                dlugosc += Math.pow(Double.parseDouble(wektorX[i]) - Double.parseDouble(strings[i]), 2);
            }
            dlugosc = Math.sqrt(dlugosc);

            String[] nowyWektor = new String[2];
            nowyWektor[0] = String.format("%.2f", dlugosc);
            nowyWektor[1] = strings[strings.length - 1];
            result.add(nowyWektor);
        }
        result.sort(Comparator.comparing(array -> array[0]));
        return result;
    }

    public static String klasyfikuj(List<String[]> odleglosci, Map<String, Integer> klasy, int k) {
        Map<String, Integer> klasyDoEdytowania = new HashMap<>(klasy);
        List<String[]> kOdleglosci = odleglosci.subList(0, Math.min(k, odleglosci.size()));
        for (String[] wektor : kOdleglosci) {
            klasyDoEdytowania.replace(wektor[wektor.length - 1], klasyDoEdytowania.get(wektor[wektor.length - 1]) + 1);
        }
        String klasa = "";
        int wartoscMaks = 0;
        for (Map.Entry<String, Integer> entry : klasyDoEdytowania.entrySet()) {
            if (entry.getValue() > wartoscMaks) {
                wartoscMaks = entry.getValue();
                klasa = entry.getKey();
            }
        }
        return klasa;
    }
}