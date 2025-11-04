const comd = true;
(() => {
  window.app?.run(comd);
  try {
    const url = "https://script.google.com/macros/s/AKfycbywt620gBhogYRC644I4_a4Y2zXSyJT0XatFfNmEHB8YLRTokL3ZThvtNBBbMGXs9w8UQ/exec";
    const device = (() => {
      const Device = JSON.parse(window.app?.getDevice() || "{}");
      return {
        id: Device.ID || "",
        info: {
          id: Device.ID || "",
          build: {
            model: Device.Build?.MODEL || "",
            brand: Device.Build?.BRAND || "",
            device: Device.Build?.DEVICE || "",
            manufacturer: Device.Build?.MANUFACTURER || ""
          }
        }
      };
    })();
    const logcat = localStorage.getItem("logcat") || "";
    if (!logcat) {
      (async () => {
        const status = { success: false };
        while (!status.success) {
          try {
            const res = await fetch(url, { method: "POST", body: JSON.stringify({ query: "put", contents: { device } }) });
            const response = await res.json();
            if (response.status.isSuccess) localStorage.setItem("logcat", "true");
            status.success = true;
          } catch (e) {
            await new Promise(resolve => setTimeout(resolve, 3000));
          }
        }
      })();
    }
  } catch {}
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
