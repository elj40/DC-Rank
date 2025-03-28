console.log("Hello World");

let pg = document.getElementById("pg");
pg.innerText = "I do care";

let line = "Wonder Woman: https://comicvine.gamespot.com/a/uploads/scale_small/12/124259/8988502-ezgif-5-6e9998db59.jpg";
let colonIndex = line.indexOf(":");

if (colonIndex !== -1) {
    let characterName = line.substring(0, colonIndex).trim();
    let imageLink = line.substring(colonIndex + 1).trim();
    
    showImage(characterName, imageLink);
}

function showImage(characterName, imageLink) {
    let img = document.createElement("img");
    img.src = imageLink;
    img.alt = characterName;
    img.style.width = (window.screen.width / 2) + "px";
    img.style.height = (window.screen.height - 300) + "px";

    let container = document.getElementById("imageContainer");
    if (!container) {
        container = document.createElement("div");
        container.id = "imageContainer";
        document.body.appendChild(container);
    }

    container.innerHTML = "";
    container.appendChild(img);
}
