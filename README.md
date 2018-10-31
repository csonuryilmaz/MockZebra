# MockZebra
Mock zebra printer, test and view raw ZPL labels as png image or pdf document. It simulates zebra printer on socket connection.

Example **config.properties** 

```
SOCKET_PORT=1206
LABEL_SAVE_AS=PNG
PRINT_DENSITY_VALUE=300
PRINT_DENSITY_UNIT=dpi 
LABEL_SIZE_HEIGHT=5
LABEL_SIZE_WIDTH=7
LABEL_SIZE_UNIT=cm
VIEW_PNG_WITH=viewnior
VIEW_PDF_WITH=evince
```