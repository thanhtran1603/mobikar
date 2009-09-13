<?php
include_once ('../scripts/myErrorHandler.php');
include_once ('../scripts/Database.php');
include_once ('../scripts/utils.php');
$login= NULL;
if (isset ($_GET['l']))
	$login= $_GET['l'];
$password= NULL;
if (isset ($_GET['p']))
	$password= $_GET['p'];
$password2= NULL;
if (isset ($_GET['p2']))
	$password2= $_GET['p2'];
$isAllowed= FALSE;
$uid= NULL;
$sid= NULL;
$reason_text= "";
if ($login != null && $password != null && $password2 != null ) {
	$login = trim(strtolower($login));
	$login2 = urlencode(htmlentities(htmlspecialchars($login), ENT_QUOTES));
	$password = urlencode(htmlentities(htmlspecialchars($password), ENT_QUOTES));
	if (strlen($login) < 5 || strlen($login) > 15){
		$reason_text= "Identyfikator musi mie&#x107; od 5 do 15 znak&#xf3;w";
	}
	elseif (strcmp($login,$login2) != 0 ){
		$reason_text= "Identyfikator mo&#x17c;e zawiera&#x107; tylko litery i cyfry";
	}
	elseif (strcmp($password,$password2) != 0 ){
		$reason_text= "Pola hase&#x142; nie zgadzaj&#x105; si&#x119;";
	}
	else{
		$db= new Database();
		$rowUser= $db->getUser($login, NULL);
		if ($rowUser != NULL) {
			$reason_text= "U&#x17c;ytkownik o podanym identyfikatorze ju&#x17c; istnieje";
		}
		else{
			$rowUser= $db->addUser($login, $password);
			$uid= $rowUser['id'];
			$isAllowed= TRUE;
			$sid= getCode();
			if ($db->updateUserSid($uid, $sid) == FALSE) {
				$reason_text= "Undefined error";
				$isAllowed= FALSE;
			}
			trigger_error("login:".$login." password(".$rowUser['password']."):".$password."uid:".$uid." sid".$sid, E_USER_NOTICE);
		}
		$db->destroy();
	}
}

$wap_title = "mobiKAR - login";
include("add_head.php");
?>
<?php if ($isAllowed == FALSE) { ?>
	<h1>Nowe konto</h1>
	<div>
		<?php
			if ($reason_text != NULL)
				echo "$reason_text";
			else
				echo "Za&#x142;&#xf3;&#x17c; nowe konto";
		?>
		<br/>
		Zak&#x142;adaj&#x105;c konto akceptujesz <a href="reg.php">warunki</a>
		korzystania z serwisu
		<br/>
		Podaj&#x105;c swoje <a href="help.php?id=login_id">ID</a> i <a href="help.php?id=login_password">has&#x142;o</a>
	</div>
	<div style="text-align: center">
		<form title="Dalej" name="Dalej" action="login_new.php" method="get">
			<fieldset>
				Tw&#x00F3;j ID: <input name="l" size="9" maxlength="30" type="text"/>
				<br/>
				i has&#x142;o: <input name="p" size="9" maxlength="30" type="password"/>
				<br/>
				powt&#xf3;rz has&#x142;o <input name="p2" size="9" maxlength="30" type="password"/>
				<br/>
				<input name="submit" type="submit" value="Dalej"/>
			</fieldset>
		</form>
	</div>
<?php } elseif ($isAllowed == TRUE) { ?>
	<h1>Witaj w&#x15b;r&#xf3;d nas</h1>
	<div>
		Witaj nowy u&#x17c;ytkowniku!
		<br/>
		Jest nam niezwykle mi&#x142;o do&#x142;&#x105;czaj&#x105;c Ciebie do grona u&#x17c;ytkownik&#xf3;w mobiKAR.net
		<br/>
		Zapami&#x119;taj swoj&#x105; identyfikacj&#x119;:
		<br/>
		LOGIN (ID): <?=$login?>
		<br/>
		HAS&#x141;O: <?=$password?>
	</div>
	<h2>Przejd&#x17a; do:</h2>
	<div>
		<a href='reg.php?uid=<?=$uid?>&amp;sid=<?=$sid?>'> Regulamin </a>
		<br/>
		<a href='start.php?uid=<?=$uid?>&amp;sid=<?=$sid?>'> Strona startowa </a>
	</div>
<?php } ?>

<?php
include("add_foot.php");
?>