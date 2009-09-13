<?php
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/utf8ToEntities.php');
include_once ('../scripts/Database.php');
// parametry
$par_request = (isset ($_GET['request']) ? $_GET['request'] : null);
$par_partner = (isset ($_GET['partner']) ? $_GET['partner'] : "0");
$par_format = (isset ($_GET['']) ? $_GET['format'] : "xhtml");
$par_lang = (isset ($_GET['lang']) ? $_GET['lang'] : "pl");
$par_css = (isset ($_GET['css']) ? $_GET['css'] : "1");

if ($par_request == null) {
	header("Location: account.php");
	return;
}

if ($par_request != null) {
	trigger_error("par_request:[$par_request]", E_USER_NOTICE);
	$db = new Database();
	if (strcmp($par_request, 'GetSongsList') == 0) {
		$par_count = (isset ($_GET['count']) ? $_GET['count'] : "10");
		$par_product = (isset ($_GET['product']) ? $_GET['product'] : "2");
		$par_category = (isset ($_GET['category']) ? $_GET['category'] : "-1");
		$categoryName = null;
		$products = null;
		if ($par_category == -1)
			$categoryName = "Nowo&#x15b;ci";
		else
			$categoryName = utf8ToEntities($db->getCategoryName($par_category));

		$products = $db->getProducts($par_product, $par_category, $par_count);
		$service = $db->getService($par_css);
		$css = (isset ($service['css']) ? $service['css'] : "http://partner.mobikar.net/mobikar.css");
		if (strcmp($par_format, 'xhtml') == 0) {
			print ("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
			print ("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
			print ("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
			print ("<script type='text/javascript'>\n");
			print ("//<![CDATA[\n");
			print ("function getOrder(pid){\n");
			print ("     window.open(\"?request=GetOrder&partner=$par_partner&format=xhtml&css=$par_css&lang=$par_lang&product=\" + pid,'','width=550, height=350,scrollbars=yes');\n");
			print ("}\n");
			print ("//]]>\n");
			print ("</script>\n");
			print ("<head>\n");
			print ("<title>mobile KARAOKE - mobiKAR</title>\n");
			print ("<link rel=\"stylesheet\" type=\"text/css\" title=\"CSS\" href=\"" . $css . "\"/>\n");
			print ("</head>\n");
			print ("<body>\n");
			print ("<div class='mk_pls'>\n");
			if ($products == null || count($products) == 0) {
				print ("Brak produktów\n");
			} else {
				print ("<table class='mk_pls'>\n");
				print ("<tr class='mk_pls_row'>\n");
				print ("<td class='mk_pls_ftl'> </td>\n");
				print ("<td class='mk_pls_ftc'> </td>\n");
				print ("<td class='mk_pls_ftr'> </td>\n");
				print ("</tr>\n");
				$i = 0;
				foreach ($products as $product) {
					if ($i++ >= $par_count)
						break;
					$id = utf8ToEntities($product['id']);
					$title = utf8ToEntities($product['title']);
					$artist = utf8ToEntities($product['artist']);
					$href = 'javascript:getOrder(' . $id . ')';
					print ("<tr class='mk_pls_row'>\n");
					print ("<td class='mk_pls_fcl'> </td>\n");
					print ("<td class='mk_pls_fcc'>\n");
					print ("<a class='mk_lst' href='" . $href . "' title='" . $id . ". " . $title . " - " . $artist . "'>" . $id . ". " . $title . " </a>\n");
					print ("<span class='mk_lsh'>&#160;</span>\n");
					print ("<a class='mk_lsa' href='" . $href . "' title='" . $id . ". " . $title . " - " . $artist . "'>" . $artist . " </a>\n");
					print ("</td>\n");
					print ("<td class='mk_pls_fcr'> </td>\n");
					print ("</tr>\n");
				}
				print ("<tr class='mk_pls_row'>\n");
				print ("<td class='mk_pls_fbl'> </td>\n");
				print ("<td class='mk_pls_fbc'> </td>\n");
				print ("<td class='mk_pls_fbr'> </td>\n");
				print ("</tr>\n");
				print ("</table>\n");
			}
			print ("");
			print ("</div>\n");
			print ("</body>\n");
			print ("</html>\n");
		}
		elseif (strcmp($par_format, 'js') == 0) {

		}

	}
	elseif (strcmp($par_request, 'GetOrder') == 0) {

		$dict['pl']['txt_js'] = "JavaScript nie załadowany!";
		$dict['pl']['tab_sms'] = "SMS";
		$dict['pl']['tab_cheaper'] = "Taniej";
		$dict['pl']['tab_targets'] = "Telefony";
		$dict['pl']['tab_help'] = "Pomoc";
		$dict['pl']['lab_artist'] = "Wykonawca";
		$dict['pl']['lab_music'] = "Muzyka";
		$dict['pl']['lab_lyrics'] = "Słowa";

		$dict['en']['txt_js'] = "JavaScript is not loaded!";
		$dict['en']['tab_sms'] = "SMS";
		$dict['en']['tab_cheaper'] = "Cheaper";
		$dict['en']['tab_targets'] = "Targets";
		$dict['en']['tab_help'] = "Help";
		$dict['en']['txt_js'] = "JavaScript is not loaded!";
		$dict['en']['lab_artist'] = "Artist";
		$dict['en']['lab_music'] = "Music";
		$dict['en']['lab_lyrics'] = "Lyrics";

		$service = $db->getService($par_css);
		$css = (isset ($service['css']) ? $service['css'] : "http://partner.mobikar.net/mobikar.css");

		$par_product = (isset ($_GET['product']) ? $_GET['product'] : "2");
		$product = $db->getProduct($par_product);
		$koszt = $product['product']['coins'];
		$suffix = "";
		if ($par_partner != 0) {
			$partner = $db->getPartner(null, $par_partner);
			$suffix = (isset ($partner['suffix']) ? "." . strtoupper($partner['suffix']) : "");
		}
		$sms_code = "AP.MK" . $suffix;
		$sms_number = "75068";
		$sms_price = "6,10 zł";
		$allpay_code = "MK" . $suffix;
		$allpay_price = "3,99 zł";
		if (count($product['songs']) > 1) {
			$sms_code = "AP.MKP" . $suffix;
			$sms_number = "79068";
			$sms_price = "10,98 zł";
			$allpay_code = "MKP" . $suffix;
			$allpay_price = "6,99 zł";
		}
		if (strcmp($par_format, 'xhtml') == 0) {
			print ("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
			print ("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
			print ("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
			print ("<!--\n");
			//print_r($_GET);
			print ("par_product:$par_product\n");
			print ("count(product['songs']): " . count($product['songs']) . "\n");
			print ("_GET['product']:" . $_GET['product'] . "\n");
			print ("-->\n");
			print ("<head>\n");
			print ("<title>mobiKAR.net - mobile KARAOKE</title>\n");
			print ("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />\n");
			print ("<link rel=\"stylesheet\" type=\"text/css\" title=\"CSS\" href=\"" . $css . "\"/>\n\n");
			print ("<script type='text/javascript' src='pay.js'></script>\n");
			print ("</head>\n");
			print ("<body onload='init();'> \n");
			print ("<div id='mk_tab_nojs'> " . $dict[$par_lang]['txt_js'] . " </div> \n");
			print ("<div class='mk_tab_menu'> \n");
			print ("  <ul id='mk_tab_menu'> \n");
			print ("    <li><a id='mk_tab_menu_sms' href=\"javascript:setTab('sms')\">" . $dict[$par_lang]['tab_sms'] . "</a></li> \n");
			print ("    <li><a id='mk_tab_menu_cheaper' href=\"javascript:setTab('cheaper')\">" . $dict[$par_lang]['tab_cheaper'] . "</a></li> \n");
			print ("    <li><a id='mk_tab_menu_targets' href=\"javascript:setTab('targets')\">" . $dict[$par_lang]['tab_targets'] . "</a></li> \n");
			print ("    <li><a id='mk_tab_menu_help' href=\"javascript:setTab('help')\">" . $dict[$par_lang]['tab_help'] . "</a></li> \n");
			print ("  </ul> \n");
			print ("</div> \n");
			print ("<div class='clr'></div> \n");
			print ("<div class='mk_tab_body'> \n");
			print ("  <div class='mk_tab_content' id='mk_tab_sms'> \n");
			if ("pl" == $par_lang) {
				print ("    <div class='mk_tab_highlight' id='mk_tab_highlight_sms'> Wyślij wiadomość SMS o treści <br/> \n");
				print ("      <span class='mk_tab_order_sms_code'>" . $sms_code . " </span><br/> \n");
				print ("      na numer <br/> \n");
				print ("      <span class='mk_tab_order_sms_number'>" . $sms_number . " </span><br/> \n");
				print ("      <span class='mk_tab_order_sms_cost'>Cena " . $sms_price . "</span><br/> \n");
				print ("    </div> \n");
				print ("    Aby otrzymać aplikację mobiKAR o numerze <b>" . $_GET['product'] . "</b> zawierającą\n");
			} else
				if ("en" == $par_lang) {
					print ("    <div class='mk_tab_highlight' id='mk_tab_highlight_sms'> Send a SMS message as follows: <br/> \n");
					print ("      <span class='mk_tab_order_sms_code'>" . $sms_code . " </span><br/> \n");
					print ("      to a number <br/> \n");
					print ("      <span class='mk_tab_order_sms_number'>" . $sms_number . " </span><br/> \n");
					print ("      <span class='mk_tab_order_sms_cost'>price " . $sms_price . "</span><br/> \n");
					print ("    </div> \n");
					print ("    TO obtain application mobiKAR number  <b>" . $_GET['product'] . "</b> containing the following\n");
				}
			print ("    <ul> \n");
			print (" <!--\n");
			//print_r($product);
			print (" -->\n");
			foreach ($product['songs'] as $song) {
				$title = utf8ToEntities($song['title']);
				$artist = utf8ToEntities($song['artist']);
				$music = utf8ToEntities($song['music']);
				$lyrics = utf8ToEntities($song['lyrics']);
				echo ("<li>$title\n");
				echo ("<ul>\n");
				echo ("<li> " . $dict[$par_lang]['lab_artist'] . ": $artist</li>\n");
				echo ("<li> " . $dict[$par_lang]['lab_music'] . ": $music</li>\n");
				echo ("<li> " . $dict[$par_lang]['lab_lyrics'] . ": $lyrics</li>\n");
				echo ("</ul>\n");
				echo ("</li>\n");
			}
			print ("    </ul> \n");
			//print ("    Koszt w &#x17c;etonach: $koszt.<br/>\n");
			if ("pl" == $par_lang) {
				print ("    wyślij wiadomość SMS o treści " . $sms_code . " na numer " . $sms_number . ". <br/> \n");
				print ("	W zamian otrzymasz w zwrotnej wiadomości SMS kod, który należy wprowadzić na stronie <b>http://wap.mobikar.net</b><br/>\n");
				print ("	Wprowadzenie kodu umożliwi Ci pobranie aplikacji mobiKAR wprost do Twojego telefonu.<br/>\n");
				print ("	Koszt wysłania wiadomości SMS na numer " . $sms_number . " wynosi " . $sms_price . ". <br/>\n");
				print ("	Wybierając odnośnik <a href=\"javascript:setTab('cheaper')\">Taniej</a> dowiesz się jak taniej otrzymać aplikację mobiKAR.<br/>\n");
				print ("	Pod odnośnikiem <a href=\"javascript:setTab('targets')\">Telefony</a> znajdziesz spis telefonów, które są gotowe do odpalenia aplikacji.<br/>\n");
				print ("	<a href=\"javascript:setTab('help')\">Pomoc</a> zawiera dokładny opis zamawiania, pobierania i uruchamiania aplikacji mobiKAR.<br/>\n");
				print ("	Usługa działa w sieciach operatorów: Plus GSM, Era, Idea. Właścicielem serwisu jest <a href='http://www.m1k0.com' target='_blank'>M1K0</a>. Serwis SMS obsługuje <a href='http://www.allpay.pl' target='_blank'>AllPay.pl</a>.<br/>\n");
				print ("	Dziękujemy za wybór naszego mobilnego KARAOKE.\n");
				print ("  </div> \n");
				print ("  <div class='mk_tab_content' id='mk_tab_cheaper'> \n");
				print ("  	<div class='mk_tab_highlight' id='mk_tab_highlight_cheaper'> Zapłać tylko<br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>" . $allpay_price . "</span><br/> \n");
				print ("      płącąc przelewem <br/> \n");
				print ("      dzięki usłudze <br/> \n");
				print ("      <span class='mk_tab_order_all_href'><a href='https://ssl.allpay.pl/?id=6774&amp;code=" . $allpay_code . "' target='_blank'>płać z AllPay</a></span><br/> \n");
				print ("    </div> \n");
				print ("  	Najtańszymi sposobemi otrzymania aplikacji mobiKAR są płatność poprzez przelew bankowy, płatność kartą, transfer międzybankowy \n");
				print ("	a także inne sposoby płaności udostępniane poprzez AllPay.pl.<br/>\n");
				print ("	Wybierając odnośnik <a href='https://ssl.allpay.pl/?id=6774&amp;code=" . $allpay_code . "' target='_blank'>płać z AllPay</a> \n");
				print ("	zostanie załadowana szyfrowana, bezpieczna strona serwisu AllPay.pl. Serwis ten umożliwia przeprowadzenie płatności za aplikację mobiKAR na wiele różnych sposobów.\n");
				print ("	Poniżej lista sposobów płatności:<br/>\n");
				print ("	Karta VISA, MasterCard, EuroCard, JCB, Diners Club, mTransfer (mBank), Płacę z Inteligo (konto Inteligo), MultiTransfer (MultiBank), AllPay Transfer (AllPay.pl), Przelew24 (BZWBK), ING Bank Śląski, Przekaz/Przelew bankowy, SEZAM (Bank Przemysłowo-Handlowy BPH SA), Pekao24 (Bank Pekao S.A.), MilleNet (Millennium Bank), Deutsche Bank PBC S.A., Kredyt Bank S.A. (KB24), Inteligo (Bank PKO BP), Lukas Bank.\n");
				print ("  </div> \n");
				print ("  <div class='mk_tab_content' id='mk_tab_targets'> \n");
				print ("	<div class='mk_tab_highlight' id='mk_tab_highlight_targets'> \n");
				print ("		Telefony <br/>z profilem JAVA<sup>TM</sup><br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>MIDP 2.0</span><br/> \n");
				print ("      lub zgodne z <br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>MMAPI 1.0</span><br/> \n");
				print ("    </div>   \n");
				print ("	mobiKAR jest multimedialną aplikacją dla środowiska JAVA. \n");
				print ("	<br/>\n");
				print ("	Telefony muszą jeszcze posiadać wsparcie dla obsługi dżwięków w środowisku JAVA.\n");
				print ("	<br/>\n");
				print ("	Telefony z profilem JAVA MIDP 2.0 posiadają pełne wsparcie do dźwięków realizaowane przez MMAPI 1.1.\n");
				print ("	<br/>\n");
				print ("	Również telefony z JAVA MIDP 1.0 również mogą posiadać wsparcie w postaci MMAPI 1.0.\n");
				print ("	<br/>\n");
				print ("	MMAPI (MultiMedia API) opisywane jest również jako JSR 135.\n");
				print ("	<br/>\n");
				print ("	Poniżej lista znanych modeli telefonów, które posiadają wsparcie dla multimediów:\n");
				print ("	<br/>\n");
				print ("	<strong>Motorola:</strong> A630, A668, A760, A768, A780, A845, C380, C385, C650, C975, C980, E1000, E398, E550, E680, T725, V180, V186, V220, V3, V300, V303, V330, V400, V500, V501, V505, V525, V536, V545, V547, V550, V551, V555, V557, V600, V620, V635, V690, V80, V878, V975, V980;\n");
				print ("	<br/>\n");
				print ("	<strong>Nokia:</strong> 3220, 3650, 5140, 6020, 6170, 6220, 6230, 6260, 6600, 6620, 6630, 6638, 6670, 7260, 7270, 7280, 7610, N-Gage;\n");
				print ("	<br/>\n");
				print ("	<strong>SAMSUNG:</strong> D500, E700, E800, D710;\n");
				print ("	<br/>\n");
				print ("	<strong>Siemens:</strong> C65, C66, CFX65, CX65, CX70, M65, S65, S66, SK65, SL65, SX1;\n");
				print ("	<br/>\n");
				print ("	<strong>SonyEricsson:</strong> T610, T630, K300, K500, K700, P800, P900, P908, P910, S700, S710, V800, Z1010, Z500.\n");
				print ("  </div> \n");
				print ("  <div class='mk_tab_content' id='mk_tab_help'>\n");
				print ("	<div class='mk_tab_highlight' id='mk_tab_highlight_help'> \n");
				print ("		Obszerna pomoc<br/> \n");
				print ("		jest dostępna na<br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>www.mobikar.net</span><br/> \n");
				print ("      a także na <br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>wap.mobikar.net</span><br/> \n");
				print ("    </div>   \n");
				print ("   Aby pobrać aplikację mobilnego KARAOKE na telefon należy na wstępie uzyskać kod. Można go zakupić poprzez wysłanie na numer <b>" . $sms_number . "</b> wiadomości SMS o treści <b>" . $sms_code . "</b>. Kod można również uzyskać kupując taniej poprzez płatność kartą, przelewem czy transferem międzybankowym realizowanym dzięki serwisowi AllPay.pl. Kod jest potwierdzeniem prawa do pobrania piosenki wraz z aplikacją mobilnego KARAOKE. <br/> Kod należy wprowadzić na stronie serwisu <b>wap.mobikar.net</b>. Po wejściu na stronę startową serwisu ukaże się zachęta do założenia konta użytkownika. Użytkownik, który założy konto w serwisie może korzystać z wielu przywilejów a jednym z nich jest pobranie za darmo pełnej wersji aplikacji wraz z ludową piosenką. Drugim ważnym przywilejem jest możliwość pobierania wielokrotnie raz zakupionej piosenki.<br/>Dla tych, co nie chcą zakładać swojego konta dostępna jest możliwość pobrania jednej aplikacji w zamian za wprowadzony prawidłowy kod.<br/> Po wprowadzeniu kodu należy wpisać numer aplikacji a następnie pobrać ją na telefon. W przypadku zapomnienia numeru lub w przypadku zmiany decyzji co do wybranego utworu istnieje możliwość przejrzenia całej listy piosenek i wybraniu właściwej do pobrania do telefonu. \n");
				print ("   </div> \n");
			} else
				if ("en" == $par_lang) {
				print ("    sent the following SMS message " . $sms_code . " to a number " . $sms_number . " <br/> \n");
				print ("	You will then receive (via SMS) a code to be entered on the page <b>http://wap.mobikar.net</b><br/>\n");
				print ("	Once you've entered this code, you can download the mobiKAR application directly to your mobile phone unit.<br/>\n");
				print ("	The price of each SMS message sent to number  " . $sms_number . " is " . $sms_price . ". <br/>\n");
				print ("	By selecting option <a href=\"javascript:setTab('cheaper')\">Cheaper</a> you will obtain information how to get this application cheaper.<br/>\n");
				print ("	By selecting option <a href=\"javascript:setTab('targets')\">Targets</a> a list of phone numbers ready to start the application is available.<br/>\n");
				print ("	The <a href=\"javascript:setTab('help')\">Help</a> available there provides detail instructions on ordering, downloading and starting mobiKAR applications..<br/>\n");
				print ("	This service is available on networks Plus GSM, Era and Idea. The service is owned by <a href='http://www.m1k0.com' target='_blank'>M1K0</a>. <a href='http://www.allpay.pl' target='_blank'>AllPay.pl</a> provides the SMS service.<br/>\n");
				print ("	Thank you for choosing our mobile KARAOKE - mobiKAR.\n");
				print ("  </div> \n");
				print ("  <div class='mk_tab_content' id='mk_tab_cheaper'> \n");
				print ("  	<div class='mk_tab_highlight' id='mk_tab_highlight_cheaper'> Pay only <br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>" . $allpay_price . "</span><br/> \n");
				print ("      when paying  <br/> \n");
				print ("      using service <br/> \n");
				print ("      <span class='mk_tab_order_all_href'><a href='https://ssl.allpay.pl/?id=6774&amp;lang=en&amp;code=" . $allpay_code . "' target='_blank'>pay by AllPay</a></span><br/> \n");
				print ("    </div> \n");
				print ("  	The cheapest way to get the mobiKAR application is by paying via bank transfer, by credit card, direct bank transfer \n");
				print ("	and other methods available via AllPay.pl.<br/>\n");
				print ("	When selected <a href='https://ssl.allpay.pl/?id=6774&amp;lang=en&amp;code=" . $allpay_code . "' target='_blank'>pay by AllPay</a> \n");
				print ("	you will be transferred to a secure site AllPay.pl.\n");
				print ("	Payment methods available there are:<br/>\n");
				print ("	VISA, MasterCard, EuroCard, JCB, Diners Club, mTransfer (mBank), Płacę z Inteligo (konto Inteligo), MultiTransfer (MultiBank), AllPay Transfer (AllPay.pl), Przelew24 (BZWBK), ING Bank Śląski, Przekaz/Przelew bankowy, SEZAM (Bank Przemysłowo-Handlowy BPH SA), Pekao24 (Bank Pekao S.A.), MilleNet (Millennium Bank), Deutsche Bank PBC S.A., Kredyt Bank S.A. (KB24), Inteligo (Bank PKO BP), Lukas Bank.\n");
				print ("  </div> \n");
				print ("  <div class='mk_tab_content' id='mk_tab_targets'> \n");
				print ("	<div class='mk_tab_highlight' id='mk_tab_highlight_targets'> \n");
				print ("		Phones <br/> with JAVA<sup>TM</sup><br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>MIDP 2.0</span><br/> \n");
				print ("      or compatible with <br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>MMAPI 1.0</span><br/> \n");
				print ("    </div>   \n");
				print ("	The mobiKAR application is a multimedia application running under JAVA environment \n");
				print ("	<br/>\n");
				print ("	All mobile phone units must supports sound under JAVA environment.\n");
				print ("	<br/>\n");
				print ("	All phones with JAVA MIDP 2.0 profile fully support MMAPI 1.1 sound reproduction.\n");
				print ("	<br/>\n");
				print ("	Some mobile phone units with JAVA MIDP 1.0 profile may support MMAPI 1.0 sound reproduction.\n");
				print ("	<br/>\n");
				print ("	The MMAPI (Multimedia API) is also known as JSR 135.\n");
				print ("	<br/>\n");
				print ("	Known mobile phone models supporting multimedia are listed below:\n");
				print ("	<br/>\n");
				print ("	<strong>Motorola:</strong> A630, A668, A760, A768, A780, A845, C380, C385, C650, C975, C980, E1000, E398, E550, E680, T725, V180, V186, V220, V3, V300, V303, V330, V400, V500, V501, V505, V525, V536, V545, V547, V550, V551, V555, V557, V600, V620, V635, V690, V80, V878, V975, V980;\n");
				print ("	<br/>\n");
				print ("	<strong>Nokia:</strong> 3220, 3650, 5140, 6020, 6170, 6220, 6230, 6260, 6600, 6620, 6630, 6638, 6670, 7260, 7270, 7280, 7610, N-Gage;\n");
				print ("	<br/>\n");
				print ("	<strong>SAMSUNG:</strong> D500, E700, E800, D710;\n");
				print ("	<br/>\n");
				print ("	<strong>Siemens:</strong> C65, C66, CFX65, CX65, CX70, M65, S65, S66, SK65, SL65, SX1;\n");
				print ("	<br/>\n");
				print ("	<strong>SonyEricsson:</strong> T610, T630, K300, K500, K700, P800, P900, P908, P910, S700, S710, V800, Z1010, Z500.\n");
				print ("  </div> \n");
				print ("  <div class='mk_tab_content' id='mk_tab_help'>\n");
				print ("	<div class='mk_tab_highlight' id='mk_tab_highlight_help'> \n");
				print ("		A comprehensive help sites<br/> \n");
				print ("		is available on <br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>www.mobikar.net</span><br/> \n");
				print ("      and <br/> \n");
				print ("      <span class='mk_tab_order_all_cost'>wap.mobikar.net</span><br/> \n");
				print ("    </div>   \n");
				print ("   A code is required for download of a mobiKAR application. Sending a SMS message <b>" . $sms_number . "</b> to number <b>" . $sms_code . "</b> can purchase the code. Paying via credit cart, bank transfer or other method available via AllPay.pl code this can be obtained cheaper. This code is a confirmation of your rights to download purchased song and application mobiKAR  The received code must be provided on the page wap.mobikar.net when requested. Upon start of the page a popup encouraging to open new user account will appear. Opening a new account in our service entitles you to some benefits, one of them being a free download of a full version of the mobiKAR application containing one country song. You are also entitled to multiple downloads once purchased song. If you do not wish to become a registered user, you are entitle only to one single download of the application for each correctly entered code. Once the correct code is entered, the number of an application (song) is required and then it can be downloaded directly to the mobile phone unit. If you forgotten the song number or decided to change your mind, you can always browse the entire list of available songs and select any one of them.\n");
				print ("   </div> \n");
				}
				print ("</div> \n");
				print ("</body>\n");
				print ("</html>\n");

		}
		elseif (strcmp($par_format, 'js') == 0) {

		}
	}
	$db->destroy();
}
?>


