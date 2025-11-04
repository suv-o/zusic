function doPost(e) {
  try {
    if (Object.keys(e.parameter || {}).length > 0) {
      const postData = e.parameter;
      const query = postData.query;
    } else {
      const postData = JSON.parse(e.postData.contents);
      const query = postData.query;
      if (["put"].includes(query)) {
        const data = postData.contents;
        return put(data.device);
      }
    }
  } catch (err) {
    return ContentService.createTextOutput(
      JSON.stringify({
        status: "error",
        message: err.message
      })
    ).setMimeType(ContentService.MimeType.JSON);
  }
}

function put(device) {
  if (!device) return;
  const id = String(device.id?.trim());
  const info = JSON.stringify(device.info);
  const now = new Date();
  const getCol = (headers, colName) => headers.indexOf(colName);
  const devicesSheet = SpreadsheetApp.getActive().getSheetByName("Devices");
  const devicesData = devicesSheet.getDataRange().getValues();
  const deviceHeaders = devicesData[0];
  let isExist;
  for (let i = 1; i < devicesData.length; i++) {
    if (String(devicesData[i][getCol(deviceHeaders, "Id")]) === id) {
      devicesSheet.getRange(i + 1, getCol(deviceHeaders, "Info") + 1).setValue(info);
      devicesSheet.getRange(i + 1, getCol(deviceHeaders, "Timestamp") + 1).setValue(now);
      isExist = true;
      break;
    }
  }
  if (!isExist) {
    const row = [];
    row[getCol(deviceHeaders, "Id")] = "'" + id;
    row[getCol(deviceHeaders, "Info")] = info;
    row[getCol(deviceHeaders, "Timestamp")] = now;
    for (let i = 0; i < deviceHeaders.length; i++) {
      if (typeof row[i] === "undefined") row[i] = "";
    }
    devicesSheet.appendRow(row);
  }
  return ContentService.createTextOutput(
    JSON.stringify({
      status: {
        isSuccess: true
      }
    })
  ).setMimeType(ContentService.MimeType.JSON);
}
