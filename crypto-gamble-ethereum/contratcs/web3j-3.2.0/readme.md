# Web3j commandline tools

## Documentation

web3j supports the auto-generation of smart contract function wrappers in Java from Solidity ABI files.
The web3j Command Line Tools tools ship with a command line utility for generating the smart contract function wrappers:

`web3j solidity generate [--javaTypes|--solidityTypes] /path/to/<smart-contract>.bin /path/to/<smart-contract>.abi -o /path/to/src/main/java -p com.your.organisation.name`

Full documentation can be found [here](https://docs.web3j.io/index.html)

## How to use

Goto the ethereum in broweser blockchain simulation to create and test your smart contract. [Link](https://ethereum.github.io/browser-solidity)
When finished download the .abi file and the .bin file via copy paste. Now create the java wrapper classes.

### Windows

Adapt the path to your project (*prDir* variable) and run the following commands via CMD.

`set prDir=C:\path\to\your\project\crypto-gamble\crypto-gamble-ethereum`

Change to the correct directory:

`cd %prDir%\contratcs\web3j-3.2.0\bin`

Run the command to generate the java files:

`web3j solidity generate --javaTypes %prDir%\contratcs\solidity\gamble\build\TrustlessGambling.bin %prDir%\contratcs\solidity\gamble\build\TrustlessGambling.abi -o %prDir%\src\main\java -p com.ossel.gamble.ethereum.generated`

