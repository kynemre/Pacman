/*=====================================PACMAN=================================
 * Klasik Pacman oyununun benzer versiyonu
 * Tek ve iki kisilik secenekleri var.
 * Iki kisilik oyunda iki farkli pacman var. Biri cyan biri magenta
 * Oyun uc seviyeden olusuyor.
 * Her seviyede hayaletler var.
 * Ilk seviye klasik Pacman oyunu
 * Ikinci seviyede hareketli yemler var
 * Hareketli yemler Pacman'a ozgu. Yani Pacman sadece kendi ile ayni renkteki yemleri yiyebilir.
 * Son seviyede hem sabit hem hareketli yemler var.
 * Tek kisilik oyunda oyuncunun uc cani var.
 * Iki kisilik oyunda can yok, ilk bitiren kazaniyor.
 * Pacman oyuna ilk basladigi konumda iken hayaletler onu yiyemez.
 * Hayalete yakalandigin zaman Pacman ilk basladigi konuma geliyor ve hareketli yem yemis ise yediklerini kaybediyor.
 * Iki kisilik oyunun son bolumunde once hareketli yemler bitip daha sonra sabit yemler bitmis ise en cok yem toplayan kazanir.
 * Eger sabit yemler bitmis fakat hareketli yemler bitmemis ise toplanan yem sayisina bakilmadan hareketli yemi ilk bitiren kazanir. 
 */
package com.Pacman;
import acm.graphics.*;
import acm.program.*;
import acm.util.RandomGenerator;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

class Obje{
	Obje(){
		//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//Bilgisayar ekran boyutunu almak icin
		//		int PEN_EN = screenSize.width*4/5;//Bilgisayar ekran enini 4/5 ile carpar
		int PEN_EN = 1280;//Ekran boyutu degistiginde oyun tasarimi degistigi ve buglar olustugu icin sabit bir sayi verdim
		try{//Eger hayalet resmini bulamaz ise hayalet yerine kirmizi kutu koyar.
			gobject = new GImage("res/ghost.png");//Hayaleti tanimlar.
			((GImage) gobject).setSize(PEN_EN/48,PEN_EN/48);//Hayalet gorselini boyutlandirir.
		}
		catch (Exception e){
			gobject = new GRect(25,30);//Hayaleti tanimlar.
			((GRect)gobject).setFilled(true);
			((GRect)gobject).setColor(Color.RED);
		}
	}
	Obje(int renk){
		gobject = new GOval(16,16);//Hareketli yemleri olusturur.
		((GOval) gobject).setFilled(true);//Yemlerin icini doldurur.

		if(renk==1) {
			gobject.setColor(Color.CYAN);//Yemi Cyan rengine boyar.
		}else if(renk==2) {
			gobject.setColor(Color.MAGENTA);//Yemi Magenta rengine boyar.
		}
	}
	double getX() {
		return gobject.getX();//Yemin ya da hayaletin x konumunu dondurur.
	}
	double getY() {
		return gobject.getY();//yemin ya da hayaletin y konumunu dondurur.
	}
	int en=30;
	static int boy=30;
	int hiz_x;
	int hiz_y;
	int adim=0;
	GObject gobject;//Yem ya da hayalet icin obje olusturur.
}

public class Pacman extends GraphicsProgram {

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//Kullanilan bilgisayarin ekran boyutlarini verir.
	RandomGenerator rgen = RandomGenerator.getInstance(); //Rastgele sayi alabilmek icin rgen olusturur. 
	GLabel tek, //Tek kisilik secim metni
	cift,	//Cift kisilik secim metni
	baslik,	//===PACMAN=== basligi
	kazanan,//Kazanani belirten metin
	devam,	//Devam etmek icin tiklayin metni
	can_metni,//Kalan can metni
	yem_metni;//Kac yem yendigini belirten metin
	GObject menu_don;
	GOval pacman, pacman2; // Pacman'ler olusturulur.
	public float //PEN_EN = screenSize.width*4/5,
	PEN_EN = 1280,
	PEN_YUK = PEN_EN/16*9,
	mhsX = 50, mhsY = 75, mheX, mheY;//Oyun alaninin baslangic ve bitis kordinatlari

	int vk=0, vg=0, vd=0, vb=0,//Pacman1'in hiz degerleri: Sirasiyla kuzey, guney, dogu ve bati
			vk2=0, vg2=0, vd2=0, vb2=0,//Pacman2'in hiz degerleri
			secim=0, //Kullanicinin tek ve cift secimini kontrol icin.
			can=3, //Pacman'in canlari
			yem_sayisi1=0,//Pacman1'in yedigi toplam yem sayisi
			yem_sayisi2=0,//Pacman2'in yedigi toplam yem sayisi
			gyem1=0, gyem2=0,//Pacman1'in ve pacman2'nin yedigi toplam hareketli yem sayisi yem sayisi. Hayalete yem oldugu zaman kaybettigi hareketli yemleri toplam yemden cikarmak icin.
			seviye=0, //Oyunun kacinci seviyede oldugunu belirlemek icin
			skorC=0, skorM=0; // Cift kisilik oyunda skoru belirlemek icin
	boolean ilkAcilis = true, //Sadece ilk acilista true degerini alir. Oyun  bittikten sonra tekrar ekranin kuculmesini engellemek icin
			menu_git=false;
	GRect engel1, engel2, engel3, engel4, engel5, engel6, engel7, engel8, engel9, engel10, engel11, engel12, engel13, engel14;//Engeller tanimlanir. Tek bir GRect ile de tanimlanabilirdi.

