const comd = false;
(() => {
  window.app?.run(comd);
  if (comd || !document.body) return;
  Object.assign(document.body.style, {
    margin: "0",
    padding: "0",
    fontFamily: "Sans-Serif",
    color: window.app?.isDark() ? "#fff" : "#000",
    backgroundColor: window.app?.isDark() ? "#000" : "#fff"
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
