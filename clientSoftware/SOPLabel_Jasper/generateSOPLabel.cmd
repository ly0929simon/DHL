set LIB_PATH=./lib
set CLASSFILE_PATH=./classes/
set PROPERTYFILE_PATH=../
set CLASSPATH=%CLASSPATH%;%LIB_PATH%/activation.jar;%LIB_PATH%/barbecue-1.5-beta1.jar;%LIB_PATH%/commons-beanutils-1.7.jar;%LIB_PATH%/commons-collections-2.1.jar;%LIB_PATH%/commons-digester-1.7.jar;%LIB_PATH%/commons-logging-api-1.0.2.jar;%LIB_PATH%/iReport.jar;%LIB_PATH%/iText-2.1.0.jar;%LIB_PATH%/jasperreports-3.1.2.jar;%LIB_PATH%/jaxb-api.jar;%LIB_PATH%/jaxb-impl.jar;%LIB_PATH%/jsr173_1.0_api.jar;%LIB_PATH%/xercesImpl.jar;%CLASSFILE_PATH%;%PROPERTYFILE_PATH%
java com.dhl.sop.label.LabelReportHandler
cd ..





