<?php
/*
 * Created on 2005-08-15
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
include_once ('myErrorHandler.php');
include_once ('Database.php');
include_once ('utils.php');
$db= new Database();
$code=null;
while(true){
	$code = getCode();
	if ($db->getPayment($code) == null){
		$idPaymentType = 7; // 7  	Przelew bankowy 6,99 zł  	3  	699
		$idProvider = 3; // 3  	2005-08-15 11:49:30  	Aukcja Allegro.pl  	
		$db->addCodeToPayment($code, $idPaymentType, $idProvider);
		break;
	}
}

$db->destroy();

?>
