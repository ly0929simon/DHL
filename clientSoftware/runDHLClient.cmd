set XML_PATH=.\lib
set CLASSPATH=.;%JAVA_HOME%\lib;%XML_PATH%\soap.jar;%XML_PATH%\xalan.jar;%XML_PATH%\xerces1.4.jar;%XML_PATH%\log4j-1.2.8.jar;%XML_PATH%\commons-lang-2.4.jar

set FUTURE_DAY=false
set RESPONSE_PATH=TransformXMLtoPDF\ResponseXMLS\
set SERVER_URL=https://xmlpitest-ea.dhl.com/XMLShippingServlet
set INPUT_FILE=TransformXMLtoPDF\RequestXML\ShipmentValidateRequest_INT_DUT_AP_PieceEnabled_With2Pcs_PcsSeg_v62.xml
java DHLClient %INPUT_FILE% %SERVER_URL% %RESPONSE_PATH% %FUTURE_DAY%