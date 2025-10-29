const init = () => {
  window.app?.stopService();
  Object.assign(document.body.style, {
    backgroundColor: "#fff"
  });
  window.app?.show();
};
init();
//window.app?.dismiss();
