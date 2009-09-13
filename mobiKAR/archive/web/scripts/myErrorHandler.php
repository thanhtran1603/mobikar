<?php
include_once ('config.php');
// set the error reporting level for this script
error_reporting(MOBIKAR_LOGGING_ERROR);
// error handler function
function myErrorHandler($errno, $errmsg, $filename, $linenum, $vars) {
	// timestamp for the error entry
	$dt= date("Y-m-d H:i:s ");
	// define an assoc array of error string
	// in reality the only entries we should
	// consider are E_WARNING, E_NOTICE, E_USER_ERROR,
	// E_USER_WARNING and E_USER_NOTICE
	$errortype= array (E_ERROR => "Error", E_WARNING => "Warning", E_PARSE => "Parsing Error", E_NOTICE => "Notice", E_CORE_ERROR => "Core Error", E_CORE_WARNING => "Core Warning", E_COMPILE_ERROR => "Compile Error", E_COMPILE_WARNING => "Compile Warning", E_USER_ERROR => "User Error", E_USER_WARNING => "User Warning", E_USER_NOTICE => "User Notice");
	// set of errors for which a var trace will be saved
	$user_errors= array (E_USER_ERROR, E_USER_WARNING, E_USER_NOTICE);
	$err= "<errorentry>\n";
	$err .= "\t<datetime>".$dt."</datetime>\n";
	$err .= "\t<errornum>".$errno."</errornum>\n";
	if (isset($errortype[$errno]))
		$err .= "\t<errortype>".$errortype[$errno]."</errortype>\n";
	$err .= "\t<errormsg>".$errmsg."</errormsg>\n";
	$err .= "\t<scriptname>".$filename."</scriptname>\n";
	$err .= "\t<scriptlinenum>".$linenum."</scriptlinenum>\n";
	if (isset ($_SERVER['HTTP_USER_AGENT'])) 
		$err .= "\t<user-agent>".$_SERVER['HTTP_USER_AGENT']."</user-agent>\n";
	if (MOBIKAR_LOGGING_WDDX == TRUE && in_array($errno, $user_errors)) {
		$err .= "\t<vartrace>".wddx_serialize_value($vars, "Variables")."</vartrace>\n";
	}
	$err .= "</errorentry>\n\n";
	$err_line= $dt;
	if (isset($errortype[$errno]))
		$err_line .= "[".$errortype[$errno]."]";
	$err_line .= $errmsg." ".$filename." ".$linenum."\n";
	// for testing
	// echo $err;
	// save to the error log, and e-mail me if there is a critical user error
	if ($errno == E_USER_ERROR) {
		error_log($err, 3, MOBIKAR_LOGGING_E_USER_ERROR);
		if (MOBIKAR_LOGGING_EMAIL != NULL)
		mail(MOBIKAR_LOGGING_EMAIL, "Critical User Error", $err);
	}
	else if ($errno == E_USER_WARNING) {
		error_log($err, 3, MOBIKAR_LOGGING_E_USER_WARNING);
	}
	else if ($errno == E_USER_NOTICE) {
		error_log($err, 3, MOBIKAR_LOGGING_E_USER_NOTICE);
	}
	else{
		error_log($err, 3, MOBIKAR_LOGGING_E_SYS);
	}
	error_log($err_line, 3, MOBIKAR_LOGGING_LOG);
}
$old_error_handler = set_error_handler("myErrorHandler");
?>