pragma solidity ^0.4.0;
contract TrustlessGambling {
   
    uint8 public constant NBR_OF_SLOTS =3;
    uint8 public constant PAYOUT_BLOCK_OFFSET =1;
    uint public constant EXPECTED_POT_AMOUNT=1000;// wei

    string public _message;
    uint public _blockNumberOnPayout;
    uint public _blockHashAsInteger;
    
    uint public nbrOfParticipants; // saves how many player have payed the exact amount
    address[NBR_OF_SLOTS] public depositAddresses; // the addresses where the player money is comming from
    address[NBR_OF_SLOTS] public payoutAddresses;
    uint public closingBlockNumber; // set when the last player enters the pot
    uint public payoutBlockNumber;
    bytes32 public payoutBlockHash;
    uint public winner; // 0 -> NBR_OF_SLOTS-1
    bool public potClosed;

    // if no one uses the checkPayout function and the cotract cannot determin who won because of missing past blockhash data
    uint public nbrOfMissedPayouts;

    function TrustlessGambling() public {
        nbrOfParticipants = 0;
        potClosed = false;
        nbrOfMissedPayouts = 0;
    }
    
    // Events allow light clients to react on
    // changes efficiently.
    event Deposit(address from, address to, uint amount);
    event Payout(address to, uint amount);
    
    function deposit() payable public {
        deposit(msg.sender);
    }

    function deposit(address _payout) payable public {
        assert(msg.value == EXPECTED_POT_AMOUNT);
        assert(!potClosed);
        _message = "new deposit";//msg.value;
        depositAddresses[nbrOfParticipants] = msg.sender;
        payoutAddresses[nbrOfParticipants] = _payout;
        nbrOfParticipants++;
        Deposit(msg.sender, _payout, msg.value);
        if (nbrOfParticipants == NBR_OF_SLOTS){
            closingBlockNumber = block.number;
            payoutBlockNumber = closingBlockNumber + PAYOUT_BLOCK_OFFSET;
            potClosed = true;
        }
    }
    
    function payout() public{
        assert(potClosed); //pot not full yet
        assert(block.number>payoutBlockNumber); // block for to select the winner not mined yet.
        _blockNumberOnPayout = block.number;
        payoutBlockHash = block.blockhash(payoutBlockNumber); // get the blockhash
        if(payoutBlockHash == 0){
             _message = "Can not payout because checkPayout-function was triggered too late. Funds are added to new pot.";
             nbrOfMissedPayouts++;
        }else{
            _message = "Payout triggered!";
            _blockHashAsInteger = uint(payoutBlockHash);
            winner = uint(payoutBlockHash) % NBR_OF_SLOTS;
            address winnerAddress = payoutAddresses[winner];
            uint amount= EXPECTED_POT_AMOUNT*NBR_OF_SLOTS; // pot amount
            amount += EXPECTED_POT_AMOUNT*NBR_OF_SLOTS*nbrOfMissedPayouts; // add missed out pot amounts 
            winnerAddress.transfer(amount); // send pot amount to winner
            nbrOfMissedPayouts = 0; // all funds are out
        }
        potClosed = false; // contains serious bug. 
        nbrOfParticipants=0; //start next round
    }

}