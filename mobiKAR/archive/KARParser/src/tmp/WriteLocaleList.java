package tmp;
/*
 * WriteLocaleList.java
 *
 * Created on 9 paüdziernik 2004, 18:18
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
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("D:/projects/java/ME/mobiKAR/mobiKAR/res/global/loc.dir"));
        dos.writeInt(2); // Ile ich jest na liúcie
        dos.writeUTF("pl");
        dos.writeUTF("en");
        dos.close();
        dos = new DataOutputStream(new FileOutputStream("D:/projects/java/ME/mobiKAR/mobiKAR/res/global/loc/pl"));
        dos.writeUTF("OK");
        dos.writeUTF("Menu");
        dos.writeUTF("Koniec");
        dos.writeUTF("Graj");
        dos.writeUTF("Moje piosenki");
        dos.writeUTF("Ustawienia");
        dos.writeUTF("Pomoc");
        dos.writeUTF("O programie");
        dos.writeUTF("Informacje");
        dos.writeUTF("Pobierz");
        dos.writeUTF("Lista z mobiKAR.net");
        dos.writeUTF("Czcionka");
        dos.writeUTF("G≥oúnoúÊ");
        dos.writeUTF("Kolory");
        dos.writeUTF("Og≥oszenia");
        dos.writeUTF("JÍzyk");
        dos.writeUTF("KrÛj");
        dos.writeUTF("Styl");
        dos.writeUTF("Rozmiar");
        dos.writeUTF("Sta≥ej szerokoúci");
        dos.writeUTF("Proporcjonalne");
        dos.writeUTF("Systemowe");
        dos.writeUTF("Wyt≥uszczone");
        dos.writeUTF("Pochylone");
        dos.writeUTF("Podkreúlone");
        dos.writeUTF("ObwÛdka");
        dos.writeUTF("CieÒ");
        dos.writeUTF("Duøe");
        dos.writeUTF("årednie");
        dos.writeUTF("Ma≥e");
        dos.writeUTF("Pierwszoplanowy");
        dos.writeUTF("T≥o");
        dos.writeUTF("Aktywny");
        dos.writeUTF("Stop");
        dos.writeUTF("WznÛw");
        dos.writeUTF("åpiewaj");
        dos.writeUTF("po");
        dos.writeUTF("teraz");
        dos.writeUTF("moje");
        dos.writeUTF("twoje");
        dos.writeUTF("Zaawansowane");
        dos.writeUTF("Wczeúniej wyúwietlaj (ms)");
        dos.writeUTF("Interlinia");
        dos.writeUTF("Stare historii");
        dos.writeUTF("Nowe linie");
        dos.writeUTF("Bazowa lina");
        dos.writeUTF("Wyúwietlanie");
        dos.writeUTF("Gradient");
        dos.writeUTF("Tapeta");
        dos.writeUTF("Pokaø panel");
        dos.writeUTF("Automatycznie");
        dos.writeUTF("Idü");
        dos.writeUTF("Aby pobraÊ nowπ piosenkÍ z Internetu, wpisz poniøej adres URL wskazujπcy na plik MIDI typu KARAOKE (z rozszerzeniem .KAR)");
        dos.writeUTF("Czy aby na pewno skasowaÊ wszystkie pobrane pliki?");
        dos.writeUTF("mobiKAR to odtwarzacz plikÛw MIDI z tekstem piosenki czyli tzw. KARAOKE.\nWiÍcej na domowej stronie aplikacji");
        dos.writeUTF("TwÛrcy");
        dos.writeUTF("Wersja");
        dos.writeUTF("Witaj w úwiecie mobilnego KARAOKE!\nJeúli chcesz zaúpiewaÊ z mobiKAR'em wybierz pierwszπ pozycjÍ z menu. Po pojawiniu siÍ ekranu wybierz opcjÍ åPIEWAJ.\nTo wszystko!\nAby zmieniÊ wyglπd czy funkcjonalnoúÊ mobiKAR'a wejdü do USTAWIENIA z poziomu MENU. Tu znajdziesz mnÛstwo opcji, ktÛre pozwπ Ci dowolnie skonfigurowaÊ aplikacjÍ.\nJeúli ten tekst nie jest wystarczajπcy odwiedü internetowπ stronÍ domowπ mobikar.net");
        dos.writeUTF("£πczenie...");
        dos.writeUTF("Pobieranie...");
        dos.writeUTF("Tylko ≥aciÒskie litery");
        dos.writeUTF("•∆ £—”åèØπÊÍ≥ÒÛúüø()<>[]");
        dos.writeUTF("ACELNOSZZacelnoszz      ");
        dos.writeUTF("PrzywrÛÊ domyúlne");
        dos.writeUTF("Has≥o");
        dos.writeUTF("Wprowadü Twoje has≥o ");
        dos.writeUTF("TwÛj login na mobikar.net to ");
        dos.writeUTF("Pokaø");
        dos.writeUTF("Aplikacja zaloguje siÍ do serwera mobikar.net i pobierze listÍ Twoich piosenek, ktÛre moøesz úciπgnπÊ do aplikacji.");
        dos.writeUTF("Tytu≥: ");
        dos.writeUTF("Artysta: ");
        dos.writeUTF("S≥owa: ");
        dos.writeUTF("Muzyka: ");
        dos.writeUTF("Opracowanie: ");
        dos.writeUTF("Wersja: ");
        dos.writeUTF("Notatka: ");
        dos.writeUTF("Czas: ");
        dos.writeUTF("Tekst: ");
        dos.writeUTF("Piosenka z Internetu");
        dos.writeUTF("Wprowadü adres piosenki i pobierz jπ");
        dos.writeUTF("Wprowadü URL wskazujπcy na opis piosenki");
        dos.writeUTF("Problem: ");
        dos.close();
        dos = new DataOutputStream(new FileOutputStream("D:/projects/java/ME/mobiKAR/mobiKAR/res/global/loc/en"));
        dos.writeUTF("OK");
        dos.writeUTF("Menu");
        dos.writeUTF("Quit");
        dos.writeUTF("Play");
        dos.writeUTF("My songs");
        dos.writeUTF("Settings");
        dos.writeUTF("Help");
        dos.writeUTF("About");
        dos.writeUTF("Info");
        dos.writeUTF("Download");
        dos.writeUTF("List from mobiKAR.net");
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
        dos.writeUTF("after");
        dos.writeUTF("active");
        dos.writeUTF("mine");
        dos.writeUTF("yours");
        dos.writeUTF("Advanced");
        dos.writeUTF("Preview (ms)");
        dos.writeUTF("Interline");
        dos.writeUTF("History lines");
        dos.writeUTF("Buffor lines");
        dos.writeUTF("Base line");
        dos.writeUTF("Displaying");
        dos.writeUTF("Gradient");
        dos.writeUTF("Wallpapper");
        dos.writeUTF("Show panel");
        dos.writeUTF("Auto");
        dos.writeUTF("Go");
        dos.writeUTF("If you want download a new song, please enter URL to karaoke file (with .KAR extension)");
        dos.writeUTF("Are You sure to delete all downloaded files?");
        dos.writeUTF("mobiKAR is a player files MIDI with lyrics - KARAOKE.\nMore at home page");
        dos.writeUTF("Creators");
        dos.writeUTF("Version");
        dos.writeUTF("Welcome in the mobile KARAOKE World!\nIf you wish sing with mobiKAR, you just choose the first option from MENU. After showing a screen choose option SING.\nThat is all!\nIn order to change lay-out or functionality you can enter to SETTINGS from MENU. There are find many settings, whose configure aplication.\nIf this text is not enought for yo, let you go to the WEB home page mobikar.net");
        dos.writeUTF("Connecting...");
        dos.writeUTF("Receiving...");
        dos.writeUTF("Only Latin chars");
        dos.writeUTF("•∆ £—”åèØπÊÍ≥ÒÛúüø");
        dos.writeUTF("ACELNOSZZacelnoszz");
        dos.writeUTF("Restore defaults");
        dos.writeUTF("Password");
        dos.writeUTF("Enter your password ");
        dos.writeUTF("Your login to mobikar.net is ");
        dos.writeUTF("Show");
        dos.writeUTF(" Application will login to server mobikar.net and download list of your songs witch you can download into application.");
        dos.writeUTF("Title: ");
        dos.writeUTF("Artist: ");
        dos.writeUTF("Lyrics: ");
        dos.writeUTF("Music: ");
        dos.writeUTF("Creator: ");
        dos.writeUTF("Version: ");
        dos.writeUTF("Note: ");
        dos.writeUTF("Time: ");
        dos.writeUTF("Text: ");
        dos.writeUTF("Song from Internet");
        dos.writeUTF("Enter song's address and download");
        dos.writeUTF("Enter URL that's contains song description");
        dos.writeUTF("Problem: ");
        dos.close();
    }
    
}
