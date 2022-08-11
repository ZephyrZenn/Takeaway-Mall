function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
}

function sendValidationCode(params) {
    return $axios({
        'url': '/user/validate',
        'method': 'get',
        params
    })
}

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

  