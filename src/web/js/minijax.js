function getXmlHttp()
{
    try
    {
        xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
        return xmlhttp;
    }
    catch (e)
    {
        try
        {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            return xmlhttp;
        }
        catch (E)
        {
            xmlhttp = false;
        }
    }
    if (!xmlhttp && typeof XMLHttpRequest!='undefined')
    {
      return new XMLHttpRequest();
    }
}

function fillDivWithUrl(divId, url)
{
    xmlhttp = getXmlHttp();
    xmlhttp.open("GET",url,true);
    xmlhttp.onreadystatechange=function()
    {
        if (xmlhttp.readyState==4)
        {
           document.getElementById(divId).style.visibility='visible';
           document.getElementById(divId).innerHTML=xmlhttp.responseText;
        }
    }
    xmlhttp.setRequestHeader('Accept','message/x-formresult')
    xmlhttp.send(null)
    return false
}