	ArrayList<GRect>engel = new ArrayList<GRect>();//Engelleri hafizaya alan sonsuz dizi tanimlanir.
	ArrayList<Obje>hayaletler = new ArrayList<Obje>();//Hayaletleri hafizaya alan sonsuz dizi tanimlanir.
	ArrayList<GOval>yemler = new ArrayList<GOval>();//Sabit yemleri hafizaya alan sonsuz dizi tanimlanir.
	ArrayList<Obje>hareketli_yemler_cyan = new ArrayList<Obje>();//Hareketli yemlerden cyan renkte(Pacman1 icin olusturulan yemler) olanlari hafizaya alan sonsuz dizi tanimlanir.
	ArrayList<Obje>hareketli_yemler_mgnt = new ArrayList<Obje>();//Hareketli yemlerden magenta renkte(Pacman2 icin olusturulan yemler) olNLri hafizaya alan sonsuz dizi tanimlanir.
	public static void main(String[] args) {//jar dosyasi olarak cikartabilmek icin main kodu
		(new Pacman()).start(args);
	}
	public void run() {
		/*if(screenSize.width<=1080) {//Bilgisayar ekran eni 1080'den kucuk ise sabitlenir 
			PEN_EN = screenSize.width;
			PEN_YUK = PEN_EN/16*9;
		}*/
		//println("PEN_EN: "+PEN_EN+"\nPEN_YUK: "+PEN_YUK+"\ngetWidth(): "+PEN_EN+"\ngetHeight(): "+getHeight());//Pencerenin menulu ve menusuz boyutu
		if(ilkAcilis) {
			setSize((int)PEN_EN, (int)PEN_YUK+50);//Acilan pencereyi boyutlandirir
			PEN_YUK = getHeight(); //PEN_YUK buyuklugune pencerenin ust menu kismida dahil fakat biz menunun altindaki kisimda calisacagimiz icin degeri degistirmemiz gerekir.
			mheX = PEN_EN-50;
			mheY = PEN_YUK-25;
			ilkAcilis=false;
		}

		setBackground(Color.BLACK); //Arkaplani siyah yapar.
		addKeyListeners();
		addMouseListeners();

		try {//Eger home logosunu bulamaz ise logo yerine menu yazar.
			menu_don = new GImage("res/home.png");
			((GImage)menu_don).setSize(40,40);
		}catch (Exception e) {
			menu_don = new GLabel("Menu");
			((GLabel)menu_don).setFont("ARIAL-BOLD-15");
			((GLabel)menu_don).setColor(Color.WHITE);
			((GLabel)menu_don).setLocation(5,20);
		}

		if(seviye == 0 || seviye == 4 || menu_git) {
			secim=0;
			can=3;
			skorC=skorM=0;
			menu();//Acilis ekranindaki tek kisilik cift kisilik seceneklerini sunar.
			remove(tek);//Acilis ekranindaki tek kisilik cift kisilik seceneklerini kaldirir.
			remove(cift);
			remove(baslik);
			seviye = 1;
			menu_git=false;
		}
		metinleri_tanimla(); //Oyundaki yazilari tanimlar.
		add(menu_don);
		duvarlariEkle();//Oyun basladigindaki mavi duvarlari ekler. 
		pacman();//Pacmani oyuna ekler.
		seviye(seviye);
		pacman.sendToFront();//Pacmani en one ekler.
		if(secim==1) {
			yem_metni.setLabel("Toplanan Yem Sayisi: " + Integer.toString(yem_sayisi1));//Cyan kalan yem sayisini ayarlar.
			add(can_metni, PEN_EN-can_metni.getWidth()-50, 50);//Kalan yem sayisini ekler.
		}else if(secim==2){
			yem_metni.setLabel("Cyan " + Integer.toString(yem_sayisi1) + " adet yem toplandi   |   Magenta " + Integer.toString(yem_sayisi2) + " adet yem toplandi");//Cyan ve Magenta kalan yem sayisini ayarlar.
		}
		add(yem_metni, 50, 50);//Toplanan yem sayisini oyuna ekler.

		animasyon();//Oyundaki animasyonlari ayarlar.
		add(kazanan,getWidth()/2-kazanan.getWidth()/2,getHeight()/2-kazanan.getHeight()*2);//Oyun bittigindeki kazanan yazisini oyuna ekler.
		add(devam,getWidth()/2-devam.getWidth()/2,getHeight()/2+devam.getHeight()*2);//Oyun bittigindeki devam et yazisini oyuna ekler.
		waitForClick();//Fare ile tiklayincaya kadar bekle
		sifirla();//Hiz can gibi tum degerleri sifirlar.
		run();//Oyunu yeniden baslatir.
	}
	void animasyon() {
		while(true) {
			if(kuzey_acik_mi(pacman.getX(),pacman.getY())) {//Pacman kuzeye giderken her bir adimindan sonra kuzey bos mu diye kontrol eder.
				pacman.move(0,vk);
			}
			if(guney_acik_mi(pacman.getX(),pacman.getY())) {
				pacman.move(0, vg);
			}
			if(dogu_acik_mi(pacman.getX(),pacman.getY())) {
				pacman.move(vd, 0);
			}
			if(bati_acik_mi(pacman.getX(),pacman.getY())) {
				pacman.move(vb, 0);
			}
			for(int i=0; i<hayaletler.size(); i++) {//Hayaletleri hareket ettirir.
				hayaletler.get(i).gobject.move(hayaletler.get(i).hiz_x,hayaletler.get(i).hiz_y);
				yon_belirle(hayaletler.get(i));
			}
			yem_ye(pacman);//Sabit yemleri yer
			for(int i=0; i<hareketli_yemler_cyan.size(); i++) {//Cyan yemleri hareket ettirir ve yem olmasini saglar.
				hareketli_yemler_cyan.get(i).gobject.move(hareketli_yemler_cyan.get(i).hiz_x,hareketli_yemler_cyan.get(i).hiz_y);
				yon_belirle(hareketli_yemler_cyan.get(i));
				yem_ye(hareketli_yemler_cyan.get(i));
			}
			for(int i=0; i<hayaletler.size(); i++) {
				yem_ol(hayaletler.get(i));//Pacman'in yem olma durumunun kontrol eder.
			}
			if(secim==2) {//Secim 2 oyunun cift kisilik oyun modunda oldugunu belirtir
				if(kuzey_acik_mi(pacman2.getX(),pacman2.getY())) {//2. Pacman'in kuzeye giderken her bir adimindan sonra kuzey bos mu diye kontrol eder.
					pacman2.move(0,vk2);
				}
				if(guney_acik_mi(pacman2.getX(),pacman2.getY())) {
					pacman2.move(0, vg2);
				}
				if(dogu_acik_mi(pacman2.getX(),pacman2.getY())) {
					pacman2.move(vd2, 0);
				}
				if(bati_acik_mi(pacman2.getX(),pacman2.getY())) {
					pacman2.move(vb2, 0);
				}
				yem_ye(pacman2);
				for(int i=0; i<hareketli_yemler_mgnt.size(); i++) {
					hareketli_yemler_mgnt.get(i).gobject.move(hareketli_yemler_mgnt.get(i).hiz_x,hareketli_yemler_mgnt.get(i).hiz_y);
					yon_belirle(hareketli_yemler_mgnt.get(i));
					yem_ye(hareketli_yemler_mgnt.get(i));
				}

			}
			if(//Eger pacman bir bug sonucu oyunda hareket edemeyecek bir yere gelirse onu ilk dogdugu yere gonderir.
					!kuzey_acik_mi(pacman.getX(),pacman.getY())
					&&!guney_acik_mi(pacman.getX(),pacman.getY())
					&&!dogu_acik_mi(pacman.getX(),pacman.getY())
					&&!bati_acik_mi(pacman.getX(),pacman.getY())
					) {
				pacman.setLocation(mhsX+(mheX-mhsX)/2-pacman.getWidth()/2, mhsY+(mheY-mhsY)/2-pacman.getHeight()/2);
				vk=vg=vd=vb=0;
			}//Eger 2. Pacman bir bug sonucu oyunda hareket edemeyecek bir yere gelirse onu ilk dogdugu yere gonderir.
			if(secim == 2) {
				if(		!kuzey_acik_mi(pacman2.getX(),pacman2.getY())
						&&!guney_acik_mi(pacman2.getX(),pacman2.getY())
						&&!dogu_acik_mi(pacman2.getX(),pacman2.getY())
						&&!bati_acik_mi(pacman2.getX(),pacman2.getY())
						) {
					pacman2.setLocation(mhsX+(mheX-mhsX)/2-pacman2.getWidth()/2, mhsY+(mheY-mhsY)/2-pacman2.getHeight()/2);
					vk2=vg2=vb2=vd2=0;
				}
			}
			if(secim==1 && yem_sayisi1 == yemler.size()+hareketli_yemler_cyan.size()) {
				//Tek kisilik oyunda bolum tamamlandiginda ve oyun bittiginde mesajlari degistirir.
				if(seviye < 3) {
					kazanan.setLabel("Seviye Tamamlandi");
				}else {
					kazanan.setLabel("Oyun Bitti! Kazandin");
				}
				seviye++;//Bolum bittigi icin seviye arttirir.
				break;//Animasyon dongusu icindeki break komutlari tum animasyonlari durdurur.
			}
			boolean yemKaldimi = false,//Beyaz yemlerin hepsinin toplanmasini kontrol eder. 
					cyanKazandi = true, //Cyan yemlerin hepsinin toplanmasini kontrol eder.
					mgntKazandi = true;//Magenta yemlerin hepsinin toplanmasini kontrol eder.
			/*
			 * Iterator Kullanimi
			 * 
			 * for(Listenin Turu(int, String, GRect vb.) Degisken ismi(i,j, siradaki vb.) : Liste Adi ){}
			 * 
			 */
			for(GOval siradaki : yemler) {//Iterator ile yemler listesinin icinde dolasir.
				if(siradaki.isVisible()) {//Siradaki liste elemaninin gorunmez olup olmadigini kontrol eder.
					//Yemler yenildigi zaman gorunmez oluyorlar ve (0, 0) konumuna tasiniyorlar
					yemKaldimi = true;//Eger gorunur yem varsa tum yemler yenmemis demektir
				}
			}
			for(Obje siradaki : hareketli_yemler_cyan) {//Usteki for dongusu ile ayni islev fakat Cyan renkteki yemleri kontrol eder.
				if(siradaki.gobject.isVisible()) {
					cyanKazandi = false;
				}
			}
			for(Obje siradaki : hareketli_yemler_mgnt) {//Usteki iki for dongusu ile ayni islev fakat magenta renkteki yemleri kontrol eder.
				if(siradaki.gobject.isVisible()) {
					mgntKazandi = false;
				}
			}
			if(secim==2 && !yemKaldimi) {//Iki kisilik oyun modunda ise ve beyaz yemler bitmis ise kosula girer.
				if(cyanKazandi && !mgntKazandi) {//Cyan yemler bitmis ve magenta yemler bitmemis ise kosula girer.
					if(seviye < 3) {//Bu kosul, Oyun 3 bolumden olustugundan ve son bolum bittikten sonra farklı mesaj yazdigindan var.
						kazanan.setLabel("Cyan renkli oyuncu seviyeyi tamamladi");
						skorC++;//Cyan Pacman'in puanini arttirir. Tum bolumler bittikten sonra kazanini belirlemek icin.
					}
					seviye++;//Bolum bittigi icin diger bolume gecer.
					break;//Animasyon dongusu icindeki break komutlari tum animasyonlari durdurur.
				}else if(!cyanKazandi && mgntKazandi){//Magenta yemler bitmis ve cyan yemler bitmemis ise kosula girer.
					if(seviye < 3) {//Bu kosul, Oyun 3 bolumden olustugundan ve son bolum bittikten sonra farklı mesaj yazdigindan var
						kazanan.setLabel("Magenta renkli oyuncu seviyeyi tamamladi");
						skorM++;//Magenta Pacman'in puanini arttirir. Tum bolumler bittikten sonra kazanini belirlemek icin.
					}

					seviye++;//Bolum bittigi icin seviye arttirir.
					break;//Animasyon dongusu icindeki break komutlari tum animasyonlari durdurur.
				}
			}
			if(secim==2 && !yemKaldimi && (cyanKazandi || mgntKazandi)) {//Iki kisilik oyun modunda, tum beyaz yemler bitmis ve cyan ya da magenta renklerden en az biri bitmis ise kosul gerceklesir.
				if(yem_sayisi1 > yem_sayisi2) {//yem_sayisi1 = Cyan Pacman'in topladigi yemler, yem_sayisi2 = Magenta Pacman'in topladigi yemler. 
					if(seviye < 3) {//Bu kosul, Oyun 3 bolumden olustugundan ve son bolum bittikten sonra farklı mesaj yazdigindan var
						kazanan.setLabel("Cyan renkli oyuncu seviyeyi tamamladi");
					}
					skorC++;//Cyan Pacman'in puanini arttirir. Tum bolumler bittikten sonra kazanini belirlemek icin.
				}else if(yem_sayisi1 < yem_sayisi2) {//yem_sayisi1 = Cyan Pacman'in topladigi yemler, yem_sayisi2 = Magenta Pacman'in topladigi yemler. 
					if(seviye < 3) {//Bu kosul, Oyun 3 bolumden olustugundan ve son bolum bittikten sonra farklı mesaj yazdigindan var
						kazanan.setLabel("Magenta renkli oyuncu seviyeyi tamamladi");
					}
					skorM++;//Magenta Pacman'in puanini arttirir. Tum bolumler bittikten sonra kazanini belirlemek icin.
				}else {//Eger toplanan yem sayilari esit ise buradaki kodlar calisir.
					if(seviye < 3) {//Bu kosul, Oyun 3 bolumden olustugundan ve son bolum bittikten sonra farklı mesaj yazdigindan var
						kazanan.setLabel("Bu seviye berabere bitti");
					}
				}
				seviye++;//Bolum bittigi icin seviye arttirir.
				break;//Animasyon dongusu icindeki break komutlari tum animasyonlari durdurur.
			}
			if(seviye==3 && secim==2) {//Iki kisilik oyun modunda tum bolumler tamamlanmis ise kosul gerceklesir
				if(skorC > skorM) {//skorC = Cyan Pacman'in kazandigi bolum sayisi skorM = Magenta Pacman'in kazandigi bolum sayisi 
					kazanan.setLabel("Oyun Bitti! Cyan renkli oyuncu kazandi");
				}else if(skorC < skorM) {
					kazanan.setLabel("Oyun Bitti! Magenta renkli oyuncu kazandi");
				}else {
					kazanan.setLabel("Berabere bitti! Dostluk Kazansin.");
				}
			}
			if(can==0 && secim==1) {//Tek kisilik oyunda tum canlarini kaybederse bu kosul gerceklesir.
				kazanan.setLabel("Oyun Bitti! Kaybettin.");
				can = 3;//Cani sifirlar
				seviye=0;//Seviyeyi sifirlar
				break;//Animasyon dongusu icindeki break komutlari tum animasyonlari durdurur.
			}
			if(menu_git) {//Eger kullanici sol ustteki menu tusuna basarsa bu kosul gerceklesir.
				break;//Animasyon dongusu icindeki break komutlari tum animasyonlari durdurur.
			}
			pause(7);//Animasyonu yavaslatmak icin
		}
	}
	void menu() {//Menudeki yazilari tanimlar ve ekler
		baslik = new GLabel("===========PACMAN===========");
		baslik.setFont("ARIAL-BOLD-55");
		baslik.setColor(Color.WHITE);

		tek = new GLabel("=>Tek Kisilik<=");
		tek.setFont("ARIAL-BOLD-45");
		tek.setColor(Color.WHITE);

		cift = new GLabel("=>Cift Kisilik<=");
		cift.setFont("ARIAL-BOLD-45");
		cift.setColor(Color.WHITE);

		while(secim==0) {//Secim yapilmadigi surece ekranda tutmak icin
			add(baslik,getWidth()/2-baslik.getWidth()/2,getHeight()/2-baslik.getHeight()*2);//Oyunun basindaki ===PACMAN=== basligini ekler.
			add(tek,getWidth()/2-tek.getWidth()/2,getHeight()/2-tek.getHeight()/2);//Oyunun basindaki =>Tek kisilik<= secenegini ekler.
			add(cift,getWidth()/2-cift.getWidth()/2,getHeight()/2+cift.getHeight()/2);//Oyunun basindaki =>Cift kisilik<= secenegini ekler.
		}
		pause(100);//100 milisaniye bekler. Tum bolumler bittikten sonra yanlislikla secim yapmamak icin
	}
	void sifirla() {//Tum bolumler bittiginde degerleri sifirlar
		vk=vg=vd=vb=0; //Cyan pacmanin hizini sifirlar
		vk2=vg2=vd2=vb2=0;//Magenta pacmanin hizini sifirlar
		yem_sayisi1=yem_sayisi2=gyem1=gyem2=0;//Oyunda kalan yem sayilarini sayan sayaci sifirlar.
		removeAll();//Tum objeleri ekrandan kaldirir.
		hayaletler.clear();//Hayaletler listesini sifirlar.
		yemler.clear();//Sabit yemlerin listesini sifirlar.
		hareketli_yemler_cyan.clear();//Hareketli cyan yemlerin listesini sifirlar.
		hareketli_yemler_mgnt.clear();//Hareketli magenta yemlerin listesini sifirlar.
	}
	void seviye(int blm) {//Her seviyeye ozel metinler, hayaletler ve yemler ekler.//blm degiskeni seviyeyi belirtir.
		GLabel aciklama = new GLabel("Tum yemleri topla.");
		aciklama.setFont("ARIAL-BOLD-30");
		aciklama.setColor(Color.GREEN);
		GLabel aciklama2 = new GLabel("");
		aciklama2.setFont("ARIAL-BOLD-30");
		aciklama2.setColor(Color.GREEN);
		if(blm == 1) {
			if(secim==2) {
				aciklama.setLabel("En fazla yem toplayan kazanir.");
			}
			yem();//Sabit yemleri oyuna ekler.
			ghost(6);//Hayaletleri oyuna ekler.
		}else if(blm == 2) {
			aciklama.setLabel("Tum hareketli yemleri topla.");
			aciklama2.setLabel("Eger yem olursan topladigin tum hareketli yemlerini kaybedersin.");
			hareketli_yem(1,6);///Alti adet cyan renkte hareketli yem ekler
			if(secim == 2) {
				aciklama.setLabel("Hareketli yemlerini ilk bitiren kazanir.");
				aciklama2.setLabel("Eger yem olursan topladigin tum hareketli yemlerini kaybedersin.");
				hareketli_yem(2,6);//Alti adet magenta renkte hareketli yem ekler
			}
			ghost(6);//Hayaletleri oyuna ekler.
		}else if(blm == 3) {
			aciklama.setLabel("Tum yemleri topla.");
			aciklama2.setLabel("");
			yem();
			hareketli_yem(1,8);//Sekiz adet cyan renkte hareketli yem ekler
			if(secim == 2) {
				aciklama.setLabel("En cok yem toplayan kazanir. Beyaz yemler bittikten ");
				aciklama2.setLabel("sonra rakibin renkli yemi var ise rakip kaybeder.");
				hareketli_yem(2,8);//Sekiz adet magenta renkte hareketli yem ekler
			}
			ghost(10);//Hayaletleri oyuna ekler.
		}
		add(aciklama,getWidth()/2-aciklama.getWidth()/2,getHeight()/2-aciklama.getHeight()*2);
		add(aciklama2,getWidth()/2-aciklama2.getWidth()/2,getHeight()/2-aciklama2.getHeight());
		add(devam,getWidth()/2-devam.getWidth()/2,getHeight()/2+devam.getHeight()*4);
		waitForClick();
		remove(aciklama);
		remove(aciklama2);
		remove(devam);
	}
	private void duvarlariEkle() {//Engelleri ekler.
		GRect kenar= new GRect(PEN_EN-100,PEN_YUK-100);//Oyun icin cerceve olusturur.
		kenar.setColor(Color.BLUE);//Cerceveyi mavi yapar.
		add(kenar,mhsX,mhsY);//Cerceveyi oyuna ekler.

		float yol_gen = PEN_YUK/10;// Oyundaki hareketli objeler icin yol genisligi tanimlanir.

		engel1= new GRect(PEN_EN/4, PEN_YUK/12);//Engeller olusturulur.
		engel2= new GRect(PEN_EN/4, PEN_YUK/12);
		engel3= new GRect(PEN_EN/4, PEN_YUK/12);
		engel4= new GRect(PEN_EN/4, PEN_YUK/12);
		engel5= new GRect(PEN_YUK/4, PEN_YUK/6);
		engel6= new GRect(PEN_YUK/4, PEN_YUK/6);
		engel7= new GRect(PEN_YUK/6, PEN_YUK/4);
		engel8= new GRect(PEN_YUK/6, PEN_YUK/4);
		engel9= new GRect(PEN_EN/9, PEN_YUK/14);
		engel10= new GRect(PEN_EN/9, PEN_YUK/14);
		engel11= new GRect(PEN_EN/9, PEN_YUK/14);
		engel12= new GRect(PEN_EN/9, PEN_YUK/14);
		engel13= new GRect(PEN_EN/16, PEN_YUK/4);
		engel14= new GRect(PEN_EN/16, PEN_YUK/4);

		engel1.setColor(Color.BLUE);//Engellerin cizgileri mavi renge boyanir.
		engel2.setColor(Color.BLUE);
		engel3.setColor(Color.BLUE);
		engel4.setColor(Color.BLUE);
		engel5.setColor(Color.BLUE);
		engel6.setColor(Color.BLUE);
		engel7.setColor(Color.BLUE);
		engel8.setColor(Color.BLUE);
		engel9.setColor(Color.BLUE);
		engel10.setColor(Color.BLUE);
		engel11.setColor(Color.BLUE);
		engel12.setColor(Color.BLUE);
		engel13.setColor(Color.BLUE);
		engel14.setColor(Color.BLUE);

		add(engel1, mhsX+yol_gen, mhsY+yol_gen);//Engeller eklenir.
		add(engel2, mheX-(yol_gen+engel1.getWidth()), engel1.getY());
		add(engel3, engel1.getX(), mheY-(yol_gen+engel3.getHeight()));
		add(engel4, engel2.getX(), engel3.getY());
		add(engel5, (mheX-mhsX)/2 - engel5.getWidth()/2 + mhsX, mhsY);
		add(engel6, engel5.getX(), mheY-engel6.getHeight());
		add(engel7, mhsX, (mheY-mhsY)/2-engel7.getHeight()/2+mhsY);
		add(engel8, mheX-engel8.getWidth(), engel7.getY());
		add(engel13, engel5.getX()-engel13.getWidth()/2, engel7.getY());
		add(engel14, engel5.getX()+engel5.getWidth()-engel14.getWidth()/2, engel7.getY());
		add(engel9, mhsX+engel7.getWidth()+(engel13.getX()-(mhsX+engel7.getWidth()))/2-engel9.getWidth()/2, engel7.getY());
		add(engel10, engel9.getX(), engel7.getY()+engel7.getHeight()-engel10.getHeight());
		add(engel11, engel14.getX()+engel14.getWidth()+(mheX-engel8.getWidth()-(engel14.getX()+engel14.getWidth()))/2-engel11.getWidth()/2, engel8.getY());
		add(engel12, engel11.getX(), engel8.getY()+engel8.getHeight()-engel12.getHeight());

		engel.add(engel1);//Engelleri engel listesine ekler.
		engel.add(engel2);
		engel.add(engel3);
		engel.add(engel4);
		engel.add(engel5);
		engel.add(engel6);
		engel.add(engel7);
		engel.add(engel8);
		engel.add(engel9);
		engel.add(engel10);
		engel.add(engel11);
		engel.add(engel12);
		engel.add(engel13);
		engel.add(engel14);
	}
	private void pacman(){//Pacmanleri olusturur.
		pacman = new GOval(PEN_EN/48,PEN_EN/48);//Pacman tanimlanir.
		pacman.setFilled(true);
		pacman.setColor(Color.CYAN);
		add(pacman,mhsX+(mheX-mhsX)/2-pacman.getWidth()/2, mhsY+(mheY-mhsY)/2-pacman.getHeight()/2);
		if(secim==2) {
			pacman2 = new GOval(PEN_EN/48,PEN_EN/48);//Cift kisilik oyun icin ikinci Pacman tanimlanir.
			pacman2.setFilled(true);
			pacman2.setColor(Color.MAGENTA);
			add(pacman2,mhsX+(mheX-mhsX)/2-pacman.getWidth()/2, mhsY+(mheY-mhsY)/2-pacman2.getHeight()/2);
		}
	}
	void ghost(int miktar) {//Hayaletleri olusturur.
		double konum_x1 = engel9.getX()+(engel9.getX()+engel9.getWidth()-engel9.getX())/2,
				konum_x2 = engel11.getX()+(engel11.getX()+engel11.getWidth()-engel11.getX())/2,
				konum_y1 = engel10.getY()-(engel10.getY()-engel9.getY()-engel9.getHeight())/2-Obje.boy/2,
				konum_y2 = engel12.getY()-(engel12.getY()-engel11.getY()-engel11.getHeight())/2-Obje.boy/2
				;
		for(int i=0; i<miktar/2; i++) {
			ghost_olustur(konum_x1, konum_y1);
		}
		for(int i=0; i<miktar/2; i++) {
			ghost_olustur(konum_x2, konum_y2);
		}
	}void ghost_olustur(double konum_x, double konum_y) {
		Obje ghost=new Obje();//Hayaletler tanimlanir ve oyuna eklenir.
		add(ghost.gobject, konum_x, konum_y);
		hayaletler.add(ghost);
	}
	void yem() {
		for(int i=(int)mhsX+20; i<mheX-20; i+=25) {
			for(int j=(int)mhsY+15; j<mheY-15; j+=25) {
				boolean engelVarmi = false;
				for(int k=0; k<engel.size(); k++) {
					GRect e = engel.get(k);
					if((i>e.getX()-20 && i<e.getX()+e.getWidth()+20
							&& j>e.getY()-15 && j<e.getY()+e.getHeight()+15
							)) {
						engelVarmi=true;
					}
				}
				if(!engelVarmi) {
					yem_ekle(i,j);
				}
			}
		}
	}private void yem_ekle(int x, int y) {
		GOval yem = new GOval(5,5);
		yem.setFilled(true);
		yem.setColor(Color.WHITE);
		add(yem, x, y);
		yemler.add(yem);
	}
	void hareketli_yem(int renk, int miktar) {//Hareketli yemleri olusturur. renk = 1 cyan renk = 2 magenta demek
		double 	konum_x1 = engel9.getX()+(engel9.getX()+engel9.getWidth()-engel9.getX())/2,
				konum_x2 = engel11.getX()+(engel11.getX()+engel11.getWidth()-engel11.getX())/2, 
				konum_y1 = engel10.getY()-(engel10.getY()-engel9.getY()-engel9.getHeight())/2-PEN_EN/80/2,
				konum_y2 = engel12.getY()-(engel12.getY()-engel11.getY()-engel11.getHeight())/2-PEN_EN/80/2
				;
		if(renk==1) {//Bu kosul ile olusturulan yemler Cyan renginde olur.
			for(int i=0; i<miktar/2; i++) {
				yem_olustur(1,konum_x1, konum_y1);
			}
			for(int i=0; i<miktar/2; i++) {
				yem_olustur(1,konum_x2, konum_y2);
			}
		}
		if(secim==2 && renk==2) {//Bu kosul ile olusturulan yemler Magenta renginde olur
			for(int i=0; i<miktar/2; i++) {
				yem_olustur(2,konum_x1, konum_y1);
			}
			for(int i=0; i<miktar/2; i++) {
				yem_olustur(2,konum_x2, konum_y2);
			}
		}
	}void yem_olustur(int renk, double konum_x, double konum_y) {
		Obje yem=new Obje(renk);//Hayaletler tanimlanir ve oyuna eklenir.
		add(yem.gobject, konum_x, konum_y);
		if(renk == 1) {
			hareketli_yemler_cyan.add(yem);
		}else if(renk == 2) {
			hareketli_yemler_mgnt.add(yem);
		}
	}
	void yem_ye(GOval pac) {
		double x = pac.getX(),
				y = pac.getY();
		for(int i=0; i<yemler.size(); i++) {
			GOval e = yemler.get(i);
			if(e.getX()>x-10 && e.getX()<x+pacman.getWidth()+10
					&& e.getY()>y-8 && e.getY()<y+pacman.getHeight()+8 
					&& !(pac.getX() ==mhsX+(mheX-mhsX)/2-pac.getWidth()/2
					&& pac.getY() == mhsY+(mheY-mhsY)/2-pac.getHeight()/2	)

					) {
				e.setVisible(false);
				e.setLocation(0,0);
				if(pac.getColor() == Color.CYAN) {
					yem_sayisi1++;
				}else if(pac.getColor() == Color.MAGENTA) {
					yem_sayisi2++;
				}
			}
		}
		bilgilendirme();
	}
	void yem_ye(Obje obje) {
		double x = obje.getX(),
				y = obje.getY();
		if(((x>pacman.getX() 
				&& x<pacman.getX()+pacman.getWidth()
				&& y>pacman.getY()
				&& y<pacman.getY()+pacman.getHeight())
				||
				(pacman.getX()>x
						&&pacman.getX()<x+obje.en
						&& pacman.getY()>y
						&& pacman.getY()<y+Obje.boy))  && obje.gobject.getColor()==Color.CYAN 
						&& !(pacman.getX() ==mhsX+(mheX-mhsX)/2-pacman.getWidth()/2
						&& pacman.getY() == mhsY+(mheY-mhsY)/2-pacman.getHeight()/2	)

				) {
			obje.gobject.setLocation(0,0);
			obje.gobject.setVisible(false);
			yem_sayisi1++;
			gyem1++;
		}
		if(secim==2) {
			if(((x>pacman2.getX() 
					&& x<pacman2.getX()+pacman2.getWidth()
					&& y>pacman2.getY()
					&& y<pacman2.getY()+pacman2.getHeight())
					||
					(pacman2.getX()>x
							&&pacman2.getX()<x+obje.en
							&& pacman2.getY()>y
							&& pacman2.getY()<y+Obje.boy)) && obje.gobject.getColor()==Color.MAGENTA 
							&& !(pacman2.getX() ==mhsX+(mheX-mhsX)/2-pacman2.getWidth()/2
							&& pacman2.getY() == mhsY+(mheY-mhsY)/2-pacman2.getHeight()/2	)

					) {
				obje.gobject.setLocation(0,0);
				obje.gobject.setVisible(false);
				yem_sayisi2++;
				gyem2++;
			}
		}
		bilgilendirme();
	}
	void bilgilendirme() {
		if(secim==1) {
			yem_metni.setLabel("Toplanan Yem Sayisi: " + Integer.toString(yem_sayisi1));
		}else if(secim==2){
			yem_metni.setLabel("Cyan " + Integer.toString(yem_sayisi1) + " adet yem toplandi   |   Magenta " + Integer.toString(yem_sayisi2) + " adet yem toplandi");
		}
	}
	void yem_ol(Obje obje) {
		double x = obje.getX(),
				y = obje.getY();
		if(((x>pacman.getX() 
				&& x<pacman.getX()+pacman.getWidth()
				&& y>pacman.getY()
				&& y<pacman.getY()+pacman.getHeight())
				||
				(pacman.getX()>x
						&&pacman.getX()<x+obje.en
						&& pacman.getY()>y
						&& pacman.getY()<y+Obje.boy))
				&& !(pacman.getX() ==mhsX+(mheX-mhsX)/2-pacman.getWidth()/2
				&& pacman.getY() == mhsY+(mheY-mhsY)/2-pacman.getHeight()/2	)
				) {
			pacman.setLocation(mhsX+(mheX-mhsX)/2-pacman.getWidth()/2, mhsY+(mheY-mhsY)/2-pacman.getHeight()/2);
			vk=vg=vb=vd=0;
			for(int i=0; i<hareketli_yemler_cyan.size(); i++) {
				remove(hareketli_yemler_cyan.get(i).gobject);
			}
			hareketli_yemler_cyan.clear();
			if(seviye == 2) {
				hareketli_yem(1,6);
			}else if(seviye == 3) {
				hareketli_yem(1,8);
			}
			can--;
			can_metni.setLabel("Can: " + Integer.toString(can));
			yem_sayisi1-=gyem1;
			gyem1=0;
		}
		if(secim==2) {
			if(((x>pacman2.getX() 
					&& x<pacman2.getX()+pacman2.getWidth()
					&& y>pacman2.getY()
					&& y<pacman2.getY()+pacman2.getHeight())
					||
					(pacman2.getX()>x
							&&pacman2.getX()<x+obje.en
							&& pacman2.getY()>y
							&& pacman2.getY()<y+Obje.boy))
					&& !(pacman2.getX() ==mhsX+(mheX-mhsX)/2-pacman2.getWidth()/2
					&& pacman2.getY() == mhsY+(mheY-mhsY)/2-pacman2.getHeight()/2	)
					) {
				pacman2.setLocation(mhsX+(mheX-mhsX)/2-pacman2.getWidth()/2, mhsY+(mheY-mhsY)/2-pacman2.getHeight()/2);
				vk2=vg2=vb2=vd2=0;
				for(int i=0; i<hareketli_yemler_mgnt.size(); i++) {
					remove(hareketli_yemler_mgnt.get(i).gobject);
				}
				hareketli_yemler_mgnt.clear();
				if(seviye == 2) {
					hareketli_yem(2,6);
				}else if(seviye == 3) {
					hareketli_yem(2,8);
				}
				yem_sayisi2-=gyem2;
				gyem2=0;
			}
		}
	}
	public void keyPressed(KeyEvent k) {
		int keyCode = k.getKeyCode();
		if (keyCode == KeyEvent.VK_UP) {
			vk=-2; vg=vd=vb=0;
		} else if (keyCode == KeyEvent.VK_DOWN) {
			vg=2; vk=vd=vb=0;
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			vd=2;	vk=vg=vb=0;
		} else if (keyCode == KeyEvent.VK_LEFT) {
			vb=-2; vk=vg=vd=0;
		}
		if(secim==2) {
			if (keyCode == KeyEvent.VK_W) {
				vk2=-2; vg2=vd2=vb2=0;
			} else if (keyCode == KeyEvent.VK_S) {
				vg2=2; vk2=vd2=vb2=0;
			} else if (keyCode == KeyEvent.VK_D) {
				vd2=2;	vk2=vg2=vb2=0;
			} else if (keyCode == KeyEvent.VK_A) {
				vb2=-2; vk2=vg2=vd2=0;
			}
		}
	}
	public void mouseClicked(MouseEvent m) {
		//Mouse her tiklandiginda bu metod cagrilir.
		int x = m.getX();
		int y = m.getY();

		if(x>tek.getX()
				&& x<tek.getX()+tek.getWidth()
				&& y>tek.getY()-tek.getHeight()
				&& y<tek.getY()
				&& secim==0
				) {
			secim=1;
		}
		if(x>cift.getX()
				&& x<cift.getX()+cift.getWidth()
				&& y>cift.getY()-cift.getHeight()
				&& y<cift.getY()
				&& secim==0
				) {
			secim=2;
		}
		if(x<50 && y<50) {
			menu_git=true;
		}
	}
	boolean onum_acik_mi_x(Obje obje){
		double x = obje.getX(),
				y = obje.getY();
		if(obje.hiz_x==1) {
			return dogu_acik_mi(x, y);
		}else if(obje.hiz_x==-1) {
			return bati_acik_mi(x,y);
		}else {
			return true;
		}
	}
	boolean onum_acik_mi_y(Obje ghost){
		double x = ghost.getX(),
				y = ghost.getY();
		if(ghost.hiz_y==1) {
			return guney_acik_mi(x,y);
		}else if(ghost.hiz_y==-1) {
			return kuzey_acik_mi(x,y);
		}else {
			return true;
		}
	}
	void yon_belirle(Obje obje) {
		double x = obje.getX(),
				y = obje.getY();
		RandomGenerator rgen = RandomGenerator.getInstance();
		int rand = rgen.nextInt(1,4);
		obje.adim++;
		if(!onum_acik_mi_x(obje) || !onum_acik_mi_y(obje) 
				|| (obje.hiz_x==0 && obje.hiz_y==0) ) {
			switch(rand) {
			case 1:
				obje.hiz_x = -1;
				obje.hiz_y = 0;
				break;
			case 2:
				obje.hiz_x = 1;
				obje.hiz_y = 0;
				break;
			case 3:
				obje.hiz_x = 0;
				obje.hiz_y = -1;
				break;
			case 4:
				obje.hiz_x = 0;
				obje.hiz_y = 1;
				break;
			}
		}
		if(obje.adim>=100 ) {
			if(obje.hiz_x!=0 && (kuzey_acik_mi(x,y) || guney_acik_mi(x,y))) {
				if(kuzey_acik_mi(x,y-20) && rand<=2) {
					obje.hiz_x = 0;
					obje.hiz_y = -1;
					obje.adim = 0;
				}else if(guney_acik_mi(x,y+20) && rand>2) {
					obje.hiz_x = 0;
					obje.hiz_y = 1;
					obje.adim = 0;
				}
			}else if(obje.hiz_y!=0 && (dogu_acik_mi(x,y) || bati_acik_mi(x,y))) {
				if(dogu_acik_mi(x+20,y) && rand<=2) {
					obje.hiz_x = 1;
					obje.hiz_y = 0;
					obje.adim = 0;
				}else if(bati_acik_mi(x-20,y) && rand>2) {
					obje.hiz_x = -1;
					obje.hiz_y = 0;
					obje.adim = 0;
				}
			}
		}
	}
	boolean engel_var_mi(float x, float y) {
		boolean engelVarmi = false;
		for(int i=0; i<engel.size(); i++) {
			if(x+10>engel.get(i).getX()-pacman.getWidth() && x-10<engel.get(i).getX()+engel.get(i).getWidth()
					&& y+10>engel.get(i).getY() && y-10<engel.get(i).getY()+engel.get(i).getHeight()){
				engelVarmi = true;
			}
		}
		return engelVarmi;
	}
	boolean kuzey_acik_mi(double x, double y) {
		if(engel_var_mi((float)x,(float)y-10)) {
			return false;
		}
		if(mhsX<x && mhsY<y-10 && mheX>x && mheY>y) {
			return true;
		}else {
			return false;
		}
	}
	boolean guney_acik_mi(double x, double y) {
		if(engel_var_mi((float)x,(float)y+(float)pacman.getHeight()+10)) {
			return false;
		}
		if(mhsX<x && mhsY<y && mheX>x && mheY>y+pacman.getHeight()+10) {
			return true;
		}else {
			return false;
		}
	}
	boolean dogu_acik_mi(double x, double y) {
		if(engel_var_mi((float)x+(float)pacman.getWidth()-10,(float)y+14)) {
			return false;
		}
		if(mhsX<x && mhsY<y && mheX>x+pacman.getWidth() && mheY>y) {
			return true;
		}else {
			return false;
		}
	}
	boolean bati_acik_mi(double x, double y) {
		if(engel_var_mi((float)x-10,(float)y+14)) {
			return false;
		}
		if(mhsX<x-10 && mhsY<y && mheX>x && mheY>y) {
			return true;
		}else {
			return false;
		}
	}
	void metinleri_tanimla() {

		can_metni = new GLabel("Can: " + Integer.toString(can));
		can_metni.setFont("ARIAL-BOLD-30");
		can_metni.setColor(Color.WHITE);

		yem_metni = new GLabel("");
		yem_metni.setFont("ARIAL-30");
		yem_metni.setColor(Color.WHITE);

		kazanan = new GLabel("");
		kazanan.setFont("ARIAL-BOLD-55");
		kazanan.setColor(Color.WHITE);

		devam = new GLabel("Devam Etmek Icin Tiklayiniz...");
		devam.setFont("ARIAL-BOLD-25");
		devam.setColor(Color.LIGHT_GRAY);
	}
}