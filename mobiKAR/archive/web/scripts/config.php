<?php
define('MOBIKAR_SERVER_HOST', 'localhost');
define('MOBIKAR_SERVER_PORT', '3306');
define('MOBIKAR_SERVER_BASE', 'mobikar');
define('MOBIKAR_SERVER_USER', 'root');
define('MOBIKAR_SERVER_PASSWORD', 'root');
define('MOBIKAR_SERVER_DOMAIN', 'mobikar.localhost/wap'); // jeśli strona nie będzie na tym adresie to zostanie tu przekierowany
define('MOBIKAR_SESSION_MAXTIME', 1800);// 30 minut
define('MOBIKAR_LOGGING_ERROR', E_ALL);
define('MOBIKAR_LOGGING_EMAIL', NULL);
define('MOBIKAR_LOGGING_WDDX', TRUE);
define('MOBIKAR_LOGGING_E_USER_ERROR', 'D:\\var\\log\\php\\mobikar.error.log');
define('MOBIKAR_LOGGING_E_USER_WARNING', 'D:\\var\\log\\php\\mobikar.warning.log');
define('MOBIKAR_LOGGING_E_USER_NOTICE', 'D:\\var\\log\\php\\mobikar.notice.log');
define('MOBIKAR_LOGGING_E_SYS', 'D:\\var\\log\\php\\mobikar.sys.log');
define('MOBIKAR_LOGGING_LOG', 'D:\\var\\log\\php\\mobikar.log');
define('MOBIKAR_PRODUCTS_DIR', '/var/mobikar/');
define('MOBIKAR_TEMPORARY_DIR', '/tmp/');
define('MOBIKAR_BROWSER_HTML', serialize(array('MOT-V980', 'ACS-NF/3.0 NEC-e228', 'Mozilla')));
?>