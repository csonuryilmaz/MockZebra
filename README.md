# MockZebra

[![GitHub stars](https://img.shields.io/github/stars/csonuryilmaz/MockZebra.svg?style=social&label=Star)](http://bit.ly/2PKSevR)   [![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)   [![Issues](https://img.shields.io/github/issues/csonuryilmaz/MockZebra.svg)](https://github.com/csonuryilmaz/MockZebra/issues)   [![Latest](https://img.shields.io/badge/release-v1.0.0.0-red.svg)](https://github.com/csonuryilmaz/MockZebra/releases/latest)   [![GitHub Releases](https://img.shields.io/github/downloads/csonuryilmaz/MockZebra/latest/total.svg)](https://github.com/csonuryilmaz/MockZebra/releases/latest)


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

### Test Platforms

I've tested app on below platforms and it works fine:

* Debian GNU/Linux 8.11 (jessie) / java 1.8.0_181
* Debian GNU/Linux 9.5 (stretch) / java 1.8.0_162

If something goes wrong, let me know the situation by opening an issue. See the [contributing](CONTRIBUTING.md) file for details. Also see our [code of conduct](CODE_OF_CONDUCT.md) file for other social details.

### License

This project is licensed under the MIT License. See the [license](LICENSE) file for details.

### Authors

**Onur Yılmaz**     

[![Twitter Follow](https://img.shields.io/twitter/follow/csonuryilmaz.svg?style=social)](http://bit.ly/2RCmMgx)

[![Stackoverflow Profile](https://stackoverflow.com/users/flair/1750142.png)](http://bit.ly/2qu14zS)
