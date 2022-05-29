call setenv server
@rem call setenv consumer

@cd %ABSPATH2CLASSES%
@cls

java -cp %CLASSPATH% -Djava.rmi.server.codebase=%SERVER_CODEBASE% -Djava.rmi.server.hostname=%SERVER_RMI_HOST% -Djava.security.policy=%SERVER_SECURITY_POLICY% %JAVAPACKAGEROLE%.%SERVER_CLASS% %HOST% %REGISTRY_PORT% %SERVICE_NAME_ON_REGISTRY% %BROKER_PORT% %BROKER_QUEUE%

@cd %ABSPATH2SRC%\%JAVASCRIPTSPATH%








