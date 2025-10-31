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
		backgroundColor: media.matches ? "#f00" : "#f00"
	});
	media.addEventListener("change", () => {
		Object.assign(document.body.style, {
			color: media.matches ? "#fff" : "#000",
			backgroundColor: media.matches ? "#f00" : "#f00"
		});
	});
})();
