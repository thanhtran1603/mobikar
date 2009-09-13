<?php
$dir = "/home/aa8494/domains/mobikar.net/public_html/wap/get";
//$dir = "/projects/java/ME/mobiKAR/web/wap/get";
$second = 600; // 10 minut

function remove_directory($dir) {
	if ($handle = opendir("$dir")) {
		while (false !== ($item = readdir($handle))) {
			if ($item != "." && $item != "..") {
				if (is_dir("$dir/$item")) {
					remove_directory("$dir/$item");
				} else {
					unlink("$dir/$item");
//					echo " removing $dir/$item<br>\n";
				}
			}
		}
		closedir($handle);
		rmdir($dir);
//		echo "removing $dir<br>\n";
	}
}

if ($handle_dir = opendir($dir)) {
	while (false !== ($filename = readdir($handle_dir))) {
		if ($filename == "." || $filename == "..") {
			continue;
		}
		$fullfilename = $dir . "/" . $filename;
//		print ("filename:$filename<br/>\n");
//		print ("is_dir(" . $fullfilename . "):[" . is_dir($fullfilename) . "] ");
//		print ("is_file(" . $fullfilename . "):[" . is_file($fullfilename) . "]<br/>\n");
		$changed = filemtime($fullfilename);
//		print ("czas:" . (time() - filemtime($fullfilename)) . "<br/>\n");
		if ((time() > ($second + filemtime($fullfilename)))) {
			if (is_file($fullfilename)) {
//				printf("unlink: $fullfilename<br/>\n");
				unlink($fullfilename);
			}
			elseif (is_dir($fullfilename)) {
//				printf("remove_directory: $fullfilename<br/>\n");
				remove_directory($fullfilename);
			}
		}
	}
	closedir($handle_dir);
}
?>