call setenv client

@cd %ABSPATH2CLASSES%
python -m http.server 8000

@cd %ABSPATH2SRC%\%JAVASCRIPTSPATH%


