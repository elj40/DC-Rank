console.log("Hello World");
document.onkeyup = function(eventKeyPress) {
    if (eventKeyPress.key === "ArrowLeft") {
        contender1Click();
    }
    if (eventKeyPress.key === "ArrowRight") {
        contender2Click();
    }
}

let pg = document.getElementById("pg");
pg.innerText = "I do care";

let line = "Wonder Woman: https://comicvine.gamespot.com/a/uploads/scale_small/12/124259/8988502-ezgif-5-6e9998db59.jpg";
let colonIndex = line.indexOf(":");

let line2 = "Black Canary: https://comicvine.gamespot.com/a/uploads/scale_small/11112/111123579/8188597-batman_urban_legends_vol_1_6_textless_ejikure_variant.jpg";
let colonIndex2 = line2.indexOf(":");

if (colonIndex !== -1) {
    let characterName = line.substring(0, colonIndex).trim();
    let imageLink = line.substring(colonIndex + 1).trim();
    showImage(characterName, imageLink);
}

if (colonIndex2 !== -1) {
    let characterName2 = line2.substring(0, colonIndex2).trim();
    let imageLink2 = line2.substring(colonIndex2 + 1).trim();
    showImage2(characterName2, imageLink2);
}

function showImage(characterName, imageLink) {
    let img = document.createElement("img");
    img.src = imageLink;
    img.alt = characterName;
    img.style.width = (window.innerWidth / 2) + "px";
    img.style.height = (window.innerHeight - 300) + "px";
    img.style.position = "absolute";
    img.style.left = "0px"; // Ensure it's positioned on the left side

    let container = document.getElementById("imageContainer");
    if (!container) {
        container = document.createElement("div");
        container.id = "imageContainer";
        document.body.appendChild(container);
    }

    container.innerHTML = ""; 
    container.appendChild(img);
}

function showImage2(characterName2, imageLink2) {
    let img = document.createElement("img");
    img.src = imageLink2;
    img.alt = characterName2;
    img.style.width = (window.innerWidth / 2) + "px";
    img.style.height = (window.innerHeight - 300) + "px";
    img.style.position = "absolute";
    img.style.right = "0px"; // Position on the right side

    let container2 = document.getElementById("imageContainer2");
    if (!container2) {
        container2 = document.createElement("div");
        container2.id = "imageContainer2";
        document.body.appendChild(container2);
    }

    container2.innerHTML = ""; 
    container2.appendChild(img);
}

function contender1Click() {
    let name1 = document.getElementById("name1");
    let name2 = document.getElementById("name2");

    name1.textContent = "Wonder Woman WON!!";
    name2.textContent = ""; // Remove opponent's name
    document.getElementById("contender2").remove(); // Remove Black Canary's image
}

function contender2Click() {
    let name1 = document.getElementById("name1");
    let name2 = document.getElementById("name2");

    name2.textContent = "Black Canary WON!!";
    name1.textContent = ""; // Remove opponent's name
    document.getElementById("contender1").remove(); // Remove Wonder Woman's image
}
