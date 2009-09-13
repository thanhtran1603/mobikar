<?php
/*
 * Created on 2005-05-24
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
define("FORMAT_WML", 0);
define("FORMAT_XHTML", 1);
$format= FORMAT_WML;
$contentType = "text/vnd.wap.wml";
if (isset ($_SERVER['HTTP_ACCEPT'])) {
	trigger_error("HTTP_ACCEPT: {$_SERVER['HTTP_ACCEPT']}", E_USER_NOTICE);
	if (strstr($_SERVER['HTTP_ACCEPT'], 'xhtml+xml') != FALSE) {
		$format= FORMAT_XHTML;
		$contentType = "application/vnd.wap.xhtml+xml";
	}
}
if (isset ($_SERVER['HTTP_USER_AGENT'])) {
	if (strstr($_SERVER['HTTP_USER_AGENT'], "Nokia7650") != FALSE){
		$format= FORMAT_WML;
		$contentType = "text/vnd.wap.wml";
	}
	if (strstr($_SERVER['HTTP_USER_AGENT'], "Mozilla") != FALSE){
		$format= FORMAT_XHTML;
		$contentType = "text/html";
	}
}
$wap_title= "mobiKAR";
// standardzik dla WMLa
$areaLeftBeg= '<p>';
$areaLeftEnd= '</p>';
$areaCenterBeg= '<p aling="center">';
$areaCenterEnd= '</p>';
$head1Beg= '<p aling="center"><big><b>';
$head1End= '</b></big></p>';
$head2Beg= '<p aling="center"><b>';
$head2End= '</b></p>';
if ($format == FORMAT_XHTML) {
	$areaLeftBeg= '<div>';
	$areaLeftEnd= '</div>';
	$areaCenterBeg= '<div style="text-align: center;">';
	$areaCenterEnd= '</div>';
	$head1Beg= '<h1>';
	$head1End= '</h1>';
	$head2Beg= '<h2>';
	$head2End= '</h2>';
}
function wapForm($format, $butonName, $action, $inputs) {
	$uid = NULL;
	$sid = NULL;
	if (isset($_GET['uid']) && (isset($_GET['sid']))){
		$uid = $_GET['uid'];
		$sid = $_GET['sid'];
	}
	if ($format == FORMAT_WML) {
		$params= '';
		if (strstr($action, '?') != FALSE)
			$params= '?a=b';
		if ($uid != null){
			$params .= '&amp;uid=$uid';
		}
		if ($sid != null){
			$params .= '&amp;sid=$sid';
		}
		foreach ($inputs as $input) {
			$name= $input['name'];
			$text= $input['text'];
			$size= $input['size'];
			$maxlength= $input['maxlength'];
			$formatWML= $input['formatWML'];
			echo ''.$text.' <input name="'.$name.'" format="'.$formatWML.'" size="'.$size.'" maxlength="'.$maxlength.'" type="text"/><br/>'."\n";
			$params .= '&amp;'.$name.'=$('.$name.')';
		}
		echo '<anchor>'.$butonName.'<go href="'.$action.$params.'"></go></anchor><br/>'."\n";
	}
	else {
		echo '<form title="'.$butonName.'" name="'.$butonName.'" action="'.$action.'" method="get">'."\n";
		echo '<fieldset>';
		if ($uid != null){
			echo '<input name="uid" value="'.$uid.'" type="hidden"/>'."\n";
		}
		if ($sid != null){
			echo '<input name="sid" value="'.$sid.'" type="hidden"/>'."\n";
		}
		foreach ($inputs as $input) {
			$name= $input['name'];
			$text= $input['text'];
			$size= $input['size'];
			$maxlength= $input['maxlength'];
			echo ''.$text.' <input name="'.$name.'" size="'.$size.'" maxlength="'.$maxlength.'" type="text"/><br/>'."\n";
		}
		echo '<input name="submit" type="submit" value="'.$butonName.'"/>'."\n";
		echo '</fieldset>';
		echo '</form>';
	}
}
?>