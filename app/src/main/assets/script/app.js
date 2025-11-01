const comd = false;
(() => {
  window.app?.run(comd);
  if (comd || !document.body) return;
  const media = window.matchMedia("(prefers-color-scheme: dark)");
  Object.assign(document.body.style, {
    margin: "0",
    padding: "0",
    fontFamily: "Sans-Serif",
    color: media.matches ? "#fff" : "#000",
    backgroundColor: media.matches ? "#000" : "#fff"
  });
  media.addEventListener("change", () => {
    Object.assign(document.body.style, {
      color: media.matches ? "#fff" : "#000",
      backgroundColor: media.matches ? "#000" : "#fff"
    });
  });
  const container = document.createElement("div");
  Object.assign(container.style, {
    width: "100%",
    height: "100%",
    position: "fixed",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    userSelect: "none"
  });
  const pre = document.createElement("pre");
  pre.innerHTML = "404!";
  container.appendChild(pre);
  document.body.appendChild(container);
})();
