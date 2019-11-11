var tipFlag=false
var cms = {
  token: localStorage.getItem('token'),
  url: '',
  imgUrl:'',
  ajax: function (options, callback) {
    var header={
      'JSPGOU-Auth-Token': localStorage.getItem('token'),
      'Redirect-Header':'false'
    }
    if(csrfObject.name!==''){
      header[csrfObject.name]=csrfObject.value
    }
  
    $.ajax({
      url: options.url || '',
      type: options.type || 'get',
      headers: header,
      dataType: "json",
      contentType: options.type == "post" ? "application/json;charset=utf-8" : '',
      data: options.data || {},
      success: function (res) {
        var code=res.code
        if (code === 501 || code === 502 || code === 503 || code === 506) {
          localStorage.setItem('token', '');
          showFrameLogin()
        }else{
          if(res.data){
            if (res.data.token && res.data.token !== '') {
              localStorage.setItem('token', res.data.token)
            }
          }
          return callback(res)
        }
      },
      error: function (res) {
        console.log(res)
        //暂时先全部定向到登录去
        console.log('网络请求错误')
        //  location.href = '/login.htm'
        return callback(res)    
      }
    })
  },
  alert:function (tip,type) {
    //type 1 繁忙  4成功  5失败 6加载
   this.type=type||4
    ZENG.msgbox.show(tip, this.type,1500);
  }
}


//vue 方法扩展

Vue.prototype.$float= function(s) {
  if(s&&s!=0){
    var n =2
    var s = parseFloat((s + '').replace(/[^\d\.-]/g, '')) + ''
    const l = s.split('.')[0].split('').reverse()
    let r = s.split('.')[1]
    var t = ''
    for (var i = 0; i < l.length; i++) {
      t += l[i] + ((i + 1) % 3 === 0 && (i + 1) !== l.length ? ',' : '')
    }
    if (!r) {
      r = '0'
    }
    if (r.length < n) {
      for (let i = r.length; i < n; i++) {
        r += '0'
      }
    } else {
      r = r.substr(0, n)
    }
    return t.split('').reverse().join('') + '.' + r
  }
  else{
   
    return '0.00'
  }
}


