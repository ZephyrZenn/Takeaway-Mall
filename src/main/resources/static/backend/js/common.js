var web_prefix = '/backend'
axios.interceptors.request.use(function (config) {
    let token = window.localStorage.getItem("Authentication")
    if (token) {
        config.headers['Authentication'] = token
    }
    return config
})