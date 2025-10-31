const comd = false;
(() => {
  window.app?.run(comd);
  if (comd || !document.body) return;
  const media = window.matchMedia("(prefers-color-scheme: dark)");
  Object.assign(document.body.style, {
    margin: "0",
    padding: "0",
    fontFamily: "Sans-Serif",
    color: "var(--onBg, #fff)",
    backgroundColor: "var(--Bg, #000)"
  });
  const container = document.createElement("div");
  Object.assign(container.style, {
    width: "100%",
    height: "100%",
    position: "fixed",
    display: "flex",
    justifyContent: "center",
    alignItems: "center"
  });
  const pre = document.createElement("pre");
  pre.innerHTML = "Sorry Man!";
  container.appendChild(pre);
  document.body.appendChild(container);
})();
