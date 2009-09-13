/*
 * WriteLocaleList.java
 *
 * Created on 9 paŸdziernik 2004, 18:18
 */
import java.io.*;
/**
 *
 * @author  MiKO
 */
public class WriteLocaleList {
    
    /** Creates a new instance of WriteLocaleList */
    public WriteLocaleList() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("/var/list"));
        dos.writeUTF("pl");
        dos.writeUTF("en");
        dos.close();
        dos = new DataOutputStream(new FileOutputStream("/var/pl"));
        dos.writeUTF("OK");
        dos.writeUTF("Menu");
        dos.writeUTF("Koniec");
        dos.writeUTF("Graj");
        dos.writeUTF("Moje piosenki");
        dos.writeUTF("Ustawienia");
        dos.writeUTF("Pomoc");
        dos.writeUTF("O programie");
        dos.writeUTF("Informacje");
        dos.writeUTF("Zmieñ nazwê");
        dos.writeUTF("Skasuj");
        dos.writeUTF("Pobierz");
        dos.writeUTF("Czcionka");
        dos.writeUTF("G³oœnoœæ");
        dos.writeUTF("Kolory");
        dos.writeUTF("Og³oszenia");
        dos.writeUTF("Jêzyk");
        dos.writeUTF("Krój");
        dos.writeUTF("Styl");
        dos.writeUTF("Rozmiar");
        dos.writeUTF("Sta³ej szerokoœci");
        dos.writeUTF("Proporcjonalne");
        dos.writeUTF("Systemowe");
        dos.writeUTF("Wyt³uszczone");
        dos.writeUTF("Pochylone");
        dos.writeUTF("Podkreœlone");
        dos.writeUTF("Obwódka");
        dos.writeUTF("Cieñ");
        dos.writeUTF("Du¿e");
        dos.writeUTF("Œrednie");
        dos.writeUTF("Ma³e");
        dos.writeUTF("Pierwszoplanowy");
        dos.writeUTF("T³o");
        dos.writeUTF("Aktywny");
        dos.writeUTF("Stop");
        dos.writeUTF("Wznów");
        dos.writeUTF("Œpiewaj");
        dos.writeUTF("teraz");
        dos.writeUTF("przed");
        dos.writeUTF("po");
        dos.writeUTF("Zaawansowane");
        dos.writeUTF("Wczeœniej wyœwietlaj (ms)");
        dos.writeUTF("Interlinia");
        dos.writeUTF("Stare historii");
        dos.writeUTF("Nowe linie");
        dos.writeUTF("Bazowa lina");
        dos.writeUTF("T³o");
        dos.writeUTF("Czysty kolor");
        dos.writeUTF("Tapeta");
        dos.writeUTF("Poka¿ panel");
        dos.writeUTF("Automatycznie");
        dos.writeUTF("IdŸ");
        dos.writeUTF("Aby pobraæ now¹ piosenkê z Internetu, wpisz poni¿ej adres URL wskazuj¹cy na plik MIDI typu KARAOKE (z rozszerzeniem .KAR)");
        dos.writeUTF("Czy aby na pewno skasowaæ wszystkie pobrane pliki?");
        dos.writeUTF("mobiKAR to odtwarzacz plików MIDI z tekstem piosenki czyli tzw. KARAOKE.\nWiêcej na domowej stronie aplikacji");
        dos.writeUTF("Twórcy");
        dos.writeUTF("Wersja");
        dos.writeUTF("Witaj w œwiecie mobilnego KARAOKE!\nJeœli chcesz zaœpiewaæ z mobiKAR'em wybierz pierwsz¹ pozycjê z menu. Po pojawiniu siê ekranu wybierz opcjê ŒPIEWAJ.\nTo wszystko!\nAby zmieniæ wygl¹d czy funkcjonalnoœæ mobiKAR'a wejdŸ do USTAWIENIA z poziomu MENU. Tu znajdziesz mnóstwo opcji, które pozw¹ Ci dowolnie skonfigurowaæ aplikacjê.\nJeœli ten tekst nie jest wystarczaj¹cy odwiedŸ stronê WEB w sieci: http://komkom.pl/mobiKAR/help.html");
        dos.close();
        dos = new DataOutputStream(new FileOutputStream("/var/en"));
        dos.writeUTF("OK");
        dos.writeUTF("Menu");
        dos.writeUTF("Quit");
        dos.writeUTF("Play");
        dos.writeUTF("My songs");
        dos.writeUTF("Settings");
        dos.writeUTF("Help");
        dos.writeUTF("About");
        dos.writeUTF("Info");
        dos.writeUTF("Rename");
        dos.writeUTF("Delete");
        dos.writeUTF("Download");
        dos.writeUTF("Fonts");
        dos.writeUTF("Volume");
        dos.writeUTF("Colors");
        dos.writeUTF("Advertisment");
        dos.writeUTF("Language");
        dos.writeUTF("Face");
        dos.writeUTF("Style");
        dos.writeUTF("Size");
        dos.writeUTF("Monospace");
        dos.writeUTF("Proportional");
        dos.writeUTF("System");
        dos.writeUTF("Bold");
        dos.writeUTF("Italic");
        dos.writeUTF("Underlined");
        dos.writeUTF("Outline");
        dos.writeUTF("Shadow");
        dos.writeUTF("Large");
        dos.writeUTF("Medium");
        dos.writeUTF("Small");
        dos.writeUTF("Foreground");
        dos.writeUTF("Background");
        dos.writeUTF("Active");
        dos.writeUTF("Stop");
        dos.writeUTF("Resume");
        dos.writeUTF("Sing");
        dos.writeUTF("active");
        dos.writeUTF("before");
        dos.writeUTF("after");
        dos.writeUTF("Advanced");
        dos.writeUTF("Preview (ms)");
        dos.writeUTF("Interline");
        dos.writeUTF("History lines");
        dos.writeUTF("Buffor lines");
        dos.writeUTF("Base line");
        dos.writeUTF("Background");
        dos.writeUTF("Plain color");
        dos.writeUTF("Wallpapper");
        dos.writeUTF("Show panel");
        dos.writeUTF("Auto");
        dos.writeUTF("Go");
        dos.writeUTF("If you want download a new song, please enter URL to karaoke file (with .KAR extension)");
        dos.writeUTF("Are You sure to delete all downloaded files?");
        dos.writeUTF("mobiKAR is a player files MIDI with lyrics a.k.a. KARAOKE.\nMore at home page");
        dos.writeUTF("Creators");
        dos.writeUTF("Version");
        dos.writeUTF("Welcome in the mobile KARAOKE World!\nIf you wish sing with mobiKAR, you just choose the first option from MENU. After showing a screen choose option SING.\nThat is all!\nIn order to change lay-out or functionality you can enter to SETTINGS from MENU. There are find many settings, whose configure aplication.\nIf this text is not enought for yo, let you go to the WEB site http://komkom.pl/mobiKAR/help.html");
        dos.close();
    }
    
    
    
}
