window.onload = init;

//DOM elements

const showFormButton = document.querySelector('.addExchange .button a');
const addButton = document.querySelector('#add_button');
const cancelButton = document.querySelector('#cancel_button');
const addExchangeForm = document.querySelector('.addExchangeForm');
const content = document.querySelector('.content');


//FUNCTIONS

function init() {
	document.querySelector('.addExchangeForm').style.display = 'none';
}


function createExchangeElement(exchange) {

	const exchangeDiv = document.createElement("div");
	exchangeDiv.setAttribute("id", exchange.id);
	exchangeDiv.setAttribute("class", "exchange " + exchange.type);

	const exchangeCrypto = document.createElement("span");
	exchangeCrypto.setAttribute("class", "exchangeCrypto");
	exchangeCrypto.innerHTML = "Crypto's name: " + exchange.crypto + "<br>";
	exchangeDiv.appendChild(exchangeCrypto);

	const exchangeCoin = document.createElement("span");
	exchangeCoin.setAttribute("class", "exchangeCoin");
	exchangeCoin.innerHTML = "Coin's name: " + exchange.coin + "<br>";
	exchangeDiv.appendChild(exchangeCoin);

	const exchangePrice = document.createElement("span");
	exchangePrice.setAttribute("class", "exchangePrice");
	exchangePrice.innerHTML = "Exchange price: " + exchange.price + "<br>";
	exchangeDiv.appendChild(exchangePrice);

	const exchangeTimeStamp = document.createElement("span");
	exchangeTimeStamp.setAttribute("class", "exchangeTimeStamp");
	exchangeTimeStamp.innerHTML = "Time exchange price: " + exchange.timeStamp + "<br>";
	exchangeDiv.appendChild(exchangeTimeStamp);

	const removeExchange = document.createElement("span");
	removeExchange.setAttribute("class", "removeExchange");
	removeExchange.innerHTML = "<a href=\"#\" id=" + exchange.id + " data-op=\"remove\">Remove exchange</a>" + "<br>" + "<br>";
	exchangeDiv.appendChild(exchangeTimeStamp);;
	exchangeDiv.appendChild(removeExchange);

	return exchangeDiv;
}


function onMessage(event) {
	const exchange = JSON.parse(event.data);
	if (exchange.action === "add") {
		const newExchangeElement = createExchangeElement(exchange);
		content.appendChild(newExchangeElement);
	}
	if (exchange.action === "remove") {
		document.getElementById(exchange.id).remove();
	}
	if (exchange.action === "update") {
		const node = document.getElementById(exchange.id);
		const statusPrice = node.children[2];
		const statusTime = node.children[3];
		statusPrice.innerHTML = "Exchange price: " + exchange.price + "<br>";
		statusTime.innerHTML = "Time exchange price: " + exchange.timeStamp + "<br>";
	}
}


//HANDLERS

function handleShowFormButton() {
	addExchangeForm.style.display = '';
}

function handleAddButton() {
	const crypto = addExchangeForm.querySelector('#crypto_name').value;
	const coin = addExchangeForm.querySelector('#coin_name').value;

	addExchangeForm.style.display = 'none';
	addExchangeForm.reset();

	const ExchangeAction = {
		action: "add",
		crypto: crypto,
		coin: coin,
	};
	socket.send(JSON.stringify(ExchangeAction));
}

function handleCancelButton() {
	addExchangeForm.style.display = 'none';
	addExchangeForm.reset();
}


//LISTENERS
const socket = new WebSocket("ws://localhost:8080/websocketexample/actions");
socket.onmessage = onMessage;

showFormButton.addEventListener('click', handleShowFormButton);
addButton.addEventListener('click', handleAddButton);
cancelButton.addEventListener('click', handleCancelButton);

content.addEventListener('click', e => {

	if (e.target.getAttribute('data-op') === 'remove') {
		const ExchangeAction = {
			action: "remove",
			id: parseInt(e.target.id)
		};
		socket.send(JSON.stringify(ExchangeAction));
	}
	
	if (e.target.getAttribute('data-op') === 'update') {
		const ExchangeAction = {
			action: "update",
			id: parseInt(e.target.id)
		};
		socket.send(JSON.stringify(ExchangeAction));
	}
});