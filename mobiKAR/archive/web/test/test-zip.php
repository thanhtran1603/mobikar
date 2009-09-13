<?php
  include_once('../scripts/config.php');
  $filename = null;
  if (isset($cfg['Products']['dir'])){
     $filename = $cfg['Products']['dir'] . 'test.zip';
  }
  require_once('../scripts/pclzip.lib.php');
  $archive = new PclZip($filename);
  $v_list = $archive->add('add_foot.php,add_head.php,add_main.php,../service/');
  if ($v_list == 0) {
    echo ("Error (archive->add): ".$archive->errorInfo(true));
  }
  $v_list = $archive->delete(PCLZIP_OPT_BY_EREG, '^service/');
  if ($v_list == 0) {
    echo("Error (archive->delete): ".$archive->errorInfo(true));
  }
  $v_list = $archive->add('../service/');
  if ($v_list == 0) {
    echo("Error (archive->add 2): ".$archive->errorInfo(true));
  }
  
  if (($list = $archive->listContent()) == 0) {
    echo("Error (zip->listContent): ".$zip->errorInfo(true));
  }
  
  for ($i=0; $i<sizeof($list); $i++) {
    for(reset($list[$i]); $key = key($list[$i]); next($list[$i])) {
      echo "File $i / [$key] = ".$list[$i][$key]."<br>";
    }
    echo "<br>";
  }
?> 