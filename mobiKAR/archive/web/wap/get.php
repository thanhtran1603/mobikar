<?php
ob_start();
include_once ('session_check.php');
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/utf8ToEntities.php');
include_once ('../scripts/Database.php');
include_once ('../scripts/create_jar.php');
include_once ('../scripts/utils.php');

$isAllowed = FALSE;
$reason = NULL;
$par_id = getParameter('id');
$par_app = getParameter('app');
$par_song = getParameter('song');
$id = $par_id;
$browser = getValFromItem($_SERVER['HTTP_USER_AGENT']);
$rowUser = NULL;
$login = NULL;
$secure = NULL;
$password = NULL;
$product = NULL;
$code = NULL;
if ($uid == null && $kod != null) {
	// ustawiamy user o kodzie 1
	$uid = 1;
}
if ($uid != NULL) {
	$db = new Database();
	$rowUser = $db->getUser(NULL, $uid);
	if ($rowUser == NULL) {
		$reason = "BAD_USER";
	} else {
		$login = getValFromItem($rowUser['login']);
		$secure = getValFromItem($rowUser['secure']);
		$password = getValFromItem($rowUser['password']);
		$product = $db->getProduct($id);
		if ($product == NULL) {
			$reason = "BAD_PRODUCT";
		} else {
			if ($kod != null) {
				// pożarcie kodu
				// TODO: sprawdzenie, czy kod ma odpowienią liczbę żetonów
				if ($paymentId = $db->updateUserAccount($uid, $kod) == true) {
					// dodanie dla klienta zamowienia
					$code = $db->addOrder($uid, $id, $paymentId);
					if ($code != null)
						$isAllowed = TRUE;
					else
						$reason = "TOOCHEAP_CODE"; // kod za tani do produktu
				} else {
					$reason = "BAD_CODE";
				}
			}
			// czy user juz zakupil ten produkt
			elseif (($order = $db->getOrder($uid, $id)) != NULL) {
				$code = $order['code'];
				$isAllowed = TRUE;
			}
			// Czy user posiada wystarczajaca liczbe punktow
			elseif ($rowUser['coins'] < $product['product']['coins']) {
				$reason = "LOW_SCORE";
			} else {
				// dodanie dla klienta zamowienia
				// płatność z którą zostanie związane zamówienie
				$code = $db->addOrder($uid, $id);
				if ($code != null)
					$isAllowed = TRUE;
				else
					$reason = "TOOCHEAP_CODE"; // kod za tani do produktu
			}
			if ($isAllowed) {
				$db->addDownload($uid, $id);
			}
		}
	}
	$db->destroy();
}
if ($uid == 1) {
	// ustawiamy user o kodzie 1
	$login = null;
}

$reason_text = NULL;
if (strcmp($reason, "BAD_USER") == 0) {
	$reason_text = "B&#x142;&#x119;dny identyfikator u&#x17c;ytkownika: $uid";
}
elseif (strcmp($reason, "TOOCHEAP_CODE") == 0) {
	$reason_text = "U&#x17c;yty kod nie jest w&#x142;a&#x15b;ciwy dla wybranego produktu";
}
elseif (strcmp($reason, "BAD_CODE") == 0) {
	$reason_text = "B&#x142;&#x119;dny kod $kod";
}
elseif (strcmp($reason, "BAD_PRODUCT") == 0) {
	$reason_text = "B&#x142;&#x119;dny identyfikator produktu: $id";
}
elseif (strcmp($reason, "LOW_SCORE") == 0) {
	$reason_text = "Brak wystarczaj&#x105;cych &#x15b;rodk&#xf3;w na koncie ";
}
if ($reason != null)
	 trigger_error("reason_text:".$reason_text, E_USER_NOTICE);
trigger_error("isAllowed:".$isAllowed, E_USER_NOTICE);
if ($isAllowed) {
	// udost&#x119;pnienie paczki
	$dir = MOBIKAR_PRODUCTS_DIR;
	$jarName = "get/$code.jar";
	trigger_error("jarName:".$jarName, E_USER_NOTICE);
	$buf = jarCreate($jarName, $id, $product, $par_app, $par_song);
	$buf = trim($buf);
	$buf .= "\nMIDlet-Jar-Size: ".filesize($jarName);
	$buf .= "\nMIDlet-Jar-URL: http://".MOBIKAR_SERVER_DOMAIN. "/get/".$code.".jar";
	if ($login != null){
		$buf .= "\nmobiKAR-browser: ".$browser;
		$buf .= "\nmobiKAR-login: ".$login;
		// to dla obsługi starych plików
		$buf .= "\nmobiKAR-user: ".$login;
		$buf .= "\nmobiKAR-key: ".substr(sha1($secure.$login), 0, 32);
		$buf .= "\nmobiKAR-password: ".substr(sha1($password.filesize($jarName)), 0, 32);
		$buf .= "\nmobiKAR-provider: 1";
	}
	$file = fopen("get/$code.jad", "wb");
	fwrite($file, $buf);
	fclose($file);
	ob_end_clean();
	header("Location: http://".MOBIKAR_SERVER_DOMAIN."/get/$code.jad");
	return;
} else {
	ob_end_clean();
	$wap_title = "mobiKAR - pobierz";
	include ("add_head.php");
?>
	<h1>Problem</h1>
	<div>
		<b>Brak dost&#x119;pu do produktu</b>
		<br/>
		<?php if ($reason_text != NULL) echo "$reason_text<br/>"; ?>
	</div>
	<h2>Przejd&#x17a; do:</h2>
	<div>
		<a href='start.php<?=$mySID?>'> Strona g&#x142;&#xf3;wna </a>
	</div>
<?php } ?>

<?php


	include ("add_foot.php");
?>