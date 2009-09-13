<?php
include_once("../scripts/myErrorHandler.php");
$reason= NULL;
if (isset ($_GET['reason'])) {
	$reason= $_GET['reason'];
}
$reason_text= "";
if (strcmp($reason, "BAD_SESSION") == 0) {
	$reason_text= "";
}
elseif (strcmp($reason, "BAD_PASSWORD") == 0) {
	$reason_text= "Nieprawid&#x142;owe has&#x142;o";
}
elseif (strcmp($reason, "BAD_USER") == 0) {
	$reason_text= "Nieprawid&#x142;owe ID";
}

$wap_title = "mobiKAR - login";
include("add_head.php");
?>
<h1>Witaj</h1>
<div>
	<?php if ($reason_text != NULL) echo "$reason_text<br/>"; ?>
	Zaloguj si&#x119; podaj&#x105;c swoje <a href="help.php?id=login_id">ID</a> i <a href="help.php?id=login_password">has&#x142;o</a>
	<br/>
	Je&#x15b;li jeste&#x15b; nowy <a href="login_new.php">za&#x142;&#xf3;&#x17c; konto</a>
</div>
<div style="text-align: center">
	<form title="Wchodze" name="Wchodze" action="login_check.php" method="get">
		<fieldset>
			Tw&#x00F3;j ID: <input name="l" size="9" maxlength="30" type="text"/><br/>
			i has&#x142;o: <input name="p" size="9" maxlength="30" type="password"/><br/>
			<input name="submit" type="submit" value="Wchodze"/>
		</fieldset>
	</form>
</div>

<?php
include("add_foot.php");
?>