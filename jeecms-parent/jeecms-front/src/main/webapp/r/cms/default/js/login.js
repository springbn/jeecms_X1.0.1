

   function encrypt (word) { // 加密函数
    var iv='D076D35C'
    var encryptKey='WfJTKO9S4eLkrPz2JKrAnzdb'
    var keyHex = CryptoJS.enc.Utf8.parse(encryptKey)
    var encrypted = CryptoJS.TripleDES.encrypt(word, keyHex, {
      iv: CryptoJS.enc.Utf8.parse(iv),
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7 })
    return encrypted.toString()
  }
  function decrypt(word) {
    var iv='D076D35C'
    var encryptKey='WfJTKO9S4eLkrPz2JKrAnzdb'
    var keyHex = CryptoJS.enc.Utf8.parse(encryptKey)
    var encrypted = CryptoJS.TripleDES.decrypt(word, keyHex, {
      iv: CryptoJS.enc.Utf8.parse(iv),
      mode: CryptoJS.mode.CBC,
      // mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7 })
    return encrypted.toString(CryptoJS.enc.Utf8)
  }