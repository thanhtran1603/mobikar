// Autorem skryptu jest: S£AWOMIR KOK£OWSKI
// www.kurshtml.boo.pl
// Jeœli chcesz wykorzystaæ ten skrypt na swojej stronie, nie usuwaj tego komentarza!


function auto_iframe(margines)
{
   if (parent != self && document.body && (document.body.scrollHeight || document.body.offsetHeight))
   {
      var undefined;
      if (isNaN(parseInt(margines))) var margines = 20;

      if (parent.document.getElementById) parent.document.getElementById('autoiframe').height = 1;
      else if (parent.document.all) parent.document.all['autoiframe'].height = 1;
      var wysokosc = document.body.scrollHeight != undefined ? document.body.scrollHeight : document.body.offsetHeight;
      if (wysokosc)
      {
        if (parent.document.getElementById)
        {
          parent.document.getElementById('autoiframe').height = wysokosc + margines;
          parent.document.getElementById('autoiframe').scrolling = 'no';
        }
        else if (parent.document.all)
        {
          parent.document.all['autoiframe'].height = wysokosc + margines;
          parent.document.all['autoiframe'].scrolling = 'no';
        }
      }
   }
}
window.onload = auto_iframe;